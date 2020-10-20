package Tests;

import Model.Simulation;
import org.json.JSONException;
import org.junit.jupiter.api.Test;

import java.time.LocalTime;
import java.util.Date;
import java.util.Timer;

import static org.junit.jupiter.api.Assertions.*;

class ManageUsersTest {

    @Test
    void addUser() throws JSONException {
        String username = "testboy";
        String password = "123456";
        String type = "Stranger";
        String location = "Garage";

        Simulation simulation = new Simulation(new Date(), java.sql.Time.valueOf(LocalTime.now()), 25, "houseinput.json");
        assertEquals("Successfully added " + username + " as a " + type.toLowerCase() + " user.", simulation.addUser(username, password, type, password));
    }

    @Test
    void removeUser() throws JSONException {
        String username = "testboy";
        String password = "123456";
        String type = "Stranger";
        String location = "Garage";

        Simulation simulation = new Simulation(new Date(), java.sql.Time.valueOf(LocalTime.now()), 25, "houseinput.json");
        simulation.addUser(username, password, type, password);
        assertEquals( "Successfully removed user.", simulation.removeUser(username + " (3)")); // fix later
    }

    @Test
    void setUserLocation() {

    }

    @Test
    void loginUser() {

    }

    @Test
    void logoutUser() {

    }


}