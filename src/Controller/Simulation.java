package Controller;

import Model.House;
import Model.Room;
import Model.User;
import Model.UserType;
import org.json.JSONException;
import java.sql.Time;
import java.util.ArrayList;
import java.io.File;
import java.io.IOException;
/**
 * represents a simulation
 */
public class Simulation{
	private String date;
	private Time time;
	private float temperature;
	private User loggedInUser;
	private ArrayList<User> users;
	private House house;
	private boolean running;

  /**
	 * creates a simulation object with date, time, temperature, houseinput as input
	 * @param date a date object
	 * @param time a time object
	 * @param temperature temperature outside of the house
	 * @param houseInput JSON file
	 * @exception JSONException if the file is not found
	 */
	public Simulation(String date, Time time, float temperature, File houseInput, boolean running) throws JSONException, IOException {
		this.house = new House(houseInput);
		this.date = date;
		this.time = time;
		this.temperature = temperature;
		this.loggedInUser = new User(UserType.PARENT, house.getRooms().get(0), "Admin", "123456");
		this.users = new ArrayList<User>();
		addUser(this.loggedInUser);
		this.running = true;
	}

	/**
	 * adds user to an arraylist
	 * @param user user object
	 */
	// adds user to users ArrayList
	public void addUser(User user) {
		// user is null
		if (user == null) return;

		if (this.users.contains(user)) {
			System.err.println("Cannot add user.  That exact user exists already.");
			return;
		}

		// a user with that id already exists
		if (this.findUserFromID(user.getID()) != null) {
			System.err.println("Cannot add user.  A user with that ID exists already.");
			return;
		}

		users.add(user);
	}

	public String addUser(String username, String password, String type, String location) {
		UserType userType = null;
		if (username.length() == 0) {
			return "ERROR: Name field must not be empty.";
		}
		else if (type == null) {
			return "ERROR: Type field must not be empty.";
		}
		else if (password.length() == 0) {
			return "ERROR: Password cannot be of length 0.";
		}
		else if (location == null) {
			return "ERROR: Must pick an initial position";
		}
		else if (type == "Parent") { userType = UserType.PARENT; }
		else if (type == "Child") { userType = UserType.CHILD; }
		else if (type == "Guest") { userType = UserType.GUEST; }
		else if (type == "Stranger") { userType = UserType.STRANGER; }
		else {
			return "ERROR: Unhandled add user case. You did something weird.";
		}

		for (User u : users) {
			if (u.getName().equals(username)) {
				return "User with given name already exists.";
			}
		}

		// Add user to simulation
		Room startingRoom = null;
		for (Room r : house.getRooms()) {
			if (r.getName().equals(location)) {
				startingRoom = r;
			}
		}
		addUser(new User(
				userType,
				startingRoom,
				username,
				password
		));

		return "Successfully added " + username + " as a " + type.toLowerCase() + " user.";
	}

  /**
	 * removes user from an arraylist
	 * @param user an existing user object
	 */
	public void removeUser(User user) {
		if (user == null) return;
		else if (user == loggedInUser) return;
		users.remove(user);
	}

	public String removeUser(String choice) {

		if (choice == null) {
			return "ERROR: Please choose a user to delete";
		}
		if (loggedInUser.getType() != UserType.PARENT) {
			return "ERROR: Need admin/parental privileges to delete a user.";
		}

		// extract id of choice
		// choice is a string shaped like: "Admin (ID)"
		choice = choice.substring(choice.indexOf("(")+1,choice.indexOf(")"));
		for (User u : users) {
			if (u.getID() == Integer.parseInt(choice)) {
				if (u.getID() == loggedInUser.getID()) {
					return "ERROR: Cannot delete logged in user.";
				}
				removeUser(u);
				return "Successfully removed user.";
			}
		}

		// Should never reach this point.
		return "ERROR: Could not find the given user to delete.";
	}

	/**
	 * finds the user using their ID
	 * @param id the ID of a user
	 * @return a user object if found, null if not
	 */
	public User findUserFromID(int id) {
		try {
			User foundUser = this.users.stream().filter(user -> user.getID() == id).findFirst().get();
			return foundUser;
		} catch (Exception e){
			return null;
		}
	}

	/**
	 * logs out the current user
	 */
	// logouts the current logged in user
	public void logout() {
		if (this.loggedInUser != null) this.loggedInUser = null;
	}

	/**
	 * prints all users
	 */
	// DEBUG METHOD to print all users in users ArrayList
	public void printUsers() {
		this.users.stream().forEach(user -> System.out.println(user));
	}

	/**
	 * gets the date on the simulation
	 * @return Date object
	 */
	public String getDate() {
		return date;
	}

	/**
	 * sets the date of the simulation
	 * @param date
	 */
	public String setDate(String date) {
		this.date = date;
		return "Date set to: " + date;
	}

	/**
	 * gets the time of the simulation
	 * @return Time object
	 */
	public Time getTime() {
		return time;
	}

	/**
	 * sets the time of the simulation using time object
	 * @param time new time to be set
	 * @return a string: "time updated"
	 */
	public String setTime(Time time) {
		this.time = time;
		return "Time set to " + time + ".";
	}

	/**
	 * get the temperature outside fo teh house in the simulation
	 * @return the temperature outside fo the house
	 */
	public float getTemperature() {
		return temperature;
	}

	public String setTemperature(String temperatureString) {
		try {
			float newTemperature = Float.parseFloat(temperatureString);
			this.temperature = newTemperature;
			return ("Setting simulation temperature to " + temperatureString + "!");
		} catch (Exception e) {
			return ("ERROR: Inputted temperature is not a valid float.");
		}
	}

	/**
	 * set the new temperature of the simulation
	 * @param temperature new temperature to be set
	 */
	public void setTemperature(float temperature) {
		this.temperature = temperature;
	}

	/**
	 * gets the current logged in user
	 * @return User object
	 */
	public User getLoggedInUser() {
		return loggedInUser;
	}

	/**
	 * set a user to a logged in user
	 * @param loggedInUser a User object
	 */
	public void setLoggedInUser(User loggedInUser) {
		this.loggedInUser = loggedInUser;
	}

	public String setLoggedInUserLocation(String location) {
		if (location == null) return "ERROR: Need to pick a location.";
		if (location.equals("Outside")) {
			loggedInUser.setLocation(null);
			return "Moved user outside.";
		}
		for (Room r : house.getRooms()) {
			if (r.getName().equals(location)) {
				loggedInUser.setLocation(r);
				return "Successfully changed logged in users location.";
			}
		}

		// This should never occur... Only existing rooms are available inputs in a dropdown.
		return "ERROR: Could not find any room matching the input.";
	}

	public String login(String username, String password) {
		for (User u : users) {
			if (u.getName().equals(username)) {
				if (u.getPassword().equals(password)) {
					if (u == loggedInUser) {
						return "ERROR: Already logged into this user.";
					}
					setLoggedInUser(u);
					return "Successfully switched users.";
				}
				else {
					return "ERROR: Incorrect password";
				}
			}
		}


		return "ERROR: Could not find any user with the provided username.";
	}

	public String editUser(String username, String currentPassword, String newPassword, String type, String location) {

		if (username == null) {
			return "ERROR: Did not choose a user to edit.";
		}
		else if (currentPassword.length() == 0) {
			return "ERROR: Need to enter the chosen user's password to make changes.";
		}
		else if (type == null &&
				newPassword.length() == 0 &&
				location == null
		) {
			return "ERROR: Need to give changes to make.";
		}

		User toChange = null;
		int userID = Integer.parseInt(username.substring(username.indexOf("(")+1,username.indexOf(")")));
		for (User u : users) {
			if (u.getID() == userID) {
				toChange = u;
				break;
			}
		}

		if (toChange == null) {
			return "ERROR: Somehow did not find user chosen from dropdown list... Try again.";
		}
		if (!toChange.getPassword().equals(currentPassword)) {
			return "ERROR: Entered password is incorrect for chosen user.";
		}
		if (currentPassword.equals(newPassword)) {
			return "ERROR: Entered current and new password are the same.";

		}

		UserType userType = null;
		if (type == "Parent") { userType = UserType.PARENT; }
		else if (type == "Child") { userType = UserType.CHILD; }
		else if (type == "Guest") { userType = UserType.GUEST; }
		else if (type == "Stranger") { userType = UserType.STRANGER; }

		// Change user type if that had input
		if (userType != null) {
			toChange.setType(userType);
		}
		// Change password if that had input
		if (newPassword.length() != 0) {
			toChange.setPassword(newPassword);
		}

		// Change location if that had input
		if (location.equals("Outside")) {
			loggedInUser.setLocation(null);
		}
		else if (location != null) {
			for (Room r : house.getRooms()) {
				if (r.getName().equals(location)) {
					loggedInUser.setLocation(r);
					break;
				}
			}
		}

		return "Successfully made requested changes to user.";
	}

	public String setHouseLocation(String location) {
		this.house.setLocation(location);
		return "Set house location to " + this.house.getLocation() + "!";
	}

	public String getHouseLocation() {
		return this.house.getLocation();
	}

  /**
	 * gets a list of all the simulation users
	 * @return a list of users
	 */
	public ArrayList<User> getUsers() { return users; }

	/**
	 * get the house object of a simulation
	 * @return a House object
	 */
	public House getHouse() { return house; }
	
	public boolean getRunning() {
		return this.running;
	}
	
	public void setRunning(boolean running) {
		this.running = running;
	}

	public String toggleRunning() {
		if (this.running) {
			this.running = false;
			return "Simulation OFF";
		}
		this.running = true;
		return "Simulation ON";
	}

	@Override
	public String toString() {
		return "Simulation [date=" + date + ", time=" + time + ", temperature=" + temperature + ", loggedInUser="
				+ loggedInUser + "]";
	}



}
