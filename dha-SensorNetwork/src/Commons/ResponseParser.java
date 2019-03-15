package Commons;

import java.net.DatagramPacket;

public class ResponseParser {

    public static int[] parseDataReceiving(DatagramPacket packet){
        String payload = new String(packet.getData());
        if(!payload.matches("[0-6]DATA[0-9]+")) return null;
        String[] tmp = payload.split("DATA");
        int[] ret = new int[2];
        ret[0] = Integer.parseInt(tmp[0]);
        ret[1] = Integer.parseInt(tmp[1]);
        return ret;
    }

    public static int isAlive(DatagramPacket packet){
        String payload = new String(packet.getData());
        if(payload.matches("ALIVE[0-9]")){
            return Integer.parseInt(payload.split("ALIVE")[0]);
        }
        return -1;
    }

}
