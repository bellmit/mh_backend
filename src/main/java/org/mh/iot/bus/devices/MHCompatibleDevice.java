package org.mh.iot.bus.devices;

import org.mh.iot.models.Device;

/**
 * Created by evolshan on 11.12.2020.
 */
public interface MHCompatibleDevice<T> {
    Device getDeviceDefinition(T base);
}
