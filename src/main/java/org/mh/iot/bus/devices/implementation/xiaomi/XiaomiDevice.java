package org.mh.iot.bus.devices.implementation.xiaomi;

import org.mh.iot.bus.devices.IOTAbstractDevice;
import org.mh.iot.bus.devices.IOTDevice;
import org.mh.iot.bus.devices.exception.CommandNotFound;
import org.mh.iot.models.DataItem;
import org.mh.iot.models.commands.Command;
import org.mh.iot.models.commands.CommandReply;
import org.mh.iot.models.commands.xiaomi.ReadCommand;
import org.mh.iot.models.commands.xiaomi.XiaomiCommand;
import org.mh.iot.models.commands.xiaomi.reply.XiaomiCommandReply;

import java.util.List;

/**
 * Created by evolshan on 11.12.2020.
 */
public abstract class XiaomiDevice extends IOTAbstractDevice implements IOTDevice {

    @Override
    public boolean isAlive() {
        CommandReply commandReply = sendCommand(new ReadCommand(device.getName()));
        return (commandReply.getCode() == CommandReply.Code.OK);
    }

}
