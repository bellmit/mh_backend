package org.mh.iot.bus.devices.implementation.xiaomi;

import org.apache.camel.json.simple.JsonObject;
import org.mh.iot.models.*;
import org.mh.iot.models.commands.Command;
import org.mh.iot.models.commands.CommandReply;
import org.mh.iot.models.commands.xiaomi.WriteCommand;
import org.mh.iot.models.commands.xiaomi.reply.ReadReply;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.HashMap;

/**
 * Created by evolshan on 13.12.2020.
 */
@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class XiaomiSocket extends XiaomiSlaveDevice {

    //private static final Logger logger = LoggerFactory.getLogger(XiaomiSwitch.class);

    private Command turnOn = null;
    private Command turnOff = null;

    @Override
    public CommandReply sendCommand(Command command) {
        Command commandToSend = command;
        if (command.getCommand() != null) {
            if (command.getCommand().equals("TOGGLE")) {
                if (currentValues.get("status").toUpperCase().equals("ON")) {
                    commandToSend = turnOff;
                } else {
                    commandToSend = turnOn;
                }
            }
        }
        return super.sendCommand(commandToSend);
    }

    @Override
    public Device getDeviceDefinition(ReadReply base) {
        turnOn = new WriteCommand(base.sid, new JsonObject(new HashMap<String, String>() {{
            put("status", "on");
        }})).command("TURN_ON");

        turnOff = new WriteCommand(base.sid, new JsonObject(new HashMap<String, String>() {{
            put("status", "off");
        }})).command("TURN_OFF");

        return new Device().
                name(base.sid).
                firmware(base.model).
                type("XIAOMI_" + base.model).
                controlInterface((new Interface()).type(InterfaceType.XIAOMI_GATEWAY).connectionString(base.sid)).
                statusInterface((new Interface()).type(InterfaceType.XIAOMI_GATEWAY).connectionString(base.sid)).
                commands(
                        Arrays.asList(
                        turnOn,
                        turnOff,
                        new Command().command("TOGGLE")
                        )
                )
                .parameters(Arrays.asList(
                        new Parameter().name("status").type(ParameterType.ON_OFF),
                        new Parameter().name("inuse").type(ParameterType.BOOLEAN),
                        new Parameter().name("power_consumed").type(ParameterType.NUMBER),
                        new Parameter().name("load_power").type(ParameterType.NUMBER)
                        ));
    }

}
