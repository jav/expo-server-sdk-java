package io.github.jav.exposerversdk;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializable;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;

import java.io.IOException;
import java.util.*;

public class ExpoPushMessage implements JsonSerializable {
    public List<String> to = null;
    public Map<String, String> data = null;
    public String title = null;
    public String subtitle = null;
    public String body = null;
    public ExpoMessageSound sound = null;
    public long ttl = -1;
    public long expiration = -1;
    private String priority = null;
    public long badge = -1;
    public String channelId = null;

    public ExpoPushMessage() {
        to = new ArrayList<>();
    }

    public ExpoPushMessage(List<String> _to, ExpoPushMessage _message) {
        to = _to;
        data = _message.data;
        title = _message.title;
        subtitle = _message.subtitle;
        body = _message.body;
        sound = _message.sound;
        ttl = _message.ttl;
        expiration = _message.expiration;
        priority = _message.priority;
        badge = _message.badge;
        channelId = _message.channelId;
    }

    public ExpoPushMessage(List<String> _to) {
        to = _to;
    }

    public ExpoPushMessage(String _to) {
        to = Arrays.asList(_to);
    }


    public List<String> getTo() {
        return to;
    }

    public void setTo(List<String> to) {
        this.to = to;
    }

    public Map<String, String> getData() {
        return data;
    }

    public void setData(Map<String, String> data) {
        this.data = data;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSubtitle() {
        return subtitle;
    }

    public void setSubtitle(String subtitle) {
        this.subtitle = subtitle;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public ExpoMessageSound getSound() {
        return sound;
    }

    public void setSound(ExpoMessageSound sound) {
        this.sound = sound;
    }

    public long getTtl() {
        return ttl;
    }

    public void setTtl(long ttl) {
        this.ttl = ttl;
    }

    public long getExpiration() {
        return expiration;
    }

    public void setExpiration(long expiration) {
        this.expiration = expiration;
    }

    public long getBadge() {
        return badge;
    }

    public void setBadge(long badge) {
        this.badge = badge;
    }

    public String getChannelId() {
        return channelId;
    }

    public void setChannelId(String channelId) {
        this.channelId = channelId;
    }

    public void setPriority(String _priority) {
        if (null != _priority &&
                !_priority.toLowerCase().equals("default") &&
                !_priority.toLowerCase().equals("high") &&
                !_priority.toLowerCase().equals("normal")
        )
            throw new IllegalArgumentException();
        priority = _priority;
    }

    public String getPriority() {
        return priority;
    }

    @Override
    public void serialize(JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        jsonGenerator.writeStartObject();
        if (to != null) {
            jsonGenerator.writeArrayFieldStart("to");
            for (String recipient : to) {
                jsonGenerator.writeString(recipient);
            }
            jsonGenerator.writeEndArray();
        }
        if (data != null)
            jsonGenerator.writeObjectField("data", data);
        if (title != null)
            jsonGenerator.writeStringField("title", title);
        if (subtitle != null)
            jsonGenerator.writeStringField("subtitle", subtitle);
        if (body != null)
            jsonGenerator.writeStringField("body", body);
        if (sound != null)
            jsonGenerator.writeObjectField("sound", sound);
        if (ttl >= 0)
            jsonGenerator.writeNumberField("ttl", ttl);
        if (expiration >= 0)
            jsonGenerator.writeNumberField("expiration", expiration);
        if (priority != null)
            jsonGenerator.writeStringField("priority", priority);

        if (badge >= 0)
            jsonGenerator.writeNumberField("badge", badge);

        if (channelId != null)
            jsonGenerator.writeStringField("channelId", channelId);

        jsonGenerator.writeEndObject();
        return;
    }

    @Override
    public void serializeWithType(JsonGenerator jsonGenerator, SerializerProvider serializerProvider, TypeSerializer typeSerializer) throws IOException {
        throw new UnsupportedOperationException("serializeWithType() not implemented.");
    }
};

