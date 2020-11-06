package Controller;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.Stack;
import java.time.LocalTime;

import javafx.event.ActionEvent;
import View.CountriesWindow;
import View.InputWindow;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import org.json.JSONException;

import Model.*;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

/**
 * Controls the actions of the simulation window created by DashboardDriver.
 * Each method predicated by the @FXML tag is directly triggered
 * by JavaFX UI elements.
 */
public class DashboardController {
    private Simulation sim;
    private final int windowLength = 30;
    private final int ROOM_SIZE = 50;
    private final File userInput = new File("users.json");
    final FileChooser fileChooser = new FileChooser();

    private static final int PERSON_HEIGHT = 25;
    private static final int PERSON_WIDTH = 25;

    @FXML
    private Label simRunningLabel;
    @FXML
    private ToggleButton simToggleButton;
    @FXML
    private VBox leftPaneControls;
    // Left pane
    @FXML
    private Label houseLocationLabel;
    @FXML
    private Label temperatureLabel;
    @FXML
    private Label currentUser;
    @FXML
    private ComboBox<String> currentUserLocationOptions;
    @FXML
    private Label permissionsDeleteUser;
    @FXML
    private Label permissionsControlWindows;
    @FXML
    private Label permissionsAwayMode;

    @FXML
    private AnchorPane tabsPane;
    // SHS
    @FXML
    private TextField loginName;
    @FXML
    private PasswordField loginPassword;
    @FXML
    private TextField createUserName;
    @FXML
    private PasswordField createUserPassword;
    @FXML
    private ComboBox<String> createUserType;
    @FXML
    private ComboBox<String> createUserLocation;
    @FXML
    private ComboBox<String> editUserChoice;
    @FXML
    private PasswordField editUserCurrentPassword;
    @FXML
    private PasswordField editUserNewPassword;
    @FXML
    private ComboBox<String> editUserType;
    @FXML
    private ComboBox<String> editUserLocation;
    @FXML
    private ComboBox<String> deleteUserChoice;

    // SHC
    @FXML
    private ComboBox<String> shcRoomSelect;
    @FXML
    private ComboBox<String> shcWindowSelect;
    @FXML
    private Button shcWindowOpenState;
    @FXML
    private Button shcWindowBlockedState;
    @FXML
    private ToggleButton shcLightAuto;
    @FXML
    private ComboBox<String> shcDoorSelect;

    @FXML
    private TextArea console;
    @FXML
    private Canvas render;
    @FXML
    private ToggleButton filePicker;
    @FXML
    private DatePicker datePicker;
    @FXML
    private Label dateLabel;
    @FXML
    private Label timeLabel;
    GraphicsContext gc;

    /**
     * Changes the simulation temperature.
     */
    @FXML
    private void changeTemperature() {
        String newTemperature = InputWindow.display("Change Temperature", "New Temperature");
        try {
            float newTemperatureInt = Float.parseFloat(newTemperature);
            sim.setTemperature(newTemperatureInt);
            updateDashboard();
            printToConsole("Setting simulation temperature to " + newTemperature + "!");
        } catch (Exception e) {
            printToConsole("ERROR: Inputted temperature is not a valid float.");
        }
    }

    /**
     * Adds a user and a user type  to the simulation object.
     */
    @FXML
    private void addUser() {
        UserType type = null;
        if (createUserName.getText().length() == 0) {
            printToConsole("ERROR: Name field must not be empty.");
            return;
        } else if (createUserType.getValue() == null) {
            printToConsole("ERROR: Type field must not be empty.");
            return;
        } else if (createUserPassword.getText() == null) {
            printToConsole("ERROR: Password cannot be null.");
        } else if (createUserType.getValue() == "Parent") {
            type = UserType.PARENT;
        } else if (createUserType.getValue() == "Child") {
            type = UserType.CHILD;
        } else if (createUserType.getValue() == "Guest") {
            type = UserType.GUEST;
        } else if (createUserType.getValue() == "Stranger") {
            type = UserType.STRANGER;
        } else {
            printToConsole("ERROR: Unhandled add user case. You did something weird.");
            return;
        }
    }

    /**
     * Changes the user location on the simulation object.
     */
    @FXML
    private void changeHouseLocation() {
        String newCountry = CountriesWindow.display("Choose Country", "Choose Country");
        printToConsole(sim.setHouseLocation(newCountry));
        updateDashboard();
    }

    /**
     * Edits the location of the user in a simulation object.
     */
    @FXML
    private void editCurrentUserLocation() throws JSONException, IOException {
        printToConsole(sim.setLoggedInUserLocation(currentUserLocationOptions.getValue()));
        updateDashboard();
        this.renderLayout(sim.getHouse());
    }

    /**
     * Logs in a user.
     */
    @FXML
    private void login() {
        printToConsole(sim.login(loginName.getText(), loginPassword.getText()));
        updateDashboard();
    }

    /**
     * Creates a new user and adds it to the simulation object.
     */
    @FXML
    private void createUser() throws JSONException, IOException {
        printToConsole(
                sim.addUser(
                        createUserName.getText(),
                        createUserPassword.getText(),
                        createUserType.getValue(),
                        createUserLocation.getValue()
                )
        );

        // Update other dropdownlist
        updateDashboard();
    }

    /**
     * Edit the information of a user.
     */
    @FXML
    private void editUser() throws JSONException, IOException {
        String choice = editUserChoice.getValue();
        printToConsole(
                sim.editUser(
                        editUserChoice.getValue(),
                        editUserCurrentPassword.getText(),
                        editUserNewPassword.getText(),
                        editUserType.getValue(),
                        editUserLocation.getValue()
                )
        );
        updateDashboard();
        this.renderLayout(sim.getHouse());
    }

    /**
     * User is deleted from the simulation and can no longer use
     * the simulation.
     */
    @FXML
    private void deleteUser() throws JSONException, IOException {
        printToConsole(sim.removeUser(deleteUserChoice.getValue()));
        updateDashboard();
    }

    /**
     * Updates window combobox and buttons in SHC tab when room is changed
     */
    @FXML
    private void shcChangeRooms() {
        Room room = sim.getHouse().getRoomFromName(shcRoomSelect.getValue());
        if (room == null)
            return;

        shcWindowSelect.getItems().clear();
        for (int i = 1; i <= room.getWindows().size(); i++) {
            shcWindowSelect.getItems().add("Window " + i);
        }
        shcWindowOpenState.setText("Pick a window");
        shcWindowBlockedState.setText("Pick a window");

        shcDoorSelect.getItems().clear();
        for (int i = 1; i <= room.getDoors().size(); i++) {
            shcWindowSelect.getItems().add("Door " + i);
        }
        // shcWindowOpenState.setText("Pick a window");
        // shcWindowBlockedState.setText("Pick a window");

        printToConsole("Now pick a window/door to view the state of.");
    }

    /**
     * Updates buttons containing current state of window when selected window is changed
     */
    @FXML
    private void shcChangeWindows() {
        if (shcWindowSelect.getValue() != null) {
            updateSHCbuttons();
            printToConsole("Successfully changed windows.");
        }
    }
    
    /**
     * Toggles the Auto Mode
     */
    @FXML
    private void shcLightAuto() {
    	printToConsole(sim.toggleLight());
    }

    /**
     * Extracts the window that wants to be changed and passes it to Window.changeOpen()
     * to attempt to change the current state of the window. The message from the
     * changeOpen() function is then printed
     */
    @FXML
    private void shcChangeOpen() throws IOException, JSONException {
        if ((shcRoomSelect.getValue() == null) || (shcWindowSelect.getValue() == null)) {
            printToConsole("ERROR: Not all fields were filled in before clicking the button");
            return;
        }
        if (sim.getLoggedInUser().getType() == UserType.STRANGER) {
            printToConsole("ERROR: Strangers are not allowed to change the state of the windows.");
            return;
        }
        String chosenWindowName = shcWindowSelect.getValue();
        int chosenWindowIndex = Integer.parseInt(chosenWindowName.substring(7)) - 1;

        Room room = sim.getHouse().getRoomFromName(shcRoomSelect.getValue());
        if (room == null) {
            return;
        }

        if ((sim.getLoggedInUser().getType() == UserType.CHILD || sim.getLoggedInUser().getType() == UserType.GUEST) && sim.getLoggedInUser().getLocation() != room) {
            printToConsole("ERROR: Children need to be in the room to change open/close windows.");
            return;
        }

        printToConsole(room.getWindows().get(chosenWindowIndex).changeOpen());
        updateSHCbuttons();
        this.renderLayout(sim.getHouse());
    }

    /**
     * Extracts the window that wants to be changed and passes it to Window.changeObstructed()
     * to attempt to change the current state of the window. The message from the
     * changeObstructed() function is then printed
     */
    @FXML
    private void shcChangeBlocked() throws IOException, JSONException {
        if ((shcRoomSelect.getValue() == null) || (shcWindowSelect.getValue() == null)) {
            printToConsole("ERROR: Not all fields were filled in before clicking the button");
            return;
        }
        if (sim.getLoggedInUser().getType() == UserType.STRANGER) {
            printToConsole("ERROR: Strangers are not allowed to change the state of the windows.");
            return;
        }
        String chosenWindowName = shcWindowSelect.getValue();
        int chosenWindowIndex = Integer.parseInt(chosenWindowName.substring(7)) - 1;

        Room room = sim.getHouse().getRoomFromName(shcRoomSelect.getValue());
        if (room == null) {
            return;
        }

        if ((sim.getLoggedInUser().getType() == UserType.CHILD || sim.getLoggedInUser().getType() == UserType.GUEST) && sim.getLoggedInUser().getLocation() != room) {
            printToConsole("ERROR: Children and guests need to be in the room to change the state of the windows.");
            return;
        }
        printToConsole(room.getWindows().get(chosenWindowIndex).changeObstructed());
        updateSHCbuttons();
        this.renderLayout(sim.getHouse());
    }

    /**
     * Updates the control buttons that show the current state of the selected window
     */
    private void updateSHCbuttons() {
        String chosenWindowName = shcWindowSelect.getValue();
        int chosenWindowIndex = Integer.parseInt(chosenWindowName.substring(7)) - 1;
        for (Room r : sim.getHouse().getRooms()) {
            if (r.getName().equals(shcRoomSelect.getValue())) {
                Window w = r.getWindows().get(chosenWindowIndex);
                if (w.getObstructed()) {
                    shcWindowOpenState.setText("Open");
                    shcWindowBlockedState.setText("Obstructed");
                } else if (w.getOpen()) {
                    shcWindowOpenState.setText("Open");
                    shcWindowBlockedState.setText("Not Obstructed");
                } else {
                    shcWindowOpenState.setText("Closed");
                    shcWindowBlockedState.setText("Not Obstructed");
                }
            }
        }

    }

    /**
     * Opens a file picker window that allows the user to choose a
     * house layout for the simulation to load.
     */
    @FXML
    private void loadHouseLayout() {
        javafx.stage.Window stage = filePicker.getScene().getWindow();
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open House layout File");

        File file = fileChooser.showOpenDialog(stage);
        if (file == null) {
            printToConsole("ERROR LOADING FILE");
            return;
        }

        try {
            afterLoadInitialize(file);
        } catch (JSONException | IOException e) {
            printToConsole("ERROR LOADING FILE");
        }
    }


    /**
     * Prints the given string to the output console of the simulation window.
     *
     * @param output prints on the console of the simulation the output
     */
    private void printToConsole(String output) {
        console.appendText(output + "\n");
    }

    /**
     * Refreshes the dashboard when there is a change that
     * has occurred in simulation object sim.
     */
    private void updateDashboard() {
        // reset name of logged in user
        // reset simulation house location
        houseLocationLabel.setText(sim.getHouseLocation());

        // reset simulation temperature
        temperatureLabel.setText(Float.toString(sim.getTemperature()));

        // reset info of logged in user
        currentUser.setText(sim.getLoggedInUser().getName());
        if (sim.getLoggedInUser().getLocation() == null) {
            currentUserLocationOptions.valueProperty().set("Outside");
        } else {
            currentUserLocationOptions.valueProperty().set(sim.getLoggedInUser().getLocation().getName());
        }
        if (sim.getLoggedInUser().getType() == UserType.PARENT) {
            permissionsDeleteUser.setText("Delete user: Able");
            permissionsControlWindows.setText("Open/Close Windows: Able");
            permissionsAwayMode.setText("Set Away Mode: Able");
        } else if (sim.getLoggedInUser().getType() == UserType.CHILD) {
            permissionsDeleteUser.setText("Delete user: Not Able");
            permissionsControlWindows.setText("Open/Close Windows: Only when in room");
            permissionsAwayMode.setText("Set Away Mode: Able");
        } else if (sim.getLoggedInUser().getType() == UserType.GUEST) {
            permissionsDeleteUser.setText("Delete user: Not Able");
            permissionsControlWindows.setText("Open/Close Windows: Only when in room");
            permissionsAwayMode.setText("Set Away Mode: Not Able");
        } else if (sim.getLoggedInUser().getType() == UserType.STRANGER) {
            permissionsDeleteUser.setText("Delete user: Not Able");
            permissionsControlWindows.setText("Open/Close Windows: Not Able");
            permissionsAwayMode.setText("Set Away Mode: Not Able");
        }

        // reset list of users
        editUserChoice.getItems().clear();
        deleteUserChoice.getItems().clear();
        for (User u : sim.getUsers()) {
            editUserChoice.getItems().add(u.getName() + " (" + u.getID() + ")");
            deleteUserChoice.getItems().add(u.getName() + " (" + u.getID() + ")");
        }

        // updating date and time
        dateLabel.setText("Date is: " + sim.getDate());
        timeLabel.setText(java.sql.Time.valueOf(LocalTime.now()).toString());

        // Reset field content
        loginName.setText("");
        loginPassword.setText("");
        createUserName.setText("");
        createUserPassword.setText("");
        createUserType.valueProperty().set(null);
        createUserLocation.valueProperty().set(null);
        editUserCurrentPassword.setText("");
        editUserNewPassword.setText("");

        // updating simToggleButton and UI control disable status based on
        // whether simulation is running or not
        boolean running = sim.getRunning();
        //console.setVisible(running);
        leftPaneControls.setDisable(! running);
        tabsPane.setDisable(! running);
        if (running)
            simRunningLabel.setText("Simulation: ON");
        else
            simRunningLabel.setText("Simulation: OFF");

    }

    /**
     * Starts up the simulation.
     *
     * @throws JSONException if JSON file not found
     */
    // Basically the constructor --> Sets variables
    public void initialize() throws JSONException {

    }

    /**
     * The simulation initializes only after a
     * layout file has been loaded in the simulation.
     *
     * @param file inputted house layout file
     * @throws JSONException if there is an error during runtime
     * @throws IOException   if there was a problem loading the file
     */
    public void afterLoadInitialize(File file) throws JSONException, IOException {
        // Set simulation
        sim = new Simulation(
                "",
                java.sql.Time.valueOf(LocalTime.now()),
                25,
                file,
                userInput
        );
        currentUser.setText(sim.getLoggedInUser().getName());

        // Set dropdown options for user type dropdowns
        createUserType.getItems().setAll("Parent", "Child", "Guest", "Stranger");
        editUserType.getItems().setAll("Parent", "Child", "Guest", "Stranger");

        // Set dropdown options for locations
        for (Room r : sim.getHouse().getRooms()) {
            createUserLocation.getItems().add(r.getName());
            editUserLocation.getItems().add(r.getName());
            currentUserLocationOptions.getItems().add(r.getName());

            // These don't need outside option
            shcRoomSelect.getItems().add(r.getName());
        }
        createUserLocation.getItems().add("Outside");
        editUserLocation.getItems().add("Outside");
        currentUserLocationOptions.getItems().add("Outside");

        shcWindowOpenState.setText("Pick a window");
        shcWindowBlockedState.setText("Pick a window");

        // enabling ON/OFF button
        simToggleButton.setDisable(false);
        // Set simulation to ON and updating the dashboard
        toggleSim();
        renderLayout(sim.getHouse());
    }

    /**
     * Takes a JSON file and attempts to render a house layout
     * for the simulation.
     *
     * @throws JSONException runtime error while loading the house layout file
     * @throws IOException   file not found
     */
    @FXML
    public void renderLayout(Model.House h) throws JSONException, IOException {
        gc = render.getGraphicsContext2D();

        gc.setFill(Color.WHITE);
        gc.fillRect(0, 0, 800, 800);

        gc.setFont(Font.font(12));
        gc.setFill(Color.BLACK);
        gc.setStroke(Color.BLACK);

        int startX = 15;
        int startY = 15;

        Stack<Room> stack = new Stack<Room>();
        Set<String> traversed = new HashSet<String>();
        HashMap<String, Room> doors = new HashMap<String, Room>();
        HashMap<Room, javafx.util.Pair<Integer, Integer>> coordinates =
                new HashMap<Room, javafx.util.Pair<Integer, Integer>>();

        for (Room r : h.getRooms())
            doors.put(r.getName(), r);

        Room firstRoom = h.getRooms().get(0);
        stack.add(firstRoom);
        traversed.add(firstRoom.getName());
        coordinates.put(firstRoom, new javafx.util.Pair<Integer, Integer>(Integer.valueOf(startX), Integer.valueOf(startY)));

        drawRoom(firstRoom, startX, startY, false);
        drawWindows(startX, startY, firstRoom.getDoors().size() * ROOM_SIZE, firstRoom.getWindows());

        while (!stack.empty()) {
            Room top = stack.pop();
            int xParent = 0;

            ArrayList<Door> doorsTop = top.getDoors();

            for (Door doorObj : doorsTop) {
                String door = doorObj.getName();

                if (traversed.contains(door))
                    continue;

                Room room = doors.get(door);
                stack.add(doors.get(door));
                traversed.add(door);

                int x = coordinates.get(top).getKey().intValue() + xParent;
                int y = coordinates.get(top).getValue().intValue() + ROOM_SIZE * top.getDoors().size();
                int size = room.getDoors().size() * ROOM_SIZE;

                this.drawRoom(room, x, y, (! doorObj.equals(doorsTop.get(doorsTop.size() - 1))) && doorsTop.size() > 1);

                if (doorObj.equals(doorsTop.get(doorsTop.size() - 1)))
                    this.drawWindows(x, y, size, room.getWindows());
                else if (door.equals(doorsTop.get(0)) || doorsTop.size() > 1 && door.equals(doorsTop.get(1)))
                    this.drawWindows(x - size, y, size, room.getWindows());

                coordinates.put(room, new javafx.util.Pair<Integer, Integer>(Integer.valueOf(x), Integer.valueOf(y)));

                xParent = size;
            }
        }

    }

    /**
     * On a given house layout, draw the windows at specified location.
     *
     * @param x            position on the x coordinate of window
     * @param y            position of y coordinate of window
     * @param size         length of a window
     * @param windows      a list of windows to draw
     */
    @FXML
    public void drawWindows(int x, int y, int size, ArrayList<Window> windows) {
        int countWindows = windows.size();

        for (int i = 0; i < countWindows; i++) {
            // setting window color based on if obstructed or not
            Window window = windows.get(i);
            if (window.getObstructed())
                gc.setStroke(Color.RED);
            else
                gc.setStroke(Color.LIGHTBLUE);

            gc.setLineWidth(3);
            int gap = 15;
            int offset = (size - windowLength * countWindows - gap * (countWindows - 1)) / 2;
            gc.strokeLine(x + size, y + offset + i * (windowLength + gap), x + size + (window.getOpen() ? offset : 0), y + offset + windowLength + i * (windowLength + gap));
        }
        gc.setLineWidth(1);
        gc.setStroke(Color.BLACK);
        gc.setFill(Color.BLACK);
    }

    /**
     * On the house layout in the simulation, draw the available lights
     * in the room.
     *
     * @param room which room the lights are in
     * @param x    position on the x coordinate of the lights
     * @param y    position of the y coordinate of the lights
     * @param size size of the lights
     */
    @FXML
    public void drawLights(Room room, int x, int y, int size) {
        for (int i = 0; i < room.getLights(); i++) {
            // change light color to show if lights are ON or OFF
            if (room.isLightsOn())
                gc.setFill(Color.GOLD);
            else
                gc.setFill(Color.SILVER);
            int offset = (size - 35);
            int xC = x + 10 + (i * 20) % offset;
            int yC = (i * 20) / offset * 20 + y + 25;
            gc.fillOval(xC, yC, 10, 10);
        }
        gc.setFill(Color.BLACK);
    }

    /**
     * Draws the room of the house layout just to be sure.
     *
     * @param room     the room of a house layout
     * @param x        coordinate of the ex position of the room
     * @param y        coordinate of the y position of the room
     * @param sideDoor true if the door is on the side (vertical on the house layout), false if not (horizontal)
     */
    @FXML
    public void drawRoom(Room room, int x, int y, boolean sideDoor) {
        int size = 50 * room.getDoors().size();
        gc.strokeRoundRect(x, y, size, size, 0, 0);
        drawLights(room, x, y, size);
        drawPeople(room, x, y, size);

        gc.setLineWidth(3);
        gc.strokeLine(x + 15, y, x + 30, y);
        if (sideDoor)
            gc.strokeLine(x + size, y + 20, x + size, y + 40);

        gc.setLineWidth(1);
        gc.setStroke(Color.BLACK);
        gc.fillText(room.getName(), x + 5, y + 17);
    }

    public void drawPeople(Room room, int x, int y, int size) {
        final int spacingX = PERSON_WIDTH;
        final int spacingY = PERSON_WIDTH;
        int posX = 0;
        int posY = 0;

        // drawing people
        Image personImage = new Image("file:stick_person.png");
        for (User user: sim.getUsersInRoom(room)) {
            if (posX > size) {
                posX = 0;
                posY += spacingY;
            }
            int finalX = x + posX;
            int finalY = y + posY;

            gc.drawImage(personImage, finalX , finalY, PERSON_HEIGHT, PERSON_WIDTH);
            posX += spacingX;
        }
    }

    /**
     * Displays the given date from the actionEvent.
     * onto the simulation
     *
     * @param actionEvent event that triggers this method
     */
    //shows entered date in label box
    public void displayDate(javafx.event.ActionEvent actionEvent) {
        printToConsole(sim.setDate(datePicker.getValue().toString()));
        updateDashboard();
    }

    /**
     * Updates the time displayed on the simulation.
     *
     * @param actionEvent event that triggers the method
     */
    //shows the time
    public void updateTime(ActionEvent actionEvent) {
        printToConsole(sim.setTime(java.sql.Time.valueOf(LocalTime.now())));
        updateDashboard();
    }

    /**
     * Turns the simulator off. The simulation does
     * not accept any input until turned back on.
     */
    @FXML
    public void toggleSim() {
        printToConsole(sim.toggleRunning());

        updateDashboard();
    }

    @FXML
    public void shcChangeDoor() {

    }


    @FXML
    public void toggleRoomLights() throws IOException, JSONException {
        Room room = sim.getHouse().getRoomFromName(shcRoomSelect.getValue());
        if (room == null) {
            return;
        }

        room.toggleLightsON();
        this.renderLayout(sim.getHouse());

    }

}
