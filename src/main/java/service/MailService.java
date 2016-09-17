package service;
/**
 * Created by Marco on 09/09/16.
 */
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow.Builder;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.GmailScopes;
import com.google.api.services.gmail.model.*;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class MailService {

    //Location where to save credential given by Google at first access
    private  final File DATA_STORE_DIR = new File(System.getProperty("user.home"), ".credentials/mail.googleapis");
    private  FileDataStoreFactory DATA_STORE_FACTORY;
    private  HttpTransport HTTP_TRANSPORT;

    private  final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
    private  final List<String> SCOPES = Arrays.asList(GmailScopes.MAIL_GOOGLE_COM);

    private Gmail service;

    private final  String user = "marco.arnaboldi91@gmail.com";

    private BigInteger oldHistoryId;

    public MailService()
    {
        try {
            HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
            DATA_STORE_FACTORY = new FileDataStoreFactory(DATA_STORE_DIR);
        } catch (Throwable var1) {
            var1.printStackTrace();
            System.exit(1);
        }

        try {
            service = getGmailService();
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    /*
        Method to check credentials, if they're not it launch a request
     */
    private Credential authorize() throws IOException
    {
        //Retrive the credential downloaded by Google and parse them in the correct structure
        InputStream in = MailService.class.getResourceAsStream("/client_secret.json");
        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));

        //Request is made and response handled
        GoogleAuthorizationCodeFlow flow = (new Builder(HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES)).setDataStoreFactory(DATA_STORE_FACTORY).setAccessType("offline").build();
        Credential credential = (new AuthorizationCodeInstalledApp(flow, new LocalServerReceiver())).authorize(user);
        System.out.println("Credentials saved to " + DATA_STORE_DIR.getAbsolutePath());
        return credential;
    }

    /*
        Ask and retrive the Gmail access permission
     */
    public Gmail getGmailService() throws IOException
    {
        //Check the authorization
        Credential credential = authorize();
        return (new com.google.api.services.gmail.Gmail.Builder(HTTP_TRANSPORT, JSON_FACTORY, credential)).setApplicationName("Mail watchdog").build();
    }


    public  List<String> getUnreadedMailByToday() throws ParseException, IOException
    {
        System.out.println("Retrieving unread mails by today...");

        ListMessagesResponse response = service.users().messages()
                                                        .list(user)
                                                        .setQ("is:unread")
                                                        .execute();
        String dateString = "null";
        String subject = "null";
        Message message;
        String mail;
        List<String> mails = new ArrayList<String>();


        for(Message msgItr : response.getMessages()) {
            message = service.users().messages()
                    .get(user, msgItr.getId())
                    .execute();
            for (MessagePartHeader headerItr : message.getPayload().getHeaders()) {
                if (headerItr.getName().equals("Subject")) {
                    subject = headerItr.getValue();
                }
                if (headerItr.getName().equals("Date")) {
                    dateString = headerItr.getValue();
                }
            }
            SimpleDateFormat dataParser = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss X", Locale.US);
            SimpleDateFormat dataFormatter = new SimpleDateFormat("dd;MM;yyyy;HH:mm:ss");
            Date date = dataParser.parse(dateString);
            mail = subject + ";" + dataFormatter.format(date);
            mails.add(mail);
        }
        System.out.println("Mails retrieved");
        return  mails;
    }


    //start a subscription to a  topic for the gmail service, like saying that I'm a listener
    public void startWatcher() throws IOException
    {
        WatchRequest request = new WatchRequest();
        List<String> labelIds = new ArrayList<>();
        labelIds.add("INBOX");
        request.setLabelIds(labelIds);
        request.setTopicName("projects/cs441hw1-143120/topics/mail");
        WatchResponse response = service.users().watch(user,request).execute();
        oldHistoryId = response.getHistoryId();
    }

    // given a history, retvies all the unread mail in that history
    public List<String > getNewMailByHistory(String userId,BigInteger historyId) throws IOException, ParseException {
        Message message;
        String subject = null;
        String dateString = null;
        String mail = null;
        List<String> mails = new ArrayList<>();
        List<HistoryMessageAdded> messages = new ArrayList<>();
        ListHistoryResponse response = service.users().history().list(userId)
                .setStartHistoryId(oldHistoryId)
                .setMaxResults(10L)
                .setLabelId("UNREAD")
                .setLabelId("INBOX")
                .execute();


        System.out.println(response.toPrettyString());
        if(response.getHistory() != null)
        {
            for (History history : response.getHistory()) {
                messages = history.getMessagesAdded();
                if(messages != null)
                {
                    for (HistoryMessageAdded msg : messages) {
                        System.out.println(msg.toPrettyString());

                        message = service.users().messages()
                                .get(user, msg.getMessage().getId())
                                .execute();
                        for (MessagePartHeader headerItr : message.getPayload().getHeaders()) {
                            if (headerItr.getName().equals("Subject")) {
                                subject = headerItr.getValue();
                            }
                            if (headerItr.getName().equals("Date")) {
                                dateString = headerItr.getValue();
                            }
                        }
                        SimpleDateFormat dataParser = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss X", Locale.US);
                        SimpleDateFormat dataFormatter = new SimpleDateFormat("dd;MM;yyyy;HH:mm:ss");
                        Date date = dataParser.parse(dateString);
                        mail = subject + ";" + dataFormatter.format(date);
                        mails.add(mail);
                    }
                }
            }
        }
        oldHistoryId = historyId;
        return  mails;
    }

    public BigInteger getOldHistoryId() {
        return oldHistoryId;
    }

    public void setOldHistoryId(BigInteger oldHistoryId) {
        this.oldHistoryId = oldHistoryId;
    }
}
