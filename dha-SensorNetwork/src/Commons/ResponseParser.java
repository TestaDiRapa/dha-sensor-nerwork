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

    public static int aliveGetType(DatagramPacket packet){
        String payload = new String(packet.getData());

        if(payload.matches("ALIVE[0-9]%[0-9]+.*")){
            Pattern p = Pattern.compile("(ALIVE)([0-9])%([0-9]+)(.*)");
            Matcher m = p.matcher(payload);
            m.find();
            return Integer.parseInt(m.group(2));
        }
        return -1;
    }

    public static int aliveGetPowerConsumption(DatagramPacket packet){
        String payload = new String(packet.getData());

        if(payload.matches("ALIVE[0-9]%[0-9]+.*")){
            Pattern p = Pattern.compile("(ALIVE)([0-9])%([0-9]+)(.*)");
            Matcher m = p.matcher(payload);
            m.find();
            return Integer.parseInt(m.group(3));
        }
        return -1;
    }

    public static int helloGetPort(DatagramPacket packet) {
        String payload = new String(packet.getData());
        if(payload.matches("HELLO[0-9]{4,6}%[0-9]+.*")){
            Pattern p = Pattern.compile("(HELLO)([0-9]{4,6})%([0-9]+)(.*)");
            Matcher m = p.matcher(payload);
            m.find();
            return Integer.parseInt(m.group(2));
        }
        return -1;
    }

    public static int helloGetFreeWatts(DatagramPacket packet) {
        String payload = new String(packet.getData());
        System.out.println(payload);
        if(payload.matches("HELLO[0-9]{4,6}%[0-9]{1,4}")){
            Pattern p = Pattern.compile("(HELLO)([0-9]{4,6})%([0-9]{1,4})");
            Matcher m = p.matcher(payload);
            m.find();
            System.out.println(m.group(3));
            return Integer.parseInt(m.group(3));
        }
        return -1;
    }


    public static byte[] dataSendMessage(int value) {
        return ("DATA" + value).getBytes(StandardCharsets.UTF_8);
    }

    public static byte[] aliveMessage(int type, int usedWatts) {
        return ("ALIVE" + type + "%" + usedWatts).getBytes(StandardCharsets.UTF_8);
    }

    public static byte[] createHelloMessage(int port, int freeWatts) {return ("HELLO"+port+"%"+freeWatts).getBytes(StandardCharsets.UTF_8);}

}
