package Controller;

import Model.Room;
import Model.Window;
import View.DashboardDriver;

import java.sql.Date;

/**
 * Class to continuously monitor the smart home temperature of each
 * room and perform automatic SHH actions if deemed necessary.
 */
public class SHHMonitor extends Thread{
    private final int SLEEP_TIME = 1000; //ms
    private Simulation masterSim;

    /**
     * Constructor of the SHHMonitor class.
     *
     * @param masterSim the Simulation object that the SHHMonitor is attached to
     */
    public SHHMonitor(Simulation masterSim) {
        super();
        this.masterSim = masterSim;
    }

    /**
     * Method that is run when the thread is started.
     * Performs the logic to send warning messages if the temperature of a room
     * is below 0.  Also controls the logic of opening and closing windows if
     * it's summer and it's cooler outside than inside.
     */
    public void run() {
        while (true) {
            // perform monitoring
            for (Room room : masterSim.getHouse().getRooms()) {
                if (room.getActualTemperature() < 0) {
                    // alert user of low temperature
                    DashboardDriver.controllerInstance.printToConsole("ALERT: The " + room.getName() + " is at a temperature below 0!  The pipes may burst!");
                } else {
                    // try to open windows
                    if (! masterSim.isSummer(Date.valueOf(masterSim.getDate()).getMonth()) || masterSim.getIsAway())
                        return;

                    if (room.getActualTemperature() < masterSim.getTemperature()) {
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

            // sleep
            try {
                Thread.sleep(SLEEP_TIME);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
