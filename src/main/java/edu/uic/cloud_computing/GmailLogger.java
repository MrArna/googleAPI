package edu.uic.cloud_computing;

import com.google.GoogleServicesApi.GoogleServices;
import com.google.api.services.gmail.model.Message;
import java.io.IOException;
import java.security.GeneralSecurityException;

/**
 * Created by gabe on 9/11/16.
 */
public class GmailLogger {
    /** Application name. */
    public static final String APPLICATION_NAME =
            "Google Sheets API Java Quickstart";
    public static void main(String[] args)
            throws IOException, InterruptedException, GeneralSecurityException{
        GoogleServices googleServices = new GoogleServices();
        ConcurrencyLock lock = new ConcurrencyLock();


        SynchronizedListQueue<MessageContent> contentQueue = new SynchronizedListQueue<>();
        SynchronizedListQueue<Message> messageQueue = new SynchronizedListQueue<>();

        InboxListener inboxListener = new InboxListener( contentQueue, messageQueue, googleServices);
        MessageToMimeLooper messageToMimeWorker = new MessageToMimeLooper(contentQueue, messageQueue, lock, googleServices);
        SheetWriter sheetWriter = new SheetWriter(contentQueue, googleServices);

        Thread worker1 = new Thread( inboxListener );
        Thread worker2 = new Thread( messageToMimeWorker );
        Thread worker3 = new Thread( sheetWriter );

        worker1.start();
        worker2.start();
        worker3.start();

        Thread.sleep(10000);

        inboxListener.terminate();
        messageToMimeWorker.terminate();
        sheetWriter.terminate();

        worker1.join();
        worker2.join();
        worker3.join();

    }



}
