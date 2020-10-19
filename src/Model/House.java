package Model;

import org.json.JSONException;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class House {
    protected ArrayList<Room> rooms;

    public ArrayList<Room> getRooms() {
		return rooms;
	}

	public House(File srcJSON) throws JSONException, IOException {
        rooms = Room.roomFromJSON(srcJSON);
    }
}
