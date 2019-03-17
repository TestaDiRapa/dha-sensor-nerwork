package Server;

import java.io.IOException;
import java.net.*;

import static Commons.Constants.*;
import static Commons.ResponseParser.isAlive;

/**
 * This thread checks for the unicast "ALIVE" messages from the devices. Has an instance of the server
 */
public class AliveChecker implements Runnable {

    private Server server;
    private boolean running = true;

    AliveChecker(Server server) {
        this.server = server;
    }

    /**
     * Main. Receives a packet and, if it is an "ALIVE" packet, updates the server
     */
    @Override
    public void run() {
        try(DatagramSocket socket = new DatagramSocket(PORT)){

            byte[] buffer = new byte[MAX];
            DatagramPacket bufferPacket = new DatagramPacket(buffer, buffer.length);
            while(running) {
                socket.receive(bufferPacket);
                int type = isAlive(bufferPacket);
                if(type >= 0){
                    server.updateAliveDevice(bufferPacket.getAddress(), bufferPacket.getPort(), type);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Stops the infinite loop of the main. (It sends a packet to avoid that it waits forever for a packet.
     */
    public void closeProtocol() {
        try(DatagramSocket socket = new DatagramSocket()){
            running = false;
            byte[] buffer = "SHUT DOWN".getBytes();
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length, InetAddress.getByName(ADDRESS), PORT);
            socket.send(packet);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}