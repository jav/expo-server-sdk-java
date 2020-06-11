package io.github.jav.exposerversdk;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializable;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;

import java.io.IOException;
import java.util.*;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties({"_debug"})
public class ExpoPushMessage implements JsonSerializable {

    @JsonProperty("to")
    public List<String> to = null;
    @JsonProperty("data")
    public Map<String, String> data = null;
    @JsonProperty("title")
    public String title = null;
    @JsonProperty("subtitle")
    public String subtitle = null;
    @JsonProperty("body")
    public String body = null;
    @JsonProperty("sound")
    public ExpoMessageSound sound = null;
    @JsonProperty("ttl")
    public long ttl = -1;
    @JsonProperty("expiration")
    public long expiration = -1;
    @JsonProperty("priority")
    private String priority = null;
    @JsonProperty("badge")
    public long badge = -1;
    @JsonProperty("channelId")
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

    @JsonProperty("to")
    public List<String> getTo() {
        return to;
    }

    @JsonProperty("to")
    public void setTo(List<String> to) {
        this.to = to;
    }

    @JsonProperty("data")
    public Map<String, String> getData() {
        return data;
    }

    @JsonProperty("data")
    public void setData(Map<String, String> data) {
        this.data = data;
    }

    @JsonProperty("title")
    public String getTitle() {
        return title;
    }

    @JsonProperty("title")
    public void setTitle(String title) {
        this.title = title;
    }

    @JsonProperty("subtitle")
    public String getSubtitle() {
        return subtitle;
    }

    @JsonProperty("subtitle")
    public void setSubtitle(String subtitle) {
        this.subtitle = subtitle;
    }

    @JsonProperty("body")
    public String getBody() {
        return body;
    }

    @JsonProperty("body")
    public void setBody(String body) {
        this.body = body;
    }

    @JsonProperty("sound")
    public ExpoMessageSound getSound() {
        return sound;
    }

    @JsonProperty("sound")
    public void setSound(ExpoMessageSound sound) {
        this.sound = sound;
    }

    @JsonProperty("ttl")
    public long getTtl() {
        return ttl;
    }

    @JsonProperty("ttl")
    public void setTtl(long ttl) {
        this.ttl = ttl;
    }

    @JsonProperty("expiration")
    public long getExpiration() {
        return expiration;
    }

    @JsonProperty("expiration")
    public void setExpiration(long expiration) {
        this.expiration = expiration;
    }

    @JsonProperty("badge")
    public long getBadge() {
        return badge;
    }

    @JsonProperty("badge")
    public void setBadge(long badge) {
        this.badge = badge;
    }

    @JsonProperty("channelId")
    public String getChannelId() {
        return channelId;
    }

    @JsonProperty("channelId")
    public void setChannelId(String channelId) {
        this.channelId = channelId;
    }

    @JsonProperty("priority")
    public void setPriority(String _priority) {
        if (null != _priority &&
                !_priority.toLowerCase().equals("default") &&
                !_priority.toLowerCase().equals("high") &&
                !_priority.toLowerCase().equals("normal")
        )
            throw new IllegalArgumentException();
        priority = _priority;
    }

    @JsonProperty("priority")
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


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ExpoPushMessage)) return false;
        ExpoPushMessage that = (ExpoPushMessage) o;
        return getTtl() == that.getTtl() &&
                getExpiration() == that.getExpiration() &&
                getBadge() == that.getBadge() &&
                Objects.equals(getTo(), that.getTo()) &&
                Objects.equals(getData(), that.getData()) &&
                Objects.equals(getTitle(), that.getTitle()) &&
                Objects.equals(getSubtitle(), that.getSubtitle()) &&
                Objects.equals(getBody(), that.getBody()) &&
                Objects.equals(getSound(), that.getSound()) &&
                Objects.equals(getPriority(), that.getPriority()) &&
                Objects.equals(getChannelId(), that.getChannelId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getTo(), getData(), getTitle(), getSubtitle(), getBody(), getSound(), getTtl(), getExpiration(), getPriority(), getBadge(), getChannelId());
    }
};

