package org.mh.iot.bus.devices.implementation.xiaomi;

import com.fasterxml.jackson.core.type.TypeReference;
import org.apache.camel.json.simple.JsonObject;
import org.mh.iot.bus.CallbackBean;
import org.mh.iot.bus.devices.exception.CannotInitializeDeviceException;
import org.mh.iot.bus.devices.exception.CannotStopDeviceException;
import org.mh.iot.bus.devices.interfaces.exceptions.CannotStartConsumerException;
import org.mh.iot.bus.devices.interfaces.exceptions.CannotStopConsumerException;
import org.mh.iot.models.*;
import org.mh.iot.models.commands.Command;
import org.mh.iot.models.commands.CommandReply;
import org.mh.iot.models.commands.xiaomi.WriteCommand;
import org.mh.iot.models.commands.xiaomi.reply.ReadReply;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by evolshan on 13.12.2020.
 */
@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class XiaomiSwitch extends XiaomiSlaveDevice {


    private Command turnOffChannel0 = null;
    private Command turnOnChannel0 = null;
    private Command turnOffChannel1 = null;
    private Command turnOnChannel1 = null;

    @Override
    public CommandReply sendCommand(Command command) {
        Command commandToSend = command;
        if (command.getCommand() != null) {
            if (command.getCommand().equals("TOGGLE_CHANNEL0")) {
                if (currentValues.get("channel_0").toUpperCase().equals("ON")) {
                    commandToSend = turnOffChannel0;
                } else {
                    commandToSend = turnOnChannel0;
                }
            }
            if (command.getCommand().equals("TOGGLE_CHANNEL1")) {
                if (currentValues.get("channel_1").toUpperCase().equals("ON")) {
                    commandToSend = turnOffChannel1;
                } else {
                    commandToSend = turnOnChannel1;
                }
            }
        }
        return super.sendCommand(commandToSend);
    }

    @Override
    public Device getDeviceDefinition(ReadReply base) {
        turnOffChannel0 = new WriteCommand(base.sid, new JsonObject(new HashMap<String, String>() {{
            put("channel_0", "off");
        }})).command("TURN_OFF_CHANNEL0");
        turnOnChannel0 = new WriteCommand(base.sid, new JsonObject(new HashMap<String, String>() {{
            put("channel_0", "on");
        }})).command("TURN_ON_CHANNEL0");
        turnOffChannel1 = new WriteCommand(base.sid, new JsonObject(new HashMap<String, String>() {{
            put("channel_1", "off");
        }})).command("TURN_OFF_CHANNEL1");
        turnOnChannel1 = new WriteCommand(base.sid, new JsonObject(new HashMap<String, String>() {{
            put("channel_1", "on");
        }})).command("TURN_ON_CHANNEL1");

        return new Device().
                name(base.sid).
                firmware(base.model).
                type("XIAOMI_" + base.model).
                controlInterface((new Interface()).type(InterfaceType.XIAOMI_GATEWAY).connectionString(base.sid)).
                statusInterface((new Interface()).type(InterfaceType.XIAOMI_GATEWAY).connectionString(base.sid)).
                commands(
                        Arrays.asList(
                            turnOffChannel0,
                            turnOnChannel0,
                            turnOffChannel1,
                            turnOnChannel1,
                            new Command().command("TOGGLE_CHANNEL0"),
                            new Command().command("TOGGLE_CHANNEL1")
                        )
                )
                .parameters(Arrays.asList(
                        new Parameter().name("channel_0").type(ParameterType.ON_OFF),
                        new Parameter().name("channel_1").type(ParameterType.ON_OFF)
                        ));
    }

}
