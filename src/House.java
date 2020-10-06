import org.json.JSONException;

import java.util.ArrayList;

public class House {
    protected ArrayList<Room> rooms;

    public House(String srcJSON) throws JSONException {
        rooms = Room.roomFromJSON(srcJSON);
    }
}
