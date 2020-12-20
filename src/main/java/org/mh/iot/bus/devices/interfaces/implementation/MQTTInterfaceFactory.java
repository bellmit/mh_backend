package org.mh.iot.bus.devices.interfaces.implementation;

import org.mh.iot.models.InterfaceType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

/**
 * Created by evolshan on 15.12.2020.
 */
@Component
public class MQTTInterfaceFactory {

    @Autowired
    private MQTTTopicTopicInterface mqttTopicTopicInterface;


    @Autowired
    private MQTTQueueTopicInterface mqttQueueTopicInterface;


    public MQTTInterface getMqttInterface(InterfaceType requestType, InterfaceType responseType){
        if (requestType == InterfaceType.MQTT_TOPIC && responseType == InterfaceType.MQTT_TOPIC)
            return mqttTopicTopicInterface;

        else if (requestType == InterfaceType.MQTT_QUEUE && responseType == InterfaceType.MQTT_TOPIC)
            return mqttQueueTopicInterface;
        else
            throw new NotImplementedException();
    }

}
