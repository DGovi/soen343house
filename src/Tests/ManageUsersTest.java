package Tests;

import Controller.Simulation;
import Model.User;
import org.json.JSONException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.time.LocalTime;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

class ManageUsersTest {

    private static Simulation simulation;
    private static final File HOUSE_FILE = new File("./houseinput.json");

    @BeforeAll
    static void resetSimulation() throws IOException, JSONException {
        // reset the simulation
        simulation = new Simulation(new String(), java.sql.Time.valueOf(LocalTime.now()), 25, HOUSE_FILE, true);
    }

    @Test
    void addUser() {
        String username = "testboy";
        String password = "123456";
        String type = "Stranger";
        String location = "Garage";

        assertEquals("Successfully added " + username + " as a " + type.toLowerCase() + " user.", simulation.addUser(username, password, type, password));
    }

    @Test
    void removeUser() {
        String username = "testboy";
        String password = "123456";
        String type = "Stranger";
        String location = "Garage";

        simulation.addUser(username, password, type, password);

        User testUser = simulation.getUsers().get(1);
        assertEquals("Successfully removed user.", simulation.removeUser(testUser.getName() + "(" + testUser.getID() + ")"));
    }

    @Test
    void setUserLocation() {
        User testUser = simulation.getUsers().get(0);
        assertEquals("Successfully made requested changes to user.", simulation.editUser(testUser.getName() +"(" + testUser.getID() + ")", testUser.getPassword(),"", "", "Kitchen"));
    }

    @Test
    void setLoggedInUserLocation() {
        assertEquals("Successfully changed logged in users location.", simulation.setLoggedInUserLocation("Kitchen"));
    }

    @Test
    void loginUser() {
        String username = "testboy";
        String password = "123456";
        String type = "Stranger";
        String location = "Garage";

        simulation.addUser(username, password, type, password);
        assertEquals("Successfully switched users.", simulation.login(username, password));
    }


}