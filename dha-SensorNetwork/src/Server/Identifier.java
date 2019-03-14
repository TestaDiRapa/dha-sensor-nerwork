package Server;

import java.net.InetAddress;
import java.util.Objects;

public class Identifier {

    private InetAddress address;
    private int port;

    public Identifier(InetAddress address, int port) {
        this.address = address;
        this.port = port;
    }

    public InetAddress getAddress() {
        return address;
    }

    public int getPort() {
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
