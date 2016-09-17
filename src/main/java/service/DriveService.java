package service;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Marco on 11/09/16.
 */
public class DriveService
{
    /** Application name. */
    private  final String APPLICATION_NAME = "Drive API Java";

    /** Directory to store user credentials for this application. */
    private  final java.io.File DATA_STORE_DIR = new java.io.File(
            System.getProperty("user.home"), ".credentials/drive-googleapis");

    /** Global instance of the {@link FileDataStoreFactory}. */
    private  FileDataStoreFactory DATA_STORE_FACTORY;

    /** Global instance of the JSON factory. */
    private  final JsonFactory JSON_FACTORY =
            JacksonFactory.getDefaultInstance();

    /** Global instance of the HTTP transport. */
    private  HttpTransport HTTP_TRANSPORT;

    /** Global instance of the scopes required by this quickstart.
     *
     * If modifying these scopes, delete your previously saved credentials
     * at ~/.credentials/drive-java-quickstart
     */
    private  final List<String> SCOPES = Arrays.asList(DriveScopes.DRIVE);

    private final  String user = "marco.arnaboldi91@gmail.com";


    private Drive service;

    public DriveService()
    {
        try {
            HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
            DATA_STORE_FACTORY = new FileDataStoreFactory(DATA_STORE_DIR);
        } catch (Throwable t) {
            t.printStackTrace();
            System.exit(1);
        }

        try {
            service = getDriveService();
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }

    }

    /**
     * Creates an authorized Credential object.
     * @return an authorized Credential object.
     * @throws IOException
     */
    public Credential authorize() throws IOException {
        // Load client secrets.
        InputStream in =
                DriveService.class.getResourceAsStream("/false_secret_client.json");
        GoogleClientSecrets clientSecrets =
                GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));

        // Build flow and trigger user authorization request.
        GoogleAuthorizationCodeFlow flow =
                new GoogleAuthorizationCodeFlow.Builder(
                        HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES)
                        .setDataStoreFactory(DATA_STORE_FACTORY)
                        .setAccessType("offline")
                        .build();
        Credential credential = new AuthorizationCodeInstalledApp(
                flow, new LocalServerReceiver()).authorize(user);
        System.out.println(
                "Credentials saved to " + DATA_STORE_DIR.getAbsolutePath());
        return credential;
    }

    /**
     * Build and return an authorized Drive client service.
     * @return an authorized Drive client service
     * @throws IOException
     */
    public Drive getDriveService() throws IOException
    {
        Credential credential = authorize();
        return new Drive.Builder(
                HTTP_TRANSPORT, JSON_FACTORY, credential)
                .setApplicationName(APPLICATION_NAME)
                .build();
    }

    public List<File> getSpreadSheetsBy(Integer num) throws IOException
    {
        // Print the names and IDs for up to 10 files.
        FileList result = service.files().list()
                .setPageSize(num)
                .setQ("mimeType = 'application/vnd.google-apps.spreadsheet' and trashed = false")
                .setFields("nextPageToken, files(id, name)")
                .execute();
        List<File> files = result.getFiles();
        return files;
    }

    //create a spreadshhet with the given name
    public String createSpreadsheet(String name) throws IOException
    {
        System.out.println("Creating new spreadsheet...");
        File newFile = new File();
        newFile.setName(name);
        newFile.setMimeType("application/vnd.google-apps.spreadsheet");
        File file = service.files().create(newFile)
                .setFields("id")
                .execute();
        System.out.println("Spreadsheet created with id: " + file.getId());
        return  file.getId();
    }

    //delete the spreadsheet of the given name
    public void deleteSpreadsheet(String name) throws IOException {
        List<File> spreads = getSpreadSheetsBy(10);
        String fileID = "null";
        if (spreads == null || spreads.size() == 0)
        {
            System.out.println("No files found.");
        }
        else
        {
            for (File file : spreads)
            {
                if(file.getName().equals(name))
                {
                    fileID = file.getId();
                    System.out.println("File found.");
                    break;
                }
            }
        }
        if (!fileID.equals("null"))
        {
            service.files().delete(fileID);
        }
    }
}
