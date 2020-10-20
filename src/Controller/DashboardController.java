package Controller;

import java.sql.Time;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.Stack;
import java.util.Date;
import java.time.LocalTime;

import javafx.event.ActionEvent;
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


	@FXML private Label currentUser;
	@FXML private TextField loginName;
	@FXML private PasswordField loginPassword;
	@FXML private TextField createUserName;
	@FXML private PasswordField createUserPassword;
	@FXML private ComboBox<String> createUserType;
	@FXML private ComboBox<String> editUserChoice;
	@FXML private PasswordField editUserCurrentPassword;
	@FXML private PasswordField editUserNewPassword;
	@FXML private ComboBox<String> editUserType;
	@FXML private ComboBox<String> deleteUserChoice;
	@FXML private TextArea console;
    @FXML private Canvas render;

    @FXML private DatePicker datePicker;
    @FXML private Label dateLabel;
    @FXML private Label timeLabel;
    GraphicsContext gc;

	/**
	 * Allows a user to log in if not already logged in.
	 */
	@FXML private void login() {
		for (User u : sim.getUsers()) {
			if (u.getName().equals(loginName.getText())) {
				if (u.getPassword().equals(loginPassword.getText())) {
					if (u == sim.getLoggedInUser()) {
						printToConsole("ERROR: Already logged into this user.");
						return;
					}
					sim.setLoggedInUser(u);
					updateDashboard();
					printToConsole("Successfully switched users.");
					return;
				}
				else {
					printToConsole("ERROR: Found a user with the same name, but different password.");
					printToConsole("Continuing search...");
				}
			}
		}
		printToConsole("ERROR: Did not find any user with the entered name:" + loginName.getText() + ".");
	}

	/**
	 * Adds a user to the simulation and adds a user type to allow privileges
	 */
	@FXML private void addUser() {
		UserType type = null;
		if (createUserName.getText().length() == 0) {
			printToConsole("ERROR: Name field must not be empty.");
			return;
		}
		else if (createUserType.getValue() == null) {
			printToConsole("ERROR: Type field must not be empty.");
			return;
		}
		else if (createUserPassword.getText() == null) {
			printToConsole("ERROR: Password cannot be null.");
		}
		else if (createUserType.getValue() == "Parent") { type = UserType.PARENT; }
		else if (createUserType.getValue() == "Child") { type = UserType.CHILD; }
		else if (createUserType.getValue() == "Guest") { type = UserType.GUEST; }
		else if (createUserType.getValue() == "Stranger") { type = UserType.STRANGER; }
		else {
			printToConsole("ERROR: Unhandled add user case. You did something weird.");
			return;
		}

		// Add user to simulation
		sim.addUser(new User(
				type,
				sim.getHouse().getRooms().get(0),
				createUserName.getText(),
				createUserPassword.getText()
		));

		// Update other dropdownlist
		updateDashboard();

		// Print action to console
		printToConsole(
				"Successfully added " +
						createUserName.getText() +
						" as a " +
						(createUserType.getValue()).toLowerCase() +
						" user."
		);

		// Reset field content
		createUserName.setText("");
	}

	/**
	 * Edit the information,such as user password, or priveleges,
	 * of a specific user given that they are logged in.
	 */
	@FXML private void editUser() {
		String choice = editUserChoice.getValue();

		if (choice == null) {
			printToConsole("ERROR: Did not choose a user to edit.");
			return;
		}
		else if (editUserCurrentPassword.getText().length() == 0) {
			printToConsole("ERROR: Need to enter the chosen user's password to make changes.");
			return;
		}
		else if (editUserType.getValue() == null && editUserNewPassword.getText().length() == 0) {
			printToConsole("ERROR: Need to give changes to make.");
			return;
		}

		User toChange = null;
		choice = choice.substring(choice.indexOf("(")+1,choice.indexOf(")"));
		for (User u : sim.getUsers()) {
			if (u.getID() == Integer.parseInt(choice)) {
				toChange = u;
				break;
			}
		}

		if (toChange == null) {
			printToConsole("ERROR: Somehow did not find user chosen from dropdown list... Try again.");
			return;
		}
		if (!toChange.getPassword().equals(editUserCurrentPassword.getText())) {
			printToConsole("ERROR: Entered password is not correct for chosen user.");
			return;
		}
		if (editUserCurrentPassword.getText().equals(editUserNewPassword.getText())) {
			printToConsole("ERROR: Current and new password are the same.");
			return;
		}

		UserType type = null;
		if (editUserType.getValue() == "Parent") { type = UserType.PARENT; }
		else if (editUserType.getValue() == "Child") { type = UserType.CHILD; }
		else if (editUserType.getValue() == "Guest") { type = UserType.GUEST; }
		else if (editUserType.getValue() == "Stranger") { type = UserType.STRANGER; }

		if (type == null) {
			toChange.setPassword(editUserNewPassword.getText());
			printToConsole("Set new password for chosen user.");
			return;
		}
		toChange.setPassword(editUserNewPassword.getText());
		toChange.setType(type);
		printToConsole("Set new password and changed type for chosen user.");

		editUserCurrentPassword.setText("");
		editUserNewPassword.setText("");
	}

	/**
	 * User is deleted from the simulation and can no longer use
	 * the simulation.
	 */
	@FXML private void deleteUser() {
		String choice = deleteUserChoice.getValue();
		if (choice == null) {
			printToConsole("ERROR: Please choose a user to delete");
			return;
		}
		if (sim.getLoggedInUser().getType() != UserType.PARENT) {
			printToConsole("ERROR: Need admin/parental privileges to delete a user.");
			return;
		}

		// extract id of choice
		choice = choice.substring(choice.indexOf("(")+1,choice.indexOf(")"));
		for (User u : sim.getUsers()) {
			if (u.getID() == Integer.parseInt(choice)) {
				if (u.getID() == sim.getLoggedInUser().getID()) {
					printToConsole("ERROR: Cannot delete logged in user.");
					return;
				}
				sim.removeUser(u);
				updateDashboard();
				printToConsole("Successfully removed user.");
				return;
			}
		}

		printToConsole("ERROR: Somehow could not find user in dropdown list...");
		printToConsole("CHECK ANY USER UPDATING ACTIONS FOR MISSING DROPDOWN UPDATES.");
		return;
	}

	/**
	 * @param output prints on the console of the simulation the output
	 */
	private void printToConsole(String output) {
		console.appendText(output + "\n");
	}

	// Use whenever there is a change to users (logged in, names, or number of users)
	private void updateDashboard() {
		// reset name of logged in user
		currentUser.setText(sim.getLoggedInUser().getName());

		// reset list of users
		editUserChoice.getItems().clear();
		deleteUserChoice.getItems().clear();
		for (User u : sim.getUsers()) {
			editUserChoice.getItems().add(u.getName() + " (" + u.getID() + ")");
			deleteUserChoice.getItems().add(u.getName() + " (" + u.getID() + ")");
		}

		// updating date and time
		dateLabel.setText("Date is: " + sim.getDate());
		timeLabel.setText(java.sql.Time.valueOf(LocalTime.now()).toString());
	}

	/**
	 * starts up the simulation
	 * @exception  JSONException if JSON file not found
	 */
	// Basically the constructor --> Sets variables
	public void initialize() throws JSONException {

		// Set simulation
		sim = new Simulation(
				new String(),
				java.sql.Time.valueOf(LocalTime.now()),
				25,
				"houseinput.json"
		);
		currentUser.setText(sim.getLoggedInUser().getName());

		// Set dropdown options for user type dropdowns
		createUserType.getItems().setAll("Parent", "Child", "Guest", "Stranger");
		editUserType.getItems().setAll("Parent", "Child", "Guest", "Stranger");

		// Set dropdown options for dropdowns with users
		updateDashboard();
		renderLayout();

	}
    
    private int windowLength = 30;
    private int ROOM_SIZE = 50;

	/**
	 * Takes a JSON file and attempts to render a house layout
	 * for the simulation
	 * @exception JSONException JSON file not found
	 */
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

	/**
	 * On a given house layout, draw the windows at specified location
	 * @param x position on the x coordinate of window
	 * @param y position of y coordinate of window
	 * @param size length of a window
	 * @param countWindows number of windows on  a house layout
	 */
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

	/**
	 * on the house layout in the simulation, draw the available lights
	 * in the room
	 *
	 * @param room which room the lights are in
	 * @param x position on the x coordinate of the lights
	 * @param y position of the y coordinate of the lights
	 * @param size size of the lights
	 */
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

	/**
	 * Draws the room of the house layout just to be sure
	 * @param room the room of a house layout
	 * @param x coordinate of the ex position of the room
	 * @param y coordinate of the y position of the room
	 * @param sideDoor true if the door is on the side (vertical on the house layout), false if not (horizontal)
	 */
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

	/**
	 * displays the given date from the actionEvent
	 * onto the simulation
	 * @param actionEvent event that triggers this method
	 */
	//shows entered date in label box
	public void displayDate(javafx.event.ActionEvent actionEvent) {
		printToConsole(sim.setDate(datePicker.getValue().toString()));
		updateDashboard();
	}

	/**
	 * updates the time displayed on the simulation
	 * @param actionEvent event that triggers the method
	 */
	//shows the time
	public void updateTime(ActionEvent actionEvent) {
		printToConsole(sim.setTime(java.sql.Time.valueOf(LocalTime.now())));
		updateDashboard();
	}
}
