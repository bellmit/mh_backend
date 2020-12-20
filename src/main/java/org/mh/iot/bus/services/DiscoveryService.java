package org.mh.iot.bus.services;

import org.mh.iot.bus.devices.IOTDevice;
import org.mh.iot.bus.devices.discovery.MHDeviceDiscoveryService;
import org.mh.iot.bus.devices.discovery.XiaomiDiscoveryService;
import org.mh.iot.bus.devices.implementation.xiaomi.XiaomiDevice;
import org.mh.iot.bus.exception.DeviceNotOnlineException;
import org.mh.iot.models.Device;
import org.mh.iot.models.commands.Command;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * Created by evolshan on 09.12.2020.
 * discovering all devices
 */
@Component
public class DiscoveryService {

    @Autowired
    private ApplicationContext ctx;

    private static final Logger logger = LoggerFactory.getLogger(DiscoveryService.class);

    @Autowired
    private MHDeviceDiscoveryService mhDeviceDiscoveryService;

    @Autowired
    private XiaomiDiscoveryService xiaomiDiscoveryService;

    public Map<String, IOTDevice> getOnlineDevices(){
        Map<String, IOTDevice> onlineDevices = new HashMap<>();
        onlineDevices.putAll(mhDeviceDiscoveryService.getOnlineDevices());
        onlineDevices.putAll(xiaomiDiscoveryService.getOnlineDevices());
        return onlineDevices;
    }

    public void start(){
        mhDeviceDiscoveryService.startDiscovering();
        xiaomiDiscoveryService.startDiscovering();
    }

    public void end(){
        mhDeviceDiscoveryService.stopDiscovering();
        xiaomiDiscoveryService.stopDiscovering();
    }

    public IOTDevice tryToDiscoverByModel(Device device) throws DeviceNotOnlineException {
        if (device.getType().toUpperCase().startsWith("MH")) //for MH devices
            return mhDeviceDiscoveryService.discoverByModel(device);
         else if (device.getType().toUpperCase().startsWith("XIAOMI"))
            return xiaomiDiscoveryService.discoverByModel(device);
         else
            throw new DeviceNotOnlineException("Not known device type");
    }
}
