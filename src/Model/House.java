package Model;

import org.json.JSONException;

import java.util.ArrayList;

public class House {
    private final String DEFAULT_LOCATION = "Canada";

    protected ArrayList<Room> rooms;
    private String location;

    public ArrayList<Room> getRooms() {
		return rooms;
	}

	public House(String srcJSON) throws JSONException {
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
