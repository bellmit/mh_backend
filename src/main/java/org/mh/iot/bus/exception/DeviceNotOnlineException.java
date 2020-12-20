package org.mh.iot.bus.exception;

/**
 * Created by evolshan on 10.12.2020.
 */

public class DeviceNotOnlineException extends Exception {

    public DeviceNotOnlineException(){ super(); }

    public DeviceNotOnlineException(Throwable cause) {
        super(cause);
    }

    public DeviceNotOnlineException(String message, Throwable cause) {
        super(message, cause);
    }

    public DeviceNotOnlineException(String message) {
        super(message);
    }
}