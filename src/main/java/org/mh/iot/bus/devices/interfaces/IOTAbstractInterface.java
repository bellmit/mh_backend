package org.mh.iot.bus.devices.interfaces;

import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.FluentProducerTemplate;
import org.apache.camel.PollingConsumer;
import org.apache.camel.builder.RouteBuilder;
import org.mh.iot.bus.devices.interfaces.exceptions.CannotSendMessage;
import org.mh.iot.bus.devices.interfaces.exceptions.MessageNotReceived;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Future;

/**
 * Created by evolshan on 09.12.2020.
 */
public abstract class IOTAbstractInterface {

    @Autowired
    private CamelContext camelContext;

    Map<String, PollingConsumer> pollingConsumers = new HashMap<>();

    /**
     * sends plain message to defined destination
     * @param message
     * @param to - destination string
     */
    protected Future<Exchange> sendMessageAsync(Object message, String to) {
        FluentProducerTemplate template = camelContext.createFluentProducerTemplate();
        template.withBody(message).to(to);
        return template.asyncSend();
    }

    /**
     * @param routeBuilder builder
     * @throws Exception
     */
    protected void addRoutes(RouteBuilder routeBuilder) throws Exception {
        //add to camel context
        camelContext.addRoutes(routeBuilder);
    }

    protected void removeRoute(String routeId) throws Exception {
        camelContext.stopRoute(routeId);
        camelContext.removeRoute(routeId);
    }


    protected String sendRequestReplyAsync(Object message, String to, String from, MessageSelector messageSelector, int timeout) throws CannotSendMessage, MessageNotReceived {
        long curTime = System.currentTimeMillis();
        long waitingTime = timeout * 1000;
        PollingConsumer consumer = pollingConsumers.get(from);
        try {
            if (consumer == null) {
                consumer = camelContext.getEndpoint(from).
                        createPollingConsumer();
                consumer.start();
                pollingConsumers.put(from, consumer);
            }
        } catch (Exception ex){
            throw new CannotSendMessage("Cannot send message to: " + to);
        }

        sendMessageAsync(message, to);
        while (waitingTime > 0){

            Exchange receive = consumer.receive(waitingTime);
            if (receive == null)
                throw new MessageNotReceived("Message for MessageSelector: " + messageSelector.toString() + "not received");
            String reply = receive.getIn().getBody(String.class);
            if (reply == null){
                throw new MessageNotReceived("Message for MessageSelector: " + messageSelector.toString() + "not received");
            }
            if (messageSelector.test(reply)){
                return reply;
            } else { // reduce waiting time
                waitingTime = waitingTime - (System.currentTimeMillis() - curTime);
                curTime = System.currentTimeMillis();
            }
        }
        throw new MessageNotReceived("Message for MessageSelector: " + messageSelector.toString() + "not received");

    }
}
