package Server;

import java.net.InetAddress;
import java.util.Objects;

/**
 * Class used to identify the device as PORT + IP
 */
public class Identifier {

    private InetAddress address;
    private int port;

    Identifier(InetAddress address, int port) {
        this.address = address;
        this.port = port;
    }

    private InetAddress getAddress() {
        return address;
    }

    private int getPort() {
        return port;
    }

    @Override
    public int hashCode() {
        return Objects.hash(address, port);
    }

    @Override
    public boolean equals(Object obj) {
        return (obj instanceof Identifier) &&
                (address.equals(((Identifier) obj).getAddress())) &&
                (port == ((Identifier) obj).getPort());
    }
}
