package Controller;

import Model.Room;

public class SHHMonitor extends Thread{
    private final int SLEEP_TIME = 1000; //ms
    private Simulation masterSim;

    public SHHMonitor(Simulation masterSim) {
        super();
        this.masterSim = masterSim;
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
