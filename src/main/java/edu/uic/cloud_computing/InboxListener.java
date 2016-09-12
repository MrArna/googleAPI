package edu.uic.cloud_computing;

import com.google.GoogleServicesApi.GoogleServices;
import com.google.api.services.gmail.model.*;

import com.google.api.services.gmail.model.Message;

import java.io.IOException;
import java.lang.Thread;

import java.util.Arrays;
import java.util.List;

public class InboxListener implements Runnable{
    /** Application name. */
    public static final String APPLICATION_NAME =
            "Gmail API Java Quickstart";

    private GoogleServices googleServices;
    private boolean running = true;

    protected SynchronizedListQueue<MessageContent> contentQueue;
    protected SynchronizedListQueue<Message> messageQueue;



    public InboxListener(SynchronizedListQueue<MessageContent> contentQueue,
                         SynchronizedListQueue<Message> messageQueue,
                         GoogleServices googleServices) throws IOException{
        this.contentQueue = contentQueue;
        this.messageQueue = messageQueue;
        this.googleServices = googleServices;

    }

    public void terminate(){
        running = false;
    }



    private Message markMessageAsRead(String messageId) throws IOException {
        ModifyMessageRequest mod = new ModifyMessageRequest().setRemoveLabelIds(Arrays.asList("UNREAD"));
        return googleServices.getGmailService().users().messages().modify("me",messageId,mod).execute();
    }


    @Override
    public void run() {
        ListMessagesResponse response = null;
        while(running) {

            try{
                response = googleServices.getGmailService().users().messages().list("me").setQ("is:unread").execute();
            }
            catch(Exception e) {
                e.printStackTrace();
                System.exit(1);
            }

            while(response.getMessages() != null) {
                // get messages from response
                List<Message> messages = response.getMessages();

                try {
                    for(Message message : messages) {
                        messageQueue.add(markMessageAsRead(message.getId()));
                    }
                }
                catch (Exception e) {
                    e.printStackTrace();
                    System.exit(3);
                }

                if (response.getNextPageToken() != null) {
                    String pageToken = response.getNextPageToken();
                    try {
                        response = googleServices.getGmailService().users().messages()
                                .list("me")
                                .setQ("")
                                .setPageToken(pageToken)
                                .execute();
                    }
                    catch (Exception e){
                        e.printStackTrace();
                        System.exit(2);
                    }

                } else {
                    break;
                }
            }
            try {
                Thread.sleep(1000);
            }
            catch (Exception e){
                e.printStackTrace();
                System.exit(5);
            }
        }

    }

//
//    private Runnable messageToMimeConverter = new Runnable() {
//        @Override
//        public void run() {
//            Message m;
//            while(messageListQueue.size() != 0 ) {
//                try {
//                    m = messageListQueue.getNext();
//                    messageContentListQueue.add(new MessageContent( getMimeMessage( m.getId()) ));
//                }
//                catch (Exception e) {
//                    e.printStackTrace();
//                    System.exit(1);
//                }
//            }
//            synchronized (lock){
//                lock.openLock();
//            }
//        }
//    };
}
