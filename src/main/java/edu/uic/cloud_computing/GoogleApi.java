package edu.uic.cloud_computing;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.gmail.Gmail;
import com.google.api.client.googleapis.services.json.AbstractGoogleJsonClient;
import com.google.api.services.sheets.v4.Sheets;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

//import static edu.uic.cloud_computing.GoogleSheetsApi.APPLICATION_NAME;

public class GoogleApi {


    /** Global instance of the {@link FileDataStoreFactory}. */
    protected static FileDataStoreFactory DATA_STORE_FACTORY;

    /** Global instance of the JSON factory. */
    protected static final JsonFactory JSON_FACTORY =
            JacksonFactory.getDefaultInstance();

    /** Global instance of the HTTP transport. */
    protected static HttpTransport HTTP_TRANSPORT;



    /**
     * Creates an authorized Credential object.
     * @return an authorized Credential object.
     * @throws IOException
     */
    public static Credential authorize( List<String> SCOPES, java.io.File DATA_STORE_DIR) throws IOException {
        // Load client secrets.
        InputStream in =
                GoogleSheetsApi.class.getResourceAsStream("/edu/uic/cloud_computing/client_secret.json");

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
                flow, new LocalServerReceiver()).authorize("user");
        System.out.println(
                "Credentials saved to " + DATA_STORE_DIR.getAbsolutePath());
        return credential;
    }

    /**
     * Build and return an authorized Sheets API client service.
     * @return an authorized Sheets API client service
     * @throws IOException
     */

    protected static AbstractGoogleJsonClient getService(String SERVICE,  List<String> SCOPES, java.io.File DATA_STORE_DIR) {
        Credential credential = null;
        try {
            credential = authorize(SCOPES, DATA_STORE_DIR);
        }
        catch (Throwable t) {
            t.printStackTrace();
            System.exit(1);
        }


        if(SERVICE.equals("SHEETS") ) {

            return new Sheets.Builder(HTTP_TRANSPORT, JSON_FACTORY, credential)
                    .setApplicationName(GoogleSheetsApi.APPLICATION_NAME)
                    .build();
        }
        else if (SERVICE.equals("GMAIL") ) {
            return new Gmail.Builder(HTTP_TRANSPORT, JSON_FACTORY, credential)
                    .setApplicationName(GmailApi.APPLICATION_NAME)
                    .build();
        }

        return null;
    }


}
