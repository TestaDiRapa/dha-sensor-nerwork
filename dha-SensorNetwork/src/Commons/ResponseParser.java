package Commons;

import java.net.DatagramPacket;
import java.nio.charset.StandardCharsets;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ResponseParser {

    public static Integer parseDataReceiving(DatagramPacket packet){
        String payload = new String(packet.getData());
        if(!payload.matches("DATA[0-9]+.*")) return null;
        Pattern p = Pattern.compile("(DATA)([0-9]+)(.*)");
        Matcher m = p.matcher(payload);
        m.find();
        return Integer.parseInt(m.group(2));
    }

    public static int isAlive(DatagramPacket packet){
        String payload = new String(packet.getData());
        //FARE CON MATCH
        if(payload.matches("ALIVE[0-9].*")){
            Pattern p = Pattern.compile("(ALIVE)([0-9])(.*)");
            Matcher m = p.matcher(payload);
            m.find();
            return Integer.parseInt(m.group(2));
        }
        return -1;
    }

    public static byte[] dataSendMessage(int value) {
        return ("DATA" + value).getBytes(StandardCharsets.UTF_8);
    }

    public static byte[] aliveMessage(int type) {
        return ("ALIVE" + type).getBytes(StandardCharsets.UTF_8);
    }

}
