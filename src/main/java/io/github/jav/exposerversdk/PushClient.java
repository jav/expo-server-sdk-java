package io.github.jav.exposerversdk;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.jav.exposerversdk.helpers.DefaultPushServerResolver;
import io.github.jav.exposerversdk.helpers.PushServerResolver;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class PushClient extends PushClientCustomData<ExpoPushMessage> {

    public PushClient() {
        super();
    }

    public PushClient setBaseApiUrl(URL _baseApiUrl) {
        baseApiUrl = _baseApiUrl;
        return this;
    }

    @Override
    public CompletableFuture<List<ExpoPushTicket>> sendPushNotificationsAsync(List<ExpoPushMessage> messages) {
        return super.sendPushNotificationsAsync(messages);
    }
}
