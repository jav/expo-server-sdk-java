package io.github.jav.exposerversdk;

public class ExpoPushMessageTicketPair<TPushMessage> {
    public TPushMessage message;
    public ExpoPushTicket ticket;

    ExpoPushMessageTicketPair(TPushMessage message, ExpoPushTicket ticket) {
        this.message = message;
        this.ticket = ticket;
    }
}
