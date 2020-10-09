import java.sql.Time;
import java.util.Date;

public class Simulation {
	private Date date;
	private Time time;
	private float temperature;
	private User loggedInUser;
	
	public Simulation(Date date, Time time, float temperature, User loggedInUser) {
		this.date = date;
		this.time = time;
		this.temperature = temperature;
		this.loggedInUser = loggedInUser;
	}
	
	// Test main method
	public static void main(String args[]) {
		System.out.println("memes");
	}
	
	// logouts the current logged in user
	public void logout() {
		if (this.loggedInUser != null) this.loggedInUser = null;
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

	
}
