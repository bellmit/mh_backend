package org.mh.iot.bus.devices.interfaces.exceptions;

/**
 * Created by evolshan on 10.12.2020.
 */

public class MessageNotReceived extends Exception {

    public MessageNotReceived(){ super(); }

    public MessageNotReceived(Throwable cause) {
        super(cause);
    }

    public MessageNotReceived(String message, Throwable cause) {
        super(message, cause);
    }

    public MessageNotReceived(String message) {
        super(message);
    }
}