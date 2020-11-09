package Tests;

import Controller.Simulation;
import Model.User;
import org.json.JSONException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.*;

class ManageUsersTest {
    private static final File HOUSE_FILE = new File("./houseinput.json");
    private final File USER_FILE = new File("test.json");

    @Test
    void addUser() throws IOException, JSONException {
        Simulation simulation = Simulation.createInstance(new String(), java.sql.Time.valueOf(LocalTime.now()), 25, HOUSE_FILE, USER_FILE);
        String username = "testboy";
        String password = "123456";
        String type = "Stranger";
        String location = "Garage";

        assertEquals("Successfully added " + username + " as a " + type.toLowerCase() + " user.", simulation.addUser(username, password, type, password));
    }

    @Test
    void removeUser() throws IOException, JSONException {
        Simulation simulation =Simulation.createInstance(new String(), java.sql.Time.valueOf(LocalTime.now()), 25, HOUSE_FILE, USER_FILE);
        String username = "testboy";
        String password = "123456";
        String type = "Stranger";
        String location = "Garage";

        simulation.addUser(username, password, type, password);

        User testUser = simulation.getUsers().get(1);
        assertEquals("Successfully removed user.", simulation.removeUser(testUser.getName() + "(" + testUser.getID() + ")"));
    }

    @Test
    void setUserLocation() throws IOException, JSONException {
        Simulation simulation = Simulation.createInstance(new String(), java.sql.Time.valueOf(LocalTime.now()), 25, HOUSE_FILE, USER_FILE);
        User testUser = simulation.getUsers().get(0);
        assertEquals("Successfully made requested changes to user.", simulation.editUser(testUser.getName() +"(" + testUser.getID() + ")", testUser.getPassword(),"", "", "Kitchen"));
    }

    @Test
    void setLoggedInUserLocation() throws IOException, JSONException {
        Simulation simulation = Simulation.createInstance(new String(), java.sql.Time.valueOf(LocalTime.now()), 25, HOUSE_FILE, USER_FILE);
        assertEquals("Successfully changed logged in user's location.", simulation.setLoggedInUserLocation("Kitchen"));
    }

    @Test
    void loginUser() throws IOException, JSONException {
        Simulation simulation = Simulation.createInstance(new String(), java.sql.Time.valueOf(LocalTime.now()), 25, HOUSE_FILE, USER_FILE);
        String username = "testboy2";
        String password = "123456";
        String type = "Stranger";
        String location = "Garage";

        simulation.addUser(username, password, type, password);
        assertEquals("Successfully switched users.", simulation.login(username, password));
    }

    @AfterEach
    void deleteUserFIle() {
        USER_FILE.delete();
    }


}