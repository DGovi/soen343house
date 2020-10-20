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
        assertEquals(simulation.addUser(username, password, type, password), "Successfully added " + username + " as a " + type.toLowerCase() + " user.");
    }

    @Test
    void removeUser() {

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