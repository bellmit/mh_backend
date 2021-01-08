package org.mh.iot.bus.devices.implementation;

import org.mh.iot.bus.CallbackBean;
import org.mh.iot.bus.devices.IOTAbstractDevice;
import org.mh.iot.bus.devices.IOTDevice;
import org.mh.iot.bus.devices.MHCompatibleDevice;
import org.mh.iot.bus.devices.exception.CannotInitializeDeviceException;
import org.mh.iot.bus.devices.exception.CannotStopDeviceException;
import org.mh.iot.bus.devices.interfaces.exceptions.CannotSendMessage;
import org.mh.iot.bus.devices.interfaces.exceptions.CannotStopConsumerException;
import org.mh.iot.bus.devices.interfaces.exceptions.MessageNotReceived;
import org.mh.iot.bus.devices.interfaces.implementation.MQTTInterface;
import org.mh.iot.bus.devices.interfaces.implementation.MQTTInterfaceFactory;
import org.mh.iot.bus.devices.interfaces.implementation.StatusMessageIdSelector;
import org.mh.iot.models.Device;
import org.mh.iot.models.StatusMessage;
import org.mh.iot.models.commands.Command;
import org.mh.iot.models.commands.CommandReply;
import org.mh.iot.models.commands.MHIsAliveCommand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;

/**
 * Created by evolshan on 09.12.2020.
 */
@Component("mhDevice")
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class MHDevice extends IOTAbstractDevice implements IOTDevice, MHCompatibleDevice<StatusMessage> {

    private static final Logger logger = LoggerFactory.getLogger(MHDevice.class);

    private Long lastDeviceActivity = null;

    @Value("${mh.defaultDeviceReplyTimeout}")
    private int defaultDeviceReplyTimeout;

    private MQTTInterface mqttInterface;

    @Autowired
    private MQTTInterfaceFactory mqttInterfaceFactory;


    @Override
    public boolean isAlive(){
        if (device.getWakeUpInterval() > 0) { //wake up for timeout devices
            long curTimeInSeconds = System.currentTimeMillis() / 1000;
            return lastDeviceActivity != null && (curTimeInSeconds - lastDeviceActivity) <= (device.getWakeUpInterval() + 20) * 2L;//20 Sec - device connection timeout
        } else { //always alive devices
            MHIsAliveCommand isAliveCommand = new MHIsAliveCommand();
            CommandReply isAliveReply = sendCommand(isAliveCommand);
            return (isAliveReply.getCode() == CommandReply.Code.OK);
        }
    }

    @Override
    public void init(Device device, Map<String, String> initialValues) throws CannotInitializeDeviceException {
        this.device = device;
        if (initialValues != null) {
            lastDeviceActivity = System.currentTimeMillis() / 1000;
            this.currentValues.putAll(initialValues);
        }
        mqttInterface = mqttInterfaceFactory.getMqttInterface(device.getControlInterface().getType(), device.getStatusInterface().getType());
        try {
            mqttInterface.createDurableListener(device.getStatusInterface().getConnectionString(), new StatusBean());
        } catch (Exception ex){
            throw new CannotInitializeDeviceException("Device " + device.getName() + " cannot be initialized!", ex);
        }
    }

    @Override
    public void stop() throws CannotStopDeviceException {
        try {
            mqttInterface.deleteDurableListener(device.getStatusInterface().getConnectionString());
        } catch (CannotStopConsumerException ex){
            throw new CannotStopDeviceException("Error by stoping device " + device.getName() + ". " + ex.getMessage(), ex);
        }
    }

    @Override
    public CommandReply sendCommand(Command command) {
        try {
            //for not "always in run" devices wakeUpInterval used to increase reply timeout
            int timeout = defaultDeviceReplyTimeout + device.getWakeUpInterval();
            //generate command id
            if (command.getId() == null || command.getId().equals("")){
                command.id(UUID.randomUUID().toString());
            }
            String result = mqttInterface.sendRequest(command.getJson(),
                    device.getControlInterface().getConnectionString(),
                    device.getStatusInterface().getConnectionString(),
                    new StatusMessageIdSelector(command.getId()), timeout);


            return new CommandReply().reply(objectMapper.readValue(result, StatusMessage.class)).code(CommandReply.Code.OK);
        }  catch (MessageNotReceived e) {
            return new CommandReply().code(CommandReply.Code.ERROR).message("Message receiving timeout. " + e.getMessage());
        } catch (IOException e) {
            return new CommandReply().code(CommandReply.Code.ERROR).message("Wrong reply format from device. " + e.getMessage());
        } catch (CannotSendMessage e) {
            return new CommandReply().code(CommandReply.Code.ERROR).message("Cannot send message. " + e.getMessage());
        }
    }

    @Override
    public Device getDeviceDefinition(StatusMessage base) {
        return base.getDevice();
    }


    private class StatusBean implements CallbackBean {

        @Override
        public void gotMessage(String message) {
            try {
                lastDeviceActivity = System.currentTimeMillis() / 1000;
                logger.debug("MHDevice: " + device.getName() + " got new message: " + message);

                StatusMessage statusMessage = objectMapper.readValue(message, StatusMessage.class);
                //statusMessage.getCurrentValues().forEach(item -> currentValues.put(item.getName(), item.getValue()));
                currentValues.putAll(statusMessage.getCurrentValues());
            } catch (IOException e) {
                logger.error("Got unknown message: " + message);
            }
        }
    }


}
