package org.mh.iot.bus.devices.interfaces.exceptions;

/**
 * Created by evolshan on 10.12.2020.
 */

public class CannotSendMessage extends Exception {

    public CannotSendMessage(){ super(); }

    public CannotSendMessage(Throwable cause) {
        super(cause);
    }

    public CannotSendMessage(String message, Throwable cause) {
        super(message, cause);
    }

    public CannotSendMessage(String message) {
        super(message);
    }
}