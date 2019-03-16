package Commons;

import java.net.DatagramPacket;
import java.nio.charset.StandardCharsets;

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
        //FARE CON MATCH
        if(payload.equals("ALIVE")){
            return Integer.parseInt(payload.split("ALIVE")[0]);
        }
        return -1;
    }

    public static byte[] dataSendMessage(int type, int value) {
        return (type + "DATA" + value).getBytes(StandardCharsets.UTF_8);
    }

    public static byte[] aliveMessage(int type) {
        return ("ALIVE" + type).getBytes(StandardCharsets.UTF_8);
    }

}
