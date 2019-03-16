package Server;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import static Commons.Constants.*;

public class Server implements Runnable{

    private final Map<Identifier, Device> devices = new HashMap<>();
    private ServerGUI gui;

    public Server(ServerGUI gui){
        this.gui = gui;
    }

    @Override
    public void run() {
        try {
            MulticastSocket multicastSocket = new MulticastSocket(MULTICAST_PORT);

            AliveChecker aliveChecker = new AliveChecker(this);
            MulticastListener multicastListener = new MulticastListener(this, multicastSocket);

            new Thread(aliveChecker).start();
            new Thread(multicastListener).start();

            while(true) {
                sendHello(multicastSocket);
                //Mettere 20000
                Thread.sleep(5000);
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    private synchronized void sendHello(MulticastSocket socket) throws IOException {
        byte [] payload = ("HELLO"+PORT).getBytes(StandardCharsets.UTF_8);
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
        Collection<Device> values;
        synchronized (devices) {
            Identifier i = new Identifier(address, port);
            Device d = devices.get(i);
            if(d == null){
                devices.put(i, new Device(type));
            }
            else{
                d.resetLastCommunication();
            }
            values = devices.values();
        }
        gui.updateDevices(values);
    }
}
