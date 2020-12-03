package Model;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.*;

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

    /**
     * Creates a room object with a name, windows,
     * lights, and doors.
     *
     * @param name    the name of the room
     * @param windows an arraylist of windows
     * @param lights  number of windows
     * @param doors   on ArrayList of Door objects
     */
    public Room(String name, ArrayList<Window> windows, int lights, ArrayList<Door> doors) {
        this.name = name;
        this.windows = windows;
        this.lights = lights;
        this.doors = doors;
        this.lightsOn = true;
        roomMotionSensor = new MotionSensor(false);
        this.temperatures = new float[3];
        this.temperatures[0] = this.temperatures[1] = this.temperatures[2] = 24;
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
                                list
                        )
                );
            }
        }
        return rooms;
    }

}