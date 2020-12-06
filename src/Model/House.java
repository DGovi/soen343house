package Model;

import org.json.JSONException;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Represents a house in a house layout.
 * The house has an array of rooms, and a location.
 */
public class House {
    private static final String DEFAULT_LOCATION = "Canada";

    private final ArrayList<Room> rooms;
    private String location;

    /**
     * Creates a House object given a List of Rooms
     * and a locating String.
     *
     * @param r a list of Rooms
     * @param l a String representing the country location of the house
     */
    public House(ArrayList<Room> r, String l) {
        rooms = r;
        location = l;
    }

    /**
     * Gets the room objects from an arraylist.
     *
     * @return the rooms in house object
     */
    public ArrayList<Room> getRooms() {
        return rooms;
    }


    /**
     * Creates a house using an inputted JSON file.
     *
     * @param srcJSON the file in which the house layout is contained
     * @throws JSONException if the file is not found or is incorrect
     */
    public House(File srcJSON) throws JSONException, IOException {
        this.rooms = Room.roomFromJSON(srcJSON);
        this.location = DEFAULT_LOCATION;
    }

    /**
     * Gets the String location of the house.
     *
     * @return a string descibing the house
     */
    public String getLocation() {
        return location;
    }

    /**
     * Sets the location of the house.
     *
     * @param location the current location of the house
     */
    public void setLocation(String location) {
        this.location = location;
    }

    /**
     * Gets the Room object from a String
     * representing it's name.
     *
     * @param name the name of the Room to find
     * @return the Room object if it exists, null otherwise
     */
    public Room getRoomFromName(String name) {
        if (name == null)
            return null;

        for (Room r : this.getRooms()) {
            if (name.equals(r.getName())) {
                return r;
            }
        }

        return null;
    }
}
