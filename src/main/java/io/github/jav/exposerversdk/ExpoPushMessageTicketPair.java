package io.github.jav.exposerversdk;

public class ExpoPushMessageTicketPair<TPushMessage> {
    public TPushMessage message;
    public ExpoPushTicket ticket;

    ExpoPushMessageTicketPair(TPushMessage message, ExpoPushTicket ticket) {
        this.message = message;
        this.ticket = ticket;
    }
    
    public TPushMessage getMessage() {
        return message;
    }

    public ExpoPushTicket getTicket() {
        return ticket;
    }
}
