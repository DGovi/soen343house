package Controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.Stack;
import java.util.Date;
import java.time.LocalTime;

import View.InputWindow;
import org.json.JSONException;

import Model.*;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

public class DashboardController {
	private Simulation sim;
	private int windowLength = 30;
	private int ROOM_SIZE = 50;

	@FXML private Label temperatureLabel;
	@FXML private Label currentUser;
	@FXML private ComboBox<String> currentUserLocationOptions;
	@FXML private TextField loginName;
	@FXML private PasswordField loginPassword;
	@FXML private TextField createUserName;
	@FXML private PasswordField createUserPassword;
	@FXML private ComboBox<String> createUserType;
	@FXML private ComboBox<String> createUserLocation;
	@FXML private ComboBox<String> editUserChoice;
	@FXML private PasswordField editUserCurrentPassword;
	@FXML private PasswordField editUserNewPassword;
	@FXML private ComboBox<String> editUserType;
	@FXML private ComboBox<String> editUserLocation;
	@FXML private ComboBox<String> deleteUserChoice;
	@FXML private TextArea console;
    @FXML private Canvas render;
    GraphicsContext gc;
  
  @FXML private void changeTemperature() {
		String newTemperature = InputWindow.display("Change Temperature", "New Temperature");
		try {
			float newTemperatureInt = Float.parseFloat(newTemperature);
			sim.setTemperature(newTemperatureInt);
			updateDashboard();
			printToConsole("Setting simulation temperature to " + newTemperature + "!");
		} catch (Exception e) {
			printToConsole("ERROR: Inputted temperature is not a valid float.");
		}
	}

  @FXML private void editCurrentUserLocation() {
    	printToConsole(sim.setLoggedInUserLocation(currentUserLocationOptions.getValue()));
    	updateDashboard();
	}

	@FXML private void login() {
		printToConsole(sim.login(loginName.getText(), loginPassword.getText()));
		updateDashboard();
	}

	@FXML private void createUser() {
		printToConsole(
				sim.addUser(
						createUserName.getText(),
						createUserPassword.getText(),
						createUserType.getValue(),
						createUserLocation.getValue()
				)
		);

		// Update other dropdownlist
		updateDashboard();
	}

	@FXML private void editUser() {
		printToConsole(
				sim.editUser(
						editUserChoice.getValue(),
						editUserCurrentPassword.getText(),
						editUserNewPassword.getText(),
						editUserType.getValue(),
						editUserLocation.getValue()
				)
		);
		updateDashboard();
	}

	@FXML private void deleteUser() {
		printToConsole(sim.removeUser(deleteUserChoice.getValue()));
		updateDashboard();
	}

	private void printToConsole(String output) {
		console.appendText(output + "\n");
	}

	// Use whenever there is a change to users (logged in, names, or number of users)
	private void updateDashboard() {
    // reset simulation temperature
		temperatureLabel.setText(Float.toString(sim.getTemperature()));
    
		// reset info of logged in user
		currentUser.setText(sim.getLoggedInUser().getName());
		if (sim.getLoggedInUser().getLocation() == null) {
			currentUserLocationOptions.valueProperty().set("Outside");
		}
		else {
			currentUserLocationOptions.valueProperty().set(sim.getLoggedInUser().getLocation().getName());
		}

		// reset list of users
		editUserChoice.getItems().clear();
		deleteUserChoice.getItems().clear();
		for (User u : sim.getUsers()) {
			editUserChoice.getItems().add(u.getName() + " (" + u.getID() + ")");
			deleteUserChoice.getItems().add(u.getName() + " (" + u.getID() + ")");
		}

		// Reset field content
		loginName.setText("");
		loginPassword.setText("");
		createUserName.setText("");
		createUserPassword.setText("");
		createUserType.valueProperty().set(null);
		createUserLocation.valueProperty().set(null);
		editUserCurrentPassword.setText("");
		editUserNewPassword.setText("");
	}

	// Basically the constructor --> Sets variables
	public void initialize() throws JSONException {

		// Set simulation
		sim = new Simulation(
				new Date(),
				java.sql.Time.valueOf(LocalTime.now()),
				25,
				"houseinput.json"
		);
		currentUser.setText(sim.getLoggedInUser().getName());

		// Set dropdown options for user type dropdowns
		createUserType.getItems().setAll("Parent", "Child", "Guest", "Stranger");
		editUserType.getItems().setAll("Parent", "Child", "Guest", "Stranger");

		// Set dropdown options for locations
		for (Room r : sim.getHouse().getRooms()) {
			createUserLocation.getItems().add(r.getName());
			editUserLocation.getItems().add(r.getName());
			currentUserLocationOptions.getItems().add(r.getName());
		}
		createUserLocation.getItems().add("Outside");
		editUserLocation.getItems().add("Outside");
		currentUserLocationOptions.getItems().add("Outside");

		// Set dropdown options for dropdowns with users
		updateDashboard();
		renderLayout();

	}
    
    @FXML public void renderLayout() throws JSONException {
	    
	    Model.House h = new Model.House("houseinput.json");

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
		drawWindows(startX, startY, firstRoom.getDoors().size() * ROOM_SIZE, firstRoom.getWindows().size());
		
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
				int countWindows = room.getWindows().size();
				
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
	
	@FXML public void drawWindows(int x, int y, int size, int countWindows) {
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
    
    @FXML public void drawLights(Room room, int x, int y, int size) {
    	for(int i = 0; i < room.getLights(); i++) {
			gc.setFill(Color.GOLD);
			int offset = (size - 35);
			int xC = x + 10 + (i * 20) % offset;
			int yC = (i * 20) / offset * 20 + y + 25;
			gc.fillOval(xC, yC, 10, 10);
		}
    	gc.setFill(Color.BLACK);
    }

	@FXML public void drawRoom(Room room, int x, int y, boolean sideDoor) {
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
	
	@FXML public void endSim() {
		
		System.exit(0);
	}
	
    
}
