package org.mh.iot.bus.devices.discovery;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.mh.iot.bus.devices.IOTDevice;
import org.mh.iot.bus.devices.exception.CannotInitializeDeviceException;
import org.mh.iot.bus.devices.implementation.xiaomi.*;
import org.mh.iot.bus.devices.interfaces.exceptions.CannotSendMessage;
import org.mh.iot.bus.devices.interfaces.exceptions.MessageNotReceived;
import org.mh.iot.bus.devices.interfaces.implementation.UDPInterface;
import org.mh.iot.bus.exception.DeviceNotOnlineException;
import org.mh.iot.models.Device;
import org.mh.iot.models.commands.xiaomi.GetIdListCommand;
import org.mh.iot.models.commands.xiaomi.ReadCommand;
import org.mh.iot.models.commands.xiaomi.WhoisCommand;
import org.mh.iot.models.commands.xiaomi.reply.GetIdListReply;
import org.mh.iot.models.commands.xiaomi.reply.ReadReply;
import org.mh.iot.models.commands.xiaomi.reply.WhoisReply;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by evolshan on 11.12.2020.
 */
@Component("xiaomiDeviceDiscoveryService")
public class XiaomiDiscoveryService implements DeviceDiscoveryService<XiaomiDevice>, Runnable{

    private static final Logger logger = LoggerFactory.getLogger(XiaomiDiscoveryService.class);

    @Autowired
    private ApplicationContext ctx;

    @Autowired
    private UDPInterface udpInterface;

    @Value("${xiaomi.udp.multicastAddress}")
    private String MULTICAST_ADDRESS;

    @Value("${xiaomi.udp.multicastPort}")
    private String MULTICAST_PORT;

    @Value("${xiaomi.udp.gatewayDiscoveryPort}")
    private int GATEWAY_DISCOVERY_PORT;

    private ObjectMapper objectMapper = new ObjectMapper();

    private Map<String, XiaomiDevice> onlineDevices = new HashMap<>();

    @Value("${mh.deviceDiscoveryInterval}")
    private int discoveryInterval;

    @Value("${mh.defaultDeviceReplyTimeout}")
    private int defaultDeviceReplyTimeout;

    @Value("${mh.isAliveRetryCount}")
    private int isAliveRetryCount;

    private Thread thread = null;

    @Override
    public void startDiscovering() {
        isStarted = true;
        thread = new Thread(this, "XiaomiDiscSrv");
        thread.start();
    }

    @Override
    public IOTDevice discoverByModel(Device device) throws DeviceNotOnlineException {
        throw new DeviceNotOnlineException("Not implemented for xiaomi devices");
    }

    @Override
    public Map<String, XiaomiDevice> getOnlineDevices() {
        return onlineDevices;
    }

    @Override
    public void stopDiscovering() {
        stopAllDevices(onlineDevices);
        isStarted = false;
    }

    private boolean isStarted = false;

    @Override
    public void run() {
        while (isStarted) {
            //check online devices
            onlineDevices = removeDownDevices(onlineDevices, defaultDeviceReplyTimeout * isAliveRetryCount + 5, isAliveRetryCount);

            String replyString = "";
            try {
                replyString = udpInterface.sendRequest(new WhoisCommand().getJson(), MULTICAST_ADDRESS + ":" + GATEWAY_DISCOVERY_PORT, s -> true, defaultDeviceReplyTimeout);
                WhoisReply whoisReply = objectMapper.readValue(replyString, WhoisReply.class);
                XiaomiGateway gateway;
                if (!onlineDevices.containsKey(whoisReply.sid)) { //not in list
                    gateway = ctx.getBean(XiaomiGateway.class);
                    gateway.init(gateway.getDeviceDefinition(whoisReply), null);
                    onlineDevices.put(gateway.getDevice().getName(), gateway);
                    logger.info("Discovered new XIAOMI gateway: " + gateway.getDevice().getName());
                } else {
                    gateway = (XiaomiGateway)onlineDevices.get(whoisReply.sid);
                }

                // discover slaves devices
                GetIdListCommand command = new GetIdListCommand();
                replyString = gateway.sendCommand(command).getReply().getUnparsedMessage();
                GetIdListReply getIdListReply = objectMapper.readValue(replyString, GetIdListReply.class);

                String[] slaveDevices = objectMapper.readValue(getIdListReply.data, String[].class);
                for (String slaveDeviceSid : slaveDevices) {
                    if (onlineDevices.containsKey(slaveDeviceSid)) //slave device already initialized
                        continue;
                    ReadCommand readCommand = new ReadCommand(slaveDeviceSid);
                    replyString = gateway.sendCommand(readCommand).getReply().getUnparsedMessage();
                    ReadReply readReply = objectMapper.readValue(replyString, ReadReply.class);
                    XiaomiSlaveDevice slaveDevice;
                    switch (XiaomiDeviceType.getTypeByModel(readReply.model)) {
                        case XIAOMI_SOCKET: {
                            slaveDevice = ctx.getBean(XiaomiSocket.class);
                            break;
                        }
                        case XIAOMI_SWITCH: {
                            slaveDevice = ctx.getBean(XiaomiSwitch.class);
                            break;
                        }
                        default: {
                            logger.error("Connected unknown device. Model: " + readReply.model);
                            slaveDevice = null;
                            break;
                        }
                    }
                    if (slaveDevice != null) {
                        slaveDevice.useGateway(gateway);
                        TypeReference<HashMap<String, String>> typeRef = new TypeReference<HashMap<String, String>>() {};
                        Map<String, String> currentValues = null;
                        try {
                            currentValues = objectMapper.readValue(readReply.data, typeRef);
                        } catch (IOException e) {
                            logger.error("Unexpected message data format: " + readReply.data);
                        }

                        slaveDevice.init(slaveDevice.getDeviceDefinition(readReply), currentValues);
                        onlineDevices.put(slaveDevice.getDevice().getName(), slaveDevice);
                        logger.info("Discovered new slave XIAOMI device: " + slaveDevice.getDevice().getName() + ". Model: " + readReply.model);
                    }
                }
            } catch (CannotSendMessage e) {
                logger.error("Cannot send XIAOMI gateway discovery command. " + e.getMessage());
            } catch (MessageNotReceived messageNotReceived) {
                logger.error("Cannot find gateway, message not received");
            } catch (IOException e) {
                logger.error("Got unknown reply from gateway: " + replyString);
            } catch (CannotInitializeDeviceException e) {
                logger.error("Error gateway initialization." + e.getMessage(), e);
            }
            try {
                Thread.sleep(discoveryInterval * 1000);
            } catch (InterruptedException e) {
                logger.error("Interrupted exception: " + e.getMessage());
            }
        }

    }
}
