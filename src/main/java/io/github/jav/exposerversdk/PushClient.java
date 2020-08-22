package io.github.jav.exposerversdk;

import java.net.URL;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class PushClient extends PushClientCustomData<ExpoPushMessage> {

    public PushClient() throws PushClientException {
        super();
    }

    public URL getBaseApiUrl() {
        return baseApiUrl;
    }

    public PushClient setBaseApiUrl(URL _baseApiUrl) {
        baseApiUrl = _baseApiUrl;
        return this;
    }

    @Override
    public CompletableFuture<List<ExpoPushTicket>> sendPushNotificationsAsync(List<ExpoPushMessage> messages) throws PushNotificationException {
        return super.sendPushNotificationsAsync(messages);
    }

    @Override
    public List<List<ExpoPushMessage>> chunkPushNotifications(List<ExpoPushMessage> messages) {
        return super.chunkPushNotifications(messages);
    }
}
