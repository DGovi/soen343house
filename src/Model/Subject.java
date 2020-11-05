package Model;

public interface Subject {

    public void addMotionSensor(Observer O);
    public void removeMotionSensor(Observer O);
    public String notifyMotionSensors();
}
