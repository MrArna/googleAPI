package edu.uic.cloud_computing;

import com.google.GoogleServicesApi.GoogleServices;
import com.google.api.client.repackaged.org.apache.commons.codec.binary.Base64;
import com.google.api.services.gmail.model.Message;

import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.MimeMessage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * Created by gabe on 9/11/16.
 */
public class MessageToMimeLooper implements Runnable {

    private SynchronizedListQueue<Message> messageQueue;
    private SynchronizedListQueue<MessageContent> contentQueue;
    private ConcurrencyLock lock;
    private GoogleServices service;
    private boolean running = true;

    public void terminate(){
        running = false;
    }

    public MessageToMimeLooper(SynchronizedListQueue<MessageContent> contentQueue,
                               SynchronizedListQueue<Message> messageQueue,
                               ConcurrencyLock lock,
                               GoogleServices service){
        this.messageQueue = messageQueue;
        this.contentQueue = contentQueue;
        this.lock = lock;
        this.service = service;
    }

    /**
     * Get a Message and use it to create a MimeMessage.
     *
     * @param messageId ID of Message to retrieve.
     * @return MimeMessage MimeMessage populated from retrieved Message.
     * @throws IOException
     * @throws MessagingException
     */
    private MimeMessage getMimeMessage(String messageId)
            throws IOException, MessagingException {
        Message message = service.getGmailService().users().messages().get("me", messageId).setFormat("raw").execute();

        byte[] emailBytes = Base64.decodeBase64(message.getRaw());

        Properties props = new Properties();
        Session session = Session.getDefaultInstance(props, null);

        MimeMessage email = new MimeMessage(session, new ByteArrayInputStream(emailBytes));

        return email;
    }

    @Override
    public void run() {
        Message m;
        while( running ) {
            while(messageQueue.size() != 0 ) {
                try {
                    m = messageQueue.getNext();
                    contentQueue.add(new MessageContent(getMimeMessage(m.getId()) ));
                }
                catch (Exception e) {
                    e.printStackTrace();
                    System.exit(1);
                }
            }
        }
    }
}
