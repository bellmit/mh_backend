package org.mh.iot.bus.devices.exception;

/**
 * Created by evolshan on 10.12.2020.
 */

public class CannotInitializeDeviceException extends Exception {

    public CannotInitializeDeviceException(){ super(); }

    public CannotInitializeDeviceException(Throwable cause) {
        super(cause);
    }

    public CannotInitializeDeviceException(String message, Throwable cause) {
        super(message, cause);
    }

    public CannotInitializeDeviceException(String message) {
        super(message);
    }
}