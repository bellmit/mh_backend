package org.mh.iot.bus.devices;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.mh.iot.bus.devices.exception.CommandNotFound;
import org.mh.iot.models.DataItem;
import org.mh.iot.models.Device;
import org.mh.iot.models.commands.Command;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by evolshan on 10.12.2020.
 */
public abstract class IOTAbstractDevice {
    protected Map<String, String> currentValues = new HashMap<>();
    protected ObjectMapper objectMapper = new ObjectMapper();
    protected Device device;

    public Device getDevice(){
        return device;
    }

    public String getCurrentValue(String parameterName){
        return currentValues.get(parameterName);
    }

    protected void setCurrentValue(String name, String value){
        currentValues.put(name, value);
    }

    public Map<String, String> getCurrentValues(){
        return currentValues;
    }

    public Command buildCommand(String commandName, List<DataItem> data) throws CommandNotFound {
        Command foundCommand = this.device.getCommands().stream().filter(command -> command.getCommand().equals(commandName)).findFirst().orElseThrow(CommandNotFound::new);
        return foundCommand.data(data);
    }

}
