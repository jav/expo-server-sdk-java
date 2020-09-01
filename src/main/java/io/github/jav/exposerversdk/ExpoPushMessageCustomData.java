package io.github.jav.exposerversdk;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.github.jav.exposerversdk.enums.Priority;

import java.util.*;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties({"_debug"})
public class ExpoPushMessageCustomData<T> {

    private List<String> to = null;
    private Map<String, T> data = null;
    private String title = null;
    private String subtitle = null;
    private String body = null;
    private ExpoMessageSound sound = null;
    private Long ttl = null;
    private Long expiration = null;
    private Priority priority = null;
    private Long badge = null;
    private String channelId = null;

    public ExpoPushMessageCustomData() {
        to = new ArrayList<>();
    }

    public ExpoPushMessageCustomData(ExpoPushMessageCustomData<T> _message) {
        to = _message.to;
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

    public ExpoPushMessageCustomData(List<String> _to, ExpoPushMessageCustomData<T> _message) {
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

    @Override
    public Object clone() {
            return new ExpoPushMessageCustomData(this);
    }

    public ExpoPushMessageCustomData(List<String> _to) {
        to = _to;
    }

    public ExpoPushMessageCustomData(String _to) {
        to = Arrays.asList(_to);
    }

    public List<String> getTo() {
        return to;
    }

    public void setTo(List<String> to) {
        this.to = to;
    }

    public void addTo(String to) {
        this.to.add(to);
    }

    public void addAllTo(List<String> to) {
        this.to.addAll(to);
    }

    public Map<String, T> getData() {
        return data;
    }

    public void setData(Map<String, T> data) {
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

    public Long getTtl() {
        return ttl;
    }

    public void setTtl(Long ttl) {
        this.ttl = ttl;
    }

    public Long getExpiration() {
        return expiration;
    }

    public void setExpiration(Long expiration) {
        this.expiration = expiration;
    }

    public Long getBadge() {
        return badge;
    }

    public void setBadge(Long badge) {
        this.badge = badge;
    }

    public String getChannelId() {
        return channelId;
    }

    public void setChannelId(String channelId) {
        this.channelId = channelId;
    }

    public void setPriority(Priority _priority) {
        priority = _priority;
    }

    public Priority getPriority() {
        return priority;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ExpoPushMessageCustomData)) return false;
        ExpoPushMessageCustomData that = (ExpoPushMessageCustomData) o;
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

