package io.github.jav.exposerversdk;

import java.util.List;

public class PushNotificationReceiptsErrorsException extends Exception {
    public List<ExpoPushError> errors;
    public List<ExpoPushReceipt> receipts;

    public PushNotificationReceiptsErrorsException(List<ExpoPushError> errors, List<ExpoPushReceipt> receipts) {
        this.errors = errors;
        this.receipts = receipts;
    }
}
