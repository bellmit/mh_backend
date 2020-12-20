package org.mh.iot;

import org.mh.iot.bus.BusController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

/**
 * Created by evolshan on 09.12.2020.
 */
@SpringBootApplication(scanBasePackages={"org.mh.iot"})
public class MHMain {

    @Autowired
    private ApplicationContext ctx;

    public static void main(String[] args){
        SpringApplication.run(MHMain.class, args);
    }
}