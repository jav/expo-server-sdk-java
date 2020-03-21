package org.ubillos.pushnotifications.notificationsSDK;


import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializable;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;

import java.io.IOException;

public class ExpoMessageSound implements JsonSerializable {


    private Boolean critical = null;
    private String name = null;
    private long volume = -1;

    ExpoMessageSound() {
    }

    ExpoMessageSound(String _name) {
        if (_name != null && !_name.equals("default"))
            throw new IllegalArgumentException();
        name = _name.toLowerCase();
    }

    ExpoMessageSound(Boolean _critical, String _name, long _volume) {
        critical = _critical;
        name = _name;
        volume = _volume;
    }

    public Boolean getCritical() {
        return critical;
    }

    public void setCritical(Boolean _critical) {
        critical = _critical;
    }

    public String getName() {
        return name;
    }

    public void setName(String _name) {
        if (_name != null && !_name.toLowerCase().equals("default"))
            throw new IllegalArgumentException();
        if (_name == null) {
            name = null;
        } else {
            name = "default";
        }
    }

    public long getVolume() {
        return volume;
    }

    public void setVolume(long _volume) {
        // negative numbers means that volume is 'unset'
        // numbers above 100 are forbidden
        if (_volume > 100)
            throw new IllegalArgumentException();
        volume = _volume;
    }

    @Override
    public void serialize(JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        if (critical != null || volume > 0) {
            jsonGenerator.writeStartObject();
            if(critical != null)
                jsonGenerator.writeBooleanField("critical", critical);
            if(name != null)
                jsonGenerator.writeStringField("name", name);
            if(volume > 0)
                jsonGenerator.writeNumberField("volume", volume);
            jsonGenerator.writeEndObject();
            return;
        }
        if (name != null) {
            jsonGenerator.writeString(name);
            return;
        }
        jsonGenerator.writeNull();
        return;
    }

    @Override
    public void serializeWithType(JsonGenerator jsonGenerator, SerializerProvider serializerProvider, TypeSerializer typeSerializer) throws IOException {
        throw new UnsupportedOperationException("serializeWithType() not implemented.");
    }
}
