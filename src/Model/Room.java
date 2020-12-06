package Model;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.sql.Date;
import java.sql.Time;
import java.util.*;

import Controller.Simulation;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

/**
 * Represents a room within the smart home House.
 * Users are located in Rooms.
 * Users interact open or close Windows of Rooms.
 * Users block or unblock Room Windows.
 */
public class Room {
    protected String name;
    protected ArrayList<Window> windows;
    protected int lights;
    protected ArrayList<Door> doors;
    private boolean lightsOn;
    protected MotionSensor roomMotionSensor;
    protected float[] temperatures;
    private float actualTemperature;
    private boolean hvacON;

    private static final long morningDayBound = new Time(6, 0, 0).getTime();
    private static final long dayNightBound = new Time(18, 0, 0).getTime();

    /**
     * Creates a room object with a name, windows,
     * lights, and doors.
     *
     * @param name    the name of the room
     * @param windows an arraylist of windows
     * @param lights  number of windows
     * @param doors   on ArrayList of Door objects
     * @param temperature the starting temperature of this room
     */
    public Room(String name, ArrayList<Window> windows, int lights, ArrayList<Door> doors, float temperature) {
        this.name = name;
        this.windows = windows;
        this.lights = lights;
        this.doors = doors;
        this.lightsOn = true;
        this.roomMotionSensor = new MotionSensor(false);
        this.temperatures = new float[3];
        this.temperatures[0] = this.temperatures[1] = this.temperatures[2] = temperature; // morning - day - night
        this.actualTemperature = temperature;
        this.hvacON = false;
    }


    /**
     * Gets an arraylist of doors.
     *
     * @return an arraylist of doors
     */
    public ArrayList<Door> getDoors() {
        return doors;
    }

    /**
     * Set the doors in room.
     *
     * @param doors a list of doors
     */
    public void setDoors(ArrayList<Door> doors) {
        this.doors = doors;
    }

    /**
     * Get the name of the room.
     *
     * @return the name of the room
     */
    public String getName() {
        return name;
    }

    /**
     * Set the name of a room.
     *
     * @param name the new name of a room
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Get the windows of a room.
     *
     * @return the list of windows
     */
    public ArrayList<Window> getWindows() {
        return windows;
    }

    /**
     * Get the numbers of lights in a room.
     *
     * @return the number of lights in a room
     */
    public int getLights() {
        return lights;
    }

    /**
     * Set the number of lights in a room.
     *
     * @param lights the new number of lights in a room
     */
    public void setLights(int lights) {
        this.lights = lights;
    }

    public MotionSensor getRoomMotionSensor() {
        return roomMotionSensor;
    }

    /**
     * Gets the boolean value of the lightsOn attribute
     *
     * @return the value of the lightsOn attribute
     */
    public boolean isLightsOn() {
        return lightsOn;
    }

    /**
     * Sets the value of the lightsOn attribute.
     *
     * @param lightsOn the new value to set
     */
    public void setLightsOn(boolean lightsOn) {
        this.lightsOn = lightsOn;
    }

    /**
     * toggles the lightsOn attribute.
     *
     * @return String identifying if the lights in the Room are now on or off
     */
    public String toggleLightsON() {
        this.setLightsOn(!this.isLightsOn());
        if (this.isLightsOn())
            return "Lights turned ON!";
        else
            return "Lights turned OFF!";
    }

    /**
     * Allows to get the desired temperature of the room
     * @return float temperature of the room
     */
    public float[] getTemperatures() {
        return temperatures;
    }

    /**
     * Allows to set the desired temperature of the room
     * @param morning desired temperature of the room in the morning
     * @param day desired temperature of the room during the day
     * @param night desired temperature of the room at night
     * @return message stating success
     */
    public String setTemperatures(float morning, float day, float night) {
        this.temperatures[0] = morning;
        this.temperatures[1] = day;
        this.temperatures[2] = night;

        return "Successfully set the periodic temperatures of the selected room.";
    }

    /**
     * Creates room objects from a inputted JSON file.
     *
     * @param srcJSONPath a file that is needed as input
     * @return a list of room objects
     * @throws org.json.JSONException if there is a runtime error
     * @throws IOException            if there is a loading error
     */
    public static ArrayList<Room> roomFromJSON(File srcJSONPath) throws org.json.JSONException, IOException {
        String marshalled = new String(Files.readAllBytes(srcJSONPath.toPath()));
        JSONTokener tokener = new JSONTokener(marshalled);
        JSONObject object = new JSONObject(tokener);
        Iterator<String> keys = object.keys();
        ArrayList<Room> rooms = new ArrayList<Room>();

        while (keys.hasNext()) {
            String key = keys.next();
            if (object.get(key) instanceof JSONObject) {
                JSONArray array = object.getJSONObject(key).getJSONArray("doorsTo");
                ArrayList<Door> list = new ArrayList<Door>();

                if (key.equals("Entrance") || key.equals("Garage"))
                    list.add(new Door(key, "Outside"));

                for (int i = 0; i < array.length(); i++) {
                    String doorName = array.get(i).toString();
                    Door doorToAdd = new Door(key, doorName);
                    list.add(doorToAdd);
                }

                if (key.equals("Backyard"))
                    list.add(new Door(key, "Outside"));

                ArrayList<Window> windows = new ArrayList<Window>();
                for (int i = 0; i < object.getJSONObject(key).getInt("windows"); i++)
                    windows.add(new Window());

                rooms.add(new Room(
                                key,
                                windows,
                                object.getJSONObject(key).getInt("lights"),
                                list,
                                Simulation.DEFAULT_TEMPERATURE
                        )
                );
            }
        }
        return rooms;
    }

    /**
     * Gets the actual temperature of the Room.
     * This value signifies the real temperature of the Room at the current moment
     * not the desired one.
     *
     * @return the actual temperature of the Room
     */
    public float getActualTemperature() {
        return actualTemperature;
    }

    /**
     * Sets the actual temperature of the Room.
     *
     * @param actualTemperature  the new actual temperature of the room
     */
    public void setActualTemperature(float actualTemperature) {
        this.actualTemperature = actualTemperature;
    }

    /**
     * Calculates and returns the desired temperature of the room.
     * This is the temperature that the HVAC system will gradually and continuously
     * shift the room temperature towards.
     *
     * @param currentTime the current time of the simulation
     * @param parentSim the Simulation object the room is a member of
     * @return the desired temperature in degrees celsius
     */
    public float calculateDesiredTemperature(Simulation parentSim, Time currentTime) {
        if (parentSim.getIsAway()) {
            if (parentSim.isWinter(Date.valueOf(parentSim.getDate()).getMonth())) { // winter
                return parentSim.getWinterAwayTemp();
            } else { // summer
                return parentSim.getSummerAwayTemp();
            }
        } else {
            long time = currentTime.getTime();
            if (time < Room.morningDayBound)
                return this.getTemperatures()[0]; // morning
            else if (time < Room.morningDayBound)
                return this.getTemperatures()[1]; // daytime
            else
                return this.getTemperatures()[2]; // nighttime
        }

    }

    /**
     * Gets the ON/OFF status of the HVAC system in the given room.
     *
     * @return true if the HVAC is ON, false otherwise
     */
    public boolean isHvacON() {
        return hvacON;
    }

    /**
     * Sets the ON/OFF status of the HVAC system in the given room.
     *
     * @param hvacON the new HVAC ON/OFF status
     */
    public void setHvacON(boolean hvacON) {
        this.hvacON = hvacON;
    }
}