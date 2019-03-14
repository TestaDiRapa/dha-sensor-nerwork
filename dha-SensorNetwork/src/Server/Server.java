package Server;

import java.io.IOException;
import java.net.MulticastSocket;
import java.util.HashMap;
import java.util.Map;

import static Commons.Constants.MULTICAST_PORT;

public class Server implements Runnable{

    private Map<Device, Integer> devices;

    public Server(){
        devices = new HashMap<>();
    }

    @Override
    public void run() {
        try(MulticastSocket multicastSocket = new MulticastSocket(MULTICAST_PORT);) {
            while(true) {

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
