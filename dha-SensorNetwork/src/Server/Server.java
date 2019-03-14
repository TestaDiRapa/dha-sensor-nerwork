package Server;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

import static Commons.Constants.*;

public class Server implements Runnable{

    private Map<Identifier, Device> devices;

    public Server(){
        devices = new HashMap<>();
    }

    @Override
    public void run() {
        try {
            MulticastSocket multicastSocket = new MulticastSocket();
            while(true) {
                sendHello(multicastSocket);
                Thread.sleep(20000);
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    private synchronized void sendHello(MulticastSocket socket) throws IOException {
        byte [] payload = "HELLO".getBytes(StandardCharsets.UTF_8);
        DatagramPacket packet = new DatagramPacket(payload, payload.length, InetAddress.getByName(ADDRESS), MULTICAST_PORT);
        socket.send(packet);
    }

    public synchronized void updateValue(InetAddress address, int port, int value) {
        Device d = devices.get(new Identifier(address, port));
        if(d != null){
            d.setLastValueSent(value);
            d.setLastCommunication(Instant.now());
        }
    }
}
