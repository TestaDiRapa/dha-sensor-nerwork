package Server;

import java.net.InetAddress;
import java.util.Objects;

/**
 * A class that represents a device as IP + PORT
 */
public class Device {

    private InetAddress address;
    private int port;

    public Device(InetAddress address, int port) {
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
        return (obj instanceof Device) &&
                (address.equals(((Device) obj).getAddress())) &&
                (port == ((Device) obj).getPort());
    }
}
