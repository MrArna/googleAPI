package service;

import com.google.api.services.drive.model.File;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

/**
 * Created by Marco on 17/09/16.
 */
public class SpreadsheetServiceTest {

    private SpreadsheetService service;
    private DriveService driveService;

    @Before
    public void setUp() throws Exception
    {
        service = new SpreadsheetService();
        driveService = new DriveService();
    }


    //modify the test spreadsheet, if found
    @Test
    public void modifyTestSpreadsheet() throws Exception
    {
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
                if(file.getName().equals("test"))
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
            fileID = driveService.createSpreadsheet("test");
            service.decorateSheet(fileID);
        }
        assertNotEquals(fileID,"null");
    }

}