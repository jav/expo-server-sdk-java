package io.github.jav.exposerversdk;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class PushClient<Z extends ExpoPushMessage<?>> {
    static public final long PUSH_NOTIFICATION_CHUNK_LIMIT = 100;
    static public final long PUSH_NOTIFICATION_RECEIPT_CHUNK_LIMIT = 300;
    public URL baseApiUrl = null;
    public PushServerResolver pushServerResolver = new DefaultPushServerResolver();

    static public final Set<String> INVALID_TOKEN_ERRORS= new HashSet<>(Arrays.asList("DeviceNotRegistered"));

    public PushClient() {
        try {
            baseApiUrl = new URL("https://exp.host/--/api/v2");
        } catch (MalformedURLException e) {
            //Will never fail
        }
    }
    
    public PushClient<Z> setBaseApiUrl(URL _baseApiUrl) {
        baseApiUrl = _baseApiUrl;
        return this;
    }
    
    public URL getBaseApiUrl() {
        return baseApiUrl;
    }

    public CompletableFuture<ExpoPushResponse> sendPushNotificationsAsync(List<Z> messages) {
        try {
            
            
            return _postNotificationAsync(new URL(baseApiUrl + "/push/send"), messages)
                    .thenApply((String jsonString) -> {
                        try {
                            ObjectMapper mapper = new ObjectMapper();
                            ExpoPushResponse response = mapper.readValue(jsonString,  ExpoPushResponse.class);
                            List<ExpoPushTicket> data = response.getData();
                            int currentMessageIndex = -1;
                            Z currentMessage = null;
                            int currentRecepientIndex = -1;
                            int currentRecepientSize = -1;
                            Status overallStatus = Status.OK;
                            // we need to find the invalid recepients
                            List<String> invalidTokens = new ArrayList<>();
                            for (int i=0; i< data.size(); i++) {
                                if (++currentRecepientIndex > currentRecepientSize) {
                                    currentMessageIndex++;
                                    currentMessage = messages.get(currentMessageIndex);
                                    currentRecepientIndex = 0;
                                    currentRecepientSize = currentMessage.getTo().size();
                                }
                                ExpoPushTicket ticket = data.get(i);
                                ticket.setTo(currentMessage.getTo().get(currentRecepientIndex));
                                if (ticket.getStatus() != Status.OK) {
                                    if (overallStatus == Status.OK) {
                                        overallStatus = ticket.getStatus();
                                    }
                                    if (ticket.getDetails() != null && INVALID_TOKEN_ERRORS.contains(ticket.getDetails().getError())) {
                                        invalidTokens.add(ticket.getTo());   
                                    }
                                }
                                response.setInvalidTokens(invalidTokens);
                            }
                            
                            return response;
                        } catch (IOException e) {
                            ExpoPushResponse response = new ExpoPushResponse();
                            response.setStatus(Status.ERROR);
                            response.setCause(e);
                            return response;
                        }
                    });
        } catch (MalformedURLException e) {
            throw new IllegalStateException("Incorrect baseApiUrl" + baseApiUrl, e);
        }
    }

    public CompletableFuture<List<ExpoPushReceiept>> getPushNotificationReceiptsAsync(List<String> _ids) {
        try {
            return _postReceiptsAsync(new URL(baseApiUrl + "/push/getReceipts"), _ids)
                    .thenApply((String jsonString) -> {
                        try {
                            ObjectMapper mapper = new ObjectMapper();
                            JsonNode dataNode = mapper.readTree(jsonString).get("data");
                            List<ExpoPushReceiept> retList = new ArrayList<>();
                            Iterator<Map.Entry<String, JsonNode>> it = dataNode.fields();
                            while (it.hasNext()) {
                                Map.Entry<String, JsonNode> field = it.next();
                                String key = field.getKey();
                                JsonNode expoPushRecieptJsonNode = field.getValue();
                                ExpoPushReceiept epr = mapper.treeToValue(expoPushRecieptJsonNode, ExpoPushReceiept.class);

                                epr.id = key;
                                retList.add(epr);
                            }
                            return retList;
                        } catch (IOException e) {
                            return _ids.stream().map(id -> new ExpoPushReceiept(id, e)).collect(Collectors.toList());
                        }
                    });
        } catch (URISyntaxException e) {
            throw new IllegalStateException("Incorrect baseApiUrl" + baseApiUrl, e);
        } catch (MalformedURLException e) {
            throw new IllegalStateException("Incorrect baseApiUrl" + baseApiUrl, e);
        }
    }

    protected <T> CompletableFuture<String> _postNotificationAsync(URL url, List<T> messages) {
        ObjectMapper objectMapper = new ObjectMapper();
        String json = null;

        try {
            json = objectMapper.
                    writeValueAsString(messages);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Could not convert to json", e);
        }
        return pushServerResolver.postAsync(url, json);
    }


    private static class JsonReceiptHelper<T> {
        @SuppressWarnings("unused")
        public List<T> ids;
        public JsonReceiptHelper(List<T> _ids) {
            ids = _ids;
        }
    }

    private <T> CompletableFuture<String> _postReceiptsAsync(URL url, List<T> receipts) throws URISyntaxException {
        JsonReceiptHelper<T> jsonReceiptHelper = new PushClient.JsonReceiptHelper<T>(receipts);

        ObjectMapper objectMapper = new ObjectMapper();
        String json = null;

        try {
            json = objectMapper.
                    writeValueAsString(jsonReceiptHelper);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Could not convert to json", e);
        }
        return pushServerResolver.postAsync(url, json);
    }

    static public boolean isExponentPushToken(String token) {
        String regex = "[a-zA-Z0-9]{8}-[a-zA-Z0-9]{4}-[a-zA-Z0-9]{4}-[a-zA-Z0-9]{4}-[a-zA-Z0-9]{12}";
        String regex2 = "ExponentPushToken\\[.+\\]";
        String regex3 = "ExpoPushToken\\[.+\\]";

        if (token.matches(regex)) return true;
        if (token.matches(regex2)) return true;
        if (token.matches(regex3)) return true;
        return false;
    }

    public static long _getActualMessagesCount(List<? extends ExpoPushMessage<?>> messages) {
        return messages.stream().reduce(0, (acc, cur) -> acc + cur.to.size(), Integer::sum);
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

    @SuppressWarnings("unchecked")
    public List<List<Z>> chunkPushNotifications(List<Z> messages) {
        List<List<Z>> chunks = new ArrayList<>();
        List<Z> chunk = new ArrayList<>();

        long chunkMessagesCount = 0;
        for (Z message : messages) {
            List<String> partialTo = new ArrayList<>();
            for (String recipient : message.to) {
                if (recipient.length() <= 0) continue;
                partialTo.add(recipient);
                chunkMessagesCount++;
                if (chunkMessagesCount >= PUSH_NOTIFICATION_CHUNK_LIMIT) {
                    // Cap this chunk here if it already exceeds PUSH_NOTIFICATION_CHUNK_LIMIT.
                    // Then create a new chunk to continue on the remaining recipients for this message.
                    
                    chunk.add((Z) message.toChunk(partialTo));
                    chunks.add(chunk);
                    chunk = new ArrayList<>();
                    chunkMessagesCount = 0;
                    partialTo = new ArrayList<>();
                }
            }

            if (partialTo.size() > 0) {
                chunk.add((Z) message.toChunk(partialTo));
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
