package org.mh.iot.bus.devices.interfaces.implementation;

import org.mh.iot.bus.CallbackBean;
import org.mh.iot.bus.devices.interfaces.MessageSelector;
import org.mh.iot.bus.devices.interfaces.ReceiverInterface;
import org.mh.iot.bus.devices.interfaces.SyncSenderInterface;
import org.mh.iot.bus.devices.interfaces.exceptions.CannotSendMessage;
import org.mh.iot.bus.devices.interfaces.exceptions.CannotStartConsumerException;
import org.mh.iot.bus.devices.interfaces.exceptions.CannotStopConsumerException;
import org.mh.iot.bus.devices.interfaces.exceptions.MessageNotReceived;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by evolshan on 11.12.2020.
 */
@Component("udpTwoWayInterface")
public class UDPInterface implements ReceiverInterface, SyncSenderInterface {

    private static final Logger logger = LoggerFactory.getLogger(UDPInterface.class);

    @Value("${xiaomi.udp.defaultUDPIncomingTimeout}")
    private int defaultUDPIncomingTimeout;

    private class DurableListener implements Runnable{
        private boolean isReceiving = false;
        private String group;
        private int port;
        private CallbackBean callbackBean;
        private MulticastSocket socket;

        DurableListener(String connectionString, CallbackBean statusSaverBean) throws URISyntaxException, IOException {
            URI url = new URI("my://" + connectionString); //workaround to parse ip:port
            this.group = url.getHost();
            this.port = url.getPort();
            this.callbackBean = statusSaverBean;
            this.socket = new MulticastSocket(this.port);
            this.socket.setSoTimeout(defaultUDPIncomingTimeout * 1000);
            this.socket.joinGroup(InetAddress.getByName(group));
            isReceiving = true;
        }

        private String receive() throws IOException {
            byte buffer[] = new byte[1024];
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
            socket.receive(packet);
            return new String(buffer, 0, packet.getLength());
        }

        @Override
        public void run() {
            while (isReceiving){
                try {
                    String received = receive();
                    callbackBean.gotMessage(received);
                } catch (IOException e) {
                }
            }

        }

        void stop(){
            isReceiving = false;
            socket.close();
        }
    }

    private DurableListener listener = null;

    @Override
    public void createDurableListener(String connectionString, CallbackBean statusSaverBean) throws CannotStartConsumerException {
        try {
            ExecutorService deviceListenerThreadPool = Executors.newFixedThreadPool(1);
            listener = new DurableListener(connectionString, statusSaverBean);
            deviceListenerThreadPool.execute(listener);
            deviceListenerThreadPool.shutdown(); //fixing pool, no more threads accepted
        } catch (URISyntaxException e) {
            listener.stop();
            throw new CannotStartConsumerException("Wrong connection string. Should be ip:port. " + connectionString, e);
        } catch (IOException e) {
            listener.stop();
            throw new CannotStartConsumerException("Socket initialization error. " + e.getMessage(), e);
        } catch (Exception e) {
            listener.stop();
            throw new CannotStartConsumerException("Unknown exception. " + e.getMessage(), e);
        }
    }

    @Override
    public void deleteDurableListener(String connectionString) throws CannotStopConsumerException {
        if (listener != null)
            listener.stop();
    }


    @Override
    public void sendNotification(String message, String connectionString) throws CannotSendMessage {
        try {
            DatagramSocket socket = new DatagramSocket();
            sendNotification(socket, message, connectionString);
            socket.close();
        } catch (SocketException e) {
            throw new CannotSendMessage("Cannot open socket. ", e);
        }
    }

    private void sendNotification(DatagramSocket socket, String message, String connectionString) throws CannotSendMessage{
        try {
            URI url = new URI("my://" + connectionString); //workaround to parse ip:port
            InetSocketAddress address = new InetSocketAddress(url.getHost(), url.getPort());
            DatagramPacket packet = new DatagramPacket(message.getBytes(), message.length(), address);
            socket.send(packet);
        } catch (IOException ex) {
            throw new CannotSendMessage("IO error by sending message " + connectionString, ex);
        } catch (URISyntaxException ex) {
            throw new CannotSendMessage("Wrong connection string. Should be ip:port. " + connectionString, ex);
        }
    }

    @Override
    public String sendRequest(String message, String connectionString, MessageSelector selector, int timeout) throws CannotSendMessage, MessageNotReceived {
        sendNotification(message, connectionString);
        try {
            byte buffer[] = new byte[1024];
            DatagramSocket socket = new DatagramSocket();
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
            socket.setSoTimeout(timeout * 1000);
            sendNotification(socket, message, connectionString);
            socket.receive(packet);
            String response = new String(buffer, 0, packet.getLength());
            socket.close();
            if (selector.test(response))
                return response;
            else
                throw new MessageNotReceived("Message for MessageSelector: " + selector.toString() + "not received");

        } catch (IOException e) {
            throw new MessageNotReceived("IO socket exception. Message not received.", e);
        }
    }

}
