package Server;

import java.time.Instant;

/**
 * A class that represents a device as IP + PORT
 */
class Device implements Comparable<Device>{

    private int type;
    private Integer lastValueSent;
    private Instant lastCommunication;

    Device(int type) {
        this.type = type;
        lastCommunication = Instant.now();
        lastValueSent = null;
    }

    Integer getLastValueSent() {
        return lastValueSent;
    }

    void setLastValueSent(Integer lastValueSent) {
        this.lastValueSent = lastValueSent;
    }

    int getType() {
        return type;
    }

    Instant getLastCommunication() {
        return lastCommunication;
    }

    void resetLastCommunication() {
        lastCommunication = Instant.now();
    }

    @Override
    public int compareTo(Device o) {
        return -lastCommunication.compareTo(o.lastCommunication);
    }
}
