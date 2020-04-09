package io.github.jav.exposerversdk;

import com.fasterxml.jackson.annotation.*;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializable;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;
import org.apache.commons.lang3.builder.EqualsBuilder;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties({"_debug"})
public class ExpoPushReceiept implements JsonSerializable {

    @JsonProperty("id")
    public String id = null;
    @JsonProperty("status")
    private String status = null;
    @JsonProperty("message")
    private String message = null;
    @JsonProperty("details")
    private Details details = null;

    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("status")
    public String getStatus() {
        return status;
    }

    @JsonProperty("status")
    public void setStatus(String status) {
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
    public Details getDetails() {
        return details;
    }

    @JsonProperty("details")
    public void setDetails(Details details) {
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


    @Override
    public void serialize(JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        jsonGenerator.writeStartObject();
        jsonGenerator.writeStringField("status", status);
        if (status != null) {
            if (status.equals("ok")) {
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
    public boolean equals(Object _o) {
        if (_o == null)
            return false;

        if (_o == this)
            return true;

        if (_o.getClass() != getClass())
            return false;

        ExpoPushReceiept o = (ExpoPushReceiept) _o;
        return new EqualsBuilder().
                append(status, o.status).
                append(id, o.id).
                append(message, o.message).
                append(details, o.details).
                isEquals();
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
        public Details setError(String error) {
            this.error = error;
            return this;
        }

        @JsonProperty("sentAt")
        public Integer getSentAt() {
            return sentAt;
        }

        @JsonProperty("sentAt")
        public void setSentAt(Integer sentAt) {
            this.sentAt = sentAt;
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
        public boolean equals(Object _o) {
            if (_o == null)
                return false;

            if (_o == this)
                return true;

            if (_o.getClass() != getClass())
                return false;

            ExpoPushReceiept.Details o = (ExpoPushReceiept.Details) _o;
            return new EqualsBuilder().
                    append(getError(), o.getError()).
                    isEquals();
        }
    }
}