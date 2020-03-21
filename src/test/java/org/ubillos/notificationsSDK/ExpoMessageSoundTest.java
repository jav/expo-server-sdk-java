package org.ubillos.pushnotifications.notificationsSDK;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ExpoMessageSoundTest {

    @Test
    void volumeMayOnlyBeZeroToOneHundredInclusive() {
        ExpoMessageSound ems = new ExpoMessageSound();
        Integer i = 0;
        for (i = -100; i < 100; i += 5) {
            ems.setVolume(i);
            // No assert(), throwing an exception will fail the test
        }

        // test a few valeus higher than 100
        for (i = 101; i < 1000; i += 99) {
            Integer volume = i;
            assertThrows(IllegalArgumentException.class, () -> {
                ems.setVolume(volume);
            });
        }
    }

    @Test
    void soundNameMayOnlyBeDefaultOrNull() {
        ExpoMessageSound ems = new ExpoMessageSound();
        ems.setName("default");
        ems.setName("defaULT");
        ems.setName(null);
        assertThrows(IllegalArgumentException.class, () -> {
            ems.setName("efault");
        });
        assertThrows(IllegalArgumentException.class, () -> {
            ems.setName("defaul");
        });

    }

    @Test
    void jsonSerializesCorrectly() throws IOException {
        // Expected json serialization results are either
        // null
        // 'default'
        // {
        //   critical?: boolean;
        //   name?: 'default' | null;
        //   volume?: number;
        // }
        // (where '?' means 'optional'
        ObjectMapper mapper = new ObjectMapper();
        JsonFactory factory = new JsonFactory();
        Writer writer = null;
        JsonGenerator generator = null;
        String emsJson = null;
        String jsonControl = null;
        ExpoMessageSound ems = null;

        // Empty object
        writer = new StringWriter();
        generator = factory.createGenerator(writer);
        generator.writeNull();
        generator.close();
        jsonControl = writer.toString();
        ems = new ExpoMessageSound();
        emsJson = mapper.writeValueAsString(ems);
        assertEquals(mapper.readTree(jsonControl), mapper.readTree(emsJson));

        // Name = 'default'
        writer = new StringWriter();
        generator = factory.createGenerator(writer);
        generator.writeString("default");
        generator.close();
        jsonControl = writer.toString();
        ems = new ExpoMessageSound();
        ems.setName("DeFaUlT");
        emsJson = mapper.writeValueAsString(ems);
        assertEquals(mapper.readTree(jsonControl), mapper.readTree(emsJson));

        // critical = true
        writer = new StringWriter();
        generator = factory.createGenerator(writer);
        generator.writeStartObject();
        generator.writeBooleanField("critical", true);
        generator.writeEndObject();
        generator.close();
        jsonControl = writer.toString();
        ems = new ExpoMessageSound();
        ems.setCritical(true);
        emsJson = mapper.writeValueAsString(ems);
        assertEquals(mapper.readTree(jsonControl), mapper.readTree(emsJson));

        // critical = false
        writer = new StringWriter();
        generator = factory.createGenerator(writer);
        generator.writeStartObject();
        generator.writeBooleanField("critical", false);
        generator.writeEndObject();
        generator.close();
        jsonControl = writer.toString();
        ems = new ExpoMessageSound();
        ems.setCritical(false);
        emsJson = mapper.writeValueAsString(ems);
        assertEquals(mapper.readTree(jsonControl), mapper.readTree(emsJson));

        // volume = 1
        writer = new StringWriter();
        generator = factory.createGenerator(writer);
        generator.writeStartObject();
        generator.writeNumberField("volume", 1);
        generator.writeEndObject();
        generator.close();
        jsonControl = writer.toString();
        ems = new ExpoMessageSound();
        ems.setVolume(1);
        emsJson = mapper.writeValueAsString(ems);
        assertEquals(mapper.readTree(jsonControl), mapper.readTree(emsJson));

        // critical = true, name = "default", volume = 1
        writer = new StringWriter();
        generator = factory.createGenerator(writer);
        generator.writeStartObject();
        generator.writeBooleanField("critical", true);
        generator.writeStringField("name", "default");
        generator.writeNumberField("volume", 1);
        generator.writeEndObject();
        generator.close();
        jsonControl = writer.toString();
        ems = new ExpoMessageSound();
        ems.setVolume(1);
        ems.setCritical(true);
        ems.setName("deFAUlt");
        emsJson = mapper.writeValueAsString(ems);
        assertEquals(mapper.readTree(jsonControl), mapper.readTree(emsJson));
    }
}