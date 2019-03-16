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

    private final Map<Identifier, Device> devices = new HashMap<>();

    @Override
    public void run() {
        try {
            MulticastSocket multicastSocket = new MulticastSocket();

            AliveChecker aliveChecker = new AliveChecker(this);
            MulticastListener multicastListener = new MulticastListener(this, multicastSocket);

            new Thread(aliveChecker).start();
            new Thread(multicastListener).start();

            while(true) {
                sendHello(multicastSocket);
                System.out.println("SEND");
                //Mettere 20000
                Thread.sleep(5000);
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

    void updateValue(InetAddress address, int port, int value) {
        synchronized (devices) {
            Device d = devices.get(new Identifier(address, port));
            if (d != null) {
                d.setLastValueSent(value);
                d.resetLastCommunication();
            }
        }
    }

    void updateAliveDevices(InetAddress address, int port, int type) {
        synchronized (devices) {
            Identifier i = new Identifier(address, port);
            Device d = devices.get(i);
            if(d == null){
                devices.put(i, new Device(type));
                //MOSTRARE QUALI DEVICE SONO CONNESSI
                System.out.println(devices);
            }
            else{
                d.resetLastCommunication();
            }
        }
    }
}
