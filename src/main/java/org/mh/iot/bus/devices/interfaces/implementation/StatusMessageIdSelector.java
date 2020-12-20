package org.mh.iot.bus.devices.interfaces.implementation;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.mh.iot.bus.devices.interfaces.MessageSelector;
import org.mh.iot.models.StatusMessage;

/**
 * Created by evolshan on 10.12.2020.
 */
public class StatusMessageIdSelector implements MessageSelector {

    private String id;
    ObjectMapper objectMapper = new ObjectMapper();

    public StatusMessageIdSelector(String id){
        this.id = id;
    }
    @Override
    public boolean test(String message) {
        try {
            StatusMessage statusMessage = objectMapper.readValue(message, StatusMessage.class);
            return statusMessage.getRequestId().equals(id);
        } catch (Exception ex){
            return false;
        }
    }
}
