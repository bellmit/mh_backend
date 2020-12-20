package org.mh.iot.models;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by evolshan on 10.12.2020.
 */
public class StatusMessage {

    @JsonIgnore
    private String unparsedMessage;

    private Device device;
    private String requestId;
    private String error;
    Map<String, String> currentValues = new HashMap<>();

    public Device getDevice() {
        return device;
    }

    public StatusMessage device(Device device) {
        this.device = device;
        return this;
    }

    public String getRequestId() {
        return requestId;
    }

    public StatusMessage requestId(String requestId) {
        this.requestId = requestId;
        return this;
    }

    public String getError() {
        return error;
    }

    public StatusMessage error(String error) {
        this.error = error;
        return this;
    }

    public Map<String, String> getCurrentValues() {
        return currentValues;
    }

    public StatusMessage currentValues(Map<String, String> currentValues) {
        this.currentValues = currentValues;
        return this;
    }

    @JsonIgnore
    public String getUnparsedMessage() {
        return unparsedMessage;
    }

    public StatusMessage unparsedMessage(String unparsedMessage) {
        this.unparsedMessage = unparsedMessage;
        return this;
    }
}
