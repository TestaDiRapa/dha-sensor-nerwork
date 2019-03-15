package Server;

import java.time.Instant;

/**
 * A class that represents a device as IP + PORT
 */
public class Device {

    private int type;
    private Integer lastValueSent;
    private Instant lastCommunication;

    Device(int type) {
        this.type = type;
        lastCommunication = Instant.now();
        lastValueSent = null;
    }

    public int getLastValueSent() {
        return lastValueSent;
    }

    void setLastValueSent(int lastValueSent) {
        this.lastValueSent = lastValueSent;
    }

    public int getType() {
        return type;
    }

    public Instant getLastCommunication() {
        return lastCommunication;
    }

    void resetLastCommunication() {
        lastCommunication = Instant.now();
    }
}
