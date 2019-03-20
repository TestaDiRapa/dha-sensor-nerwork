package Server;

import java.time.Instant;

/**
 * A class that represents a device as IP + PORT
 */
class Device implements Comparable<Device>{

    private int type;
    private int powerConsumption;
    private Instant lastCommunication;

    Device(int type, int powerConsumption) {
        this.type = type;
        this.powerConsumption = powerConsumption;
        lastCommunication = Instant.now();
    }

    public int getPowerConsumption() {
        return powerConsumption;
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
