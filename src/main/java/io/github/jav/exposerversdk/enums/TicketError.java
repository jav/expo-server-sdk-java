package io.github.jav.exposerversdk.enums;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum TicketError {
        @JsonProperty("DeviceNotRegistered")
        DEVICENOTREGISTERED("DeviceNotRegistered");
        INVALIDCREDENTIALS("InvalidCredentials");

        private String error;
        private TicketError(String error) {
                this.error = error;
        }

        @Override
        public String toString(){
                return error;
        }
}
