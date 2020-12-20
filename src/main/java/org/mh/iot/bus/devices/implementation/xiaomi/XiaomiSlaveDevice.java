package org.mh.iot.bus.devices.implementation.xiaomi;

import com.fasterxml.jackson.core.type.TypeReference;
import org.mh.iot.bus.CallbackBean;
import org.mh.iot.bus.devices.MHCompatibleDevice;
import org.mh.iot.bus.devices.exception.CannotInitializeDeviceException;
import org.mh.iot.bus.devices.exception.CannotStopDeviceException;
import org.mh.iot.bus.devices.interfaces.exceptions.CannotStartConsumerException;
import org.mh.iot.bus.devices.interfaces.exceptions.CannotStopConsumerException;
import org.mh.iot.models.Device;
import org.mh.iot.models.commands.Command;
import org.mh.iot.models.commands.CommandReply;
import org.mh.iot.models.commands.xiaomi.reply.ReadReply;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by evolshan on 13.12.2020.
 */
public abstract class XiaomiSlaveDevice extends  XiaomiDevice implements MHCompatibleDevice<ReadReply> {

    public void useGateway(XiaomiGateway gateway) {
        this.gateway = gateway;
    }

    private static final Logger logger = LoggerFactory.getLogger(XiaomiSlaveDevice.class);

    private XiaomiGateway gateway = null;

    @Override
    public CommandReply sendCommand(Command command) {
        if (gateway == null){
            throw new RuntimeException("Gateway not initialized. Call useGateway method");
        }
        String commandName = command.getCommand();
        if (commandName != null && !commandName.isEmpty()){
            command = device.getCommands().stream().filter(item -> item.getCommand().equals(commandName)).findFirst().orElse(null);
        }
        if (command == null){
            return new CommandReply().code(CommandReply.Code.ERROR).message("Unknown command. " + commandName);
        }
        return gateway.sendCommand(command);
    }

    @Override
    public void init(Device device, Map<String, String> initialValues) throws CannotInitializeDeviceException {
        this.device = device;
        if (initialValues != null)
            this.currentValues.putAll(initialValues);
        try {
            if (this.gateway == null){
                throw new CannotInitializeDeviceException("Gateway not initialized. Call useGateway method");
            }
            this.gateway.createDurableListener(this.device.getName(), new StatusCheckerBean());
        } catch (CannotStartConsumerException e) {
            throw new CannotInitializeDeviceException("Device " + device.getName() + " cannot be initialized!", e);
        }
    }

    private class StatusCheckerBean implements CallbackBean {

        @Override
        public void gotMessage(String message) {
            try {
                ReadReply readReply = objectMapper.readValue(message, ReadReply.class);
                if (readReply.sid.equals(device.getName())){
                    //{\"channel_0\":\"on\"}
                    TypeReference<HashMap<String, String>> typeRef = new TypeReference<HashMap<String, String>>(){};
                    Map<String, String> data = objectMapper.readValue(readReply.data, typeRef);
                    currentValues.putAll(data);
                } else {
                    logger.error("Wrong sid. Report not for expected device");
                }
            } catch (IOException e) {
                logger.error("Unexpected message: " + message);
            }
        }
    }

    @Override
    public void stop() throws CannotStopDeviceException {
        try {
            gateway.deleteDurableListener(device.getName());
        } catch (CannotStopConsumerException e) {
            throw new CannotStopDeviceException("Cannot stop xiaomi slave device. " + e.getMessage(), e);
        }
    }
}
