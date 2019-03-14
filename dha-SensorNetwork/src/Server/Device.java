package Server;

import java.time.Instant;

/**
 * A class that represents a device as IP + PORT
 */
public class Device {

    private int type;
    private int lastValueSent;
    private Instant lastCommunication;

    public Device(int type) {
        this.type = type;
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

    public Instant getLastCommunication() {
        return lastCommunication;
    }

    public void setLastCommunication(Instant lastCommunication) {
        this.lastCommunication = lastCommunication;
    }
}
