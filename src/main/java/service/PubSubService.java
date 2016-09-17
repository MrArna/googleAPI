package service;

import com.google.api.services.pubsub.Pubsub;
import com.google.api.services.pubsub.model.*;
import configuration.PortableConfiguration;
import org.json.JSONObject;

import java.io.IOException;
import java.math.BigInteger;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Marco on 12/09/16.
 */
public class PubSubService {


    public static void listenSubscription(MailService gmailService, SpreadsheetService spreadsheetService) throws IOException, ParseException {
        Pubsub pubsub = PortableConfiguration.createPubsubClient();
        String subscriptionName =
                "projects/cs441hw1-143120/subscriptions/mailNotification";
        // You can fetch multiple messages with a single API call.
        int batchSize = 10;
        PullRequest pullRequest = new PullRequest()
                // Setting ReturnImmediately to false instructs the API to
                // wait to collect the message up to the size of
                // MaxEvents, or until the timeout.
                .setReturnImmediately(true)
                .setMaxMessages(batchSize);

        //pull the subscription for new publication, if there're some call the mail parsing service
        do {
            PullResponse pullResponse = pubsub.projects().subscriptions()
                    .pull(subscriptionName, pullRequest).execute();
            List<String> ackIds = new ArrayList<>(batchSize);
            List<ReceivedMessage> receivedMessages =
                    pullResponse.getReceivedMessages();
            if (receivedMessages == null || receivedMessages.isEmpty()) {
                // The result was empty.
                System.out.println("There were no messages. Press Enter to exit.");
                continue;
            }
            for (ReceivedMessage receivedMessage : receivedMessages) {
                PubsubMessage pubsubMessage = receivedMessage.getMessage();
                if (pubsubMessage != null) {
                    System.out.print("Message: ");
                    System.out.println(
                            new String(pubsubMessage.decodeData(), "UTF-8"));
                }

                JSONObject jsonObject = new JSONObject(new String(pubsubMessage.decodeData(), "UTF-8"));
                String userId = jsonObject.getString("emailAddress");
                BigInteger historyId = jsonObject.getBigInteger("historyId");
                spreadsheetService.appenRows(gmailService.getNewMailByHistory(userId,historyId));
                ackIds.add(receivedMessage.getAckId());
            }
            // Ack can be done asynchronously if you care about throughput. MUST done.
            AcknowledgeRequest ackRequest =
                    new AcknowledgeRequest().setAckIds(ackIds);
            pubsub.projects().subscriptions()
                    .acknowledge(subscriptionName, ackRequest).execute();
            // Keep pulling till a key is pressed
        } while (System.in.available() == 0);
    }







}
