package org.ubillos.pushnotifications.notificationsSDK;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PushClientTest {

    long _coundAndValidateMessages(List<List<ExpoPushMessage>> chunks) {
        long totalMessageCount = 0;
        for (List<ExpoPushMessage> chunk : chunks) {
            long chunkMessagesCount = PushClient._getActualMessagesCount(chunk);
            assertTrue(chunkMessagesCount <= PushClient.PUSH_NOTIFICATION_CHUNK_LIMIT);
            totalMessageCount += chunkMessagesCount;
        }
        return totalMessageCount;
    }

    @Test
    public void chunkListsOfPushNotificationMessages() {
        PushClient client = new PushClient();
        List<ExpoPushMessage> messages = new ArrayList<>(Collections.nCopies(999, new ExpoPushMessage("?")));
        List<List<ExpoPushMessage>> chunks = client.chunkPushNotifications(messages);
        long totalMessageCount = 0;
        for (List<ExpoPushMessage> chunk : chunks) {
            totalMessageCount += chunk.size();
        }
        assertEquals(totalMessageCount, messages.size());
    }

    @Test
    public void canChunkSmallListsOfPushNotificationMessages() {
        PushClient client = new PushClient();
        List<ExpoPushMessage> messages = new ArrayList<>(Collections.nCopies(10, new ExpoPushMessage("?")));
        List<List<ExpoPushMessage>> chunks = client.chunkPushNotifications(messages);
        assertEquals(1, chunks.size());
        assertEquals(10, chunks.get(0).size());
    }

    @Test
    public void canChunkSinglePushNotificationMessageWithListsOfRecipients() {
        int messagesLength = 999;
        PushClient client = new PushClient();
        List<ExpoPushMessage> messages = new ArrayList<>();
        messages.add(new ExpoPushMessage(Collections.nCopies(messagesLength, "?")));
        List<List<ExpoPushMessage>> chunks = client.chunkPushNotifications(messages);
        chunks.stream().forEach(
                (c) -> assertEquals(1, c.size())
        );
        long totalMessageCount = _coundAndValidateMessages(chunks);
        assertEquals(totalMessageCount, messagesLength);
    }

    @Test
    public void canChunkSinglePushNotificatoinMessageWithSmallListsOfRecipients() {
        int messagesLength = 10;
        PushClient client = new PushClient();
        List<ExpoPushMessage> messages = new ArrayList<>();
        messages.add(new ExpoPushMessage(Collections.nCopies(messagesLength, "?")));
        List<List<ExpoPushMessage>> chunks = client.chunkPushNotifications(messages);
        assertEquals(1, chunks.size());
        assertEquals(1, chunks.get(0).size());
        assertEquals(messagesLength, chunks.get(0).get(0).to.size());
    }

    @Test
    public void chunksPushNotificationMessagesMixedWithListsOfRecipientsAndSingleRecipient() {
        PushClient client = new PushClient();
        List<ExpoPushMessage> messages = new ArrayList<>();
        messages.add(new ExpoPushMessage(Collections.nCopies(888, "?")));
        messages.addAll(Collections.nCopies(999, new ExpoPushMessage("?")));
        messages.add(new ExpoPushMessage(Collections.nCopies(90, "?")));
        messages.addAll(Collections.nCopies(10, new ExpoPushMessage("?")));

        List<List<ExpoPushMessage>> chunks = client.chunkPushNotifications(messages);
        long totalMessageCount = _coundAndValidateMessages(chunks);
        assertEquals(888 + 999 + 90 + 10, totalMessageCount);
    }

    @Test
    public void isExponentPushTokenCanValidateTokens() {
        String validToken1 = "ExpoPushToken[xxxxxxxxxxxxxxxxxxxxxx]";
        assertEquals(true, PushClient.isExponentPushToken(validToken1), "Test that \"ExpoPusToken[\" is a valid token");
        String validToken2 = "ExponentPushToken[xxxxxxxxxxxxxxxxxxxxxx]";
        assertEquals(true, PushClient.isExponentPushToken(validToken2), "Test that \"ExponentPushToken[\" is a valid token");
        String validToken3 = "F5741A13-BCDA-434B-A316-5DC0E6FFA94F";
        assertEquals(true, PushClient.isExponentPushToken(validToken3), "Test that \"F5741A13-BCDA-434B-A316-5DC0E6FFA94F\" is a valid token");

        String inValidToken1 = "ExponentPushToken xxxxxxxxxxxxxxxxxxxxxx";
        assertEquals(false, PushClient.isExponentPushToken(inValidToken1), "Stripped []");
        String inValidToken2 = "ExpoXXXXXushToken[xxxxxxxxxxxxxxxxxxxxxx]";
        assertEquals(false, PushClient.isExponentPushToken(inValidToken1), "Mangeled prefix");
        String inValidToken3 = "ExponentPushToken[]";
        assertEquals(false, PushClient.isExponentPushToken(inValidToken1), "Empty key field");
    }

    @Test
    public void chunkPushNotificationReceiptIdsCanChunkCorrectly() {
        PushClient client = new PushClient();
        List<String> recieptIds = new ArrayList<>(Collections.nCopies(60, "F5741A13-BCDA-434B-A316-5DC0E6FFA94F"));
        List<List<String>> chunks = client.chunkPushNotificationReceiptIds(recieptIds);
        int totalReceiptIdCount = 0;
        for (List<String> chunk : chunks) {
            totalReceiptIdCount += chunk.size();
        }
        assertEquals(recieptIds.size(), totalReceiptIdCount);
    }

    @Test
    public void chunkTwoMessagesAndOneAdditionalMessageWithNoRecipient() {
        PushClient client = new PushClient();

        List<ExpoPushMessage> messages = new ArrayList<>(Collections.nCopies(2, new ExpoPushMessage("?")));
        messages.add(new ExpoPushMessage(new ArrayList<>()));
        List<List<ExpoPushMessage>> chunks = client.chunkPushNotifications(messages);
        assertEquals(1, chunks.size());
        long totalMessageCount = _countAndValidateMessages(chunks);
        assertEquals(2, totalMessageCount);
    }

    @Test
    public void chunkOneMessageWith100Recipients() {
        PushClient client = new PushClient();
        List<ExpoPushMessage> messages = new ArrayList<>();
        messages.add(new ExpoPushMessage(Collections.nCopies(100, "?")));
        List<List<ExpoPushMessage>> chunks = client.chunkPushNotifications(messages);
        assertEquals(1, chunks.size());
        assertEquals(1, chunks.get(0).size());
        assertEquals(100, chunks.get(0).get(0).to.size());
    }

    @Test
    // TODO: This test only works because we assume that max-chunk size is 100
    public void chunkOneMessageWith101Recipients() {
        PushClient client = new PushClient();
        List<ExpoPushMessage> messages = new ArrayList<>();
        messages.add(new ExpoPushMessage(Collections.nCopies(101, "?")));
        List<List<ExpoPushMessage>> chunks = client.chunkPushNotifications(messages);
        assertEquals(2, chunks.size());
        assertEquals(1, chunks.get(0).size());
        assertEquals(1, chunks.get(1).size());
        long totalMessageCount = _coundAndValidateMessages(chunks);
        assertEquals(101, totalMessageCount);
    }

    @Test
    // TODO: This test only works because we assume that max-chunk size is 100
    public void chunkOneMessageWith99RecipientsAndTwoAdditionalMessages() {
        PushClient client = new PushClient();
        List<ExpoPushMessage> messages = new ArrayList<>();
        messages.add(new ExpoPushMessage(Collections.nCopies(99, "?")));
        messages.add(new ExpoPushMessage("?"));
        messages.add(new ExpoPushMessage("?"));

        List<List<ExpoPushMessage>> chunks = client.chunkPushNotifications(messages);
        assertEquals(2, chunks.size());
        assertEquals(2, chunks.get(0).size());
        assertEquals(1, chunks.get(1).size());
        long totalMessageCount = _coundAndValidateMessages(chunks);
        assertEquals(99 + 2, totalMessageCount);
    }

    @Test
    // TODO: This test only works because we assume that max-chunk size is 100
    public void chunkOneMessageWith100RecipientsAndTwoAdditionalMessages() {
        PushClient client = new PushClient();
        List<ExpoPushMessage> messages = new ArrayList<>();
        messages.add(new ExpoPushMessage(Collections.nCopies(100, "?")));
        messages.add(new ExpoPushMessage("?"));
        messages.add(new ExpoPushMessage("?"));

        List<List<ExpoPushMessage>> chunks = client.chunkPushNotifications(messages);
        assertEquals(2, chunks.size());
        assertEquals(1, chunks.get(0).size());
        assertEquals(2, chunks.get(1).size());
        long totalMessageCount = _coundAndValidateMessages(chunks);
        assertEquals(100 + 2, totalMessageCount);
    }


    @Test
    // TODO: This test only works because we assume that max-chunk size is 100
    public void chunk99MessagesAndOneAdditionalMessageWithTwoRecipients() {
        PushClient client = new PushClient();
        List<ExpoPushMessage> messages = new ArrayList<>((Collections.nCopies(99, new ExpoPushMessage("?"))));
        messages.add(new ExpoPushMessage(Collections.nCopies(2, "?")));

        List<List<ExpoPushMessage>> chunks = client.chunkPushNotifications(messages);
        assertEquals(2, chunks.size());
        assertEquals(100, chunks.get(0).size());
        assertEquals(1, chunks.get(1).size());
        long totalMessageCount = _coundAndValidateMessages(chunks);
        assertEquals(99 + 2, totalMessageCount);
    }

    @Test
    public void chunkNoMessage() {
        PushClient client = new PushClient();
        List<ExpoPushMessage> messages = new ArrayList<>();
        List<List<ExpoPushMessage>> chunks = client.chunkPushNotifications(messages);
        assertEquals(0, chunks.size());
    }

    @Test
    public void chunkSingleMessageWithNoRecipient() {
        PushClient client = new PushClient();
        List<ExpoPushMessage> messages = new ArrayList();
        messages.add(new ExpoPushMessage());
        List<List<ExpoPushMessage>> chunks = client.chunkPushNotifications(messages);
        assertEquals(0, chunks.size());
    }

    private long _countAndValidateMessages(List<List<ExpoPushMessage>> chunks) {
        long totalMessageCount = 0;
        for (List<ExpoPushMessage> chunk : chunks) {
            long chunkMessagesCount = PushClient._getActualMessagesCount(chunk);
            assert (chunkMessagesCount <= PushClient.PUSH_NOTIFICATION_CHUNK_LIMIT);
            totalMessageCount += chunkMessagesCount;
        }
        return totalMessageCount;

    }
}