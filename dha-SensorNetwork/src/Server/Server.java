package Server;

import java.util.HashMap;
import java.util.Map;

public class Server implements Runnable{

    private Map<Device, Integer> devices;

    public Server(){
        devices = new HashMap<>();
    }

    @Override
    public void run() {

    }
}
