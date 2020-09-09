package io.github.jav.exposerversdk;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.jav.exposerversdk.enums.ReceiptError;
import io.github.jav.exposerversdk.enums.Status;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

class ExpoPushRecieptTest {
    @Test
    void testHashcodeImplementation() {
        ExpoPushReceipt a = new ExpoPushReceipt();
        ExpoPushReceipt b = new ExpoPushReceipt();
        assertEquals(a, b);

        // check equals self, as in issue https://github.com/jav/expo-server-sdk-java/issues/6
        assertEquals(a, a);

        a.setDetails(new ExpoPushReceipt.Details());
        assertNotEquals(a, b);
        b.setDetails(new ExpoPushReceipt.Details());
        assertEquals(a, b);

        ExpoPushReceipt.Details detailsA = a.getDetails();
        detailsA.setAdditionalProperty("foo", "bar");
        assertNotEquals(a, b);
        ExpoPushReceipt.Details detailsB = b.getDetails();
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
        ExpoPushReceipt ept = null;

        // Empty object
        writer = new StringWriter();
        generator = factory.createGenerator(writer);
        generator.writeStartObject();
        generator.writeEndObject();
        generator.close();
        jsonControl = writer.toString();
        ept = new ExpoPushReceipt();
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
        ept = new ExpoPushReceipt();
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
        generator.writeEndObject();
        generator.writeStringField("message", "message");
        generator.writeEndObject();
        generator.close();
        jsonControl = writer.toString();
        ept = new ExpoPushReceipt();
        ept.setStatus(Status.ERROR);
        ept.setMessage("message");
        ept.setDetails( new ExpoPushReceipt.Details().setError(ReceiptError.MESSAGETOOBIG));
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
        ExpoPushReceipt epr = mapper.treeToValue(dataNode, ExpoPushReceipt.class);
        epr.setId("2011eb6d-d4d3-440c-a93c-37ac4b51ea09");

        assertEquals("2011eb6d-d4d3-440c-a93c-37ac4b51ea09", epr.getId());
        assertEquals(Status.ERROR, epr.getStatus());
        assertEquals(ReceiptError.MESSAGETOOBIG, epr.getDetails().getError());
    }

    @Test
    void equals() throws IOException {
        ExpoPushReceipt epr1;
        epr1 = new ExpoPushReceipt();
        ExpoPushReceipt epr2;
        epr2 = new ExpoPushReceipt();
        assertEquals(epr1, epr2);

        epr1.setId("1");
        assertNotEquals(epr1, epr2);
        epr2.setId("1");
        assertEquals(epr1, epr2);

        epr1.setStatus(Status.ERROR);
        assertNotEquals(epr1, epr2);
        epr2.setStatus(Status.ERROR);
        assertEquals(epr1, epr2);

        epr1.setMessage("message");
        assertNotEquals(epr1, epr2);
        epr2.setMessage("message");
        assertEquals(epr1, epr2);

        epr1.setDetails((new ExpoPushReceipt.Details()).setError(ReceiptError.MESSAGETOOBIG));
        assertNotEquals(epr1, epr2);
        epr2.setDetails((new ExpoPushReceipt.Details()).setError(ReceiptError.MESSAGETOOBIG));
        assertEquals(epr1, epr2);
    }
}