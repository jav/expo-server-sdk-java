package io.github.jav.exposerversdk;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.jav.exposerversdk.enums.Status;
import io.github.jav.exposerversdk.enums.TicketError;
import io.github.jav.exposerversdk.helpers.DefaultPushServerResolver;
import io.github.jav.exposerversdk.helpers.PushServerResolver;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.stream.Collectors;

public class PushClientCustomData<TPushMessage extends ExpoPushMessageCustomData<?>> {
    public long PUSH_NOTIFICATION_CHUNK_LIMIT = 100;
    public long PUSH_NOTIFICATION_RECEIPT_CHUNK_LIMIT = 300;
    public URL baseApiUrl = null;
    public PushServerResolver pushServerResolver = new DefaultPushServerResolver();

    public PushClientCustomData() throws PushClientException {
        try {
            baseApiUrl = new URL("https://exp.host/--/api/v2");
        } catch (MalformedURLException e) {
            throw new PushClientException(e);
        }
    }

    public URL getBaseApiUrl() {
        return baseApiUrl;
    }

    public PushClientCustomData setBaseApiUrl(URL _baseApiUrl) {
        baseApiUrl = _baseApiUrl;
        return this;
    }

    public CompletableFuture<List<ExpoPushTicket>> sendPushNotificationsAsync(List<TPushMessage> messages) throws PushNotificationException {
        CompletableFuture<List<ExpoPushTicket>> ret = null;
        try {
            ret = _postNotificationAsync(new URL(baseApiUrl + "/push/send"), messages)
                    .thenApply((String jsonString) -> {
                        try {
                            ObjectMapper mapper = new ObjectMapper();
                            JsonNode dataNode = mapper.readTree(jsonString).get("data");
                            List<ExpoPushTicket> retList = new ArrayList<>();
                            for (JsonNode node : dataNode) {
                                retList.add(mapper.convertValue(node, ExpoPushTicket.class));
                            }
                            return retList;
                        } catch (IOException e) {
                            throw new PushNotificationException(e, messages);
                        }
                    });
        } catch (Exception e) {
            throw new PushNotificationException(e, messages);
        }
        return ret;
    }

    public CompletableFuture<List<ExpoPushReceipt>> getPushNotificationReceiptsAsync(List<String> _ids) throws PushNotificationReceiptsException {
        CompletableFuture<List<ExpoPushReceipt>> ret = null;
        try {
            ret = _postReceiptsAsync(new URL(baseApiUrl + "/push/getReceipts"), _ids)
                    .thenApply((String jsonString) -> {
                        try {
                            ObjectMapper mapper = new ObjectMapper();
                            JsonNode dataNode = mapper.readTree(jsonString).get("data");
                            List<ExpoPushReceipt> retList = new ArrayList<>();
                            Iterator<Map.Entry<String, JsonNode>> it = dataNode.fields();
                            while (it.hasNext()) {
                                Map.Entry<String, JsonNode> field = it.next();
                                String key = field.getKey();
                                JsonNode expoPushRecieptJsonNode = field.getValue();
                                ExpoPushReceipt epr = mapper.treeToValue(expoPushRecieptJsonNode, ExpoPushReceipt.class);

                                epr.setId(key);
                                retList.add(epr);
                            }
                            return retList;
                        } catch (Exception e) {
                            throw new PushNotificationReceiptsException(e, _ids);
                        }
                    });
        } catch (Exception e) {
            throw new PushNotificationReceiptsException(e, _ids);
        }
        return ret;
    }

    protected CompletableFuture<String> _postNotificationAsync(URL url, List<? extends TPushMessage> messages) throws CompletionException {
        ObjectMapper objectMapper = new ObjectMapper();
        String json = null;

        try {
            json = objectMapper.
                    writeValueAsString(messages);
        } catch (JsonProcessingException e) {
            throw new PushNotificationException(e, messages);
        }
        return pushServerResolver.postAsync(url, json);
    }

    public List<ExpoPushMessageTicketPair<TPushMessage>> zipMessagesTickets(
            List<TPushMessage> messages,
            List<ExpoPushTicket> tickets
    ) {
        List<ExpoPushMessageTicketPair<TPushMessage>> ret = new ArrayList<>();

        for (int i = 0; i < messages.size(); i++) {
            ret.add(new ExpoPushMessageTicketPair<>(messages.get(i), tickets.get(i)));
        }

        return ret;
    }


    public List<ExpoPushMessageTicketPair<TPushMessage>> filterAllSuccessfulMessages(
            List<ExpoPushMessageTicketPair<TPushMessage>> zippedMessagesTickets) {
        return zippedMessagesTickets.stream().filter(p -> p.ticket.getStatus() == Status.OK).collect(Collectors.toList());
    }

    public List<ExpoPushMessageTicketPair<TPushMessage>> filterAllMessagesWithError(
            List<ExpoPushMessageTicketPair<TPushMessage>> zippedMessagesTickets) {
        return filterAllMessagesWithError(zippedMessagesTickets, null);
    }


    public List<ExpoPushMessageTicketPair<TPushMessage>> filterAllMessagesWithError(
            List<ExpoPushMessageTicketPair<TPushMessage>> zippedMessagesTickets, TicketError ticketError) {

        return zippedMessagesTickets.stream().filter(
                p -> p.ticket.getStatus() == Status.ERROR && (ticketError == null || p.ticket.getDetails().getError() == ticketError)
        ).collect(Collectors.toList());
    }

    public List<String> getTicketIdsFromPairs(List<ExpoPushMessageTicketPair<TPushMessage>> okTicketMessagePairs) {
        return getTicketIds(okTicketMessagePairs.stream().map(p->p.ticket).collect(Collectors.toList()));
    }

    public List<String> getTicketIds(List<ExpoPushTicket> okTicketMessages) {
        return okTicketMessages.stream().map(t->t.getId()).collect(Collectors.toList());
    }


    private class JsonReceiptHelper<T> {
        public List<T> ids;

        public JsonReceiptHelper(List<T> _ids) {
            ids = _ids;
        }
    }

    private <T> CompletableFuture<String> _postReceiptsAsync(URL url, List<T> receipts) throws CompletionException {
        JsonReceiptHelper<T> jsonReceiptHelper = new PushClientCustomData.JsonReceiptHelper(receipts);

        ObjectMapper objectMapper = new ObjectMapper();
        String json = null;

        try {
            json = objectMapper.
                    writeValueAsString(jsonReceiptHelper);
        } catch (JsonProcessingException e) {
            throw new CompletionException(e);
        }

        return pushServerResolver.postAsync(url, json);
    }

    static public boolean isExponentPushToken(String token) {
        String prefixA = "ExponentPushToken[";
        String prefixB = "ExpoPushToken[";
        String postfix = "]";
        String regex = "[a-zA-Z0-9]{8}-[a-zA-Z0-9]{4}-[a-zA-Z0-9]{4}-[a-zA-Z0-9]{4}-[a-zA-Z0-9]{12}";

        if (token.matches(regex)) return true;
        if (!token.endsWith(postfix)) return false;
        if (token.startsWith(prefixA)) return true;
        if (token.startsWith(prefixB)) return true;
        return false;
    }

    public long _getActualMessagesCount(List<TPushMessage> messages) {
        return messages.stream().reduce(0, (acc, cur) -> acc + cur.getTo().size(), Integer::sum);
    }

    public List<List<String>> chunkPushNotificationReceiptIds(List<String> recieptIds) {
        return _chunkItems(recieptIds, PUSH_NOTIFICATION_RECEIPT_CHUNK_LIMIT);
    }

    public <T> List<List<T>> _chunkItems(List<T> items, long chunkSize) {
        List<List<T>> chunks = new ArrayList<>();
        List<T> chunk = new ArrayList<>();
        for (T item : items) {
            chunk.add(item);
            if (chunk.size() >= chunkSize) {
                chunks.add(chunk);
                chunk = new ArrayList<>();
            }
        }

        if (chunk.size() > 0) {
            chunks.add(chunk);
        }
        return chunks;
    }

    public List<List<TPushMessage>> chunkPushNotifications(List<TPushMessage> messages) {
        List<List<TPushMessage>> chunks = new ArrayList<>();
        List<TPushMessage> chunk = new ArrayList<>();

        long chunkMessagesCount = 0;
        for (TPushMessage message : messages) {
            List<String> partialTo = new ArrayList<>();
            for (String recipient : message.getTo()) {
                if (recipient.length() <= 0) continue;
                partialTo.add(recipient);
                chunkMessagesCount++;
                if (chunkMessagesCount >= PUSH_NOTIFICATION_CHUNK_LIMIT) {
                    // Cap this chunk here if it already exceeds PUSH_NOTIFICATION_CHUNK_LIMIT.
                    // Then create a new chunk to continue on the remaining recipients for this message.
                    // Because we're using generics, we can't use the constructor. Instead, clone() the message
                    TPushMessage tmpCopy = (TPushMessage) message.clone();
                    tmpCopy.setTo(partialTo);
                    chunk.add(tmpCopy);
                    chunks.add(chunk);
                    chunk = new ArrayList<>();
                    chunkMessagesCount = 0;
                    partialTo = new ArrayList<>();
                }
            }

            if (partialTo.size() > 0) {
                TPushMessage tmpCopy = (TPushMessage) message.clone();
                tmpCopy.setTo(partialTo);
                chunk.add(tmpCopy);
            }

            if (chunkMessagesCount >= PUSH_NOTIFICATION_CHUNK_LIMIT) {
                // Cap this chunk if it exceeds PUSH_NOTIFICATION_CHUNK_LIMIT.
                // Then create a new chunk to continue on the remaining messages.
                chunks.add(chunk);
                chunk = new ArrayList<>();
                chunkMessagesCount = 0;
            }
        }

        if (chunkMessagesCount > 0) {
            // Add the remaining chunk to the chunks.
            chunks.add(chunk);
        }

        return chunks;
    }
}
