package Server;

import Commons.ResponseParser;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import static Commons.Constants.*;

public class MulticastListener implements Runnable{

    private Server mainServer;

    public MulticastListener(Server mainServer) {
        this.mainServer = mainServer;
    }

    @Override
    public void run() {
        try(MulticastSocket multicastSocket = new MulticastSocket(PORT)){

            multicastSocket.joinGroup(InetAddress.getByName(ADDRESS));
            byte[] payload = new byte[MAX];
            DatagramPacket packet = new DatagramPacket(payload, payload.length);

            while (true) {
                multicastSocket.receive(packet);
                int[] params = ResponseParser.parseDataReceiving(packet);
                if(params != null){
                    mainServer.updateValue(packet.getAddress(), packet.getPort(), params[1]);
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
