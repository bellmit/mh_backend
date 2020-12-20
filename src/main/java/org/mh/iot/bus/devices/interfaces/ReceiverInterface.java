package org.mh.iot.bus.devices.interfaces;

import org.mh.iot.bus.CallbackBean;
import org.mh.iot.bus.devices.interfaces.exceptions.CannotStartConsumerException;
import org.mh.iot.bus.devices.interfaces.exceptions.CannotStopConsumerException;

/**
 * Created by evolshan on 09.12.2020.
 */
public interface ReceiverInterface {
    /**
     * creates durable consumer
     * @param connectionString
     * @param statusSaverBean
     */
    void createDurableListener(String connectionString, CallbackBean statusSaverBean) throws CannotStartConsumerException;

    void deleteDurableListener(String connectionString) throws CannotStopConsumerException;

}
