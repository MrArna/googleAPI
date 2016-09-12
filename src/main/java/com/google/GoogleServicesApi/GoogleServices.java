package com.google.GoogleServicesApi;

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
import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.GmailScopes;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.SheetsScopes;
import edu.uic.cloud_computing.InboxListener;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.GeneralSecurityException;
import java.util.Arrays;
import java.util.List;

public class GoogleServices {
    /** Global instance of the scopes required by Sheets Api.
     *
     * If modifying these scopes, delete your previously saved credentials
     * at ~/.credentials/cs441.googleapi.google-sheets-service
     */
    protected static final List<String> SHEETS_SCOPES =
            (List<String>) Arrays.asList(SheetsScopes.SPREADSHEETS_READONLY);

    /** Global instance of the scopes required by Gmail Api
     *
     * If modifying these scopes, delete your previously saved credentials
     * at ~/.credentials/cs441.googleapi/gmail-service
     */
    private static final List<String> GMAIL_SCOPES =
            Arrays.asList(GmailScopes.GMAIL_LABELS, GmailScopes.GMAIL_READONLY, GmailScopes.GMAIL_COMPOSE,
                    GmailScopes.GMAIL_INSERT, GmailScopes.GMAIL_MODIFY, GmailScopes.GMAIL_SEND,
                    GmailScopes.GMAIL_SETTINGS_BASIC, GmailScopes.GMAIL_SETTINGS_SHARING, GmailScopes.MAIL_GOOGLE_COM);

    /** Directory to store user credentials for this application. */
    private static final java.io.File SHEETS_DATA_STORE_DIR = new java.io.File(
            System.getProperty("user.home"), ".credentials/cs441.googleapi/sheet-writer-service");

    /** Directory to store user credentials for this application. */
    private static final java.io.File GMAIL_DATA_STORE_DIR = new java.io.File(
            System.getProperty("user.home"), ".credentials/cs441.googleapi/inbox-listerner.service");

    /** Global instance of the {@link FileDataStoreFactory}. */
    private FileDataStoreFactory GMAIL_DATA_STORE_FACTORY;
    private FileDataStoreFactory SHEETS_DATA_STORE_FACTORY;

    /** Global instance of the JSON factory. */
    private final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();

    /** Global instance of the HTTP transport. */
    private HttpTransport HTTP_TRANSPORT;
    private Credential credential = null;

    private Gmail gmailService;
    private Sheets sheetsService;

    public GoogleServices() throws IOException, GeneralSecurityException{
        HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
        setGmailervice(GMAIL_DATA_STORE_DIR);
        setSheetService(SHEETS_DATA_STORE_DIR);
    }

    /**
     * Creates an authorized Credential object.
     * @return an authorized Credential object.
     * @throws IOException
     */
    private Credential authorize( List<String> SCOPES, File DATA_STORE_DIR,
                                  FileDataStoreFactory DATA_STORE_FACTORY) throws IOException {
        // Load client secrets.
        InputStream in =
                GoogleServices.class.getResourceAsStream("/client_secret.json");

        GoogleClientSecrets clientSecrets =
                GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));

        // Build flow and trigger user authorization request.
        GoogleAuthorizationCodeFlow flow =
                new GoogleAuthorizationCodeFlow.Builder(
                        HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES)
                        .setDataStoreFactory(DATA_STORE_FACTORY)
                        .setAccessType("offline")
                        .build();

        this.credential = new AuthorizationCodeInstalledApp(
                flow, new LocalServerReceiver()).authorize("user");

        System.out.println(
                "Credentials saved to " + DATA_STORE_DIR.getAbsolutePath());
        return credential;
    }

    /**
     * Build and return an authorized Gmail API client service.
     * @return an authorized Gmail API client service
     * @throws IOException
     */
    private void setGmailervice(File DATA_STORE_DIR) throws IOException, GeneralSecurityException{
        GMAIL_DATA_STORE_FACTORY = new FileDataStoreFactory(DATA_STORE_DIR);
        authorize(GMAIL_SCOPES, DATA_STORE_DIR, GMAIL_DATA_STORE_FACTORY);
        gmailService =  new Gmail.Builder(HTTP_TRANSPORT, JSON_FACTORY, credential)
                .setApplicationName(InboxListener.APPLICATION_NAME)
                .build();
    }

    /**
     * Build and return an authorized Sheets API client service.
     * @return an authorized Sheets API client service
     * @throws IOException
     */
    private void setSheetService(File DATA_STORE_DIR) throws IOException, GeneralSecurityException{
        SHEETS_DATA_STORE_FACTORY = new FileDataStoreFactory(DATA_STORE_DIR);
        authorize(SHEETS_SCOPES, DATA_STORE_DIR, SHEETS_DATA_STORE_FACTORY);
        sheetsService = new Sheets.Builder(HTTP_TRANSPORT, JSON_FACTORY, credential)
                .setApplicationName(InboxListener.APPLICATION_NAME)
                .build();
    }

    public Gmail getGmailService() {
        return gmailService;
    }

    public Sheets getSheetsService(){
        return sheetsService;
    }


}
