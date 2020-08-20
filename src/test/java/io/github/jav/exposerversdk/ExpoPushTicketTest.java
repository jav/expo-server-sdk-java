package io.github.jav.exposerversdk;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.jav.exposerversdk.enums.Status;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;

import static org.junit.jupiter.api.Assertions.*;

class ExpoPushTicketTest {
    @Test
    void testHashcodeImplementation() {
        ExpoPushTicket a = new ExpoPushTicket();
        ExpoPushTicket b = new ExpoPushTicket();
        assertEquals(a, b);

        // check equals self, as in issue https://github.com/jav/expo-server-sdk-java/issues/6
        assertEquals(a, a);

        a.setDetails(new ExpoPushTicket.Details());
        assertNotEquals(a, b);
        b.setDetails(new ExpoPushTicket.Details());
        assertEquals(a, b);

        ExpoPushTicket.Details detailsA = a.getDetails();
        detailsA.setAdditionalProperty("foo", "bar");
        assertNotEquals(a, b);
        ExpoPushTicket.Details detailsB = b.getDetails();
        detailsB.setAdditionalProperty("foo", "bar");
        assertEquals(a, b);
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
        ept.setStatus(Status.OK);
        ept.setId("123");
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
        generator.writeNumberField("sentAt", 123);
        generator.writeEndObject();
        generator.writeStringField("message", "message");
        generator.writeEndObject();
        generator.close();
        jsonControl = writer.toString();
        ept = new ExpoPushTicket();
        ept.setStatus(Status.ERROR);
        ept.setMessage("message");

        ept.setDetails(new ExpoPushTicket.Details().setError("MessageTooBig").setSentAt(123));
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

        ept1.setId("1");
        assertNotEquals(ept1, ept2);
        ept2.setId("1");
        assertEquals(ept1, ept2);

        ept1.setStatus(Status.ERROR);
        assertNotEquals(ept1, ept2);
        ept2.setStatus(Status.ERROR);
        assertEquals(ept1, ept2);

        ept1.setMessage("message");
        assertNotEquals(ept1, ept2);
        ept2.setMessage("message");
        assertEquals(ept1, ept2);

        ept1.setDetails(new ExpoPushTicket.Details().setError("MessageTooBig"));
        assertNotEquals(ept1, ept2);
        ept2.setDetails(new ExpoPushTicket.Details().setError("MessageTooBig"));
        assertEquals(ept1, ept2);
    }
}