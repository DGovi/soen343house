package Model;

public class MotionSensor implements Observer {

    private boolean isAway;

    private static int motionSensorIDcount = 0;
    private int motionSensorID;

    private Subject sim;

    public MotionSensor(boolean isAway){
        this.motionSensorID = motionSensorIDcount++;
    }

    public int getMotionSensorID(){
        return motionSensorID;
    }
    @Override
    public void update(boolean isAwayValue) {
        this.isAway = isAwayValue;
    }
}
