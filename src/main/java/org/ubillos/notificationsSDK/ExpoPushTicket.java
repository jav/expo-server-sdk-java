package org.ubillos.pushnotifications.notificationsSDK;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializable;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class ExpoPushTicket implements JsonSerializable {
    public String status = null;
    public String id = null;
    public String message = null;
    public Details details = null;

    Logger logger = LoggerFactory.getLogger(ExpoPushTicket.class);

    public ExpoPushTicket() {
    }


    @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
    ExpoPushTicket(@JsonProperty("status") String _status,
                   @JsonProperty("id") String _id,
                   @JsonProperty("message") String _message,
                   @JsonProperty("details") String _details) {
        status = _status;
        id = _id;
        message = _message;
        details = new Details(_details);
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
    }
}