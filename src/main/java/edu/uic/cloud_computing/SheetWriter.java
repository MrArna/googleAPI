package edu.uic.cloud_computing;

import com.google.GoogleServicesApi.GoogleServices;

import java.io.IOException;

public class SheetWriter implements Runnable{
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
}
