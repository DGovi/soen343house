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
    private final String DEFAULT_LOCATION = "Canada";

    private ArrayList<Room> rooms;
    private String location;

    /**
     * Gets the room objects from an arraylist.
     * @return the rooms in house object
     */
    public ArrayList<Room> getRooms() {
		return rooms;
	}


   /**
     * Creates a house using an inputted JSON file.
     * @param srcJSON the file in which the house layout is contained
     * @exception JSONException if the file is not found or is incorrect
     */
  	public House(File srcJSON) throws JSONException, IOException {
        this.rooms = Room.roomFromJSON(srcJSON);
        this.location = DEFAULT_LOCATION;
    }

    /**
     * Gets the String location of the house.
     * @return a string descibing the house
     */
    public String getLocation() {
        return location;
    }

    /**
     * Sets the location of the house.
     * @param location the current location of the house
     */
    public void setLocation(String location) {
        this.location = location;
    }
}
