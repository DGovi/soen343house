package Controller;

public class SHHMonitor extends Thread{
    private final int SLEEP_TIME = 1000; //ms
    private Simulation master_sim;

    public SHHMonitor(Simulation master_sim) {
        super();
        this.master_sim = master_sim;
    }

    public void run() {
        while (true) {
            // perform monitoring
            checkIfShouldOpenWindow();
            checkIfTempBelowZero();

            // sleep
            try {
                Thread.sleep(SLEEP_TIME);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void checkIfShouldOpenWindow() {

    }

    private void checkIfTempBelowZero() {

    }
}
