package Tests;

import Model.Room;
import Model.Simulation;
import Model.User;
import Model.UserType;
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

        User testUser = simulation.getUsers().get(1);
        assertEquals("Successfully removed user.", simulation.removeUser(testUser.getName() + "(" + testUser.getID() + ")"));
    }

    @Test
    void setUserLocation() throws JSONException {
        Simulation simulation = new Simulation(new Date(), java.sql.Time.valueOf(LocalTime.now()), 25, "houseinput.json");
        User testUser = simulation.getUsers().get(0);
        assertEquals("Successfully made requested changes to user.", simulation.editUser(testUser.getName() +"(" + testUser.getID() + ")", testUser.getPassword(),"", "", "Kitchen"));
    }

    @Test
    void setLoggedInUserLocation() throws JSONException {
        Simulation simulation = new Simulation(new Date(), java.sql.Time.valueOf(LocalTime.now()), 25, "houseinput.json");
        assertEquals("Successfully changed logged in users location.", simulation.setLoggedInUserLocation("Kitchen"));
    }

    @Test
    void loginUser() throws JSONException {
        String username = "testboy";
        String password = "123456";
        String type = "Stranger";
        String location = "Garage";

        Simulation simulation = new Simulation(new Date(), java.sql.Time.valueOf(LocalTime.now()), 25, "houseinput.json");
        simulation.addUser(username, password, type, password);
        assertEquals("Successfully switched users.", simulation.login(username, password));
    }


}