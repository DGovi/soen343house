package House;
import soen343house.UserType;

public class User {
	private UserType type;
	private int ID;
	private Room location;

	public User(UserType type, int ID, Room location) {
		this.type = type;
		this.ID = ID;
		this.location = location;
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

	public void setID(int iD) {
		ID = iD;
	}

	public Room getLocation() {
		return location;
	}

	public void setLocation(Room location) {
		this.location = location;
	}

	@Override
	public String toString() {
		return "User [type=" + type + ", ID=" + ID + ", location=" + location + "]";
	}
	
	
	
}
