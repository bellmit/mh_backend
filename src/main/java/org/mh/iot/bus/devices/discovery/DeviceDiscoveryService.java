package org.mh.iot.bus.devices.discovery;

import org.mh.iot.bus.devices.IOTDevice;
import org.mh.iot.bus.devices.exception.CannotStopDeviceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Created by evolshan on 10.12.2020.
 */
public interface DeviceDiscoveryService<T extends IOTDevice> {
    Logger logger = LoggerFactory.getLogger(DeviceDiscoveryService.class);

    void startDiscovering();
    //<deviceName, IOTDevice>
    Map<String, T> getOnlineDevices();
    void stopDiscovering();

    class CheckIsOnline implements Runnable{
        private IOTDevice device;
        private Map<String, IOTDevice> notAliveDevices;
        private int isAliveRetryCount;
        CheckIsOnline(Map<String, IOTDevice> notAliveDevices, IOTDevice device, int isAliveRetryCount){
            this.isAliveRetryCount = isAliveRetryCount;
            this.device = device;
            this.notAliveDevices = notAliveDevices;
        }

        @Override
        public void run() {
            for (int i = 0; i < isAliveRetryCount; i++){
                if (device.isAlive()){
                    return;
                }
                logger.warn("Is alive retry " + (i + 1));
            }

            notAliveDevices.put(device.getDevice().getName(), device);
            try {
                logger.warn("Device offline: " + device.getDevice().getName());
                device.stop();
            } catch (CannotStopDeviceException e) {
                logger.error("Cannot stop device. " + e.getMessage(), e);
            }

        }
    }


    /**
     * stopes and removes hanging devices
     * @param onlineDevices incoming list
     * @param timeoutSec timeout for awaitTermination in seconds
     * @return new list
     */
    default Map<String, T> removeDownDevices(Map<String, T> onlineDevices, int timeoutSec, int isAliveRetryCount) {
        Map<String, IOTDevice> notAliveDevices = new ConcurrentHashMap<>();

        ExecutorService es = Executors.newCachedThreadPool();
        onlineDevices.entrySet().forEach(item -> {
            es.execute(new CheckIsOnline(notAliveDevices, item.getValue(), isAliveRetryCount));
        });
        es.shutdown();
        try {
            es.awaitTermination(timeoutSec, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            logger.error("Interrupted exception: " + e.getMessage());
        }
        //remove from list
        notAliveDevices.entrySet().forEach(item -> {
            onlineDevices.remove(item.getKey());
        });
        return onlineDevices;
    }

    default void stopAllDevices(Map<String, T> onlineDevices){
        onlineDevices.entrySet().forEach(item -> {
            try {
                item.getValue().stop();
            } catch (CannotStopDeviceException e) {
                logger.error("Cannot stop device. " + e.getMessage(), e);
            }
        });
        onlineDevices.clear();
    }
}
