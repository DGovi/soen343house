package Dashboard;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.Stack;

import org.json.JSONException;

import House.Room;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

public class DashboardController {
    @FXML private Canvas render;
    GraphicsContext gc;
    
    private int windowLength = 30;
    private int ROOM_SIZE = 50;
    
    public void renderLayout() throws JSONException {
	    
	    House.House h = new House.House("houseinput.json");

	    gc = render.getGraphicsContext2D();
	    
	    gc.setFill(Color.WHITE);
	    gc.fillRect(0, 0, 800, 800);

	    gc.setFont(Font.font(12));
	    gc.setFill(Color.BLACK);
	    gc.setStroke(Color.BLACK);
	    
	    int startX = 15;
	    int startY = 15;
	    
		Stack<Room> stack = new Stack<Room>();
		Set<String> traversed = new HashSet<String>();
		HashMap<String, Room> doors = new HashMap<String, Room>();
		HashMap<Room, javafx.util.Pair<Integer, Integer>> coordinates = 
						new HashMap<Room, javafx.util.Pair<Integer, Integer>>();
		
		for(Room r : h.getRooms()) 
			doors.put(r.getName(), r);
		
		Room firstRoom = h.getRooms().get(0);
		stack.add(firstRoom);
		traversed.add(firstRoom.getName());
		coordinates.put(firstRoom, new javafx.util.Pair<Integer, Integer>(Integer.valueOf(startX), Integer.valueOf(startY)));
		
		drawRoom(firstRoom, startX, startY, false);
		
		while(!stack.empty()) {
			Room top = stack.pop();
			int xParent = 0;
			
			ArrayList<String> doorsTop = top.getDoors();
			
			for (String door : doorsTop) {
				if(traversed.contains(door))
					continue;
					
				Room room = doors.get(door);
				stack.add(doors.get(door));
				traversed.add(door);
				
				int x = coordinates.get(top).getKey().intValue() + xParent;
				int y = coordinates.get(top).getValue().intValue() + ROOM_SIZE  * top.getDoors().size();
				int size = room.getDoors().size() * ROOM_SIZE;
				int countWindows = room.getWindows();
				
				this.drawRoom(room, x, y, door != doorsTop.get(doorsTop.size()-1) && doorsTop.size() > 1);
				
				if(door == doorsTop.get(doorsTop.size() - 1))
					drawWindows(x, y, size, countWindows);
				else if(door == doorsTop.get(0) || doorsTop.size() > 1 && door == doorsTop.get(1))
					drawWindows(x - size, y, size, countWindows);
				
				coordinates.put(room, new javafx.util.Pair<Integer, Integer>(Integer.valueOf(x), Integer.valueOf(y)));
				
				xParent = size;
			}
		}
    }
	
	public void drawWindows(int x, int y, int size, int countWindows) {
    	for(int i = 0; i < countWindows; i++) {
			gc.setStroke(Color.LIGHTBLUE);
			gc.setLineWidth(3);
			int gap = 15;
			int offset = (size - windowLength * countWindows - gap * (countWindows - 1))/2;
			gc.strokeLine(x + size, y + offset + i * (windowLength + gap), x + size, y + offset + windowLength + i * (windowLength + gap));
		}
    	gc.setLineWidth(1);
    	gc.setStroke(Color.BLACK);
    	gc.setFill(Color.BLACK);
    }
    
    public void drawLights(Room room, int x, int y, int size) {
    	for(int i = 0; i < room.getLights(); i++) {
			gc.setFill(Color.GOLD);
			int offset = (size - 35);
			int xC = x + 10 + (i * 20) % offset;
			int yC = (i * 20) / offset * 20 + y + 25;
			gc.fillOval(xC, yC, 10, 10);
		}
    	gc.setFill(Color.BLACK);
    }

	public void drawRoom(Room room, int x, int y, boolean sideDoor) {
    	int size = 50 * room.getDoors().size();
		gc.strokeRoundRect(x, y, size, size, 0, 0);
		drawLights(room, x, y, size);
		
		gc.setLineWidth(3);
		gc.strokeLine(x + 15, y, x + 30, y);
		if(sideDoor)
			gc.strokeLine(x+size, y + 20, x + size, y + 40);
		
		gc.setLineWidth(1);
		gc.setStroke(Color.BLACK);
		gc.fillText(room.getName(), x + 5, y + 17);
    }
    
}
