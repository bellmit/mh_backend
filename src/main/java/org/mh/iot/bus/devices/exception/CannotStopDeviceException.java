package org.mh.iot.bus.devices.exception;

/**
 * Created by evolshan on 10.12.2020.
 */

public class CannotStopDeviceException extends Exception {

    public CannotStopDeviceException(){ super(); }

    public CannotStopDeviceException(Throwable cause) {
        super(cause);
    }

    public CannotStopDeviceException(String message, Throwable cause) {
        super(message, cause);
    }

    public CannotStopDeviceException(String message) {
        super(message);
    }
}