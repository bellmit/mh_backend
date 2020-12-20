package org.mh.iot.services;

import org.mh.iot.models.Device;

import java.util.List;
import java.util.Optional;

/**
 * Created by evolshan on 24.10.2018.
 */
public interface UsedDevicesService {
    Device saveDevice(Device deviceModel);
    List<Device> getUsedDevices();
    Optional<Device> findByName(String name);
    void deleteDevice(String deviceName);
}
