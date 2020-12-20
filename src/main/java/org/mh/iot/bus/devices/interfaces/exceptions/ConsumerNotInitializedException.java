package org.mh.iot.bus.devices.interfaces.exceptions;

/**
 * Created by evolshan on 10.12.2020.
 */

public class ConsumerNotInitializedException extends Exception {

    public ConsumerNotInitializedException(){ super(); }

    public ConsumerNotInitializedException(Throwable cause) {
        super(cause);
    }

    public ConsumerNotInitializedException(String message, Throwable cause) {
        super(message, cause);
    }

    public ConsumerNotInitializedException(String message) {
        super(message);
    }
}