package Controller;

import Model.Room;

/**
 * A thread to continuously handle the logic of the smart home HCC
 * HVAC heating and cooling rooms.  As well as the decay of room
 * temperature towards the outside temperature when the HVAC is off in a room.
 */
public class HVACController extends  Thread{
    private Simulation masterSim;
    private final float HVAC_POWER = (float) 0.1;
    private final float DECAY_POWER = (float) 0.5;
    private final float TEMP_MATCH_LEEWAY = (float) 0.05;
    private final float HVAC_START_LEEWAY= (float) 0.25;

    /**
     * Constructor of the DashboardController class.
     *
     * @param masterSim the Simulation object that the HVAC is attached to
     */
    public HVACController(Simulation masterSim){
        super();
        this.masterSim = masterSim;
    }

    /**
     * Method that is run when the thread is started.
     * Performs the logic to increase/decrease room temperatures
     * based on their desired temperatures, the simulation speed, and the
     * power of the HVAC system.
     */
    public void run() {
        while (true) {
            // perform HVAC logic
            for (Room room : masterSim.getHouse().getRooms()) {
                float speed = masterSim.getSimulationTimes().getTimeSpeed();
                float HVACStep = HVAC_POWER * speed;
                float decayStep = DECAY_POWER * speed;

                float currentTemperature = room.getActualTemperature();
                float desiredTemperature = room.calculateDesiredTemperature(masterSim, masterSim.getSimulationTimes().getTime());
                float outsideTemperature = masterSim.getTemperature();

                float desiredTempDiff = Math.abs(currentTemperature - desiredTemperature);
                float outsideTempDiff = Math.abs(currentTemperature - outsideTemperature);

                if (room.isHvacON()) {
                    if (desiredTempDiff < TEMP_MATCH_LEEWAY)
                       room.setHvacON(false);

                    if (desiredTemperature < currentTemperature)
                        room.setActualTemperature(currentTemperature - HVACStep);
                    else
                        room.setActualTemperature(currentTemperature + HVACStep);
                } else {
                    if (desiredTempDiff > HVAC_START_LEEWAY)
                        room.setHvacON(true);

                    if (outsideTempDiff > TEMP_MATCH_LEEWAY) {
                       if (outsideTemperature < currentTemperature)
                           room.setActualTemperature(currentTemperature - decayStep);
                       else
                           room.setActualTemperature(currentTemperature + decayStep);
                    }
                }
            }

            // sleep
            try {
                long sleepTime = (long) (1000 * (1/ masterSim.getSimulationTimes().getTimeSpeed()));
                Thread.sleep(sleepTime);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }


}
