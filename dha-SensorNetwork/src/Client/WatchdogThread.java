package Client;

class WatchdogThread {

    private Thread watchdog;
    private boolean stop = false;

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

    void restart() {
        watchdog.interrupt();
        start();
    }

    boolean haveIToStop(){
        return stop;
    }

}
