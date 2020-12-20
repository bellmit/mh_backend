package org.mh.iot.bus.devices;

import org.mh.iot.bus.devices.exception.CannotInitializeDeviceException;
import org.mh.iot.bus.devices.exception.CannotStopDeviceException;
import org.mh.iot.bus.devices.exception.CommandNotFound;
import org.mh.iot.models.DataItem;
import org.mh.iot.models.commands.CommandReply;
import org.mh.iot.models.Device;
import org.mh.iot.models.commands.Command;

import java.util.List;
import java.util.Map;

/**
 * Created by evolshan on 09.12.2020.
 */
public interface IOTDevice {
    void init(Device device, Map<String, String> initialValues) throws CannotInitializeDeviceException;

    void stop() throws CannotStopDeviceException;

    Device getDevice();

    Map<String, String> getCurrentValues();
    /**
     *
     * @param command
     * @return command reply
     */
    CommandReply sendCommand(Command command);

    Command buildCommand(String commandName, List<DataItem> data) throws CommandNotFound;

    boolean isAlive();
}
