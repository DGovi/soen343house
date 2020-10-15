package House;

import org.json.JSONException;
import java.util.*; 
import java.io.*; 


import java.util.ArrayList;

public class House {
    protected ArrayList<Room> rooms;

    public ArrayList<Room> getRooms() {
		return rooms;
	}

	public House(String srcJSON) throws JSONException {
        rooms = Room.roomFromJSON(srcJSON);
    }
	
//	public ArrayList<Room> bfsHouse() {
//		
//		Stack<Room> stack = new Stack<Room>();
//		Set<String> traversed = new HashSet<String>();
//		ArrayList<Room> result = new ArrayList<Room>();
//		HashMap<String, Room> doors = new HashMap<String, Room>();
//		
//		if(rooms.size() == 0) return new ArrayList<Room>();
//		
//		for(Room r : rooms)
//			doors.put(r.name, r);
//		
//		Room firstRoom = rooms.get(0);
//		stack.add(firstRoom);
//		traversed.add(firstRoom.name);
//		
//		while(!stack.empty()) {
//			Room top = stack.pop();
//			result.add(top);
//			for (String door : top.doors) {
//				if(!traversed.contains(door)) {
//					stack.add(doors.get(door));
//					traversed.add(door);
//				}
//			}
//		}
//		
//		
//		return result;
//		
//	}
}
