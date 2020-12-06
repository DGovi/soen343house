package Controller;

import Model.Room;
import View.DashboardDriver;

public class HVACController extends  Thread{
    private Simulation masterSim;
    private final float HVAC_POWER = (float) 0.1;
    private final float DECAY_POWER = (float) 0.5;
    private final float TEMP_MATCH_LEEWAY = (float) 0.05;
    private final float HVAC_START_LEEWAY= (float) 0.25;

    public HVACController(Simulation masterSim){
        super();
        this.masterSim = masterSim;
    }

    public void run() {
        while (true) {
            // perform HVAC logic
            for (Room room : masterSim.getHouse().getRooms()) {
                float currentTemperature = room.getRealTemperature();
                float desiredTemperature = room.calculateDesiredTemperature(masterSim.getTime());
                float outsideTemperature = masterSim.getTemperature();

                float desiredTempDiff = Math.abs(currentTemperature - desiredTemperature);
                float outsideTempDiff = Math.abs(currentTemperature - outsideTemperature);

                if (room.isHvacON()) {
                    if (desiredTempDiff < TEMP_MATCH_LEEWAY)
                       room.setHvacON(false);

                    if (desiredTemperature < currentTemperature)
                        room.setRealTemperature(currentTemperature - HVAC_POWER);
                    else
                        room.setRealTemperature(currentTemperature + HVAC_POWER);
                } else {
                    if (desiredTempDiff > HVAC_START_LEEWAY)
                        room.setHvacON(true);

                    if (outsideTempDiff > TEMP_MATCH_LEEWAY) {
                       if (outsideTemperature < currentTemperature)
                           room.setRealTemperature(currentTemperature - DECAY_POWER);
                       else
                           room.setRealTemperature(currentTemperature + DECAY_POWER);
                    }
                }
            }

            // sleep
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }


}
