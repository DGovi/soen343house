package Model;

import org.json.JSONException;
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
	private boolean running;

	public Simulation(Date date, Time time, float temperature, String houseInput, boolean running) throws JSONException {
		this.house = new House(houseInput);
		this.date = date;
		this.time = time;
		this.temperature = temperature;
		this.loggedInUser = new User(UserType.PARENT, house.rooms.get(0), "Admin", "123456");
		this.users = new ArrayList<User>();
		addUser(this.loggedInUser);
		this.running = true;
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

	// remove user from users ArrayList
	public void removeUser(User user) {
		if (user == null) return;
		users.remove(user);
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

	public void setTemperature(float temperature) {
		this.temperature = temperature;
	}

	public User getLoggedInUser() {
		return loggedInUser;
	}

	public void setLoggedInUser(User loggedInUser) {
		this.loggedInUser = loggedInUser;
	}

	public ArrayList<User> getUsers() { return users; }

	public House getHouse() { return house; }
	
	public boolean getRunning() {
		return this.running;
	}
	
	public void setRunning(boolean running) {
		this.running = running;
	}

	@Override
	public String toString() {
		return "Simulation [date=" + date + ", time=" + time + ", temperature=" + temperature + ", loggedInUser="
				+ loggedInUser + "]";
	}



}
