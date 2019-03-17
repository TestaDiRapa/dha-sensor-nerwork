package Server;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import static Commons.Constants.*;

/**
 * The Server class, has a reference to the GUI
 */
public class Server implements Runnable{

    private final Map<Identifier, Device> devices = new HashMap<>();
    private final ServerGUI gui;

    /**
     * Constructor
     * @param gui a ServerGUI instance
     */
    Server(ServerGUI gui){
        this.gui = gui;
    }

    /**
     * Basically the main. Opens the socket and instantiates the threads for read the datagrams in multicast and unicast.
     * Periodically sends a "HELLO" message to allow other devices to connect and updates the GUI
     */
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
                updateGui();

            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * Creates the HELLO message and sends it in multicast
     * @param socket the multicast socket
     * @throws IOException exception
     */
    private synchronized void sendHello(MulticastSocket socket) throws IOException {
        byte [] payload = ("HELLO"+PORT).getBytes(StandardCharsets.UTF_8);
        DatagramPacket packet = new DatagramPacket(payload, payload.length, InetAddress.getByName(ADDRESS), MULTICAST_PORT);
        socket.send(packet);
    }

    /**
     * Updates the value of a connected device, if exists
     * @param address the IP address of the device
     * @param port the port of the device
     * @param value the value received
     */
    void updateValue(InetAddress address, int port, int value) {
        synchronized (devices) {
            Device d = devices.get(new Identifier(address, port));
            if (d != null) {
                d.setLastValueSent(value);
                d.resetLastCommunication();
            }
        }
    }

    /**
     * Updates the last confirm time of a device if exists, otherwise it adds it to the connected devices
     * @param address the IP address of the device
     * @param port the port of the device
     * @param type the type of device
     */
    void updateAliveDevice(InetAddress address, int port, int type) {
        synchronized (devices) {
            Identifier i = new Identifier(address, port);
            Device d = devices.get(i);
            if(d == null){
                devices.put(i, new Device(type));
            }
            else{
                d.resetLastCommunication();
            }
        }
        updateGui();
    }

    /**
     * Force updating the GUI information on devices
     */
    private void updateGui() {
        synchronized (gui) {
            synchronized (devices) {
                gui.updateDevices(devices.values());
            }
        }
    }
}
