package org.mh.iot.bus.devices.interfaces.exceptions;

/**
 * Created by evolshan on 10.12.2020.
 */

public class CannotStopConsumerException extends Exception {

    public CannotStopConsumerException(){ super(); }

    public CannotStopConsumerException(Throwable cause) {
        super(cause);
    }

    public CannotStopConsumerException(String message, Throwable cause) {
        super(message, cause);
    }

    public CannotStopConsumerException(String message) {
        super(message);
    }
}