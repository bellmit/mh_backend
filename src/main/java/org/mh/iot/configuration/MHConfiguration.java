package org.mh.iot.configuration;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.camel.CamelContext;
import org.apache.camel.builder.DefaultErrorHandlerBuilder;
import org.apache.camel.component.jms.JmsComponent;
import org.apache.camel.impl.DefaultCamelContext;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import javax.jms.ConnectionFactory;

/**
 * Created by evolshan on 14.09.2018.
 */
@Configuration
@ComponentScan(basePackages = {"org.mh.iot"})
public class MHConfiguration {

    @Value("${camel.ActiveMQ.BrokerURL}") String activeMQBrokerURL;

    @Bean
    public ConnectionFactory jmsConnectionFactory(){
        return new ActiveMQConnectionFactory(activeMQBrokerURL);
    }

    @Bean
    public JmsComponent jmsComponent(){
        JmsComponent jmsComponent = JmsComponent.jmsComponentAutoAcknowledge(jmsConnectionFactory());
        jmsComponent.setTestConnectionOnStartup(true);
        return jmsComponent;
    }

    @Bean
    public CamelContext camelContext(){
        CamelContext camelContext = new DefaultCamelContext();
        camelContext.addComponent("activemq", jmsComponent());
        camelContext.setErrorHandlerBuilder(new DefaultErrorHandlerBuilder());
        return camelContext;
    }
}
