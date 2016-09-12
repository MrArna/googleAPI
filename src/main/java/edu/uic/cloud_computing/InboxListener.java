package edu.uic.cloud_computing;

import com.google.GoogleServicesApi.GoogleServices;
import com.google.api.services.gmail.model.*;

import com.google.api.services.gmail.model.Message;

import java.io.IOException;
import java.lang.Thread;

import java.util.Arrays;
import java.util.List;

public class InboxListener implements Runnable{
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
        try {
            while (running) {
                response = googleServices.getGmailService().users().messages().list("me").setQ("is:unread").execute();

                while (response.getMessages() != null) {
                    // get messages from response
                    List<Message> messages = response.getMessages();

                    for (Message message : messages)
                        messageQueue.add(markMessageAsRead(message.getId()));

                    if (response.getNextPageToken() != null) {
                        String pageToken = response.getNextPageToken();
                        response = googleServices.getGmailService().users().messages()
                                .list("me")
                                .setQ("")
                                .setPageToken(pageToken)
                                .execute();
                    }
                    else break;

                }
                Thread.sleep(1000);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }

    }
}
