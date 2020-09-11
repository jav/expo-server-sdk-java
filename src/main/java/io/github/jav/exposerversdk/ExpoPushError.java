package io.github.jav.exposerversdk;

import com.fasterxml.jackson.annotation.*;
import io.github.jav.exposerversdk.enums.Status;
import io.github.jav.exposerversdk.enums.TicketError;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties({"_debug"})
public class ExpoPushError {

    private String code = null;
    private String message = null;

    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @JsonAnyGetter
    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    @JsonAnySetter
    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ExpoPushError)) return false;
        ExpoPushError that = (ExpoPushError) o;
        return Objects.equals(code, that.code) &&
                Objects.equals(getMessage(), that.getMessage()) &&
                Objects.equals(getAdditionalProperties(), that.getAdditionalProperties());
    }

    @Override
    public int hashCode() {
        return Objects.hash(code, getMessage(), getAdditionalProperties());
    }
}