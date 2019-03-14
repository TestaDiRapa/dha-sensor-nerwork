package Server;

import java.net.InetAddress;
import java.util.Objects;

/**
 * A class that represents a device as IP + PORT
 */
public class Device {

    private InetAddress address;
    private int port;
    private int type;
    private int lastValueSent;

    public Device(InetAddress address, int port, int type) {
        this.address = address;
        this.port = port;
        this.type = type;
    }

    public InetAddress getAddress() {
        return address;
    }

    public int getPort() {
        return port;
    }

    public int getLastValueSent() {
        return lastValueSent;
    }

    public void setLastValueSent(int lastValueSent) {
        this.lastValueSent = lastValueSent;
    }

    public int getType() {
        return type;
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
