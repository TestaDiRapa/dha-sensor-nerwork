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

    private AliveChecker aliveChecker;
    private MulticastListener multicastListener;
    private boolean running = true;
    private MulticastSocket multicastSocket;

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
            multicastSocket = new MulticastSocket(MULTICAST_PORT);

            aliveChecker = new AliveChecker(this);
            multicastListener = new MulticastListener(this, multicastSocket);

            new Thread(aliveChecker).start();
            new Thread(multicastListener).start();

            while(running) {
                sendHello();
                //Mettere 20000
                Thread.sleep(20000);
                updateGui();

            }

            multicastSocket.close();

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * Closes all the sub-threads and itself
     */
    void stopProtocol() {
        aliveChecker.stopProtocol();
        multicastListener.stopProtocol();
        running = false;
    }

    /**
     * Creates the HELLO message and sends it in multicast
     * @throws IOException exception
     */
    public synchronized void sendHello() throws IOException {
        byte [] payload = ("HELLO"+PORT).getBytes(StandardCharsets.UTF_8);
        DatagramPacket packet = new DatagramPacket(payload, payload.length, InetAddress.getByName(ADDRESS), MULTICAST_PORT);
        multicastSocket.send(packet);
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
    void updateGui() {
        synchronized (gui) {
            synchronized (devices) {
                gui.updateDevices(devices.values());
            }
        }
    }
}
