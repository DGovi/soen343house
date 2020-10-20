package Model;

/**
 * represents a user in a simulation
 * a user is someone who can interact with
 * the simulation
 * a user has a type, an ID,
 * a location in the house (room name)
 * a name and a password
 */
public class User {
	static private int counter = 0;

	private UserType type;
	private int ID;
	private Room location;
	private String name;
	private String password;

	/**
	 * creates a user with a type, a location, a name, and a password
	 * @param type is one of the four types
	 * @param location the location the user is in the house
	 * @param name name of the user
	 * @param password the password set by the user
	 */
	public User(UserType type, Room location, String name, String password) {
		this.type = type;
		this.ID = counter++;
		this.location = location;
		this.name = name;
		this.password = password;
	}

	/**
	 * gets the type of the user
	 * @return type
	 */
	public UserType getType() {
		return type;
	}

	/**
	 * sets the type of a user
	 * @param type one of the four types
	 */
	public void setType(UserType type) {
		this.type = type;
	}

	/**
	 * gets the id of the user
	 * @return the id of the user
	 */
	public int getID() {
		return ID;
	}

	/**
	 * gets the location of the user
	 * @return a room object 
	 */
	public Room getLocation() {
		return location;
	}

	/**
	 * sets the locatino of a user
	 * @param location room object in which a user is in
	 */
	public void setLocation(Room location) {
		this.location = location;
	}

	/**
	 * get the name of a user
	 * @return the name of a user
	 */
	public String getName() { return this.name; }

	/**
	 * get the password for a user
	 * @return the users password
	 */
	public String getPassword() { return this.password; }

	/**
	 * sets a new password for the user
	 * @param password the new password for the user
	 */
	public void setPassword(String password) { this.password = password; }

	@Override
	public String toString() {
		return "User [type=" + type + ", ID=" + ID + ", location=" + location + "]";
	}
	
	
	
}
