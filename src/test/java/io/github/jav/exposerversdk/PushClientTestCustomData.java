package io.github.jav.exposerversdk;

import io.github.jav.exposerversdk.enums.ReceiptError;
import io.github.jav.exposerversdk.enums.Status;
import io.github.jav.exposerversdk.enums.TicketError;
import io.github.jav.exposerversdk.helpers.PushServerResolver;
import org.junit.jupiter.api.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

class PushClientTestCustomData {

    @Test
    public void apiBaseUrlIsOverridable() throws MalformedURLException, PushClientException {
        PushClientCustomData<ExpoPushMessageCustomData<Integer>> client = new PushClientCustomData<>();
        URL apiUrl = client.getBaseApiUrl();
        assertEquals(apiUrl, client.getBaseApiUrl());
        URL mockBaseApiUrl = new URL("http://example.com/");
        client.setBaseApiUrl(mockBaseApiUrl);
        assertEquals(mockBaseApiUrl, client.getBaseApiUrl());
    }

    @Test
    public void chunkListsOfPushNotificationMessages() throws PushClientException {
        PushClientCustomData<ExpoPushMessageCustomData<Integer>> client = new PushClientCustomData<>();
        List<ExpoPushMessageCustomData<Integer>> messages = new ArrayList<>(Collections.nCopies(999, new ExpoPushMessageCustomData<>("?")));
        List<List<ExpoPushMessageCustomData<Integer>>> chunks = client.chunkPushNotifications(messages);
        long totalMessageCount = 0;
        for (List<ExpoPushMessageCustomData<Integer>> chunk : chunks) {
            totalMessageCount += chunk.size();
        }
        assertEquals(totalMessageCount, messages.size());
    }

    @Test
    public void canChunkSmallListsOfPushNotificationMessages() throws PushClientException {
        PushClientCustomData<ExpoPushMessageCustomData<Integer>> client = new PushClientCustomData<>();
        List<ExpoPushMessageCustomData<Integer>> messages = new ArrayList<>(Collections.nCopies(10, new ExpoPushMessageCustomData<Integer>("?")));
        List<List<ExpoPushMessageCustomData<Integer>>> chunks = client.chunkPushNotifications(messages);
        assertEquals(1, chunks.size());
        assertEquals(10, chunks.get(0).size());
    }

    @Test
    public void canChunkSinglePushNotificationMessageWithListsOfRecipients() throws PushClientException {
        int messagesLength = 999;
        PushClientCustomData<ExpoPushMessageCustomData<Integer>> client = new PushClientCustomData<>();
        List<ExpoPushMessageCustomData<Integer>> messages = new ArrayList<>();
        messages.add(new ExpoPushMessageCustomData<>(Collections.nCopies(messagesLength, "?")));
        List<List<ExpoPushMessageCustomData<Integer>>> chunks = client.chunkPushNotifications(messages);
        chunks.stream().forEach(
                (c) -> assertEquals(1, c.size())
        );
        long totalMessageCount = _countAndValidateMessages(chunks);
        assertEquals(totalMessageCount, messagesLength);
    }

    @Test
    public void canChunkSinglePushNotificatoinMessageWithSmallListsOfRecipients() throws PushClientException {
        int messagesLength = 10;
        PushClientCustomData<ExpoPushMessageCustomData<Integer>> client = new PushClientCustomData<>();
        List<ExpoPushMessageCustomData<Integer>> messages = new ArrayList<>();
        messages.add(new ExpoPushMessageCustomData<>(Collections.nCopies(messagesLength, "?")));
        List<List<ExpoPushMessageCustomData<Integer>>> chunks = client.chunkPushNotifications(messages);
        assertEquals(1, chunks.size());
        assertEquals(1, chunks.get(0).size());
        assertEquals(messagesLength, chunks.get(0).get(0).getTo().size());
    }

    @Test
    public void chunksPushNotificationMessagesMixedWithListsOfRecipientsAndSingleRecipient() throws PushClientException {
        PushClientCustomData<ExpoPushMessageCustomData<Integer>> client = new PushClientCustomData<>();
        List<ExpoPushMessageCustomData<Integer>> messages = new ArrayList<>();
        messages.add(new ExpoPushMessageCustomData<>(Collections.nCopies(888, "?")));
        messages.addAll(Collections.nCopies(999, new ExpoPushMessageCustomData<>("?")));
        messages.add(new ExpoPushMessageCustomData<>(Collections.nCopies(90, "?")));
        messages.addAll(Collections.nCopies(10, new ExpoPushMessageCustomData<>("?")));

        List<List<ExpoPushMessageCustomData<Integer>>> chunks = client.chunkPushNotifications(messages);
        long totalMessageCount = _countAndValidateMessages(chunks);
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
    public void chunkPushNotificationReceiptIdsCanChunkCorrectly() throws PushClientException {
        PushClientCustomData<ExpoPushMessageCustomData<Integer>> client = new PushClientCustomData<>();
        List<String> recieptIds = new ArrayList<>(Collections.nCopies(60, "F5741A13-BCDA-434B-A316-5DC0E6FFA94F"));
        List<List<String>> chunks = client.chunkPushNotificationReceiptIds(recieptIds);
        int totalReceiptIdCount = 0;
        for (List<String> chunk : chunks) {
            totalReceiptIdCount += chunk.size();
        }
        assertEquals(recieptIds.size(), totalReceiptIdCount);
    }

    @Test
    public void chunkTwoMessagesAndOneAdditionalMessageWithNoRecipient() throws PushClientException {
        PushClientCustomData<ExpoPushMessageCustomData<Integer>> client = new PushClientCustomData<>();

        List<ExpoPushMessageCustomData<Integer>> messages = new ArrayList<>(Collections.nCopies(2, new ExpoPushMessageCustomData<>("?")));
        messages.add(new ExpoPushMessageCustomData<>(new ArrayList<>()));
        List<List<ExpoPushMessageCustomData<Integer>>> chunks = client.chunkPushNotifications(messages);
        assertEquals(1, chunks.size());
        long totalMessageCount = _countAndValidateMessages(chunks);
        assertEquals(2, totalMessageCount);
    }

    @Test
    public void chunkOneMessageWith100Recipients() throws PushClientException {
        PushClientCustomData<ExpoPushMessageCustomData<Integer>> client = new PushClientCustomData<>();
        List<ExpoPushMessageCustomData<Integer>> messages = new ArrayList<>();
        messages.add(new ExpoPushMessageCustomData<Integer>(Collections.nCopies(100, "?")));
        List<List<ExpoPushMessageCustomData<Integer>>> chunks = client.chunkPushNotifications(messages);
        assertEquals(1, chunks.size());
        assertEquals(1, chunks.get(0).size());
        assertEquals(100, chunks.get(0).get(0).getTo().size());
    }

    @Test
    // TODO: This test only works because we assume that max-chunk size is 100
    public void chunkOneMessageWith101Recipients() throws PushClientException {
        PushClientCustomData<ExpoPushMessageCustomData<Integer>> client = new PushClientCustomData<>();
        List<ExpoPushMessageCustomData<Integer>> messages = new ArrayList<>();
        messages.add(new ExpoPushMessageCustomData<>(Collections.nCopies(101, "?")));
        List<List<ExpoPushMessageCustomData<Integer>>> chunks = client.chunkPushNotifications(messages);
        assertEquals(2, chunks.size());
        assertEquals(1, chunks.get(0).size());
        assertEquals(1, chunks.get(1).size());
        long totalMessageCount = _countAndValidateMessages(chunks);
        assertEquals(101, totalMessageCount);
    }

    @Test
    // TODO: This test only works because we assume that max-chunk size is 100
    public void chunkOneMessageWith99RecipientsAndTwoAdditionalMessages() throws PushClientException {
        PushClientCustomData<ExpoPushMessageCustomData<Integer>> client = new PushClientCustomData<>();
        List<ExpoPushMessageCustomData<Integer>> messages = new ArrayList<>();
        messages.add(new ExpoPushMessageCustomData<>(Collections.nCopies(99, "?")));
        messages.add(new ExpoPushMessageCustomData<>("?"));
        messages.add(new ExpoPushMessageCustomData<>("?"));

        List<List<ExpoPushMessageCustomData<Integer>>> chunks = client.chunkPushNotifications(messages);
        assertEquals(2, chunks.size());
        assertEquals(2, chunks.get(0).size());
        assertEquals(1, chunks.get(1).size());
        long totalMessageCount = _countAndValidateMessages(chunks);
        assertEquals(99 + 2, totalMessageCount);
    }

    @Test
    // TODO: This test only works because we assume that max-chunk size is 100
    public void chunkOneMessageWith100RecipientsAndTwoAdditionalMessages() throws PushClientException {
        PushClientCustomData<ExpoPushMessageCustomData<Integer>> client = new PushClientCustomData<>();
        List<ExpoPushMessageCustomData<Integer>> messages = new ArrayList<>();
        messages.add(new ExpoPushMessageCustomData<>(Collections.nCopies(100, "?")));
        messages.add(new ExpoPushMessageCustomData<>("?"));
        messages.add(new ExpoPushMessageCustomData<>("?"));

        List<List<ExpoPushMessageCustomData<Integer>>> chunks = client.chunkPushNotifications(messages);
        assertEquals(2, chunks.size());
        assertEquals(1, chunks.get(0).size());
        assertEquals(2, chunks.get(1).size());
        long totalMessageCount = _countAndValidateMessages(chunks);
        assertEquals(100 + 2, totalMessageCount);
    }


    @Test
    // TODO: This test only works because we assume that max-chunk size is 100
    public void chunk99MessagesAndOneAdditionalMessageWithTwoRecipients() throws PushClientException {
        PushClientCustomData<ExpoPushMessageCustomData<Integer>> client = new PushClientCustomData<>();
        List<ExpoPushMessageCustomData<Integer>> messages = new ArrayList<>((Collections.nCopies(99, new ExpoPushMessageCustomData<>("?"))));
        messages.add(new ExpoPushMessageCustomData<>(Collections.nCopies(2, "?")));

        List<List<ExpoPushMessageCustomData<Integer>>> chunks = client.chunkPushNotifications(messages);
        assertEquals(2, chunks.size());
        assertEquals(100, chunks.get(0).size());
        assertEquals(1, chunks.get(1).size());
        long totalMessageCount = _countAndValidateMessages(chunks);
        assertEquals(99 + 2, totalMessageCount);
    }

    @Test
    public void chunkNoMessage() throws PushClientException {
        PushClientCustomData<ExpoPushMessageCustomData<Integer>> client = new PushClientCustomData<>();
        List<ExpoPushMessageCustomData<Integer>> messages = new ArrayList<>();
        List<List<ExpoPushMessageCustomData<Integer>>> chunks = client.chunkPushNotifications(messages);
        assertEquals(0, chunks.size());
    }

    @Test
    public void chunkSingleMessageWithNoRecipient() throws PushClientException {
        PushClientCustomData<ExpoPushMessageCustomData<Integer>> client = new PushClientCustomData<>();
        List<ExpoPushMessageCustomData<Integer>> messages = new ArrayList();
        messages.add(new ExpoPushMessageCustomData<>());
        List<List<ExpoPushMessageCustomData<Integer>>> chunks = client.chunkPushNotifications(messages);
        assertEquals(0, chunks.size());
    }

    private <T extends ExpoPushMessageCustomData<?>> long _countAndValidateMessages(List<List<T>> chunks) throws PushClientException {
        long totalMessageCount = 0;
        for (List<T> chunk : chunks) {
            PushClientCustomData<T> client = new PushClientCustomData<T>();
            long chunkMessagesCount = client._getActualMessagesCount(chunk);
            assert (chunkMessagesCount <= client.PUSH_NOTIFICATION_CHUNK_LIMIT);
            totalMessageCount += chunkMessagesCount;
        }
        return totalMessageCount;

    }

    @Test
    public void getOneReceipt() throws InterruptedException, ExecutionException, PushClientException {
        final String SOURCE_JSON = "{" +
                "    \"data\": " +
                "    {" +
                "        \"2011eb6d-d4d3-440c-a93c-37ac4b51ea09\": { " +
                "            \"status\":\"error\"," +
                "            \"message\":\"The Apple Push Notification service ...  this error means.\"," +
                "            \"details\":{" +
                "                \"apns\":{" +
                "                    \"reason\":\"PayloadTooLarge\", " +
                "                    \"statusCode\":413" +
                "                }," +
                "                \"error\":\"MessageTooBig\"," +
                "                \"sentAt\":1586353449" +
                "            }," +
                "            \"__debug\": {}" +
                "        }" +
                "    }" +
                "}";


        CompletableFuture<String> mockResponseFuture = new CompletableFuture<>().completedFuture(SOURCE_JSON);

        PushServerResolver pushServerResolverMock = mock(PushServerResolver.class);
        when(pushServerResolverMock.postAsync(any(), any())).thenReturn(mockResponseFuture);

        PushClient client = new PushClient();
        client.pushServerResolver = pushServerResolverMock;

        List<CompletableFuture<List<ExpoPushReceipt>>> messageRepliesFutures = new ArrayList<>();
        List<List<String>> receiptIdChunks = client.chunkPushNotificationReceiptIds(Arrays.asList("2011eb6d-d4d3-440c-a93c-37ac4b51ea09"));
        for (List<String> chunk : receiptIdChunks) {
            messageRepliesFutures.add(client.getPushNotificationReceiptsAsync(chunk));
        }

        ExpoPushReceipt receipt = messageRepliesFutures.get(0).get().get(0);
        assertEquals("2011eb6d-d4d3-440c-a93c-37ac4b51ea09", receipt.getId());
        assertEquals(Status.ERROR, receipt.getStatus());
        assertTrue(receipt.getMessage().startsWith("The Apple Push"));
        assertTrue(receipt.getMessage().endsWith("this error means."));
        assertEquals(ReceiptError.MESSAGETOOBIG, receipt.getDetails().getError());
    }


    @Test
    public void sendOnePushNotificationsAsyncThrowsExceptionWithTheFailedMessage() throws PushClientException {
        PushServerResolver pushServerResolverMock = mock(PushServerResolver.class);
        when(pushServerResolverMock.postAsync(any(), any())).thenThrow(new CompletionException(new Exception("Exception!")));

        PushClientCustomData<ExpoPushMessageCustomData<Integer>> client = new PushClientCustomData<>();
        client.pushServerResolver = pushServerResolverMock;

        ExpoPushMessageCustomData<Integer> myMessage = new ExpoPushMessageCustomData<>();
        myMessage.setTo(Arrays.asList("Recipient 1"));
        Map<String, Integer> data = new HashMap<>();
        data.put("firstData", 111);
        data.put("secondData", 222);
        myMessage.setData(data);

        List<ExpoPushMessageCustomData<Integer>> messages = new ArrayList<>(Arrays.asList(myMessage));
        List<List<ExpoPushMessageCustomData<Integer>>> messageChunks = client.chunkPushNotifications(messages);

        PushNotificationException pushNotificationException = null;

        try {
            for (List<ExpoPushMessageCustomData<Integer>> chunk : messageChunks) {
                client.sendPushNotificationsAsync(chunk);
            }
        } catch (PushNotificationException e) {
            pushNotificationException = e;
        }

        assertNotNull(pushNotificationException);
        assertEquals(pushNotificationException.messages.size(), 1);
        assertEquals(pushNotificationException.messages.get(0).getTo().get(0), "Recipient 1");
        assertEquals(pushNotificationException.messages.get(0).getData().get("firstData"), 111);
        assertEquals(pushNotificationException.messages.get(0).getData().get("secondData"), 222);
    }

    @Test
    public void sendManyPushNotificationsAsyncThrowsExceptionWithOnlyTheFailedMessages() throws PushClientException {
        final String TICKETS_JSON = "{" +
                "    \"data\": " +
                "     [" +
                "         {" +
                "           \"status\": \"ok\"," +
                "           \"id\":\"2011eb6d-d4d3-440c-a93c-37ac4b51ea09\" " +
                "         }" +
                "     ]" +
                "}";

        PushServerResolver pushServerResolverMock = mock(PushServerResolver.class);

        when(pushServerResolverMock.postAsync(any(), any())).thenAnswer(new Answer() {
            private int count = 0;

            public Object answer(InvocationOnMock invocation) {

                if (count++ % 2 == 0) {
                    CompletableFuture<String> mockResponseFuture = new CompletableFuture<>().completedFuture(TICKETS_JSON);
                    return mockResponseFuture;
                } else {
                    throw new CompletionException(new Exception("Exception!"));
                }
            }
        });

        PushClientCustomData<ExpoPushMessageCustomData<Integer>> client = new PushClientCustomData<>();
        client.PUSH_NOTIFICATION_CHUNK_LIMIT = 1; // Ensure there's only one message per chunk.
        client.pushServerResolver = pushServerResolverMock;

        List<ExpoPushMessageCustomData<Integer>> myMessages = new ArrayList<>();
        ExpoPushMessageCustomData<Integer> myMessage;
        Map<String, Integer> data;

        myMessage = new ExpoPushMessageCustomData<>();
        myMessage.setTo(Arrays.asList("Recipient 1"));
        data = new HashMap<>();
        data.put("firstData", 11);
        data.put("secondData", 12);
        myMessage.setData(data);
        myMessages.add(myMessage);

        myMessage = new ExpoPushMessageCustomData<>();
        myMessage.setTo(Arrays.asList("Recipient 2"));
        data = new HashMap<>();
        data.put("firstData", 21);
        data.put("secondData", 22);
        myMessage.setData(data);
        myMessages.add(myMessage);

        myMessage = new ExpoPushMessageCustomData<>();
        myMessage.setTo(Arrays.asList("Recipient 3"));
        data = new HashMap<>();
        data.put("firstData", 13);
        data.put("secondData", 23);
        myMessage.setData(data);
        myMessages.add(myMessage);

        List<List<ExpoPushMessageCustomData<Integer>>> messageChunks = client.chunkPushNotifications(myMessages);

        PushNotificationException pushNotificationException = null;

        try {
            for (List<ExpoPushMessageCustomData<Integer>> chunk : messageChunks) {
                client.sendPushNotificationsAsync(chunk);
            }
        } catch (PushNotificationException e) {
            pushNotificationException = e;
        }

        assertNotNull(pushNotificationException);
        assertEquals(1, pushNotificationException.messages.size());
        assertEquals("Recipient 2", pushNotificationException.messages.get(0).getTo().get(0));
        assertEquals(21, pushNotificationException.messages.get(0).getData().get("firstData"));
        assertEquals(22, pushNotificationException.messages.get(0).getData().get("secondData"));
    }

    @Test
    public void sendPushNotificationsReceiptAsyncThrowsExceptionWithAllFailedMessages() throws PushClientException {
        PushServerResolver pushServerResolverMock = mock(PushServerResolver.class);
        when(pushServerResolverMock.postAsync(any(), any())).thenThrow(new CompletionException(new Exception("Exception!")));

        PushClientCustomData<ExpoPushMessageCustomData<Integer>> client = new PushClientCustomData<>();
        client.pushServerResolver = pushServerResolverMock;

        List<List<String>> receiptIdChunks = client.chunkPushNotificationReceiptIds(Arrays.asList("2011eb6d-d4d3-440c-a93c-37ac4b51ea09"));

        PushNotificationReceiptsException pushNotificationReceiptsException = null;

        try {
            for (List<String> chunk : receiptIdChunks) {
                client.getPushNotificationReceiptsAsync(chunk);
            }
        } catch (PushNotificationReceiptsException e) {
            pushNotificationReceiptsException = e;
        }

        assertNotNull(pushNotificationReceiptsException);
        assertEquals(pushNotificationReceiptsException.ids.size(), 1);
        assertEquals(pushNotificationReceiptsException.ids.get(0), "2011eb6d-d4d3-440c-a93c-37ac4b51ea09");
    }

    @Test
    public void filterTicketsWithStatus() throws PushClientException {
        List<ExpoPushMessageCustomData<Integer>> messages = new ArrayList<>();
        messages.add(new ExpoPushMessageCustomData<>("Recipient 1"));
        messages.add(new ExpoPushMessageCustomData<>("Recipient 2"));
        messages.add(new ExpoPushMessageCustomData<>("Recipient 3"));

        ExpoPushTicket ept;
        List<ExpoPushTicket> tickets = new ArrayList<>();
        ept = new ExpoPushTicket();
        ept.setStatus(Status.OK);
        ept.setId("1");
        tickets.add(ept);
        ept = new ExpoPushTicket();
        ept.setStatus(Status.ERROR);
        ExpoPushTicket.Details details = new ExpoPushTicket.Details();
        details.setError(TicketError.DEVICENOTREGISTERED);
        ept.setDetails(details);
        ept.setId("2");
        tickets.add(ept);
        ept = new ExpoPushTicket();
        ept.setStatus(Status.OK);
        ept.setId("3");
        tickets.add(ept);

        PushClientCustomData<ExpoPushMessageCustomData<Integer>> client = new PushClientCustomData<>();

        List<ExpoPushMessageTicketPair< ExpoPushMessageCustomData<Integer>>> zipMessage = client.zipMessagesTickets(messages, tickets);
        List<ExpoPushMessageTicketPair<ExpoPushMessageCustomData<Integer>>> failedMessages = client.filterAllMessagesWithError(zipMessage, TicketError.DEVICENOTREGISTERED);
        assertEquals(1, failedMessages.size());
        assertEquals("Recipient 2", failedMessages.get(0).message.getTo().get(0));

    }
}
