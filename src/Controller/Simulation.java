package Controller;

import Model.House;
import Model.Room;
import Model.User;
import Model.UserType;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.nio.file.Files;
import java.sql.Time;
import java.util.ArrayList;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;

/**
 * The primary controller of the smart home simulation.
 * Stores many important simulation variables.
 */
public class Simulation {
    private String date;
    private Time time;
    private float temperature;
    private User loggedInUser;
    private final ArrayList<User> users;
    private final House house;
    private File usersFile;
    private boolean running;

    /**
     * Creates a simulation object with date, time, temperature, houseinput as input.
     *
     * @param date        a date object
     * @param time        a time object
     * @param temperature temperature outside of the house
     * @param houseInput  JSON file
     * @throws JSONException if runtime exception occurs
     * @throws IOException   if the file is not found
     */
    public Simulation(String date, Time time, float temperature, File houseInput) throws JSONException, IOException {
        this.house = new House(houseInput);
        this.date = date;
        this.time = time;
        this.temperature = temperature;
        this.loggedInUser = new User(UserType.PARENT, house.getRooms().get(0), "Admin", "123456");
        this.users = new ArrayList<>();
        addUser(this.loggedInUser);
        this.running = false;
    }

    /**
     * Constructor that includes the JSON file with user information in it
     * @param date current date
     * @param time current time
     * @param temperature current temperature
     * @param houseInput house input JSON file
     * @param usersFile users JSON file
     * @throws JSONException
     * @throws IOException
     */
    public Simulation(String date, Time time, float temperature, File houseInput, File usersFile) throws JSONException, IOException {
        this.house = new House(houseInput);
        this.date = date;
        this.time = time;
        this.temperature = temperature;
        this.usersFile = usersFile;
        this.users = usersFromJSON(usersFile);
        this.loggedInUser = users.get(users.size() - 1);
        this.running = true;
    }

    /**
     * Adds user to users list.
     *
     * @param user user object
     */
    public void addUser(User user) throws JSONException, IOException {
        // user is null
        if (user == null) return;

        if (this.users.contains(user)) {
            System.err.println("Cannot add user.  That exact user exists already.");
            return;
        }

        // a user with that id already exists
        if (this.findUserFromID(user.getID()) != null) {
            System.err.println("Cannot add user.  A user with that ID exists already.");
            return;
        }

        users.add(user);
        updateUsersJSON();
    }

    /**
     * Adds a user to the simulation given information as strings.
     *
     * @param username name as string
     * @param password password as string
     * @param type     UserType as string
     * @param location room name as string
     * @return message String conveying what action occurred during the function
     */
    public String addUser(String username, String password, String type, String location) throws JSONException, IOException {
        UserType userType;
        if (username.length() == 0) {
            return "ERROR: Name field must not be empty.";
        } else if (type == null) {
            return "ERROR: Type field must not be empty.";
        } else if (password.length() == 0) {
            return "ERROR: Password cannot be of length 0.";
        } else if (location == null) {
            return "ERROR: Must pick an initial position";
        } else if (type == "Parent") {
            userType = UserType.PARENT;
        } else if (type == "Child") {
            userType = UserType.CHILD;
        } else if (type == "Guest") {
            userType = UserType.GUEST;
        } else if (type == "Stranger") {
            userType = UserType.STRANGER;
        } else {
            return "ERROR: Unhandled add user case. You did something weird.";
        }

        for (User u : users) {
            if (u.getName().equals(username)) {
                return "User with given name already exists.";
            }
        }

        // Add user to simulation
        Room startingRoom = null;
        for (Room r : house.getRooms()) {
            if (r.getName().equals(location)) {
                startingRoom = r;
            }
        }
        addUser(new User(
                userType,
                startingRoom,
                username,
                password
        ));

        return "Successfully added " + username + " as a " + type.toLowerCase() + " user.";
    }

    /**
     * Removes user from an the simulation.
     *
     * @param user an existing user object
     */
    public void removeUser(User user) {
        if (user == null) return;
        else if (user == loggedInUser) return;
        users.remove(user);
    }

    /**
     * Removes user given a string in the following form: "Name (ID)".
     *
     * @param choice String of form "Name (ID)"
     * @return message String saying what action was taken given the choice.
     */
    public String removeUser(String choice) throws JSONException, IOException {

        if (choice == null) {
            return "ERROR: Please choose a user to delete";
        }
        if (loggedInUser.getType() != UserType.PARENT) {
            return "ERROR: Need admin/parental privileges to delete a user.";
        }

        // extract id of choice
        // choice is a string shaped like: "Admin (ID)"
        choice = choice.substring(choice.indexOf("(") + 1, choice.indexOf(")"));
        for (User u : users) {
            if (u.getID() == Integer.parseInt(choice)) {
                if (u.getID() == loggedInUser.getID()) {
                    return "ERROR: Cannot delete logged in user.";
                }
                removeUser(u);
                updateUsersJSON();
                return "Successfully removed user.";
            }
        }

        // Should never reach this point.
        return "ERROR: Could not find the given user to delete.";
    }

    /**
     * Finds the user using their ID.
     *
     * @param id the ID of a user
     * @return a user object if found, null if not
     */
    public User findUserFromID(int id) {
        try {
            User foundUser = this.users.stream().filter(user -> user.getID() == id).findFirst().get();
            return foundUser;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Logs out the user that is currently logged in
     *
     * @deprecated will crash the simulation
     */
    public void logout() {
        if (this.loggedInUser != null) this.loggedInUser = null;
    }

    /**
     * Gets the date of the simulation.
     *
     * @return date as a string
     */
    public String getDate() {
        return date;
    }

    /**
     * Sets the date of the simulation.
     *
     * @param date the date of the simulation as a string
     */
    public String setDate(String date) {
        this.date = date;
        return "Date set to: " + date + ".";
    }

    /**
     * Gets the time of the simulation.
     *
     * @return Time object
     */
    public Time getTime() {
        return time;
    }

    /**
     * Sets the time of the simulation using time object.
     *
     * @param time new time to be set
     * @return a string: "time updated"
     */
    public String setTime(Time time) {
        this.time = time;
        return "Time set to: " + time + ".";
    }

    /**
     * Gets the temperature outside the home in the simulation.
     *
     * @return the temperature outside of the house
     */
    public float getTemperature() {
        return temperature;
    }

    public String setTemperature(String temperatureString) {
        try {
            float newTemperature = Float.parseFloat(temperatureString);
            this.temperature = newTemperature;
            return ("Setting simulation temperature to " + temperatureString + "!");
        } catch (Exception e) {
            return ("ERROR: Inputted temperature is not a valid float.");
        }
    }

    /**
     * Sets the new temperature outside of the home.
     *
     * @param temperature new temperature to be set
     */
    public void setTemperature(float temperature) {
        this.temperature = temperature;
    }

    /**
     * Gets the current logged in user.
     *
     * @return User object of the current logged in user
     */
    public User getLoggedInUser() {
        return loggedInUser;
    }

    /**
     * Sets a user to a logged in user.
     *
     * @param loggedInUser a User object
     */
    public void setLoggedInUser(User loggedInUser) {
        this.loggedInUser = loggedInUser;
    }

    /**
     * Sets the logged in users location based on the location name as a string.
     *
     * @param location String: room the user wants to switch to as a string
     * @return String: message stating what occurred (success or error)
     */
    public String setLoggedInUserLocation(String location) throws JSONException, IOException {
        if (location == null) return "ERROR: Need to pick a location.";
        if (location.equals("Outside")) {
            loggedInUser.setLocation(null);
            updateUsersJSON();
            return "Moved user outside.";
        }
        for (Room r : house.getRooms()) {
            if (r.getName().equals(location)) {
                loggedInUser.setLocation(r);
                updateUsersJSON();
                return "Successfully changed logged in user's location.";
            }
        }

        // This should never occur... Only existing rooms are available inputs in a dropdown.
        return "ERROR: Could not find any room matching the input.";
    }

    /**
     * Sets the logged in user given their name and password.
     *
     * @param username String: name of the user that wishes to log in
     * @param password String: password of the user that wishes to log in
     * @return String: message stating whether it was a success or not and why
     */
    public String login(String username, String password) {
        for (User u : users) {
            if (u.getName().equals(username)) {
                if (u.getPassword().equals(password)) {
                    if (u == loggedInUser) {
                        return "ERROR: Already logged into this user.";
                    }
                    setLoggedInUser(u);
                    return "Successfully switched users.";
                } else {
                    return "ERROR: Incorrect password";
                }
            }
        }
        return "ERROR: Could not find any user with the provided username.";
    }

    /**
     * Edits the user of given username and currentPassword.
     *
     * @param username        String: string in the form "Name (ID)"
     * @param currentPassword String password of the user who needs to be changed
     * @param newPassword     String password to change to (can be empty/null)
     * @param type            String UserType the user needs to change to as a string (can be empty/null)
     * @param location        String name of room user wants to change to as a string (can be empty/null)
     * @return String: message stating successful completion or error and why
     */
    public String editUser(String username, String currentPassword, String newPassword, String type, String location) throws JSONException, IOException {
        if (username == null) {
            return "ERROR: Did not choose a user to edit.";
        }
        else if (currentPassword.length() == 0) {
            return "ERROR: Need to enter the chosen user's password to make changes.";
        }
        else if (type == null &&
                newPassword.length() == 0 &&
                location == null) {
            return "ERROR: Need to give changes to make.";
        }

        User toChange = null;
        int userID = Integer.parseInt(username.substring(username.indexOf("(") + 1, username.indexOf(")")));
        for (User u : users) {
            if (u.getID() == userID) {
                toChange = u;
                break;
            }
        }

        if (toChange == null) {
            return "ERROR: Somehow did not find user chosen from dropdown list... Try again.";
        }
        if (!toChange.getPassword().equals(currentPassword)) {
            return "ERROR: Entered password is incorrect for chosen user.";
        }
        if (currentPassword.equals(newPassword)) {
            return "ERROR: Entered current and new password are the same.";

        }

        UserType userType = null;
        if (type == "Parent") {
            userType = UserType.PARENT;
        } else if (type == "Child") {
            userType = UserType.CHILD;
        } else if (type == "Guest") {
            userType = UserType.GUEST;
        } else if (type == "Stranger") {
            userType = UserType.STRANGER;
        }

        // Change user type if that had input
        if (userType != null) {
            toChange.setType(userType);
        }
        // Change password if that had input
        if (newPassword.length() != 0) {
            toChange.setPassword(newPassword);
        }

        //Change location if that had input
        if (location != null) {
            if (location.equalsIgnoreCase("outside")) {
                toChange.setLocation(null);
            } else {
                for (Room r : house.getRooms()) {
                    if (r.getName().equals(location)) {
                        toChange.setLocation(r);
                        break;
                    }
                }
            }
        }
        updateUsersJSON();
        return "Successfully made requested changes to user.";
    }

    /**
     * Sets the country of the house location.
     *
     * @param location String: country name as a string
     * @return error message or message of success
     */
    public String setHouseLocation(String location) {
        this.house.setLocation(location);
        return "Set house location to " + this.house.getLocation() + "!";
    }

    /**
     * Gets the name of the country the house is in.
     *
     * @return String country location of the house
     */
    public String getHouseLocation() {
        return this.house.getLocation();
    }

    /**
     * Gets a list of all the simulation users.
     *
     * @return a list of users
     */
    public ArrayList<User> getUsers() {
        return users;
    }

    /**
     * Gets the house object of a simulation.
     *
     * @return a House object
     */
    public House getHouse() {
        return house;
    }

    /**
     * Gets the current state of the simulation (running or not) as a boolean.
     *
     * @return bool: true if running, false otherwise
     */
    public boolean getRunning() {
        return this.running;
    }

    /**
     * Sets the running state of the simulation.
     *
     * @param running boolean true if running, false otherwise
     */
    public void setRunning(boolean running) {
        this.running = running;
    }

    /**
     * Toggles the running attribute of the Simulation.
     *
     * @return console msg indicating new Simulation running state.
     */
    public String toggleRunning() {
        if (this.running) {
            this.running = false;
            return "Simulation OFF";
        }
        this.running = true;
        return "Simulation ON";
    }

    /**
     * Function that loads the users from the previous session through the JSON file
     * @param srcFile JSON file containing the user information
     * @return arraylist of users from the JSON file
     * @throws org.json.JSONException
     * @throws IOException
     */
    private ArrayList<User> usersFromJSON(File srcFile) throws org.json.JSONException, IOException {
        if (srcFile == null) return null;

        String wholeFile = new String(Files.readAllBytes(srcFile.toPath()));
        JSONTokener tokener = new JSONTokener(wholeFile);
        JSONObject object = new JSONObject(tokener);
        Iterator<String> keys = object.keys();
        ArrayList<User> users = new ArrayList<User>();

        while (keys.hasNext()) {
            String key = keys.next();
            if (object.get(key) instanceof JSONObject) {
                UserType type;
                Room location = null;
                switch (object.getJSONObject(key).getString("type")) {
                    case "parent":
                        type = UserType.PARENT;
                    case "child":
                        type = UserType.CHILD;
                    case "guest":
                        type = UserType.GUEST;
                    case "stranger":
                        type = UserType.STRANGER;
                    default:
                        type = UserType.STRANGER;
                }
                try {
                    String locationName = object.getJSONObject(key).getString("location");
                    if (locationName != null) {
                        for (Room r : this.house.getRooms()) {
                            if (r.getName().equalsIgnoreCase(locationName)) {
                                location = r;
                            }
                        }
                    }
                } catch (Exception e) {
                    // This occurs when location is null --> location is already null by default
                }
                users.add(new User(
                        type,
                        location,
                        key,
                        object.getJSONObject(key).getString("password")
                ));
            }
        }

        return users;
    }

    private void updateUsersJSON() throws JSONException, IOException {
        JSONObject obj = new JSONObject();
        for (User u : this.users) {
            JSONObject userInfo = new JSONObject();
            userInfo.put("type", u.getType().toString().toLowerCase());
            userInfo.put("location", u.getLocation() == null ? JSONObject.NULL : u.getLocation().getName());
            userInfo.put("password", u.getPassword());
            obj.put(u.getName(), userInfo);
        }
        Files.write(usersFile.getAbsoluteFile().toPath(), obj.toString().getBytes());
    }

    @Override
    public String toString() {
        return "Simulation [date=" + date + ", time=" + time + ", temperature=" + temperature + ", loggedInUser="
                + loggedInUser + "]";
    }


}
