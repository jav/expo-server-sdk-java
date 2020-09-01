package io.github.jav.exposerversdk.enums;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum ReceiptError {
        @JsonProperty("DeviceNotRegistered")
        DEVICENOTREGISTERED("DeviceNotRegistered"),
        @JsonProperty("MessageTooBig")
        MESSAGETOOBIG("MessageTooBig"),
        @JsonProperty("MessageRateExceeded")
        MESSAGERATEEXCEEDED("MessageRateExceeded"),
        @JsonProperty("InvalidCredentials")
        INVALIDCREDENTIALS("InvalidCredentials"),
        @JsonProperty("InvalidProviderToken")
        INVALIDPROVIDERTOKEN("InvalidProviderToken");


        private String error;
        private ReceiptError(String error) {
                this.error = error;
        }

        @Override
        public String toString(){
                return error;
        }
}
