package Commons;

import java.net.DatagramPacket;
import java.nio.charset.StandardCharsets;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A static class that contains all the function to create and parse the messages
 */
public class ResponseParser {

    /**
     * A function that parses a datagram and returns the id of the device, if it is an ALIVE message
     * @param packet the DatagramPacket
     * @return the id of the device or -1
     */
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

    /**
     * A function that parses a datagram and return the power consumption of the device, if it is an ALIVE message
     * @param packet the DatagramPacket
     * @return the power consumption or -1
     */
    public static int aliveGetPowerConsumption(DatagramPacket packet){
        String payload = new String(packet.getData());

        if(payload.matches("ALIVE[0-9]%[0-9]+%.*")){
            Pattern p = Pattern.compile("(ALIVE)([0-9])%([0-9]+)(%.*)");
            Matcher m = p.matcher(payload);
            m.find();
            return Integer.parseInt(m.group(3));
        }
        return -1;
    }

    /**
     * A function that parses a datagram and returns the port if it is a HELLO message
     * @param packet the DatagramPacket
     * @return the port or -1
     */
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

    /**
     * A function that parses a datagram and extract the free power of the server, if it is a HELLO message
     * @param packet the DatagramPacket
     * @return the free power or null
     */
    public static Integer helloGetFreeWatts(DatagramPacket packet) {
        String payload = new String(packet.getData());
        if(payload.matches("HELLO[0-9]{4,6}%-?[0-9]{1,4}.*")){
            Pattern p = Pattern.compile("(HELLO)([0-9]{4,6})%(-?[0-9]{1,4}).*");
            Matcher m = p.matcher(payload);
            m.find();
            return Integer.parseInt(m.group(3));
        }
        return null;
    }

    /**
     * Creates an ALIVE message
     * @param type the id of the device
     * @param usedWatts the needed power
     * @return a byte array containing the message
     */
    public static byte[] aliveMessage(int type, int usedWatts) {
        return ("ALIVE" + type + "%" + usedWatts + "%").getBytes(StandardCharsets.UTF_8);
    }

    /**
     * Creates a HELLO message
     * @param port the port of the server
     * @param freeWatts the free power
     * @return a byte array containing the message
     */
    public static byte[] createHelloMessage(int port, int freeWatts) {return ("HELLO"+port+"%"+freeWatts).getBytes(StandardCharsets.UTF_8);}

}
