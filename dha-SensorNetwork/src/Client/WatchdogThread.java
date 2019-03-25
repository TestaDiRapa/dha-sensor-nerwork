package Client;

/**
 * This class implements a watchdog that sends a true signal if it doesn't receive a reset every 20 seconds
 */
class WatchdogThread {

    private Thread watchdog = new Thread();
    private boolean stop = false;

    /**
     * Asynchronous method: instantiates a thread that waits 20 seconds. If not interrupt before, changes
     * the stop variable to true
     */
    synchronized void start() {
        //I have to be sure that the previous thread stopped correctly
        watchdog.interrupt();

        watchdog = new Thread(() -> {
            try {
                Thread.sleep(5000);
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
     * Stops the watchdog
     */
    synchronized void stop() {
        watchdog.interrupt();
    }

    /**
     * Allow to retrieve the status of the watchdog
     * @return true if it wasn't reset, false otherwise
     */
    synchronized boolean haveIToStop(){
        return stop;
    }

    /**
     * @return the watchdog thread status
     */
    synchronized Thread.State status() {return watchdog.getState();}

}
