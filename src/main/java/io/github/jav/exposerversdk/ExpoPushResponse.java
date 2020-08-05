package io.github.jav.exposerversdk;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown=true)
public class ExpoPushResponse {
    List<ExpoPushTicket> data = null;
    List<String> invalidTokens = null;
    Status status;
    Exception cause;
    public List<ExpoPushTicket> getData() {
        return data;
    }
    public void setData(List<ExpoPushTicket> data) {
        this.data = data;
    }
    public List<String> getInvalidTokens() {
        return invalidTokens;
    }
    public void setInvalidTokens(List<String> invalidTokens) {
        this.invalidTokens = invalidTokens;
    }
    public Status getStatus() {
        return status;
    }
    public void setStatus(Status status) {
        this.status = status;
    }
    public Exception getCause() {
        return cause;
    }
    public void setCause(Exception cause) {
        this.cause = cause;
    }
    @Override
    public String toString() {
        return "ExpoPushResponse [data=" + data + ", invalidTokens=" + invalidTokens + ", status=" + status + "]";
    }
    
    
    
}
