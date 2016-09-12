package edu.uic.cloud_computing;

import com.google.GoogleServicesApi.GoogleServices;

import java.io.IOException;

public class SheetWriter implements Runnable{
    /** Application name. */
    protected static final String APPLICATION_NAME =
            "Google Sheets API Java Quickstart";

    private SynchronizedListQueue<MessageContent> contentQueue;
    private GoogleServices googleServices;
    private boolean running = true;

    public SheetWriter(SynchronizedListQueue<MessageContent> contentQueue,
                       GoogleServices googleServices) throws IOException {
        this.contentQueue = contentQueue;
        this.googleServices = googleServices;
    }


    public void terminate(){
        running = false;
    }

    @Override
    public void run() {
        MessageContent messageContent;
        while( running) {
            while(contentQueue.size() > 0 ) {
                try {
                    messageContent = contentQueue.getNext();
                    System.out.println(messageContent.toString());
                }
                catch (Exception e) {
                    e.printStackTrace();
                    System.exit(8);
                }
            }
        }


    }

//    public void main(String[] args) throws IOException {
//        // Build a new authorized API client service.
//        Sheets service =  getService(SheetWriter.SCOPES, SheetWriter.DATA_STORE_DIR);
//
//        // Prints the names and majors of students in a sample spreadsheet:
//        // https://docs.google.com/spreadsheets/d/1BxiMVs0XRA5nFMdKvBdBZjgmUUqptlbs74OgvE2upms/edit
//        String spreadsheetId = "1BxiMVs0XRA5nFMdKvBdBZjgmUUqptlbs74OgvE2upms";
//
//        String range = "Class Data!A2:E";
//        ValueRange response = service.spreadsheets().values()
//                .get(spreadsheetId, range)
//                .execute();
//        List<List<Object>> values = response.getValues();
//        if (values == null || values.size() == 0) {
//            System.out.println("No data found.");
//        } else {
//            System.out.println("Name, Major");
//            for (List row : values) {
//                // Print columns A and E, which correspond to indices 0 and 4.
//                System.out.printf("%s, %s\n", row.get(0), row.get(4));
//            }
//        }
//
//    }
}
