package org.mh.iot.bus.devices.implementation.xiaomi;

/**
 * Created by evolshan on 14.12.2020.
 */
public class XiaomiDeviceType {
    public enum Type {
        XIAOMI_SWITCH,
        XIAOMI_SOCKET,
        UNKNOWN
    }

    public static Type getTypeByModel(String model) {
        switch (model){
            case "ctrl_86plug.aq1":
                return Type.XIAOMI_SOCKET;
            case "ctrl_neutral2":
                return Type.XIAOMI_SWITCH;
            default:
                return Type.UNKNOWN;
        }
    }

}
