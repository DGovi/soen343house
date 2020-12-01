package Controller;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Time;
import java.util.*;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.Stack;
import java.time.LocalTime;

import javafx.event.ActionEvent;
import View.CountriesWindow;
import View.InputWindow;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
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
    private Spinner<Integer> summerSpinner1;
    @FXML
    private Spinner<Integer> summerSpinner2;
    @FXML
    private Spinner<Integer> winterSpinner1;
    @FXML
    private Spinner<Integer> winterSpinner2;

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
    private Button shcDoorOpenState;

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
    @FXML
    private TextField timeHourInput;
    @FXML
    private TextField timeMinuteInput;
    @FXML
    private TextField timeSecondInput;
    @FXML
    private TextField timeSpeedInput;
    GraphicsContext gc;

    //SHP
    @FXML
    private CheckBox awayButton;
    @FXML
    private CheckBox intruderCheck;
    @FXML
    private TextField TimeframeFrom;
    @FXML
    private TextField TimeframeTo;

    @FXML
    private TextField copDelayField;
    @FXML
    private Button copDelayButton;
    @FXML
    private VBox lightsLeftOnBox;

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
        } else if (createUserType.getValue().equals("Parent")) {
            type = UserType.PARENT;
        } else if (createUserType.getValue().equals("Child")) {
            type = UserType.CHILD;
        } else if (createUserType.getValue().equals("Guest")) {
            type = UserType.GUEST;
        } else if (createUserType.getValue().equals("Stranger")) {
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
        String locationName = currentUserLocationOptions.getValue();
        Room previousLocation = sim.getLoggedInUser().getLocation();

        String message = sim.setLoggedInUserLocation(locationName);
        printToConsole(message);

        if(message.equals("Sorry the doors are locked")){
            String name = previousLocation == null ? "Outside" : previousLocation.getName();
            currentUserLocationOptions.setValue(name);
            return;
        }

        if(previousLocation != null && previousLocation.getName().equals(locationName)){
            return;
        }

        if(sim.getLightAuto()){
            if(!locationName.equals("Outside")){
                printToConsole("Automatically turning lights on.");
                sim.getHouse().getRoomFromName(locationName).setLightsOn(true);
            }
            if(previousLocation != null && sim.getUsersInRoom(previousLocation).isEmpty()){
                previousLocation.setLightsOn(false);
            }
        }

        updateDashboard();
        this.renderLayout(sim.getHouse());
    }

    @FXML
    private void setSeasons() {
        if (sim.getLoggedInUser().getType() == UserType.PARENT) {
            printToConsole(sim.setSeasons(
                    summerSpinner1.getValue(),
                    summerSpinner2.getValue(),
                    winterSpinner1.getValue(),
                    winterSpinner2.getValue()
            ));
        }
        else {
            printToConsole("ERROR: User must be a parent to change the season intervals.");
        }
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
     *
     * Add checkboxes for the SHP page.
     *
     */
    @FXML
    private void updateSHP() {
        lightsLeftOnBox.getChildren().clear();

        for (Room r : sim.getHouse().getRooms()){
            HBox h = new HBox();
            h.setPadding(new Insets(10, 0, 0, 10));
            CheckBox box = new CheckBox();
            box.setSelected(sim.getRoomsWithAwayLights().contains(r));
            box.setPadding(new Insets(0, 10, 0, 0));
            box.setOnAction(actionEvent -> sim.toggleAwayLight(r.getName()));
            h.getChildren().add(box);
            h.getChildren().add(new Text(r.getName()));
            lightsLeftOnBox.getChildren().add(h);
        }
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
            shcDoorSelect.getItems().add("Door " + i);
        }

        shcDoorOpenState.setText("Pick a door");

        logText(sim.pw, printToConsole("Now pick a window/door to view the state of."));
    }

    /**
     * Updates buttons containing current state of window when selected window is changed
     */
    @FXML
    private void shcChangeWindows() {
        String message;
        if (shcWindowSelect.getValue() != null) {
            updateSHCWindowButtons();
            logText(sim.pw, printToConsole("Successfully changed windows."));
        }
    }

    /**
     * Turns off all the lights in the house that don't have a person in it.
     */
    private void shcTurnOffAllLights() throws IOException, JSONException {
        for(Room r : sim.getHouse().getRooms()){
            if(sim.getUsersInRoom(r).isEmpty()){
                r.setLightsOn(false);
            }
        }
        this.renderLayout(sim.getHouse());
    }

    /**
     * Toggles the Auto Mode
     */
    @FXML
    private void shcLightAuto() throws IOException, JSONException {
    	  logText(sim.pw, printToConsole(sim.toggleLight()));
        if(sim.getLightAuto()) {
            shcTurnOffAllLights();
        }
    }

    /**
     * Extracts the window that wants to be changed and passes it to Window.changeOpen()
     * to attempt to change the current state of the window. The message from the
     * changeOpen() function is then printed
     */
    @FXML
    private void shcChangeOpen() throws IOException, JSONException {
        if ((shcRoomSelect.getValue() == null) || (shcWindowSelect.getValue() == null)) {
            logText(sim.pw, printToConsole("ERROR: Not all fields were filled in before clicking the button"));
            return;
        }
        if (sim.getLoggedInUser().getType() == UserType.STRANGER) {
            logText(sim.pw, printToConsole("ERROR: Strangers are not allowed to change the state of the windows."));
            return;
        }
        String chosenWindowName = shcWindowSelect.getValue();
        int chosenWindowIndex = Integer.parseInt(chosenWindowName.substring(7)) - 1;

        Room room = sim.getHouse().getRoomFromName(shcRoomSelect.getValue());
        if (room == null) {
            return;
        }

        if ((sim.getLoggedInUser().getType() == UserType.CHILD || sim.getLoggedInUser().getType() == UserType.GUEST) && sim.getLoggedInUser().getLocation() != room) {
            logText(sim.pw, printToConsole("ERROR: Children need to be in the room to change open/close windows."));
            return;
        }

        logText(sim.pw, printToConsole(room.getWindows().get(chosenWindowIndex).changeOpen()));
        updateSHCWindowButtons();
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
            logText(sim.pw, printToConsole("ERROR: Not all fields were filled in before clicking the button"));
            return;
        }
        if (sim.getLoggedInUser().getType() == UserType.STRANGER) {
            logText(sim.pw, printToConsole("ERROR: Strangers are not allowed to change the state of the windows."));
            return;
        }
        String chosenWindowName = shcWindowSelect.getValue();
        int chosenWindowIndex = Integer.parseInt(chosenWindowName.substring(7)) - 1;

        Room room = sim.getHouse().getRoomFromName(shcRoomSelect.getValue());
        if (room == null) {
            return;
        }

        if ((sim.getLoggedInUser().getType() == UserType.CHILD || sim.getLoggedInUser().getType() == UserType.GUEST) && sim.getLoggedInUser().getLocation() != room) {
            logText(sim.pw, printToConsole("ERROR: Children and guests need to be in the room to change the state of the windows."));
            return;
        }
        logText(sim.pw, printToConsole(room.getWindows().get(chosenWindowIndex).changeObstructed()));
        updateSHCWindowButtons();
        this.renderLayout(sim.getHouse());
    }

    /**
     * Updates the control buttons that show the current state of the selected window.
     */
    private void updateSHCWindowButtons() {
        // updating window buttons
        String chosenWindowName = shcWindowSelect.getValue();
        if (chosenWindowName == null)
            return;

        int chosenWindowIndex = Integer.parseInt(chosenWindowName.substring(7)) - 1;
        Room room = sim.getHouse().getRoomFromName(shcRoomSelect.getValue());
        if (room == null)
            return;

        Window w = room.getWindows().get(chosenWindowIndex);
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

    /**
     * Updates the control buttons that show the current state of the selected door.
     */
    public void updateSHCDoorButtons() {
        String chosenDoorName = shcDoorSelect.getValue();
        if (chosenDoorName == null)
            return;;

        int chosenDoorIndex = Integer.parseInt(chosenDoorName.substring(5)) - 1;
        Room room = sim.getHouse().getRoomFromName(shcRoomSelect.getValue());
        if (room == null)
            return;

        Door d = room.getDoors().get(chosenDoorIndex);
        if (d.isOpen())
            shcDoorOpenState.setText("Door Open");
        else
            shcDoorOpenState.setText("Door Closed");
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
    private String printToConsole(String output) {
        console.appendText(output + "\n");
        return output;
    }

    /**
     * prints the output to a file
     * @param pw printwriter object
     * @param output string output to be printed in the file
     */
    public void logText(PrintWriter pw, String output){
        pw.write(output + "\n");
        pw.flush();
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
        timeLabel.setText(sim.getTime().toString());
        timeHourInput.setText("");
        timeMinuteInput.setText("");
        timeSecondInput.setText("");
        // Do not reset time speed setting so user knows current time speed at all times

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
        sim = Simulation.createInstance(
                "",
                java.sql.Time.valueOf(LocalTime.now()),
                25,
                file,
                userInput
        );
        currentUser.setText(sim.getLoggedInUser().getName());

        summerSpinner1.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 12, sim.getSummer()[0]));
        summerSpinner2.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 12, sim.getSummer()[1]));
        winterSpinner1.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 12, sim.getWinter()[0]));
        winterSpinner2.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 12, sim.getWinter()[1]));

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

        //Turn all lights in house off that don't have people in them
        shcTurnOffAllLights();

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
    public void renderLayout(Model.House h) {
        gc = render.getGraphicsContext2D();

        gc.setFill(Color.WHITE);
        gc.fillRect(0, 0, 800, 800);

        gc.setFont(Font.font(12));
        gc.setFill(Color.BLACK);
        gc.setStroke(Color.BLACK);

        int startX = 15;
        int startY = 15;

        Stack<Room> stack = new Stack<>();
        Set<String> traversed = new HashSet<>();
        HashMap<String, Room> doors = new HashMap<>();
        HashMap<Room, javafx.util.Pair<Integer, Integer>> coordinates = new HashMap<>();

        for (Room r : h.getRooms())
            doors.put(r.getName(), r);

        Room firstRoom = h.getRooms().get(0);
        stack.add(firstRoom);
        traversed.add(firstRoom.getName());
        coordinates.put(firstRoom, new javafx.util.Pair<>(startX, startY));

        int frSize = firstRoom.getDoors().size() * ROOM_SIZE;
        drawRoom(firstRoom, startX, startY, false, null);
        drawPeople(firstRoom, startX + 5, startY + frSize - PERSON_HEIGHT - 5);
        drawWindows(startX, startY, frSize, firstRoom.getWindows());

        drawPeopleOutside();

        while (!stack.empty()) {
            Room top = stack.pop();
            int xParent = 0;

            ArrayList<Door> doorsTop = top.getDoors();
            for (Door doorObj : doorsTop) {
                String door = doorObj.getTo();

                if (traversed.contains(door))
                    continue;

                if(door.equals("Outside"))
                    continue;

                Room room = doors.get(door);
                stack.add(doors.get(door));
                traversed.add(door);
                Room roomAbove = doors.get(doorObj.getFrom());

                int x = coordinates.get(top).getKey() + xParent;
                int y = coordinates.get(top).getValue() + ROOM_SIZE * top.getDoors().size();
                int size = room.getDoors().size() * ROOM_SIZE;

                drawPeople(room, x + 5, y + size - PERSON_HEIGHT - 5);

                boolean sideDoor = false;
                for(int i = 0; i < doorsTop.size(); i++){
                    if(doorsTop.get(i).getTo().equals(door) && i + 1 < doorsTop.size()){
                        Room n = doors.get(doorsTop.get(i + 1).getTo());
                        for(Door d : room.getDoors()){
                            if(d.getTo() == null || n == null)
                                continue;
                            Room cr = doors.get(d.getTo());
                            if(cr != null && cr.equals(n)){
                                sideDoor = true;
                                break;
                            }
                        }
                    }
                }

                this.drawRoom(room, x, y, (! doorObj.equals(doorsTop.get(doorsTop.size() - 1))) && doorsTop.size() > 1 && sideDoor, roomAbove);

                if (doorObj.equals(doorsTop.get(doorsTop.size() - 1)))
                    this.drawWindows(x, y, size, room.getWindows());
                else if (doorObj.equals(doorsTop.get(0)) || (doorsTop.size() > 1 && doorObj.equals(doorsTop.get(1))))
                    this.drawWindows(x - size, y, size, room.getWindows());

                coordinates.put(room, new javafx.util.Pair<>(x, y));
                xParent = x + size - coordinates.get(top).getKey();
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
    public void drawRoom(Room room, int x, int y, boolean sideDoor, Room roomAbove) {
        // drawing room rectangle
        int size = 50 * room.getDoors().size();
        gc.strokeRoundRect(x, y, size, size, 0, 0);

        // drawing lights
        drawLights(room, x, y, size);

        // drawing doors
        gc.setLineWidth(3);
        boolean topDoorOpen = false;

        String nameTested = roomAbove != null ? roomAbove.getName() : "Outside";


        for (Door d : room.getDoors())
            if (d.getTo().equals(nameTested) && d.isOpen()) {
                topDoorOpen = true;
                break;
            }

        if (topDoorOpen)
            gc.strokeLine(x + 15, y, x + 30, y - 15);
        else
            gc.strokeLine(x + 15, y, x + 30, y);

        if (sideDoor) {
            boolean sideDoorOpen = false;
            for (Door d : room.getDoors()) {
                if (!d.getTo().equals(roomAbove.getName()) && !d.getTo().equals("Outside") && d.isOpen()) {
                    sideDoorOpen = true;
                    break;
                }
            }

            if (sideDoorOpen)
                gc.strokeLine(x + size, y + 20, x + size + 15, y + 40);
            else
                gc.strokeLine(x + size, y + 20, x + size, y + 40);
        }

        if(room.getName().equals("Garage")){
            boolean entranceOpen = false;
            for (Door d : room.getDoors())
                if (d.getTo().equals("Outside") && d.isOpen()) {
                    entranceOpen = true;
                    break;
                }

            if (entranceOpen)
                gc.strokeLine(x, y + 20, x + 15, y + 40);
            else
                gc.strokeLine(x, y + 20, x, y + 40);
        }

        if(room.getName().equals("Backyard")){
            boolean bottomDoor = false;
            for (Door d : room.getDoors())
                if (d.getTo().equals("Outside") && d.isOpen()) {
                    bottomDoor = true;
                    break;
                }

            if (bottomDoor)
                gc.strokeLine(x + 15, y + size, x + 30, y + size - 15);
            else
                gc.strokeLine(x + 15, y + size, x + 30, y + size);
        }

        // drawing room name
        gc.setLineWidth(1);
        gc.setStroke(Color.BLACK);
        gc.fillText(room.getName(), x + 5, y + 17);
    }

    /**
     * Draws the people who are currently outside the house
     */
    public void drawPeopleOutside(){
        int baseLineX = 200;
        int outsideX = baseLineX;
        int outsideY = 15;
        int usersDrawn = 0;
        for (User u : sim.getUsers()){
            if(u.getLocation() == null) {
                drawPerson(outsideX, outsideY);
                usersDrawn++;
                if(usersDrawn % 3 == 0) {
                    outsideX = baseLineX;
                    outsideY += PERSON_HEIGHT + 5;
                }else{
                    outsideX += PERSON_WIDTH + 5;
                }
            }
        }
    }

    /**
     * Draws the people who are currently present in a given room
     * @param room the room in question
     * @param x the x position of the room
     * @param y the y position of the room
     */
    public void drawPeople(Room room, int x, int y) {
        final int spacingX = PERSON_WIDTH;
        final int spacingY = PERSON_WIDTH;
        int posX = 0;
        int posY = 0;

        int size = ROOM_SIZE * room.getDoors().size();

        // drawing people
        Image personImage = new Image("file:stick_person.png");
        for (User user: sim.getUsersInRoom(room)) {
            if (posX >= size) {
                posX = 0;
                posY -= spacingY;
            }
            int finalX = x + posX;
            int finalY = y + posY;

            gc.drawImage(personImage, finalX , finalY, PERSON_HEIGHT, PERSON_WIDTH);
            posX += spacingX;
        }
    }

    /**
     * Draws one person
     * @param x the x position of where to draw
     * @param y the y position of where to draw
     */
    public void drawPerson(int x, int y) {
        Image personImage = new Image("file:stick_person.png");
        gc.drawImage(personImage, x , y, PERSON_HEIGHT, PERSON_WIDTH);
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
     * Sets the time settings filled by user, or sets time speed only.
     *
     * @param actionEvent event that triggers the method
     */
    //shows the time
    public void setTime(ActionEvent actionEvent) {
        int hour;
        int minute;
        int second;
        float speed;

        // if time itself is empty but, options are: timespeed filled, or not filled
        if (timeHourInput.getText().length() == 0 &&
                timeMinuteInput.getText().length() == 0 &&
                timeSecondInput.getText().length() == 0) {
            if (timeSpeedInput.getText().length() == 0) {
                printToConsole("Require at least time speed setting.");
            }
            else {
                speed = Float.parseFloat(timeSpeedInput.getText());
                if (speed <= 0) {
                    printToConsole("Invalid time speed input.");
                    return;
                }
                sim.setTimeSpeed(speed);
                printToConsole("Set time speed.");
            }
            return;
        }

        // If it's not the case that all three time settings are empty, have to check all of them individually
        if (timeHourInput.getText().length() > 0 && timeHourInput.getText().length() < 3) {
            hour = Integer.parseInt(timeHourInput.getText());
            if (hour > 24 || hour < 0) {
                printToConsole("Invalid time input.");
                return;
            }
        }
        else {
            printToConsole("Invalid time input.");
            return;
        }

        if (timeMinuteInput.getText().length() > 0 && timeMinuteInput.getText().length() < 3) {
            minute = Integer.parseInt(timeMinuteInput.getText());
            if (minute > 59 || minute < 0) {
                printToConsole("Invalid time input.");
                return;
            }
        }
        else {
            printToConsole("Invalid time input.");
            return;
        }
        if (timeSecondInput.getText().length() > 0 && timeSecondInput.getText().length() < 3) {
            second = Integer.parseInt(timeSecondInput.getText());
            if (second > 59 || second < 0) {
                printToConsole("Invalid time input.");
                return;
            }
        }
        else {
            printToConsole("Invalid time input.");
            return;
        }
        if (timeSpeedInput.getText().length() > 0) {
            speed = Float.parseFloat(timeSpeedInput.getText());
            if (speed <= 0) {
                printToConsole("Invalid time speed input.");
                return;
            }
        }
        else {
            printToConsole("Invalid time speed input.");
            return;
        }
        Time t = new Time(hour, minute, second);
        printToConsole(sim.setTimeSpeed(speed));
        printToConsole(sim.setTime(t));
        updateDashboard();
    }

    /**
     * Simply updates dashboard, which updates time field
     */
    @FXML
    public void updateTime() {
        if(sim.getIsAway()) {
            String f = TimeframeFrom.getText();
            String t = TimeframeTo.getText();
            if (!f.equals("From") && !t.equals("To")) {
                DateFormat formatter = new SimpleDateFormat("HH:mm");
                try {
                    Time from =  new java.sql.Time(formatter.parse(f).getTime());
                    Time to =  new java.sql.Time(formatter.parse(t).getTime());
                    String[] s = sim.getTime().toString().split(":");
                    Time now = new java.sql.Time(formatter.parse(s[0] + ":" + s[1]).getTime());
                    if(now.after(to) || now.before(from)){
                        updateDashboard();
                        return;
                    }
                } catch (ParseException e) {
                    updateDashboard();
                    return;
                }
            }
            for(Room r : sim.getHouse().getRooms()){
                r.setLightsOn(false);
            }
            for(Room r : sim.getRoomsWithAwayLights()){
                r.setLightsOn(true);
            }
        }
        renderLayout(sim.getHouse());
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

    /**
     * Change the value for the SHC door selector combo-box.
     */
    @FXML
    public void shcChangeDoor() {
        String message;
        if (shcDoorSelect.getValue() != null) {
            updateSHCDoorButtons();
            logText(sim.pw, printToConsole("Successfully changed door."));
        }
    }

    /**
     * Opens or closes the door selected by the SHC module.
     * @throws IOException
     * @throws JSONException
     */
    @FXML
    public void shcChangeDoorOpen() throws IOException, JSONException {

        if ((shcRoomSelect.getValue() == null) || (shcDoorSelect.getValue() == null)) {
            logText(sim.pw, printToConsole("ERROR: Not all fields were filled in before clicking the button"));
            return;
        }
        if (sim.getLoggedInUser().getType() == UserType.STRANGER) {
            logText(sim.pw, printToConsole("ERROR: Strangers are not allowed to change the state of the doors."));
            return;
        }

        String chosenDoorName = shcDoorSelect.getValue();
        if (chosenDoorName == null)
            return;;

        int chosenDoorIndex = Integer.parseInt(chosenDoorName.substring(5)) - 1;
        Room room = sim.getHouse().getRoomFromName(shcRoomSelect.getValue());
        if (room == null)
            return;

        Door d = room.getDoors().get(chosenDoorIndex);
        logText(sim.pw, printToConsole(d.toggleOpen()));
        updateSHCDoorButtons();
        this.renderLayout(sim.getHouse());
    }

    /**
     * Toggles the lightsOn property of the room selected by the
     * SHC module, and updates the visualization to reflect this new
     * value.
     * @throws IOException
     * @throws JSONException
     */
    @FXML
    public void toggleRoomLights() throws IOException, JSONException {
        Room room = sim.getHouse().getRoomFromName(shcRoomSelect.getValue());
        if (room == null) {
            return;
        }

        logText(sim.pw, printToConsole(room.toggleLightsON()));
        this.renderLayout(sim.getHouse());
    }
  
    /**
     * sets the isAway boolean to whatever it is on the
     * checkbox field in the SHP. It also notifies the observers.
     * @param actionEvent event that triggers this method
     */
    public void setAwayMode(ActionEvent actionEvent){
        logText(sim.pw, printToConsole(sim.setSimulationAway(awayButton.isSelected())));
        logText(sim.pw, printToConsole(sim.notifyMotionSensors()));
        updateDashboard();
        this.renderLayout(sim.getHouse());
    }

    /**
     * simulates an intruder invading a home
     * @param actionEvent event that triggers this method
     */
    public void invadeHome(ActionEvent actionEvent){
        logText(sim.pw, printToConsole(sim.invadeSimHome(intruderCheck.isSelected())));

        Timer timer = new Timer();
        timer.schedule(new CopCaller(), sim.getCopDelay()*1000);

        updateDashboard();
    }

    /**
     * Updates the copDelay attribute of the simulation.
     */
    @FXML
    public void updateCopDelay(){
        String copDelayString = copDelayField.getText();
        if (copDelayString.isEmpty()) {
            printToConsole("ERROR: You must enter a value for the new delay.");
            return;
        }

        int copDelay;
        try {
            copDelay = Integer.valueOf(copDelayString);
            if (copDelay < 0) {
                printToConsole("ERROR: You must enter a valid and positive integer.");
                return;
            }
            printToConsole(sim.setCopDelay(copDelay));
        } catch (Exception e) {
            printToConsole("ERROR: You must enter a valid integer.");
        }
    }

    /**
     * A Runnable nested class that prints a message to console
     * indicating that the cops have been called.
     * This functionality needs to be encapsulated in a runnable class
     * as it will be created and passed to a Timer.
     */
    private class CopCaller extends TimerTask {

        /**
         * Prints a message indicating that the cops have been called.
         */
        @Override
        public void run() {
            printToConsole("INTRUDER ALERT! THE AUTHORITIES HAVE BEEN NOTIFIED!");
        }
    }


}
