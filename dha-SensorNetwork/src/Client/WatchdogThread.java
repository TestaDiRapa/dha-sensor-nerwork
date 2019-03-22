package Client;

/**
 * This class implements a watchdog that sends a true signal if it doesn't receive a reset every 20 seconds
 */
class WatchdogThread {

    private Thread watchdog;
    private boolean stop = false;

    /**
     * Asynchronous method: instantiates a thread that waits 20 seconds. If not interrupt before, changes
     * the stop variable to true
     */
    void start() {
        watchdog = new Thread(() -> {
            try {
                Thread.sleep(20000);
                stop = true;
            } catch (InterruptedException e) {
                stop = false;
            }
        });
        watchdog.start();
    }

    /**
     * Interrupts the thread and restart it
     */
    void restart() {
        watchdog.interrupt();
        start();
    }

    /**
     * Allow to retrieve the status of the watchdog
     * @return true if it wasn't reset, false otherwise
     */
    boolean haveIToStop(){
        return stop;
    }

}
