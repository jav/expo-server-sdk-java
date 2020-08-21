package io.github.jav.exposerversdk;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CompletionException;

public class PushNotificationReceiptsException extends CompletionException {
    public IOException exception;
    public List<String> ids;
    public PushNotificationReceiptsException(IOException e, List<String> ids) {
        this.exception = e;
        this.ids = ids;
    }
}
