f![Java CI with Maven](https://github.com/jav/expo-server-sdk-java/workflows/Java%20CI%20with%20Maven/badge.svg)

## expo-server-sdk-java
This is a java implementation of the [node server-side library](https://github.com/expo/expo-server-sdk-node) for working with expo using Java.
For other implementations, see [the expo docs](https://docs.expo.io/versions/latest/guides/push-notifications/#2-call-expos-push-api-with-the).


## Usage
Add package to your source tree (the package is not yet uploaded to e.g. Maven Central).

Then try the [Example Server](https://github.com/jav/expo-server-sdk-java/blob/master/src/java/io/github/jav/exposerversdk/example/ExampleExpoServer.java)
```java
package io.github.jav.exposerversdk.examples;

import io.github.jav.exposerversdk.ExpoPushMessage;
import io.github.jav.exposerversdk.ExpoPushMessageTicketPair;
import io.github.jav.exposerversdk.ExpoPushReceiept;
import io.github.jav.exposerversdk.ExpoPushTicket;
import io.github.jav.exposerversdk.PushClient;
import io.github.jav.exposerversdk.PushClientException;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

public class ExampleExpoServer {
    public static void main(String[] args) throws PushClientException, InterruptedException {
        String recipient = "ExponentPushToken[thisisaninvalidtokennn]"; // To test, you must replace the recipient with a valid token!
        String title = "My message title!";
        String message = "A push message from ExampleExpoServer";

        if (!PushClient.isExponentPushToken(recipient))
            throw new Error("Token:" + recipient + " is not a valid token.");

        ExpoPushMessage expoPushMessage = new ExpoPushMessage();
        expoPushMessage.getTo().add(recipient);
        expoPushMessage.setTitle(title);
        expoPushMessage.setBody(message);

        List<ExpoPushMessage> expoPushMessages = new ArrayList<>();
        expoPushMessages.add(expoPushMessage);

        PushClient client = new PushClient();
        List<List<ExpoPushMessage>> chunks = client.chunkPushNotifications(expoPushMessages);

        List<CompletableFuture<List<ExpoPushTicket>>> messageRepliesFutures = new ArrayList<>();

        for (List<ExpoPushMessage> chunk : chunks) {
            messageRepliesFutures.add(client.sendPushNotificationsAsync(chunk));
        }

        // Wait for each completable future to finish
        List<ExpoPushTicket> allTickets = new ArrayList<>();
        for (CompletableFuture<List<ExpoPushTicket>> messageReplyFuture : messageRepliesFutures) {
            try {
                for (ExpoPushTicket ticket : messageReplyFuture.get()) {
                    allTickets.add(ticket);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        }

        List<ExpoPushMessageTicketPair<ExpoPushMessage>> zippedMessagesTickets = client.zipMessagesTickets(expoPushMessages, allTickets);

        List<ExpoPushMessageTicketPair<ExpoPushMessage>> okTicketMessages = client.filterAllSuccessfulMessages(zippedMessagesTickets);
        String okTicketMessagesString = okTicketMessages.stream().map(
                p -> "Title: " + p.message.getTitle() + ", Id:" + p.ticket.getId()
        ).collect(Collectors.joining(","));
        System.out.println(
                "Recieved OK ticket for " +
                        okTicketMessages.size() +
                        " messages: " + okTicketMessagesString
        );

        List<ExpoPushMessageTicketPair<ExpoPushMessage>> errorTicketMessages = client.filterAllMessagesWithError(zippedMessagesTickets);
        String errorTicketMessagesString = errorTicketMessages.stream().map(
                p -> "Title: " + p.message.getTitle() + ", Error: " + p.ticket.getDetails().getError()
        ).collect(Collectors.joining(","));
        System.out.println(
                "Recieved ERROR ticket for " +
                        errorTicketMessages.size() +
                        " messages: " +
                        errorTicketMessagesString
        );


        // Countdown 30s
        int wait = 30;
        for (int i = wait; i >= 0; i--) {
            System.out.print("Waiting for " + wait + " seconds. " + i + "s\r");
            Thread.sleep(1000);
        }
        System.out.println("Fetching reciepts...");

        List<String> ticketIds = (client.getTicketIdsFromPairs(okTicketMessages));
        CompletableFuture<List<ExpoPushReceiept>> receiptFutures = client.getPushNotificationReceiptsAsync(ticketIds);

        List<ExpoPushReceiept> receipts = new ArrayList<>();
        try {
            receipts = receiptFutures.get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println(
                "Recieved " + receipts.size() + " receipts:");

        for (ExpoPushReceiept reciept : receipts) {
            System.out.println(
                    "Receipt for id: " +
                            reciept.getId() +
                            " had status: " +
                            reciept.getStatus());

        }

        System.exit(0);
    }
}
```

## Maven
```
<dependency>
  <groupId>io.github.jav</groupId>
  <artifactId>expo-server-sdk</artifactId>
  <version>0.9.0</version>
</dependency>
```
## Gradle
`implementation 'io.github.jav:expo-server-sdk:0.9.0'`

## Maven central entry
https://search.maven.org/artifact/io.github.jav/expo-server-sdk

## See Also

  * https://github.com/expo/expo-server-sdk-node
  * https://github.com/expo/expo-server-sdk-ruby
  * https://github.com/expo/expo-server-sdk-python
  * https://github.com/expo/expo-server-sdk-rust
