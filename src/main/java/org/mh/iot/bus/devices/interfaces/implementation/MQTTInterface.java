package org.mh.iot.bus.devices.interfaces.implementation;

import org.mh.iot.bus.devices.interfaces.AsyncSenderInterface;
import org.mh.iot.bus.devices.interfaces.IOTAbstractInterface;
import org.mh.iot.bus.devices.interfaces.ReceiverInterface;
import org.mh.iot.bus.devices.interfaces.exceptions.CannotStopConsumerException;
import org.springframework.stereotype.Component;

/**
 * Created by evolshan on 09.12.2020.
 */
@Component("mqttInterface")
public abstract class MQTTInterface extends IOTAbstractInterface implements AsyncSenderInterface, ReceiverInterface {


    @Override
    public void deleteDurableListener(String connectionString) throws CannotStopConsumerException{
        try {
            removeRoute(connectionString);
        } catch (Exception e) {
            throw new CannotStopConsumerException("Cannot stop route with id: " + connectionString + ". " + e.getMessage(), e);
        }
    }
}
