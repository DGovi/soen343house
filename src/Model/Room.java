package Model;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.*;
import java.io.InputStream;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

public class Room {

    protected String name;
    protected ArrayList<Window> windows;
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

    Room(String name, ArrayList<Window> windows, int lights, ArrayList<String> doors) {
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

	public ArrayList<Window> getWindows() {
		return windows;
	}

	public int getLights() {
		return lights;
	}

	public void setLights(int lights) {
		this.lights = lights;
	}

    public static ArrayList<Room> roomFromJSON(File srcJSONPath) throws org.json.JSONException, IOException {
        String marshalled = Files.readString(srcJSONPath.toPath(), StandardCharsets.US_ASCII);

        JSONTokener tokener = new JSONTokener(marshalled);
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
                ArrayList<Window> windows = new ArrayList<Window>();
                for (int i = 0; i < object.getJSONObject(key).getInt("windows"); i++) {
                    windows.add(new Window());
                }
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