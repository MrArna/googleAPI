package service;

import com.google.api.services.drive.model.File;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

/**
 * Created by Marco on 17/09/16.
 */
public class DriveServiceTest {

    DriveService service;

    @Before
    public void setUp() throws Exception
    {
        service = new DriveService();

    }


    //delete, if exist, and create a test file
    @Test
    public void createSpreadSheet() throws Exception
    {
        List<File> files = service.getSpreadSheetsBy(10);
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
                    System.out.println("File found. Deleting test");
                    service.deleteSpreadsheet("test");
                    break;
                }
            }
        }
        fileID = service.createSpreadsheet("test");
        System.out.println("No files found. Creating test");
        assertNotEquals(fileID,"null");
    }

}