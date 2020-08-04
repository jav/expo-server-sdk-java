package io.github.jav.exposerversdk;

import com.fasterxml.jackson.annotation.*;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializable;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties({"_debug"})
public class ExpoPushTicket implements JsonSerializable {

    @JsonProperty("id")
    public String id = null;
    @JsonProperty("status")
    private Status status = null;
    @JsonProperty("message")
    private String message = null;
    @JsonProperty("details")
    private ExpoPushTicket.Details details = null;
    @JsonIgnore
    private Exception cause = null;
    
    public ExpoPushTicket() {
        
    }
    public ExpoPushTicket(Exception cause) {
        this.cause = cause;
        this.status = Status.EXCEPTION;
        this.message = cause.getClass().getName();
        this.details = new ExpoPushTicket.Details();
        this.details.error = cause.getMessage();
    }

    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("status")
    public Status getStatus() {
        return status;
    }

    @JsonProperty("status")
    public void setStatus(Status status) {
        this.status = status;
    }

    @JsonProperty("message")
    public String getMessage() {
        return message;
    }

    @JsonProperty("message")
    public void setMessage(String message) {
        this.message = message;
    }

    @JsonProperty("details")
    public ExpoPushTicket.Details getDetails() {
        return details;
    }

    @JsonProperty("details")
    public void setDetails(ExpoPushTicket.Details details) {
        this.details = details;
    }

    @JsonAnyGetter
    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    @JsonAnySetter
    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

    public Exception getCause() {
        return cause;
    }
    
    @Override
    public void serialize(JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        jsonGenerator.writeStartObject();
        jsonGenerator.writeStringField("status", status == null ? null : status.toString());
        if (status != null) {
            if (status == Status.OK) {
                jsonGenerator.writeStringField("id", id);

            } else {
                jsonGenerator.writeStringField("message", message);
                jsonGenerator.writeObjectField("details", details);
            }
        }
        jsonGenerator.writeEndObject();
        return;
    }

    @Override
    public void serializeWithType(JsonGenerator jsonGenerator, SerializerProvider serializerProvider, TypeSerializer typeSerializer) throws IOException {
        throw new UnsupportedOperationException("serializeWithType() not implemented.");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ExpoPushTicket)) return false;
        ExpoPushTicket that = (ExpoPushTicket) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(getStatus(), that.getStatus()) &&
                Objects.equals(getMessage(), that.getMessage()) &&
                Objects.equals(getDetails(), that.getDetails()) &&
                Objects.equals(getAdditionalProperties(), that.getAdditionalProperties());
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, getStatus(), getMessage(), getDetails(), getAdditionalProperties());
    }

    @JsonIgnoreProperties({"apns", "fcm"})
    public static class Details {
        @JsonProperty("error")
        private String error;
        @JsonProperty("sentAt")
        private Integer sentAt;
        @JsonIgnore
        private Map<String, Object> additionalProperties = new HashMap<String, Object>();

        @JsonProperty("error")
        public String getError() {
            return error;
        }

        @JsonProperty("error")
        public ExpoPushTicket.Details setError(String error) {
            this.error = error;
            return this;
        }

        @JsonProperty("sentAt")
        public Integer getSentAt() {
            return sentAt;
        }

        @JsonProperty("sentAt")
        public Details setSentAt(Integer sentAt) {
            this.sentAt = sentAt;
            return this;
        }

        @JsonAnyGetter
        public Map<String, Object> getAdditionalProperties() {
            return this.additionalProperties;
        }

        @JsonAnySetter
        public void setAdditionalProperty(String name, Object value) {
            this.additionalProperties.put(name, value);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof Details)) return false;
            Details details = (Details) o;
            return Objects.equals(getError(), details.getError()) &&
                    Objects.equals(getSentAt(), details.getSentAt()) &&
                    Objects.equals(getAdditionalProperties(), details.getAdditionalProperties());
        }

        @Override
        public int hashCode() {
            return Objects.hash(getError(), getSentAt(), getAdditionalProperties());
        }
    }
}