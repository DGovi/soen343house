package Model;

import org.json.JSONException;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

/**
 * represents a house in a house layout
 */
public class House {
    private final String DEFAULT_LOCATION = "Canada";

    protected ArrayList<Room> rooms;
    private String location;

    /**
     * gets the room objects from an arraylist
     * @return the rooms in house object
     */
    public ArrayList<Room> getRooms() {
		return rooms;
	}


   /**
     * creates a house using an inputted JSON file
     * @param srcJSON the file in which the house layout is contained
     * @exception JSONException if the file is not found or is incorrect
     */
  	public House(File srcJSON) throws JSONException, IOException {
        this.rooms = Room.roomFromJSON(srcJSON);
        this.location = DEFAULT_LOCATION;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }
}
