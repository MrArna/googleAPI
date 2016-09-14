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
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.SheetsScopes;
import com.google.api.services.sheets.v4.model.ValueRange;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Marco on 11/09/16.
 */
public class SpreadsheetService
{

    /** Application name **/
    private final String APPLICATION_NAME = "Spreadsheet Service";

    /** Directory to store user credentials for this application. */
    private final java.io.File DATA_STORE_DIR = new java.io.File(
            System.getProperty("user.home"), ".credentials/sheets.googleapis");

    /** Global instance of the {@link FileDataStoreFactory}. */
    private FileDataStoreFactory DATA_STORE_FACTORY;

    /** Global instance of the JSON factory. */
    private final JsonFactory JSON_FACTORY =
            JacksonFactory.getDefaultInstance();

    /** Global instance of the HTTP transport. */
    private HttpTransport HTTP_TRANSPORT;

    /** Global instance of the scopes required by this quickstart.
     *
     * If modifying these scopes, delete your previously saved credentials
     * at ~/.credentials/sheets.googleapis.com-java-quickstart
     */
    private  final List<String> SCOPES =
            Arrays.asList(SheetsScopes.DRIVE);

    private final String user = "marco.arnaboldi91@gmail.com";

    private  String lastFileId;


    private Sheets service;

    public SpreadsheetService()
    {
        try {
            HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
            DATA_STORE_FACTORY = new FileDataStoreFactory(DATA_STORE_DIR);
        } catch (Throwable t) {
            t.printStackTrace();
            System.exit(1);
        }

        try {
            service = getSheetsService();
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
                SpreadsheetService.class.getResourceAsStream("/client_secret.json");
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
     * Build and return an authorized Sheets API client service.
     * @return an authorized Sheets API client service
     * @throws IOException
     */
    public Sheets getSheetsService() throws IOException
    {
        Credential credential = authorize();
        return new Sheets.Builder(HTTP_TRANSPORT, JSON_FACTORY, credential)
                .setApplicationName(APPLICATION_NAME)
                .build();
    }


    public void decorateSheet(String id) throws IOException
    {
        System.out.println("Setting up the spreadsheet");
        ValueRange content = new ValueRange();
        List<Object> headers = new ArrayList<Object>();
        List<List<Object>> matrix = new ArrayList<List<Object>>();
        headers.add("Subject");
        headers.add("Day");
        headers.add("Month");
        headers.add("Year");
        headers.add("Time");
        matrix.add(headers);
        content.setValues(matrix);
        content.setMajorDimension("ROWS");
        content.setRange("Foglio1!A1:E1");
        service.spreadsheets().values()
                .update(id,"Foglio1!A1:E1",content)
                .setValueInputOption("USER_ENTERED")
                .execute();
        System.out.println("Setting up completed");
        lastFileId = id;
    }

    public void appenRows(String fileId, List<String> rows) throws IOException
    {
        System.out.println("Appending new rows...");
        ValueRange content = new ValueRange();
        List<Object> cells = new ArrayList<Object>();
        List<List<Object>> matrix = new ArrayList<List<Object>>();

        for(String row : rows)
        {
            cells = new ArrayList<Object>();
            for(String cell : row.split(";"))
            {
                cells.add(cell);
            }
            matrix.add(cells);
        }
        content.setValues(matrix);
        content.setMajorDimension("ROWS");
        content.setRange("Foglio1!A:E");
        service.spreadsheets().values()
                .append(fileId,"Foglio1!A:E",content)
                .setValueInputOption("USER_ENTERED")
                .execute();
        System.out.println("Rows appended successfully");
        lastFileId = fileId;
    }

    public void appenRows(List<String> rows) throws IOException
    {
        appenRows(lastFileId, rows);
    }


}
