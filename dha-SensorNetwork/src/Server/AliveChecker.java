package Server;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

import static Commons.Constants.MAX;
import static Commons.Constants.PORT;
import static Commons.Constants.PORTSERVER;
import static Commons.ResponseParser.isAlive;

public class AliveChecker implements Runnable {

    private Server server;

    public AliveChecker(Server server) {
        this.server = server;
    }

    @Override
    public void run() {
        try(DatagramSocket socket = new DatagramSocket(PORTSERVER)){
            //METTERE MAX
            byte[] buffer = new byte[5];
            DatagramPacket bufferPacket = new DatagramPacket(buffer, buffer.length);
            while(true) {
                socket.receive(bufferPacket);
                int type = isAlive(bufferPacket);
                if(type >= 0){
                    server.updateAliveDevices(bufferPacket.getAddress(), bufferPacket.getPort(), type);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
