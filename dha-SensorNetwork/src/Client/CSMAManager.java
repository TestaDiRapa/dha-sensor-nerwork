package Client;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.MulticastSocket;
import java.util.Random;

import static Commons.Constants.MAX;
import static Commons.ResponseParser.helloGetFreeWatts;

public class CSMAManager {

    private boolean disconnect;
    private int wait = 0;
    private MulticastSocket socket;

    CSMAManager(MulticastSocket socket) {
        this.socket = socket;
    }

    public void check() {
        new Thread(() -> {
            disconnect = false;
            byte[] message = new byte[MAX];
            DatagramPacket messagePacket = new DatagramPacket(message, message.length);
            try {
                Integer res;
                do{
                    socket.receive(messagePacket);
                    res = helloGetFreeWatts(messagePacket);
                    System.out.println(res);
                }while (res == null);

                disconnect = (res < 0);
                if(disconnect) wait++;
                else wait = 0;

            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }

    boolean disconnect() {return disconnect;}

    void csmaWait() throws InterruptedException {
        if(wait > 0) Thread.sleep(new Random().nextInt(wait*10000));
    }
}
