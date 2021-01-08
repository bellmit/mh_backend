package org.mh.iot.bus.devices.implementation.xiaomi;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import org.mh.iot.bus.CallbackBean;
import org.mh.iot.bus.devices.IOTAbstractDevice;
import org.mh.iot.bus.devices.MHCompatibleDevice;
import org.mh.iot.bus.devices.exception.CannotInitializeDeviceException;
import org.mh.iot.bus.devices.exception.CannotStopDeviceException;
import org.mh.iot.bus.devices.interfaces.ReceiverInterface;
import org.mh.iot.bus.devices.interfaces.exceptions.CannotSendMessage;
import org.mh.iot.bus.devices.interfaces.exceptions.CannotStartConsumerException;
import org.mh.iot.bus.devices.interfaces.exceptions.CannotStopConsumerException;
import org.mh.iot.bus.devices.interfaces.exceptions.MessageNotReceived;
import org.mh.iot.bus.devices.interfaces.implementation.UDPInterface;
import org.mh.iot.models.*;
import org.mh.iot.models.commands.Command;
import org.mh.iot.models.commands.CommandReply;
import org.mh.iot.models.commands.xiaomi.GetIdListCommand;
import org.mh.iot.models.commands.xiaomi.ReadCommand;
import org.mh.iot.models.commands.xiaomi.WhoisCommand;
import org.mh.iot.models.commands.xiaomi.XiaomiCommand;
import org.mh.iot.models.commands.xiaomi.reply.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Created by evolshan on 11.12.2020.
 */
@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class XiaomiGateway extends XiaomiDevice implements MHCompatibleDevice<WhoisReply>, ReceiverInterface {

    private static final Logger logger = LoggerFactory.getLogger(XiaomiGateway.class);

    @Autowired
    private UDPInterface udpInterface;

    @Value("${xiaomi.udp.multicastAddress}")
    private String MULTICAST_ADDRESS;

    @Value("${xiaomi.udp.multicastPort}")
    private String MULTICAST_PORT;

    @Value("${mh.defaultDeviceReplyTimeout}")
    private int defaultDeviceReplyTimeout;

    @Value("${xiaomi.udp.password}")
    private String password;

    @Value("${xiaomi.commandRetryCount}")
    private int commandRetryCount;

    @Value("${xiaomi.commandRetryInterval}")
    private int commandRetryInterval;

    private Optional<String> key = Optional.empty();

    private Cipher cipher;
    private static final byte[] IV =
            {     0x17, (byte)0x99, 0x6d, 0x09, 0x3d, 0x28, (byte)0xdd, (byte)0xb3,
                    (byte)0xba,       0x69, 0x5a, 0x2e, 0x6f, 0x58,       0x56,       0x2e};


    private void configureCipher(String password){
        try {
            cipher = Cipher.getInstance("AES/CBC/NoPadding");
            final SecretKeySpec keySpec = new SecretKeySpec(password.getBytes(), "AES");
            final IvParameterSpec ivSpec = new IvParameterSpec(IV);
            cipher.init(Cipher.ENCRYPT_MODE, keySpec, ivSpec);
        } catch (Exception e) {
            throw new RuntimeException("Cypher configuration exception: " + e);
        }
    }

    private String keyToHexString(byte[] bytes){
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02X", b));
        }
        return sb.toString();
    }


    private void updateKey(String token) {
        if(cipher != null) {
            try {
                String keyAsHexString = keyToHexString(cipher.doFinal(token.getBytes(StandardCharsets.US_ASCII)));
                key = Optional.of(keyAsHexString);
            } catch (Exception e) {
                throw new RuntimeException("Cypher configuration exception: " + e);
            }
        } else {
            throw new RuntimeException("Unable to update key with not set cipher");
        }
    }

    @Override
    public void init(Device device, Map<String, String> initialValues) throws CannotInitializeDeviceException {
        this.device = device;
        if (initialValues != null)
            this.currentValues.putAll(initialValues);
        configureCipher(password);
        try {
            udpInterface.createDurableListener(MULTICAST_ADDRESS + ":" + MULTICAST_PORT, new StatusCallbackBean());
        } catch (CannotStartConsumerException e) {
            throw new CannotInitializeDeviceException("Device " + device.getName()  + "cannot be initialized!", e);
        }
    }

    private boolean isMyself(String sid) {
        return sid.equals(this.device.getName());
    }

    Map<String, CallbackBean> slaveDevicesCallback = new HashMap<>();

    /**
     * used to forward messages to slave devices
     * @param connectionString
     * @param statusSaverBean
     * @throws CannotStartConsumerException
     */
    @Override
    public void createDurableListener(String connectionString, CallbackBean statusSaverBean) throws CannotStartConsumerException {
        slaveDevicesCallback.put(connectionString, statusSaverBean);
    }

    @Override
    public void deleteDurableListener(String connectionString) throws CannotStopConsumerException { //remove slave device callback
        slaveDevicesCallback.remove(connectionString);
    }

    @Override
    public void stop() throws CannotStopDeviceException {
        try {
            udpInterface.deleteDurableListener(null);
        } catch (CannotStopConsumerException e) {
            throw new CannotStopDeviceException("Cannot stop udp durable listener. " + e.getMessage(), e);
        }
    }

    private class StatusCallbackBean implements CallbackBean{

        @Override
        public void gotMessage(String message) {
            try {
                TypeReference<HashMap<String, String>> typeRefMap = new TypeReference<HashMap<String, String>>(){};
                ReadReply reply = objectMapper.readValue(message, ReadReply.class);
                logger.debug("Gateway got message: " + message);

                switch(reply.cmd) {
                    case "report":
                        if (isMyself(reply.sid)) {
                            Report report = objectMapper.readValue(message, Report.class);
                            //"data":"{\"rgb\":0,\"illumination\":791}"
                            Map<String, String> data = objectMapper.readValue(report.data, typeRefMap);
                            currentValues.putAll(data);
                        } else {
                           //send message to connected devices state changed
                            CallbackBean callbackBean = slaveDevicesCallback.get(reply.sid);
                            if (callbackBean != null)
                                callbackBean.gotMessage(message);
                        }
                        break;
                    case "heartbeat":
                        if (isMyself(reply.sid)) {
                            GatewayHeartbeat gatewayHeartbeat = objectMapper.readValue(message, GatewayHeartbeat.class);
                            updateKey(gatewayHeartbeat.token);
                        } else {
                            //send status message slave device heartbeat
                            CallbackBean callbackBean = slaveDevicesCallback.get(reply.sid);
                            if (callbackBean != null)
                                callbackBean.gotMessage(message);
                        }
                        break;
                    default:
                        logger.warn("Unexpected gateway status: " + reply.cmd);
                }

            }  catch (IOException e) {
                logger.error("Got unknown message: " + message);
            }
        }
    }

    private StatusMessage buildStatusMessage(){
        return new StatusMessage()
                .device(device);
    }

    @Override
    public CommandReply sendCommand(Command command) {
        try {
            if (!(command instanceof XiaomiCommand)){ //find command by name (request by command name), otherwise command is build by discovery service or slave device
                String commandName = command.getCommand();
                if (commandName != null && !commandName.isEmpty()){
                    command = device.getCommands().stream().filter(item -> item.getCommand().equals(commandName)).findFirst().orElse(null);
                }
                if (command == null){
                    return new CommandReply().code(CommandReply.Code.ERROR).message("Unknown command. " + commandName);
                }
            }

            String unparsedMessage = "";
            for (int i = -1; i < commandRetryCount; i++) { //retrying. if commandRetryCount == 0 then only 1 request
                //add key value
                String json = command.getJson();
                if (json.contains("${KEY}") && key.isPresent()){
                    json = json.replace("${KEY}", key.get());
                }
                unparsedMessage = udpInterface.sendRequest(json, device.getControlInterface().getConnectionString(), s -> true, defaultDeviceReplyTimeout);
                if (unparsedMessage.contains("error")){
                    logger.error("Gateway command error: " + unparsedMessage);
                } else {
                    return new CommandReply().reply(buildStatusMessage().unparsedMessage(unparsedMessage)).code(CommandReply.Code.OK);
                }
                if (unparsedMessage.contains("Invalid key")) {
                    // update key
                    CommandReply commandReply = sendCommand(new GetIdListCommand());

                    if (commandReply.getCode() == CommandReply.Code.OK) {
                        GetIdListReply getIdListReply = objectMapper.readValue(commandReply.getReply().getUnparsedMessage(), GetIdListReply.class);
                        updateKey(getIdListReply.token);
                    }
                }
                Thread.sleep(commandRetryInterval * 1000L);
            }
            return new CommandReply().code(CommandReply.Code.ERROR).message(unparsedMessage);

        } catch (CannotSendMessage e) {
            return new CommandReply().code(CommandReply.Code.ERROR).message("Cannot send message. " + e.getMessage());
        } catch (MessageNotReceived e) {
            return new CommandReply().code(CommandReply.Code.ERROR).message("Message receiving timeout. " + e.getMessage());
        } catch (InterruptedException e) {
            return new CommandReply().code(CommandReply.Code.ERROR).message("Command retry exception. " + e.getMessage());
        } catch (IOException e) {
            return new CommandReply().code(CommandReply.Code.ERROR).message("Cannot update key. " + e.getMessage());

        }
    }

    @Override
    public Device getDeviceDefinition(WhoisReply base) {
        return new Device().
                name(base.sid).
                ip(base.ip).
                type("XIAOMI_" + base.model).
                firmware(base.proto_version).
                controlInterface((new Interface()).type(InterfaceType.UDP).connectionString(base.ip + ":" + MULTICAST_PORT)).
                statusInterface((new Interface()).type(InterfaceType.UDP).connectionString(MULTICAST_ADDRESS + ":" + MULTICAST_PORT)).
                commands(
                        Arrays.asList(new WhoisCommand().command("WHO_IS")) //ToDo add real command
                )
                .parameters(Arrays.asList(new Parameter().name("ALERT").type(ParameterType.ON_OFF)));
    }
}
