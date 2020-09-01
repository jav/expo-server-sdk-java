package io.github.jav.exposerversdk;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.jav.exposerversdk.enums.Priority;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

class ExpoPushMessageCustomDataTest {

    @Test
    void testHashcodeImplementation() {
        List<String> recipientsA =Arrays.asList(new String[]{"recipient1", "recipient2"});
        List<String> recipientsB =Arrays.asList(new String[]{"recipient1", "recipient2"});
        String titleA = "title";
        String titleB = "title";
        Map<String, Integer> mapA = new HashMap<>();
        mapA.put("myInt", 42);
        Map<String, Integer> mapB = new HashMap<>();
        mapB.put("myInt", 42);
        ExpoMessageSound soundA = new ExpoMessageSound();
        ExpoMessageSound soundB = new ExpoMessageSound();

        ExpoPushMessageCustomData<Integer> a = new ExpoPushMessageCustomData<>();
        ExpoPushMessageCustomData<Integer> b = new ExpoPushMessageCustomData<>();
        assertEquals(a, b);

        // check equals self, as in issue https://github.com/jav/expo-server-sdk-java/issues/6
        assertEquals(a, a);

        a.setTo(recipientsA);
        assertNotEquals(a, b);
        b.setTo(recipientsB);
        assertEquals(a, b);

        a.setTitle(titleA);
        assertNotEquals(a, b);
        b.setTitle(titleB);
        assertEquals(a, b);

        a.setData(mapA);
        assertNotEquals(a, b);
        b.setData(mapB);
        assertEquals(a, b);

        a.setSound(soundA);
        assertNotEquals(a, b);
        b.setSound(soundB);
        assertEquals(a, b);

        soundA.setVolume(100);
        assertNotEquals(a, b);
        soundB.setVolume(100);
        assertEquals(a, b);
    }

    @Test
    void jsonSerializesCorrectly() throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        JsonFactory factory = new JsonFactory();
        Writer writer = null;
        JsonGenerator generator = null;
        String epmJson = null;
        String jsonControl = null;
        ExpoPushMessageCustomData<Integer> epm = null;

        // Empty object
        writer = new StringWriter();
        generator = factory.createGenerator(writer);
        generator.writeStartObject();
        generator.writeArrayFieldStart("to");
        generator.writeEndArray();
        generator.writeEndObject();
        generator.close();
        jsonControl = writer.toString();
        epm = new ExpoPushMessageCustomData<>();
        epmJson = mapper.writeValueAsString(epm);
        assertEquals(mapper.readTree(jsonControl), mapper.readTree(epmJson));

        // Empty two recipients, title, sound, priority
        writer = new StringWriter();
        generator = factory.createGenerator(writer);
        generator.writeStartObject();
        generator.writeArrayFieldStart("to");
        generator.writeString("Recipient 1");
        generator.writeString("Recipient 2");
        generator.writeEndArray();
        generator.writeStringField("title", "My title");
        generator.writeStringField("priority", "normal");
        generator.writeObjectFieldStart("sound");
        generator.writeStringField("name", "default");
        generator.writeNumberField("volume", 60);
        generator.writeEndObject();
        generator.writeObjectFieldStart("data");
        generator.writeNumberField("myInt", 42);
        generator.writeEndObject();
        generator.writeEndObject();
        generator.close();
        jsonControl = writer.toString();
        epm = new ExpoPushMessageCustomData<>(Arrays.asList("Recipient 1", "Recipient 2"));
        epm.setTitle("My title");
        Map<String, Integer> myMap = new HashMap<>();
        myMap.put("myInt", 42);
        epm.setData(myMap);
        ExpoMessageSound ems = new ExpoMessageSound("default");
        ems.setVolume(60);
        epm.setSound(ems);
        epm.setPriority(Priority.NORMAL);
        epmJson = mapper.writeValueAsString(epm);
        assertEquals(mapper.readTree(jsonControl), mapper.readTree(epmJson));

    }
}