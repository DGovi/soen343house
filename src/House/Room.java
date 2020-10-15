package House;

import java.util.*;
import java.io.InputStream;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

public class Room {

    protected String name;
    protected int windows;
    protected int lights;
    protected ArrayList<String> doors;

    // Tester
//    public static void main(String[] args) throws org.json.JSONException {
//        ArrayList<Room> array = roomFromJSON("houseinput.json");
//        for (Room r : array) {
//            r.printRoom();
//        }
//    }

    // Used for testing
    void printRoom() {
        System.out.println("name: " + this.name);
        System.out.println("windows: " + this.windows);
        System.out.println("lights: " + this.lights);
        System.out.println("Doors to: ");
        for (String s : this.doors) {
            System.out.print(s + " ");
        }
        System.out.println();
    }

    Room(String name, int windows, int lights, ArrayList<String> doors) {
        this.name = name;
        this.windows = windows;
        this.lights = lights;
        this.doors= doors;
    }
    
    

    public ArrayList<String> getDoors() {
		return doors;
	}

	public void setDoors(ArrayList<String> doors) {
		this.doors = doors;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getWindows() {
		return windows;
	}

	public void setWindows(int windows) {
		this.windows = windows;
	}

	public int getLights() {
		return lights;
	}

	public void setLights(int lights) {
		this.lights = lights;
	}

	public static ArrayList<Room> roomFromJSON(String srcJSONPath) throws org.json.JSONException {
        InputStream is = Room.class.getResourceAsStream(srcJSONPath);

        if (is == null) {
            throw new NullPointerException("Can't open JSON file: " + srcJSONPath);
        }

        JSONTokener tokener = new JSONTokener(is);
        JSONObject object = new JSONObject(tokener);

        Iterator<String> keys = object.keys();


        ArrayList<Room> rooms = new ArrayList<Room>();

        while(keys.hasNext()) {
            String key = keys.next();
            if (object.get(key) instanceof JSONObject) {
                JSONArray array = object.getJSONObject(key).getJSONArray("doorsTo");
                ArrayList<String> list = new ArrayList<String>();
                for (int i = 0; i < array.length(); i++) {
                    list.add(array.get(i).toString());
                }
                rooms.add(new Room(
                        key,
                        object.getJSONObject(key).getInt("windows"),
                        object.getJSONObject(key).getInt("lights"),
                        list
                        )
                );
            }
        }
        return rooms;
    }
}