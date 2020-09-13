package io.github.jav.exposerversdk;

import java.util.List;

public class PushNotificationErrorsException extends Exception {
    public List<ExpoPushError> errors;
    public List<ExpoPushTicket> data;

    public PushNotificationErrorsException(List<ExpoPushError> errors, List<ExpoPushTicket> data) {
        this.errors = errors;
        this.data = data;
    }
}
