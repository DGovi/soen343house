package Controller;

import Model.*;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.FileWriter;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.sql.Time;
import java.time.LocalTime;
import java.util.ArrayList;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.Random;

/**
 * The primary controller of the smart home simulation.
 * Stores many important simulation variables.
 */
public class Simulation implements Subject {
    private static Simulation simInstance;
    public static final float DEFAULT_TEMPERATURE = 25.0F;
    private float temperature;
    private User loggedInUser;
    private final ArrayList<User> users;
    private final House house;
    private final File usersFile;
    private boolean running;
    private boolean LightAuto;
    private boolean isAway;
    private int copDelay;
    private final ArrayList<Room> awayLightsOn = new ArrayList<>();
    private final ArrayList<Zone> zones;

    private float summerAwayTemp;
    private float winterAwayTemp;
    private SHHMonitor monitor;
    private HVACController hvacController;

    private final SimulationTime simulationTimes;

    File logFile = new File("logFile.txt");
    PrintWriter pw = new PrintWriter(new FileWriter(logFile, true));

    /**
     * Constructor that includes the JSON file with user information in it
     *
     * @param date        current date
     * @param time        current time
     * @param houseInput  house input JSON file
     * @param usersFile   users JSON file
     * @throws JSONException
     * @throws IOException
     */
    private Simulation(String date, Time time, File houseInput, File usersFile) throws JSONException, IOException {
        this.house = new House(houseInput);
        this.temperature = DEFAULT_TEMPERATURE;
        this.simulationTimes = new SimulationTime(date, time);

        this.usersFile = usersFile;
        this.users = usersFromJSON(usersFile);
        this.loggedInUser = users.get(users.size() - 1);
        this.running = true;
        this.LightAuto = true;
        this.isAway = false;
        this.copDelay = 5;

        // Start with as many zones as there are rooms, each with its own room.
        this.zones = new ArrayList<>();
        for (Room r : this.house.getRooms()) {
            this.zones.add(new Zone(r));
        }

        this.summerAwayTemp = 24;
        this.winterAwayTemp = 24;

        // start SHHMonitor
        this.monitor = new SHHMonitor(this);
        this.monitor.start();

        // start HVAC
        this.hvacController = new HVACController(this);
        this.hvacController.start();;
    }

    /**
     * Creates the one instance of the Simulation class that exists for the program.
     *
     * @param date        current date
     * @param time        current time
     * @param houseInput  house input JSON file
     * @param usersFile   users JSON file
     * @return the newly created Simulation object
     * @throws IOException
     * @throws JSONException
     */
    public static Simulation createInstance(String date, Time time, File houseInput, File usersFile) throws IOException, JSONException {
        if (simInstance == null)
            simInstance = new Simulation(date, time, houseInput, usersFile);
        return simInstance;
    }

    /**
     * Gets the one Simulation object that exists.
     *
     * @return the Simulation object
     */
    public static Simulation getInstance() {
        return simInstance;
    }

    public SimulationTime getSimulationTimes() {
        return this.simulationTimes;
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
     * Adds a room to leave light on in.
     *
     * @param room name of room to leave lights on in.
     */
    public void toggleAwayLight(String room) {
        Room r = house.getRoomFromName(room);
        if (awayLightsOn.contains(r)) {
            awayLightsOn.remove(r);
        } else {
            awayLightsOn.add(r);
        }
    }

    /**
     * Gets the temperature outside the home in the simulation.
     *
     * @return the temperature outside of the house
     */
    public float getTemperature() {
        return temperature;
    }

    /**
     * Sets the temperature outside the home in the simulation.
     *
     * @param temperatureString temperature to set
     * @return Message success or failure
     */
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
     * Gets the zones
     * @return
     */
    public ArrayList<Zone> getZones() {
        return this.zones;
    }

    /**
     * Gets the desired house temperature while away mode is active in summer
     *
     * @return float
     */
    public float getSummerAwayTemp() {
        return summerAwayTemp;
    }

    /**
     * Gets the desired house temperature while away mode is active in winter
     *
     * @return float
     */
    public float getWinterAwayTemp() {
        return winterAwayTemp;
    }

    /**
     * Sets the desired house temperature while away mode is active for both summer and winter
     *
     * @param summer desired temperature for away mode in summer as float
     * @param winter desired temperature for away mode in winter as float
     * @return String saying the operation was completed successfully
     */
    public String setSeasonalAwayTemperatures(float summer, float winter) {
        this.summerAwayTemp = summer;
        this.winterAwayTemp = winter;
        return "Successfully changed the seasonal away mode temperatures.";
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
        if (isAway && !location.equals("Outside")) return "Sorry the doors are locked";
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
        } else if (currentPassword.length() == 0) {
            return "ERROR: Need to enter the chosen user's password to make changes.";
        } else if (type == null &&
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
     * Gets a list of all the simulation users.
     *
     * @return a list of users
     */
    public ArrayList<Room> getRoomsWithAwayLights() {
        return awayLightsOn;
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
     * Gets the current state of the auto mode as a boolean.
     *
     * @return bool: true if running, false otherwise
     */
    public boolean getLightAuto() {
        return this.LightAuto;
    }

    /**
     * Gets the current state of the away as boolean
     *
     * @return bool: true if away, false otherwise
     */
    public boolean getIsAway() {
        return this.isAway;
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
     * Toggles the Auto Mode of the light
     *
     * @return console msg indicating Light Auto mode state.
     */
    public String toggleLight() {
        LightAuto = !LightAuto;
        return LightAuto ? "Light Auto Mode turned ON" : "Light Auto Mode turned OFF";
    }

    /**
     * Function that loads the users from the previous session through the JSON file
     *
     * @param srcFile JSON file containing the user information
     * @return arraylist of users from the JSON file
     * @throws org.json.JSONException
     * @throws IOException
     */
    private ArrayList<User> usersFromJSON(File srcFile) throws org.json.JSONException, IOException {
        if (srcFile == null) return null;

        String wholeFile;
        if (srcFile.exists()) {
            wholeFile = new String(Files.readAllBytes(srcFile.toPath()));
        } else {
            wholeFile = "{\"Admin\":{\"password\":\"123456\",\"location\":null,\"type\":\"parent\"}}";
        }

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
                        break;
                    case "child":
                        type = UserType.CHILD;
                        break;
                    case "guest":
                        type = UserType.GUEST;
                        break;
                    case "stranger":
                        type = UserType.STRANGER;
                        break;
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

    /**
     * Change the User JSON file
     */
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

    /**
     * Gets a list of all users in the current room.
     *
     * @param room the room to search for users
     * @return an ArrayList of users
     */
    public ArrayList<User> getUsersInRoom(Room room) {
        ArrayList<User> people = new ArrayList<User>();

        for (User user : this.getUsers()) {
            if (user.getLocation() != null && user.getLocation().equals(room))
                people.add(user);
        }

        return people;
    }

    /**
     * observer pattern method to notify all observers
     *
     * @return message of which motion sensor is active
     */
    public String notifyMotionSensors() {
        String message = "";
        for (Room room : house.getRooms()) {
            room.getRoomMotionSensor().update(isAway);
            if (isAway)
                message += "MotionSensor in " + room.getName() + "(" +
                        room.getRoomMotionSensor().getMotionSensorID() + ")" + " is ON\n";
        }
        return message;
    }

    /**
     * If the simulation is set on away mode, then it kicks all users of the house
     * else it sets sets the current user to the garage (initial Room).
     *
     * @param checked boolean that shows if the Away mode box in SHP is checked
     * @return a string that describes what happened
     */
    public String setSimulationAway(boolean checked) {
        String message;

        if (checked) {
            for (User aUser : users)
                aUser.setLocation(null);

            boolean obstructed = false;
            for (Room r : house.getRooms()) {
                for (Window w : r.getWindows()) {
                    if (w.getObstructed()) {
                        obstructed = true;
                    }
                    w.setOpen(false);
                }
                r.setLightsOn(false);
            }

            for (Room r : awayLightsOn) {
                r.setLightsOn(true);
            }

            isAway = true;
            message = "Away Mode has been set";

            if (obstructed)
                message += "\nOne of the windows hasn't been closed because it's obstructed";

        } else {
            isAway = false;
            message = "\nUser has returned home in " + house.getRooms().get(0).getName() +
                    ".\nAway Mode disabled." +
                    "\nAll sensors OFF";
            loggedInUser.setLocation(house.getRooms().get(0));
        }
        return message;
    }

    /**
     * checks the state of the away mode and the intruder to make a decision
     *
     * @return String describing what is the state of the home invasion
     */
    public String invadeSimHome(boolean checked) {
        String message;
        if (checked && !isAway) {
            message = "Intruders typically only intrude when there is no one in the house.";
        } else if (checked && isAway) {
            Intruder i = new Intruder(house.getRooms().get(new Random().nextInt(house.getRooms().size())));
            message = "There is an intruder in the house ";
            for (Room room : house.getRooms()) {
                if (i.getRoom().equals(room))
                    message += " detected by Motion sensor in " + room.getName() +
                            ", motion sensor " + room.getRoomMotionSensor().getMotionSensorID();
            }
        } else
            message = "no intruders found";

        return message;
    }

    /**
     * Gets the delay in seconds between an intruder being detected in the house during
     * Away mode, and the authorities being notified.
     *
     * @return the copDelay value
     */
    public int getCopDelay() {
        return copDelay;
    }

    /**
     * Sets the delay in seconds between an intruder being detected in the house during
     * Away mode, and the authorities being notified.
     *
     * @param copDelay the new copDelay value
     * @return a message indicating the new copDelay value
     */
    public String setCopDelay(int copDelay) {
        this.copDelay = copDelay;
        return "Delay till authorities notified set to " + copDelay + " seconds!";
    }

    /**
     * Converts the Simulation into a String representation.
     *
     * @return the String representation of the Simulation
     */
    @Override
    public String toString() {
        return "Simulation [date=" + this.simulationTimes.getDate() + ", time=" + this.simulationTimes.getTime() + ", temperature=" + temperature + ", loggedInUser="
                + loggedInUser + "]";
    }

    /**
     * Gets the running status of the simulation.
     *
     * @return true if Simulation is running, false otherwise
     */
    public boolean isRunning() {
        return running;
    }

    /**
     * Gets the isLightAuto attribute of the simulation.
     *
     * @return true if auto light mode is on, false otherwise
     */
    public boolean isLightAuto() {
        return LightAuto;
    }

    /**
     * Sets the isLightAuto attribute of the simulation.
     *
     * @param lightAuto the new auto light mode value
     */
    public void setLightAuto(boolean lightAuto) {
        LightAuto = lightAuto;
    }

    /**
     * Gets the Away Mode status of the Simulation.
     *
     * @return true if Away Mode is active, false otherwise
     */
    public boolean isAway() {
        return isAway;
    }

    /**
     * Sets the Away Mode status of the Simulation.
     *
     * @param away the new Away Mode value
     */
    public void setAway(boolean away) {
        isAway = away;
    }

    /**
     * Sets the temperature for summer, when away mode is active.
     *
     * @param summerAwayTemp the new temperature for summer during away mode
     */
    public void setSummerAwayTemp(float summerAwayTemp) {
        this.summerAwayTemp = summerAwayTemp;
    }

    /**
     * Sets the temperature for winter, when away mode is active.
     *
     * @param winterAwayTemp the new temperature for winter during away mode
     */
    public void setWinterAwayTemp(float winterAwayTemp) {
        this.winterAwayTemp = winterAwayTemp;
    }
}
