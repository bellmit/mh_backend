package org.mh.iot.bus;

import org.apache.camel.CamelContext;
import org.mh.iot.bus.devices.IOTDevice;
import org.mh.iot.bus.devices.exception.CommandNotFound;
import org.mh.iot.bus.exception.DeviceNotOnlineException;
import org.mh.iot.bus.services.DiscoveryService;
import org.mh.iot.models.Device;
import org.mh.iot.models.commands.Command;
import org.mh.iot.models.commands.CommandReply;
import org.mh.iot.services.UsedDevicesService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

/**
 * Created by evolshan on 09.12.2020.
 */
@Component
public class BusController {

    private static final Logger logger = LoggerFactory.getLogger(BusController.class);

    @Autowired
    private DiscoveryService discoveryService;

    @Autowired
    private CamelContext camelContext;

    @Autowired
    private UsedDevicesService usedDevicesService;

    @Value("${mh.defaultDeviceReplyTimeout}")
    private int defaultDeviceReplyTimeout;

    @PostConstruct
    public void start(){
        discoveryService.start();
        try {
            camelContext.start();
        } catch (Exception e) {
            logger.error("Cannot start camel context");
        }
    }

    public List<Device> getOnlineDevices(){
        return discoveryService.getOnlineDevices().entrySet().stream().map(item -> item.getValue().getDevice()).collect(Collectors.toList());
    }

    @Transactional
    private IOTDevice tryToDiscover(String deviceName) throws DeviceNotOnlineException{
        Optional<Device> foundDevice = usedDevicesService.findByName(deviceName);

        if (foundDevice.isPresent()) {
            return discoveryService.tryToDiscoverByModel(foundDevice.get());
        } else {
            throw new DeviceNotOnlineException("Device not found in db");
        }
    }


    public String getCurrentParameterValue(String deviceName, String parameter) throws DeviceNotOnlineException {
        IOTDevice device = discoveryService.getOnlineDevices().get(deviceName);
        if (device == null){
            device = tryToDiscover(deviceName);
        }
        return device.getCurrentValues().get(parameter);
    }


    public CommandReply executeCommand(String deviceName, Command command) throws DeviceNotOnlineException, CommandNotFound {
        IOTDevice device = discoveryService.getOnlineDevices().get(deviceName);
        if (device == null){
            device = tryToDiscover(deviceName);
        }
        Command builtCommand = device.buildCommand(command.getCommand(), command.getData());
        return device.sendCommand(builtCommand);
    }
}
