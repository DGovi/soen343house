package Model;

import org.json.JSONException;
import java.sql.Time;
import java.util.ArrayList;
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

	/**
	 * creates a simulation object with date, time, temperature, houseinput as input
	 * @param date a date object
	 * @param time a time object
	 * @param temperature temperature outside of the house
	 * @param houseInput JSON file
	 * @exception JSONException if the file is not found
	 */
	public Simulation(String date, Time time, float temperature, String houseInput) throws JSONException {
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

	/**
	 * removes user from an arraylist
	 * @param user an existing user object
	 */
	// remove user from users ArrayList
	public void removeUser(User user) {
		if (user == null) return;
		users.remove(user);
	}

	/**
	 * finds the user using their ID
	 * @param id the ID of a user
	 * @return a user object if found, null if not
	 */
	// finds and returns the user with the given ID
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
		return "Time updated.";
	}

	/**
	 * get the temperature outside fo teh house in the simulation
	 * @return the temperature outside fo the house
	 */
	public float getTemperature() {
		return temperature;
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

	@Override
	public String toString() {
		return "Simulation [date=" + date + ", time=" + time + ", temperature=" + temperature + ", loggedInUser="
				+ loggedInUser + "]";
	}



}
