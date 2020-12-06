package Controller;

import Model.Room;
import View.DashboardDriver;

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
            checkIfTempBelowZero();
            checkIfShouldOpenWindow();

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
        for (Room room : masterSim.getHouse().getRooms()) {
            if (room.getRealTemperature() < 0) {
                DashboardDriver.controllerInstance.printToConsole("ALERT: The " + room.getName() + " is at a temperature below 0!  The pipes may burst!");
            }
        }

    }
}
