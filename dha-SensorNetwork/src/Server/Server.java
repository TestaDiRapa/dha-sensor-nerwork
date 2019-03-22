package Server;

import javax.annotation.processing.SupportedSourceVersion;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

import static Commons.Constants.*;
import static Commons.ResponseParser.createHelloMessage;

/**
 * The Server class, has a reference to the GUI
 */
public class Server implements Runnable{

    private final Map<Identifier, Device> devices = new HashMap<>();
    private final ServerGUI gui;

    private AliveChecker aliveChecker;
    private boolean running = true;
    private MulticastSocket multicastSocket;
    private int freePower = TOTAL_POWER;

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

            new Thread(aliveChecker).start();

            updateGui();

            while(running) {
                sendHello();

                Thread.sleep(2500);
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
        running = false;
    }

    /**
     * Creates the HELLO message and sends it in multicast
     * @throws IOException exception
     */
    private synchronized void sendHello() throws IOException {
        System.out.println("HELLO");
        int usedPower = 0;
        for(Map.Entry<Identifier, Device> e : devices.entrySet()) {
            if((Instant.now().toEpochMilli() - e.getValue().getLastCommunication().toEpochMilli() <= DISCONNECT_TIME))
                usedPower += e.getValue().getPowerConsumption();
        }
        freePower = TOTAL_POWER - usedPower;
        byte[] payload = createHelloMessage(PORT, freePower);
        DatagramPacket packet = new DatagramPacket(payload, payload.length, InetAddress.getByName(ADDRESS), MULTICAST_PORT);
        multicastSocket.send(packet);
    }

    /**
     * Updates the last confirm time of a device if exists, otherwise it adds it to the connected devices
     * @param address the IP address of the device
     * @param port the port of the device
     * @param type the type of device
     */
    void updateAliveDevice(InetAddress address, int port, int type, int powerConsumption) {
        synchronized (devices) {
            Identifier i = new Identifier(address, port);
            Device d = devices.get(i);
            if(d == null){
                devices.put(i, new Device(type, powerConsumption));
            }
            else{
                d.resetLastCommunication();
            }

            int usedPower = 0;
            for(Map.Entry<Identifier, Device> e : devices.entrySet()) {
                if((Instant.now().toEpochMilli() - e.getValue().getLastCommunication().toEpochMilli() <= DISCONNECT_TIME))
                    usedPower += e.getValue().getPowerConsumption();
            }

            freePower = TOTAL_POWER - usedPower;

        }
        updateGui();
    }

    /**
     * Force updating the GUI information on devices
     */
    void updateGui() {
        synchronized (gui) {
            synchronized (devices) {
                gui.updateDevices(devices.values(), freePower);
            }
        }
    }
}
