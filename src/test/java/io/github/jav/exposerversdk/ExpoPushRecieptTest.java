package io.github.jav.exposerversdk;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

class ExpoPushRecieptTest {
    @Test
    void testHashcodeImplementation() {
        ExpoPushReceiept a = new ExpoPushReceiept();
        ExpoPushReceiept b = new ExpoPushReceiept();
        assertEquals(a, b);

        // check equals self, as in issue https://github.com/jav/expo-server-sdk-java/issues/6
        assertEquals(a, a);

        a.setDetails(new ExpoPushReceiept.Details());
        assertNotEquals(a, b);
        b.setDetails(new ExpoPushReceiept.Details());
        assertEquals(a, b);

        ExpoPushReceiept.Details detailsA = a.getDetails();
        detailsA.setAdditionalProperty("foo", "bar");
        assertNotEquals(a, b);
        ExpoPushReceiept.Details detailsB = b.getDetails();
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
        ExpoPushReceiept ept = null;

        // Empty object
        writer = new StringWriter();
        generator = factory.createGenerator(writer);
        generator.writeStartObject();
        generator.writeNullField("status");
        generator.writeEndObject();
        generator.close();
        jsonControl = writer.toString();
        ept = new ExpoPushReceiept();
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
        ept = new ExpoPushReceiept();
        ept.setStatus("ok");
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
        ept = new ExpoPushReceiept();
        ept.setStatus("error");
        ept.setMessage("message");
        ept.setDetails( new ExpoPushReceiept.Details().setError("MessageTooBig"));
        emsJson = mapper.writeValueAsString(ept);
        assertEquals(mapper.readTree(jsonControl), mapper.readTree(emsJson));
    }

    @Test
    void deserializesToJsonCorrectly() throws IOException {
        final String SOURCE_JSON = " " +
                "{" +
                "    \"2011eb6d-d4d3-440c-a93c-37ac4b51ea09\": { " +
                "        \"status\":\"error\"," +
                "        \"message\":\"The Apple Push Notification service ...  this error means.\"," +
                "        \"details\":{" +
                "            \"apns\":{" +
                "                \"reason\":\"PayloadTooLarge\", " +
                "                \"statusCode\":413" +
                "            }," +
                "            \"error\":\"MessageTooBig\"," +
                "            \"sentAt\":1586353449" +
                "        }," +
                "        \"__debug\": {}" +
                "    }" +
                "}";
        ObjectMapper mapper = new ObjectMapper();

        JsonNode dataNode = mapper.readTree(SOURCE_JSON).get("2011eb6d-d4d3-440c-a93c-37ac4b51ea09");
        ExpoPushReceiept epr = mapper.treeToValue(dataNode, ExpoPushReceiept.class);
        epr.id = "2011eb6d-d4d3-440c-a93c-37ac4b51ea09";

        assertEquals("2011eb6d-d4d3-440c-a93c-37ac4b51ea09", epr.id);
        assertEquals("error", epr.getStatus());
        assertEquals("MessageTooBig", epr.getDetails().getError());
    }

    @Test
    void equals() throws IOException {
        ExpoPushReceiept epr1;
        epr1 = new ExpoPushReceiept();
        ExpoPushReceiept epr2;
        epr2 = new ExpoPushReceiept();
        assertEquals(epr1, epr2);

        epr1.id = "1";
        assertNotEquals(epr1, epr2);
        epr2.id = "1";
        assertEquals(epr1, epr2);

        epr1.setStatus("error");
        assertNotEquals(epr1, epr2);
        epr2.setStatus("error");
        assertEquals(epr1, epr2);

        epr1.setMessage("message");
        assertNotEquals(epr1, epr2);
        epr2.setMessage("message");
        assertEquals(epr1, epr2);

        epr1.setDetails((new ExpoPushReceiept.Details()).setError("MessageTooBig"));
        assertNotEquals(epr1, epr2);
        epr2.setDetails((new ExpoPushReceiept.Details()).setError("MessageTooBig"));
        assertEquals(epr1, epr2);
    }
}