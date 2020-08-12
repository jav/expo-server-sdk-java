![Java CI with Maven](https://github.com/jav/expo-server-sdk-java/workflows/Java%20CI%20with%20Maven/badge.svg)

## expo-server-sdk-java
This is a java implementation of the [node server-side library](https://github.com/expo/expo-server-sdk-node) for working with expo using Java.
For other implementations, see [the expo docs](https://docs.expo.io/versions/latest/guides/push-notifications/#2-call-expos-push-api-with-the).


## Usage
Add package to your source tree (the package is not yet uploaded to e.g. Maven Central).

Then
```java
import org.ubillos.pushnotifications.notificationsSDK.ExpoPushMessage;
import org.ubillos.pushnotifications.notificationsSDK.ExpoPushTicket;
import org.ubillos.pushnotifications.notificationsSDK.PushClient;


public class Main {
    public static void main(String[] args) {
	String recipient = "ExponentToken[XYZ]";

        if (!PushClient.isExponentPushToken(recipient))
                throw new Error("Token:" + recipient + " is not a valid token.");

        PushClient client = new PushClient();
        List<ExpoPushMessage> messages = new ArrayList<>();
        ExpoPushMessage epm = new ExpoPushMessage(notifyRequest.to);
        if (notifyRequest.title != null)
            epm.title = notifyRequest.title;
        if (notifyRequest.subtitle != null)
            epm.subtitle = notifyRequest.subtitle;
        if (notifyRequest.body != null)
            epm.body = notifyRequest.body;
        messages.add(epm);

        List<List<ExpoPushMessage>> chunks = client.chunkPushNotifications(messages);

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
    }
}
```

## Maven
```
<dependency>
  <groupId>io.github.jav</groupId>
  <artifactId>expo-server-sdk</artifactId>
  <version>0.7.0</version>
</dependency>
```
## Gradle
`implementation 'io.github.jav:expo-server-sdk:0.7.0'`

## Maven central entry
https://search.maven.org/artifact/io.github.jav/expo-server-sdk

## See Also

  * https://github.com/expo/expo-server-sdk-node
  * https://github.com/expo/expo-server-sdk-ruby
  * https://github.com/expo/expo-server-sdk-python
  * https://github.com/expo/expo-server-sdk-rust
