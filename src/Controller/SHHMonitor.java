package Controller;

import Model.Room;
import Model.Window;
import View.DashboardDriver;

import java.sql.Date;

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
        if (! masterSim.isSummer(Date.valueOf(masterSim.getDate()).getMonth()) || masterSim.getIsAway())
            return;

        for (Room room: masterSim.getHouse().getRooms()) {
            if (room.getRealTemperature() < masterSim.getTemperature()) {
                for (Window window: room.getWindows()) {
                    if (window.getObstructed()) {
                       DashboardDriver.controllerInstance.printToConsole("WARNING: The SHH module tried to open a window to cool a room but it was blocked!  Cancelling operation...");
                    } else {
                        window.setOpen(true);
                    }
                }
            }
        }

    }

    private void checkIfTempBelowZero() {
        for (Room room : masterSim.getHouse().getRooms()) {
            if (room.getRealTemperature() < 0) {
                DashboardDriver.controllerInstance.printToConsole("ALERT: The " + room.getName() + " is at a temperature below 0!  The pipes may burst!");
            }
        }

    }
}
