package org.mh.iot.bus.devices.interfaces.exceptions;

/**
 * Created by evolshan on 10.12.2020.
 */

public class CannotStartConsumerException extends Exception {

    public CannotStartConsumerException(){ super(); }

    public CannotStartConsumerException(Throwable cause) {
        super(cause);
    }

    public CannotStartConsumerException(String message, Throwable cause) {
        super(message, cause);
    }

    public CannotStartConsumerException(String message) {
        super(message);
    }
}