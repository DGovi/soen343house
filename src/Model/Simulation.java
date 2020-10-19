package Model;

import org.json.JSONException;
import org.junit.jupiter.api.Test;

import java.sql.Time;
import java.util.ArrayList;
import java.util.Date;

public class Simulation{
	private Date date;
	private Time time;
	private float temperature;
	private User loggedInUser;
	private ArrayList<User> users;
	private House house;

	public Simulation(Date date, Time time, float temperature, String houseInput) throws JSONException {
		this.house = new House(houseInput);
		this.date = date;
		this.time = time;
		this.temperature = temperature;
		this.loggedInUser = new User(UserType.PARENT, house.rooms.get(0), "Admin", "123456");
		this.users = new ArrayList<User>();
		addUser(this.loggedInUser);
	}
	
	// Test main method
//	public static void main(String args[]) {
//		Simulation sim = new Simulation(new Date(), new Time(0), 100, null);
//		sim.addUser(new User(UserType.STRANGER, 0, null));
//		sim.addUser(new User(UserType.STRANGER, 1, null));
//		sim.addUser(new User(UserType.STRANGER, 2, null));
//		sim.addUser(sim.findUserFromID(1));
//		sim.addUser(new User(UserType.STRANGER, 2, null));
//
//		System.out.println(sim.findUserFromID(1));
//		sim.removeUser(sim.findUserFromID(1));
//		sim.removeUser(sim.findUserFromID(1000000));
//		sim.addUser(null);
//
//		sim.setLoggedInUser(sim.findUserFromID(0));
//		System.out.println(sim);
//		sim.logout();
//		sim.logout();
//		System.out.println(sim);
//
//	}

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

	// remove user from users ArrayList
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
		return "ERROR: Could not find the give user to delete.";
	}

	// finds and returns the user with the given ID
	public User findUserFromID(int id) {
		try {
			User foundUser = this.users.stream().filter(user -> user.getID() == id).findFirst().get();
			return foundUser;
		} catch (Exception e){
			return null;
		}
	}

	// logouts the current logged in user
	public void logout() {
		if (this.loggedInUser != null) this.loggedInUser = null;
	}

	// DEBUG METHOD to print all users in users ArrayList
	public void printUsers() {
		this.users.stream().forEach(user -> System.out.println(user));
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public Time getTime() {
		return time;
	}

	public void setTime(Time time) {
		this.time = time;
	}

	public float getTemperature() {
		return temperature;
	}

	public String setTemperature(String temperatureString) {
		try {
			float newTemperature = Float.parseFloat(temperatureString);
			this.temperature = newTemperature;
			return ("Setting simulation temperature to " + this.temperature + "!");
		} catch (Exception e) {
			return ("ERROR: Inputted temperature is not a valid float.");
		}
	}

	public User getLoggedInUser() {
		return loggedInUser;
	}

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
		return "ERROR: Could not find any room matchin the input.";
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
		username = username.substring(username.indexOf("(")+1,username.indexOf(")"));
		for (User u : users) {
			if (u.getID() == Integer.parseInt(username)) {
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

	public ArrayList<User> getUsers() { return users; }

	public House getHouse() { return house; }

	@Override
	public String toString() {
		return "Simulation [date=" + date + ", time=" + time + ", temperature=" + temperature + ", loggedInUser="
				+ loggedInUser + "]";
	}



}
