package org.mh.iot.udp;



import org.junit.Test;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

/**
 * Created by evolshan on 11.12.2020.
 */
public class udpTest {

    String MULTICAST_ADDRESS = "224.0.0.50";
    int GATEWAY_DISCOVERY_PORT = 4321;
    int MULTICAST_PORT = 9898;

    @Test
    public void sendToGateway() throws IOException {
        DatagramSocket socket = new DatagramSocket();
        String msg = "{\"cmd\":\"whois\"}";
        byte[] buf = msg.getBytes();
        InetAddress address = InetAddress.getByName(MULTICAST_ADDRESS);
        DatagramPacket packet
                = new DatagramPacket(buf, buf.length, address, GATEWAY_DISCOVERY_PORT);
        socket.send(packet);

        socket.setSoTimeout(5000);
        byte[] replyBuf = new byte[1024];
        packet = new DatagramPacket(replyBuf, 1024);
        socket.receive(packet);
        String received = new String(
                packet.getData(), 0, packet.getLength());

    }
}
