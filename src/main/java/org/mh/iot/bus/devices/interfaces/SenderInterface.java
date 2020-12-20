package org.mh.iot.bus.devices.interfaces;

import org.mh.iot.bus.devices.interfaces.exceptions.CannotSendMessage;

/**
 * Created by evolshan on 11.12.2020.
 */
public interface SenderInterface {
    void sendNotification(String message, String connectionString) throws CannotSendMessage;
}
