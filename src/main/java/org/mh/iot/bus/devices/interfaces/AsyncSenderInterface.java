package org.mh.iot.bus.devices.interfaces;

import org.mh.iot.bus.devices.interfaces.exceptions.CannotSendMessage;
import org.mh.iot.bus.devices.interfaces.exceptions.ConsumerNotInitializedException;
import org.mh.iot.bus.devices.interfaces.exceptions.MessageNotReceived;

/**
 * Created by evolshan on 09.12.2020.
 */
public interface AsyncSenderInterface extends SenderInterface{
    String sendRequest(String message, String requestConnectionString, String replyConnectionString, MessageSelector selector, int timeout) throws CannotSendMessage, MessageNotReceived;
}
