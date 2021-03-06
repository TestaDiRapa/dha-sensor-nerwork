package Client;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.MulticastSocket;
import java.time.Instant;
import java.util.Random;

import static Commons.Constants.MAX;
import static Commons.ResponseParser.helloGetFreeWatts;

/**
 * The class implements all the functions for the  CSMA protocol. Has a reference to the MulticastSocket
 */
class CSMAManager {

    private boolean disconnect;
    private int wait = 2;
    private boolean stop;
    private final MulticastSocket socket;
    private final WatchdogThread watchdog;

    /**
     * Constructor
     * @param socket the MulticastSocket
     * @param watchdog a WatchdogThread instance
     */
    CSMAManager(MulticastSocket socket, WatchdogThread watchdog) {
        this.socket = socket;
        this.watchdog = watchdog;
    }

    /**
     * Asynchronous method. Checks for the used power and reset the watchdog
     */
    void check() {

        synchronized (socket) {
            stop = false;
            new Thread(() -> {
                disconnect = false;
                watchdog.start();

                byte[] message = new byte[MAX];
                DatagramPacket messagePacket = new DatagramPacket(message, message.length);
                try {

                    //Continues checking the multicast messages from the server while it does
                    //not receive a negative power or the client sends a stop message
                    do {
                        Integer res;

                        //Checks the multicast socket until it receives a valid message from the server
                        do {
                            socket.receive(messagePacket);
                            res = helloGetFreeWatts(messagePacket);
                            watchdog.restart();
                        } while (res == null);

                        //If the power is negative, stop and increase the wait time
                        disconnect = (res < 0);
                        if (disconnect) wait++;
                    } while (!disconnect && !stop);
                    watchdog.stop();

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }).start();
        }
    }

    /**
     * This method allows to stop the check method
     */
    void stopChecking() {
        stop = true;
    }

    /**
     * Returns the disconnect status
     * @return the disconnect status
     */
    boolean disconnect() {return disconnect;}

    /**
     * This method implements the wait for the CSMA protocol and stops while continuing listening
     * the multicast socket
     * @return the last datagram read
     * @throws IOException an exception
     */
    DatagramPacket csmaWait() throws IOException {
        byte[] message = new byte[MAX];
        DatagramPacket messagePacket = new DatagramPacket(message, message.length);

        //The starting instant (in milliseconds)
        long start = Instant.now().toEpochMilli();

        //Continue reading the socket until it has waited enough (a random time that depends on wait)
        //If wait is less than 2 (base value) it just read the first datagram
        do{
            socket.receive(messagePacket);
        }while(wait > 2 && (Instant.now().toEpochMilli() - start) < new Random().nextInt(wait*10000));

        return messagePacket;
    }

    /**
     * Resets the wait value
     */
    void resetWait() {wait = 2;}
}
