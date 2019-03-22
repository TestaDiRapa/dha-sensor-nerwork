package Client;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.MulticastSocket;
import java.time.Instant;
import java.util.Random;

import static Commons.Constants.MAX;
import static Commons.ResponseParser.helloGetFreeWatts;

class CSMAManager {

    private boolean disconnect;
    private int wait = 2;
    private boolean stop;
    private MulticastSocket socket;

    CSMAManager(MulticastSocket socket) {
        this.socket = socket;
    }

    void check(WatchdogThread watchdog) {
        stop = false;
        new Thread(() -> {
            disconnect = false;
            watchdog.start();
            byte[] message = new byte[MAX];
            DatagramPacket messagePacket = new DatagramPacket(message, message.length);
            try {
                do {
                    Integer res;
                    do {
                        socket.receive(messagePacket);
                        watchdog.restart();
                        res = helloGetFreeWatts(messagePacket);
                    } while (res == null);

                    disconnect = (res < 0);
                    if (disconnect) wait++;
                } while(!disconnect && !stop);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }

    void stopChecking() {stop = true;}

    boolean disconnect() {return disconnect;}

    DatagramPacket csmaWait() throws IOException {
        byte[] message = new byte[MAX];
        DatagramPacket messagePacket = new DatagramPacket(message, message.length);

        long start = Instant.now().toEpochMilli();
        do{
            socket.receive(messagePacket);
        }while(wait > 2 && (Instant.now().toEpochMilli() - start) < wait*10000);

        return messagePacket;
    }

    void resetWait() {wait = 2;}
}
