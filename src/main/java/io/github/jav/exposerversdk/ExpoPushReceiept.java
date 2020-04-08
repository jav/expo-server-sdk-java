package io.github.jav.exposerversdk;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializable;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;
import org.apache.commons.lang3.builder.EqualsBuilder;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class ExpoPushReceiept implements JsonSerializable {
    public String status = null;
    public String id = null;
    public String message = null;
    public Details details = null;

    public ExpoPushReceiept() {
    }


    @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
    ExpoPushReceiept(@JsonProperty("status") String _status,
                     @JsonProperty("id") String _id,
                     @JsonProperty("message") String _message,
                     @JsonProperty("details") String _detailsError) {
        status = _status;
        id = _id;
        message = _message;
        details = new Details(_detailsError);
    }

    public Details getDetails() {
        return details;
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

    public static class Details {
        private String error;

        public Details(String _error) {
            setError(_error);
        }

        public void setError(String _error) {
            String[] errorDetailsKeys = new String[]{"DeviceNotRegistered", "InvalidCredentials",
                    "MessageTooBig", "MessageRateExceeded"};
            List<String> errorDetailsKeysList = Arrays.asList(errorDetailsKeys);
            if (_error != null && !errorDetailsKeysList.contains(_error)) {
                throw new IllegalArgumentException("Member \"details\" but be one of :" + errorDetailsKeys);
            }
            error = _error;
        }

        public String getError() {
            return error;
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