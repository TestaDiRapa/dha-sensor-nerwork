package Server;

import Commons.ResponseParser;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import static Commons.Constants.*;

/**
 * This thread listens for the multicast packets containing the values of the clients and updates the server.
 * Has a reference to the server
 */
public class MulticastListener implements Runnable{

    private Server mainServer;
    private MulticastSocket multicastSocket;
    private boolean running = true;

    MulticastListener(Server mainServer, MulticastSocket multicastSocket) {
        this.mainServer = mainServer;
        this.multicastSocket = multicastSocket;
    }

    /**
     * Main. Receives packets and updates the server if they are valid
     */
    @Override
    public void run() {
        try{
            multicastSocket.joinGroup(InetAddress.getByName(ADDRESS));
            byte[] payload = new byte[MAX];
            DatagramPacket packet = new DatagramPacket(payload, payload.length);

            while (running) {
                multicastSocket.receive(packet);
                Integer param = ResponseParser.parseDataReceiving(packet);
//                if(param != null){
//                    mainServer.updateValue(packet.getAddress(), packet.getPort(), param);
//                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Stops the infinite loop of the main. (It sends a packet to avoid that it waits forever for a packet.
     */
    void stopProtocol() {
        try(MulticastSocket socket = new MulticastSocket()){
            running = false;
            byte[] buffer = "SHUT DOWN".getBytes();
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length, InetAddress.getByName(ADDRESS), MULTICAST_PORT);
            socket.send(packet);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
