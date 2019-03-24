package Server;

import java.time.Instant;

/**
 * A class that represents a device. Contains informations about it's type, the power consumption and the last ALIVE received
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

    int getPowerConsumption() {
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
