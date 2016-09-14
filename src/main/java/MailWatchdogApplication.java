import com.google.api.services.drive.model.File;
import service.DriveService;
import service.MailService;
import service.PubSubService;
import service.SpreadsheetService;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;


/**
 * Created by Marco on 11/09/16.
 */
public class MailWatchdogApplication
{
    private static MailService gmailService;
    private static DriveService driveService;
    private static SpreadsheetService spreadsheetService;

    private static void serviceInitialization()
    {
        gmailService = new MailService();
        driveService = new DriveService();
        spreadsheetService = new SpreadsheetService();
    }

    public static void main(String[] args) throws IOException, ParseException
    {


        /*String content = "{message:{data:\"eyJlbWFpbEFkZHJlc3MiOiAidXNlckBleGFtcGxlLmNvbSIsICJoaXN0b3J5SWQiOiAiMTIzNDU2Nzg5MCJ9\",message_id: \"1234567890\"}, subscription: \"projects/myproject/subscriptions/mysubscription\"}";

        JSONObject jsonObjParent = new JSONObject(content);
        System.out.println(jsonObjParent.keySet().toString());

        JSONObject jsonObj = jsonObjParent.getJSONObject("message");



        JSONObject data = new JSONObject(new String(Base64.decodeBase64(jsonObj.getString("data").getBytes()), StandardCharsets.UTF_8));

        System.out.println(data.toString());
        */


        //Service initialization
        serviceInitialization();


        //Retriving current date
        DateFormat dateFormat = new SimpleDateFormat("ddMMyyyy");
        Date date = new Date();
        String today = dateFormat.format(date);


        System.out.println("Looking for existing spreadsheet for today...");

        List<File> files = driveService.getSpreadSheetsBy(10);
        Boolean fileAlreadyExist = false;
        String fileID = "null";

        if (files == null || files.size() == 0)
        {
            System.out.println("No files found.");
        }
        else
        {
            for (File file : files)
            {
                if(file.getName().equals(today))
                {
                    fileAlreadyExist = true;
                    fileID = file.getId();
                    System.out.println("File found.");
                    break;
                }
            }
        }
        if(!fileAlreadyExist)
        {
            System.out.println("No files found.");
            fileID = driveService.createSpreadsheet(today);
            spreadsheetService.decorateSheet(fileID);
            spreadsheetService.appenRows(fileID, gmailService.getUnreadedMailByToday());

        }

        gmailService.startWatcher();

        PubSubService.listenSubscription(gmailService, spreadsheetService);

    }
}
