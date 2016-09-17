package service;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by Marco on 17/09/16.
 */
public class MailServiceTest {

    private  MailService service;

    //check for unread mails

    @Before
    public void setUp() throws Exception {
        service = new MailService();
    }

    @Test
    public void getUnreadedMailByToday() throws Exception
    {
        assertNotNull(service.getUnreadedMailByToday());
    }

}