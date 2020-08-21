package io.github.jav.exposerversdk;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CompletionException;

public class PushNotificationException<T> extends CompletionException {
    public IOException exception;
    public List<? extends ExpoPushMessageWithCustomData> messages;
    public PushNotificationException(    IOException e, List<T>messages)    {
        this.exception = e;
        this.messages = messages;
    }
}
