package io.github.jav.exposerversdk;

import java.util.List;
import java.util.concurrent.CompletionException;

public class PushNotificationException extends CompletionException {
    public Exception exception;
    public List<? extends ExpoPushMessageCustomData> messages;

    public PushNotificationException(Exception e, List<? extends ExpoPushMessageCustomData<?>> messages) {
        this.exception = e;
        this.messages = messages;
    }
}
