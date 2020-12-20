package org.mh.iot.bus.devices.interfaces.implementation;

import org.apache.camel.builder.RouteBuilder;
import org.mh.iot.bus.CallbackBean;
import org.mh.iot.bus.devices.interfaces.MessageSelector;
import org.mh.iot.bus.devices.interfaces.exceptions.CannotSendMessage;
import org.mh.iot.bus.devices.interfaces.exceptions.CannotStartConsumerException;
import org.mh.iot.bus.devices.interfaces.exceptions.MessageNotReceived;
import org.springframework.stereotype.Component;

/**
 * Created by evolshan on 09.12.2020.
 */
@Component("mqttTopicTopicInterface")
public class MQTTTopicTopicInterface extends MQTTInterface {


    @Override
    public void sendNotification(String message, String connectionString) throws CannotSendMessage {
        super.sendMessageAsync(message, "activemq:topic:" + connectionString);
    }

    @Override
    public String sendRequest(String message, String requestConnectionString, String replyConnectionString, MessageSelector selector, int timeout) throws CannotSendMessage, MessageNotReceived{
        return super.sendRequestReplyAsync(message, "activemq:topic:" + requestConnectionString, "activemq:topic:" + replyConnectionString, selector, timeout);
    }

    @Override
    public void createDurableListener(String connectionString, CallbackBean statusSaverBean) throws CannotStartConsumerException {
        try {

            addRoutes(new RouteBuilder() {
                @Override
                public void configure() throws Exception {
                    from("activemq:topic:" + connectionString).
                            id(connectionString).
                            bean(statusSaverBean);
                }
            });
        } catch (Exception ex){
            throw new CannotStartConsumerException("Cannot create route with subscription to topic: " + connectionString, ex);
        }
    }
}
