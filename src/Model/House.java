package Model;

import org.json.JSONException;

import java.util.ArrayList;

/**
 * represents a house in a house layout
 */
public class House {
    protected ArrayList<Room> rooms;

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
	public House(String srcJSON) throws JSONException {
        rooms = Room.roomFromJSON(srcJSON);
    }
}
