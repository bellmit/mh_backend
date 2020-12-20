package org.mh.iot.bus.devices.exception;

/**
 * Created by evolshan on 10.12.2020.
 */

public class CommandNotFound extends Exception {

    public CommandNotFound(){ super(); }

    public CommandNotFound(Throwable cause) {
        super(cause);
    }

    public CommandNotFound(String message, Throwable cause) {
        super(message, cause);
    }

    public CommandNotFound(String message) {
        super(message);
    }
}