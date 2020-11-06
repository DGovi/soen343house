package Model;

import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * Represents a user actor in a simulation.
 * A user is someone who can interact with
 * the simulation.
 * A user has a type, an ID,
 * a location in the house (room name)
 * a name and a password.
 */
public class User {
    static private int counter = 0;

    private UserType type;
    private final int ID;
    private Room location;
    private final String name;
    private String password;

    /**
     * Creates a user with a type, a location, a name, and a password
     *
     * @param type     is one of the four types
     * @param location the location the user is in the house
     * @param name     name of the user
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
     * Gets the type of the user.
     *
     * @return type
     */
    public UserType getType() {
        return type;
    }

    /**
     * Sets the type of a user.
     *
     * @param type one of the four types
     */
    public void setType(UserType type) {
        this.type = type;
    }

    /**
     * Gets the id of the user.
     *
     * @return the id of the user
     */
    public int getID() {
        return ID;
    }

    /**
     * Gets the location of the user.
     *
     * @return a room object
     */
    public Room getLocation() {
        return location;
    }

    /**
     * Sets the location of a user.
     *
     * @param location room object in which a user is in
     */
    public void setLocation(Room location) {
        this.location = location;
    }

    /**
     * Gets the name of a user.
     *
     * @return the name of a user
     */
    public String getName() {
        return this.name;
    }

    /**
     * Gets the password for a user.
     *
     * @return the users password
     */
    public String getPassword() {
        return this.password;
    }

    /**
     * Sets a new password for the user.
     *
     * @param password the new password for the user
     */
    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        return "User [type=" + type + ", ID=" + ID + ", location=" + location + "]";
    }


}
