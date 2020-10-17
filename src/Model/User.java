package Model;

public class User {
	static private int counter = 0;

	private UserType type;
	private int ID;
	private Room location;
	private String name;
	private String password;

	public User(UserType type, Room location, String name, String password) {
		this.type = type;
		this.ID = counter++;
		this.location = location;
		this.name = name;
		this.password = password;
	}

	public UserType getType() {
		return type;
	}

	public void setType(UserType type) {
		this.type = type;
	}

	public int getID() {
		return ID;
	}

	public Room getLocation() {
		return location;
	}

	public void setLocation(Room location) {
		this.location = location;
	}

	public String getName() { return this.name; }

	public String getPassword() { return this.password; }

	public void setPassword(String password) { this.password = password; }

	@Override
	public String toString() {
		return "User [type=" + type + ", ID=" + ID + ", location=" + location + "]";
	}
	
	
	
}
