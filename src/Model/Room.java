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

    /**
     * creates a room object with a name, windows,
     * lights, and doors
     * @param name the name of the room
     * @param windows an arraylist of windows
     * @param lights number of windows
     * @param doors number of doors
     */
    Room(String name, ArrayList<Window> windows, int lights, ArrayList<String> doors) {
        this.name = name;
        this.windows = windows;
        this.lights = lights;
        this.doors= doors;
    }


    /**
     * gets an arraylist of doors
     * @return an arraylist of doors
     */
    public ArrayList<String> getDoors() {
		return doors;
	}

    /**
     * set the doors in room
     * @param doors a list of doors
     */
	public void setDoors(ArrayList<String> doors) {
		this.doors = doors;
	}

    /**
     * get the name of the room
     * @return the name of the room
     */
	public String getName() {
		return name;
	}

    /**
     * set the name of a room
     * @param name the new name of a room
     */
	public void setName(String name) {
		this.name = name;
	}

    /**
     * get the windows of a room
     * @return the list of windows
     */
	public ArrayList<Window> getWindows() {
		return windows;
	}

    /**
     * get the numbers of lights in a room
     * @return the number of lights in a room
     */
	public int getLights() {
		return lights;
	}

    /**
     * set the number of lights in a room
     * @param lights the new number of lights in a room
     */
	public void setLights(int lights) {
		this.lights = lights;
	}

    /**
     * creates room objects from a inputted JSON file
     * @param srcJSONPath a file that is needed as input
     * @return a list of room objects
     * @exception org.json.JSONException if the file is not found
     */
    public static ArrayList<Room> roomFromJSON(File srcJSONPath) throws org.json.JSONException, IOException {
        String marshalled = new String(Files.readAllBytes(srcJSONPath.toPath()));


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