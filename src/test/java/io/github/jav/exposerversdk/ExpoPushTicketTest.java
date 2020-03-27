package io.github.jav.exposerversdk;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;

import static org.junit.jupiter.api.Assertions.*;

class ExpoPushTicketTest {

    @Test
    void DetailErrorMayOnlyBeDeviceNotRegisteredInvalidCredentialsMessageTooBigOrMessageRateExceeded() {
        ExpoPushTicket ept = new ExpoPushTicket();
        String[] errorDetailsKeys = new String[]{"DeviceNotRegistered", "InvalidCredentials", "MessageTooBig", "MessageRateExceeded"};

        ept.details = new ExpoPushTicket.Details(null);
        ept.details = new ExpoPushTicket.Details("DeviceNotRegistered");
        ept.details = new ExpoPushTicket.Details("InvalidCredentials");
        ept.details = new ExpoPushTicket.Details("MessageTooBig");
        ept.details = new ExpoPushTicket.Details("MessageRateExceeded");

        assertThrows(IllegalArgumentException.class, () -> {
            ept.details = new ExpoPushTicket.Details("MeSSATEExcEEded");
        });
    }

    @Test
    void jsonSerializesCorrectly() throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        JsonFactory factory = new JsonFactory();
        Writer writer = null;
        JsonGenerator generator = null;
        String emsJson = null;
        String jsonControl = null;
        ExpoPushTicket ept = null;

        // Empty object
        writer = new StringWriter();
        generator = factory.createGenerator(writer);
        generator.writeStartObject();
        generator.writeNullField("status");
        generator.writeEndObject();
        generator.close();
        jsonControl = writer.toString();
        ept = new ExpoPushTicket();
        emsJson = mapper.writeValueAsString(ept);
        assertEquals(mapper.readTree(jsonControl), mapper.readTree(emsJson));


        // Status success
        writer = new StringWriter();
        generator = factory.createGenerator(writer);
        generator.writeStartObject();
        generator.writeStringField("status", "ok");
        generator.writeStringField("id", "123");
        generator.writeEndObject();
        generator.close();
        jsonControl = writer.toString();
        ept = new ExpoPushTicket();
        ept.status = "ok";
        ept.id = "123";
        emsJson = mapper.writeValueAsString(ept);
        assertEquals(mapper.readTree(jsonControl), mapper.readTree(emsJson));

        // Status error
        writer = new StringWriter();
        generator = factory.createGenerator(writer);
        generator.writeStartObject();
        generator.writeStringField("status", "error");
        generator.writeStringField("message", "message");
        generator.writeObjectFieldStart("details");
        generator.writeStringField("error", "MessageTooBig");
        generator.writeEndObject();
        generator.writeStringField("message", "message");
        generator.writeEndObject();
        generator.close();
        jsonControl = writer.toString();
        ept = new ExpoPushTicket();
        ept.status = "error";
        ept.message = "message";
        ept.details = new ExpoPushTicket.Details("MessageTooBig");
        emsJson = mapper.writeValueAsString(ept);
        assertEquals(mapper.readTree(jsonControl), mapper.readTree(emsJson));
    }


    @Test
    void equals() throws IOException {
        ExpoPushTicket ept1;
        ept1 = new ExpoPushTicket();
        ExpoPushTicket ept2;
        ept2 = new ExpoPushTicket();
        assertEquals(ept1, ept2);

        ept1.id = "1";
        assertNotEquals(ept1, ept2);
        ept2.id = "1";
        assertEquals(ept1, ept2);

        ept1.status = "error";
        assertNotEquals(ept1, ept2);
        ept2.status = "error";
        assertEquals(ept1, ept2);

        ept1.message = "message";
        assertNotEquals(ept1, ept2);
        ept2.message = "message";
        assertEquals(ept1, ept2);

        ept1.details = new ExpoPushTicket.Details("MessageTooBig");
        assertNotEquals(ept1, ept2);
        ept2.details = new ExpoPushTicket.Details("MessageTooBig");
        assertEquals(ept1, ept2);
    }
}