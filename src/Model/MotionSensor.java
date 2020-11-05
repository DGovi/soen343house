package Model;

public class MotionSensor implements Observer {

    private boolean isAway;

    private static int motionSensorIDcount = 0;
    private int motionSensorID;

    private Subject sim;

    public MotionSensor(Subject sim){
        this.sim = sim;
        this.motionSensorID = motionSensorIDcount + 1;
        sim.addMotionSensor(this);
    }

    public int getMotionSensorID(){
        return motionSensorID;
    }
    @Override
    public void update(boolean isAwayValue) {
        this.isAway = isAwayValue;
    }
}
