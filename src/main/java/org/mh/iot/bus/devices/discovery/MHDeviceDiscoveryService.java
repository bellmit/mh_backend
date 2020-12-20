package org.mh.iot.bus.devices.discovery;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.mh.iot.bus.CallbackBean;
import org.mh.iot.bus.devices.IOTDevice;
import org.mh.iot.bus.devices.exception.CannotInitializeDeviceException;
import org.mh.iot.bus.devices.implementation.MHDevice;
import org.mh.iot.bus.devices.interfaces.exceptions.CannotSendMessage;
import org.mh.iot.bus.devices.interfaces.exceptions.CannotStartConsumerException;
import org.mh.iot.bus.devices.interfaces.exceptions.MessageNotReceived;
import org.mh.iot.bus.devices.interfaces.implementation.MQTTInterface;
import org.mh.iot.bus.devices.interfaces.implementation.MQTTInterfaceFactory;
import org.mh.iot.bus.devices.interfaces.implementation.StatusMessageIdSelector;
import org.mh.iot.bus.exception.DeviceNotOnlineException;
import org.mh.iot.models.Device;
import org.mh.iot.models.InterfaceType;
import org.mh.iot.models.StatusMessage;
import org.mh.iot.models.commands.MHDiscoverCommand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by evolshan on 10.12.2020.
 */
@Component("mhDeviceDiscoveryService")
public class MHDeviceDiscoveryService implements DeviceDiscoveryService<MHDevice>, Runnable {

    private static final Logger logger = LoggerFactory.getLogger(MHDeviceDiscoveryService.class);

    private boolean isStarted = false;

    @Value("${mh.deviceDiscoveryInterval}")
    private int discoveryInterval;

    @Autowired
    private ApplicationContext ctx;

    @Autowired
    private MQTTInterfaceFactory mqttInterfaceFactory;

    @Value("${mh.defaultDeviceReplyTimeout}")
    private int defaultDeviceReplyTimeout;

    @Value("${mh.isAliveRetryCount}")
    private int isAliveRetryCount;

    @Override
    public Map<String, MHDevice> getOnlineDevices(){
        return onlineDevices;
    }

    @Override
    public void run() {
        while(isStarted){
            try {
                onlineDevices = removeDownDevices(onlineDevices, defaultDeviceReplyTimeout * isAliveRetryCount + 5, isAliveRetryCount); // +5 needed for is_alive execution
                Thread.sleep(discoveryInterval * 1000);
            } catch (InterruptedException e) {
                logger.error("Interrupted exception: " + e.getMessage());
            } catch (Exception e){
                logger.error("Unexpected exception: " + e.getMessage());
            }
        }
    }

    private Thread thread = null;

    @Override
    public void startDiscovering() {
        try {
            isStarted = true;
            MQTTInterface discoverInterface = mqttInterfaceFactory.getMqttInterface(InterfaceType.MQTT_TOPIC, InterfaceType.MQTT_TOPIC);
            discoverInterface.createDurableListener("mh.*.STATUS", new MHDeviceDiscoveryService.MQTTDiscovery());
            thread = new Thread(this, "MHDiscSrv");
            thread.start();
        } catch (CannotStartConsumerException e) {
            logger.error("Error by initializing discovery service. " + e.getMessage());
        }
    }

    @Override
    public void stopDiscovering(){
        stopAllDevices(onlineDevices);
        isStarted = false;
    }

    public IOTDevice discoverByModel(Device device) throws DeviceNotOnlineException{

        try {
            MHDevice mhDevice = ctx.getBean(MHDevice.class);
            mhDevice.init(device, null);
            if (mhDevice.isAlive()){
                onlineDevices.put(device.getName(), mhDevice);
                return mhDevice;
            } else {
                throw new DeviceNotOnlineException("Device " + device.getName() + " not online");
            }
        } catch (CannotInitializeDeviceException ex) {
            throw new DeviceNotOnlineException("Cannot discover by saved model. " + ex.getMessage());
        }
    }

    private ObjectMapper objectMapper = new ObjectMapper();
    /**
     * <deviceName, IOTDevice>
     */
    private Map<String, MHDevice> onlineDevices = new ConcurrentHashMap<>();

        private class MQTTDiscovery implements CallbackBean {

        @Override
        public void gotMessage(String message) {
            try {
                MHDiscoverCommand discoverCommand = new MHDiscoverCommand();
                StatusMessage statusMessage = objectMapper.readValue(message, StatusMessage.class);
                if (statusMessage.getRequestId() != null && statusMessage.getRequestId().equals(discoverCommand.getId())){
                    //we have discover reply
                    MHDevice mhDevice = ctx.getBean(MHDevice.class);
                    mhDevice.init(mhDevice.getDeviceDefinition(statusMessage), statusMessage.getCurrentValues());
                    onlineDevices.put(statusMessage.getDevice().getName(), mhDevice);
                    logger.info("Discovered new MH device: " + statusMessage.getDevice().getName());
                    return;
                }
                Device device = statusMessage.getDevice();
                if (statusMessage.getDevice() != null && device.getControlInterface() != null){
                    if (!onlineDevices.containsKey(device.getName())){
                        //send discovery
                        MQTTInterface discoverInterface = mqttInterfaceFactory.getMqttInterface(device.getControlInterface().getType(), InterfaceType.MQTT_TOPIC);
                        discoverInterface.sendNotification(discoverCommand.getJson(), device.getControlInterface().getConnectionString());
                        logger.info("Discovery message sent for device: " + device.getName());
                    }

                }

            } catch (IOException e) {
                logger.error("Got unknown message: " + message);
            } catch (CannotInitializeDeviceException e) {
                logger.error("Error device initialization." + e.getMessage(), e);
            } catch (CannotSendMessage e) {
                logger.error("Cannot send discovery message.", e);
            }
        }
    }
}
