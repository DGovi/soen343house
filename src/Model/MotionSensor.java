package Model;

/**
 * this class describes the observer in the observer pattern
 * it '"observes"'state of the away button, and is notified
 * when the state has been changed.
 * Every Room has a motion sensor.
 */
public class MotionSensor implements Observer {

    private boolean isAway;

    private static int motionSensorIDcount = 0;
    private int motionSensorID;

    /**
     * motion sensors that have isAway as a parameter to have the state of the away button
     * @param isAway
     */
    public MotionSensor(boolean isAway){
        this.motionSensorID = motionSensorIDcount++;
    }

    /**
     * gets the unique ID of the motion sensor
     * @return int, the motion sensor id
     */
    public int getMotionSensorID(){
        return motionSensorID;
    }

    /**
     * updates the value of isAway
     * @param isAwayValue
     */
    public void update(boolean isAwayValue) {
        this.isAway = isAwayValue;
    }
}
