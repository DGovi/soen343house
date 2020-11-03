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
    private File userInput = new File("users.json");
    final FileChooser fileChooser = new FileChooser();

    // Left pane
    @FXML
    private Label houseLocationLabel;
    @FXML
    private Label temperatureLabel;
    @FXML
    private Label currentUser;
    @FXML
    private ComboBox<String> currentUserLocationOptions;

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
        if (!sim.getRunning())
            return;
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
     * Changes the user location on the simulation object.
     */
    @FXML
    private void changeHouseLocation() {
        if (!sim.getRunning())
            return;
        String newCountry = CountriesWindow.display("Choose Country", "Choose Country");
        printToConsole(sim.setHouseLocation(newCountry));
        updateDashboard();
    }

    /**
     * Edits the location of the user in a house object.
     */
    @FXML
    private void editCurrentUserLocation() {
        if (!sim.getRunning())
            return;
        printToConsole(sim.setLoggedInUserLocation(currentUserLocationOptions.getValue()));
        updateDashboard();
    }

    /**
     * Logs in a user.
     */
    @FXML
    private void login() {
        if (!sim.getRunning())
            return;
        printToConsole(sim.login(loginName.getText(), loginPassword.getText()));
        updateDashboard();
    }

    /**
     * Creates a new user and adds it to the simulation object.
     */
    @FXML
    private void createUser() throws JSONException, IOException {
        if (!sim.getRunning())
            return;
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
        if (!sim.getRunning())
            return;
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
    }

    /**
     * User is deleted from the simulation and can no longer use
     * the simulation.
     */
    @FXML
    private void deleteUser() throws JSONException, IOException {
        if (!sim.getRunning())
            return;
        printToConsole(sim.removeUser(deleteUserChoice.getValue()));
        updateDashboard();
    }

    /**
     * Updates window combobox and buttons in SHC tab when room is changed
     */
    @FXML
    private void shcChangeRooms() {
        if (!sim.getRunning())
            return;
        // SHC stuff
        if (shcRoomSelect.getValue() != null) {
            for (Room r : sim.getHouse().getRooms()) {
                if (shcRoomSelect.getValue().equals(r.getName())) {
                    shcWindowSelect.getItems().clear();
                    for (int i = 1; i <= r.getWindows().size(); i++) {
                        shcWindowSelect.getItems().add("Window " + i);
                    }
                    shcWindowOpenState.setText("Pick a window");
                    shcWindowBlockedState.setText("Pick a window");
                    break;
                }
            }
        }
        printToConsole("Now pick a window to view the state of.");
    }

    /**
     * Updates buttons containing current state of window when selected window is changed
     */
    @FXML
    private void shcChangeWindows() {
        if (!sim.getRunning())
            return;
        if (shcWindowSelect.getValue() != null) {
            updateSHCbuttons();
            printToConsole("Successfully changed windows.");
        }
    }

    /**
     * Extracts the window that wants to be changed and passes it to Window.changeOpen()
     * to attempt to change the current state of the window. The message from the
     * changeOpen() function is then printed
     */
    @FXML
    private void shcChangeOpen() {
        if (!sim.getRunning())
            return;
        if ((shcRoomSelect.getValue() == null) || (shcWindowSelect.getValue() == null)) {
            printToConsole("ERROR: Not all fields were filled in before clicking the button");
            return;
        }
        String chosenWindowName = shcWindowSelect.getValue();
        int chosenWindowIndex = Integer.parseInt(chosenWindowName.substring(7)) - 1;
        for (Room r : sim.getHouse().getRooms()) {
            if (r.getName().equals(shcRoomSelect.getValue())) {
                printToConsole(r.getWindows().get(chosenWindowIndex).changeOpen());
                updateSHCbuttons();
                return;
            }
        }
    }

    /**
     * Extracts the window that wants to be changed and passes it to Window.changeObstructed()
     * to attempt to change the current state of the window. The message from the
     * changeObstructed() function is then printed
     */
    @FXML
    private void shcChangeBlocked() {
        if (!sim.getRunning())
            return;
        if ((shcRoomSelect.getValue() == null) || (shcWindowSelect.getValue() == null)) {
            printToConsole("ERROR: Not all fields were filled in before clicking the button");
            return;
        }
        String chosenWindowName = shcWindowSelect.getValue();
        int chosenWindowIndex = Integer.parseInt(chosenWindowName.substring(7)) - 1;
        for (Room r : sim.getHouse().getRooms()) {
            if (r.getName().equals(shcRoomSelect.getValue())) {
                printToConsole(r.getWindows().get(chosenWindowIndex).changeObstructed());
                updateSHCbuttons();
                return;
            }
        }
    }

    /**
     * Updates the control buttons that show the current state of the selected window
     */
    private void updateSHCbuttons() {
        if (!sim.getRunning())
            return;
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
            System.out.println(file.toPath());
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
        if (!sim.getRunning())
            return;
        console.appendText(output + "\n");
    }

    /**
     * Refreshes the dashboard when there is a change that
     * has occurred in simulation object sim.
     */
    private void updateDashboard() {
        if (!sim.getRunning())
            return;
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

        // Set dropdown options for dropdowns with users
        updateDashboard();
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
        if (!sim.getRunning())
            return;
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
        drawWindows(startX, startY, firstRoom.getDoors().size() * ROOM_SIZE, firstRoom.getWindows().size());

        while (!stack.empty()) {
            Room top = stack.pop();
            int xParent = 0;

            ArrayList<String> doorsTop = top.getDoors();

            for (String door : doorsTop) {
                if (traversed.contains(door))
                    continue;

                Room room = doors.get(door);
                stack.add(doors.get(door));
                traversed.add(door);

                int x = coordinates.get(top).getKey().intValue() + xParent;
                int y = coordinates.get(top).getValue().intValue() + ROOM_SIZE * top.getDoors().size();
                int size = room.getDoors().size() * ROOM_SIZE;
                int countWindows = room.getWindows().size();

                this.drawRoom(room, x, y, door != doorsTop.get(doorsTop.size() - 1) && doorsTop.size() > 1);

                if (door == doorsTop.get(doorsTop.size() - 1))
                    drawWindows(x, y, size, countWindows);
                else if (door == doorsTop.get(0) || doorsTop.size() > 1 && door == doorsTop.get(1))
                    drawWindows(x - size, y, size, countWindows);

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
     * @param countWindows number of windows on  a house layout
     */
    @FXML
    public void drawWindows(int x, int y, int size, int countWindows) {
        if (!sim.getRunning())
            return;
        for (int i = 0; i < countWindows; i++) {
            gc.setStroke(Color.LIGHTBLUE);
            gc.setLineWidth(3);
            int gap = 15;
            int offset = (size - windowLength * countWindows - gap * (countWindows - 1)) / 2;
            gc.strokeLine(x + size, y + offset + i * (windowLength + gap), x + size, y + offset + windowLength + i * (windowLength + gap));
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
        if (!sim.getRunning())
            return;
        for (int i = 0; i < room.getLights(); i++) {
            gc.setFill(Color.GOLD);
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
        if (!sim.getRunning())
            return;
        int size = 50 * room.getDoors().size();
        gc.strokeRoundRect(x, y, size, size, 0, 0);
        drawLights(room, x, y, size);

        gc.setLineWidth(3);
        gc.strokeLine(x + 15, y, x + 30, y);
        if (sideDoor)
            gc.strokeLine(x + size, y + 20, x + size, y + 40);

        gc.setLineWidth(1);
        gc.setStroke(Color.BLACK);
        gc.fillText(room.getName(), x + 5, y + 17);
    }

    /**
     * Displays the given date from the actionEvent.
     * onto the simulation
     *
     * @param actionEvent event that triggers this method
     */
    //shows entered date in label box
    public void displayDate(javafx.event.ActionEvent actionEvent) {
        if (!sim.getRunning())
            return;
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
        if (!sim.getRunning())
            return;
        printToConsole(sim.setTime(java.sql.Time.valueOf(LocalTime.now())));
        updateDashboard();
    }

    /**
     * Turns the simulator off. The simulation does
     * not accept any input until turned back on.
     */
    @FXML
    public void endSim() {
        printToConsole(sim.toggleRunning());
        console.setVisible(this.sim.getRunning());

    }


}
