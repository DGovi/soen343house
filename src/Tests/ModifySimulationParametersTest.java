package Tests;

import Controller.DashboardController;
import Controller.Simulation;
import Model.House;
import Model.Room;
import Model.Window;
import org.json.JSONException;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.sql.Time;
import java.time.LocalTime;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class ModifySimulationParametersTest {
    private static final File HOUSE_FILE = new File("./houseinput.json");
    private static final File TEST_HOUSE = new File("./testInput.json");

    @Test
    void loadHouseLayout() throws IOException, JSONException {
        Simulation simulation = new Simulation("", java.sql.Time.valueOf(LocalTime.now()), 25, TEST_HOUSE, true);
        House loadedHouse = simulation.getHouse();

        ArrayList<Room> rooms =  new ArrayList<>();
        ArrayList<Window> windows =  new ArrayList<>();

        ArrayList<String> roomsGarage = new ArrayList<>();
        ArrayList<String> roomsKitchen = new ArrayList<>();

        roomsGarage.add("Kitchen");
        roomsKitchen.add("Garage");

        windows.add(new Window());
        rooms.add(new Room("Garage", windows, 1, roomsGarage));
        rooms.add(new Room("Kitchen", new ArrayList<>(), 0, roomsKitchen));

        House testHouse = new House(rooms,"Test location");

        for(int i = 0; i < loadedHouse.getRooms().size(); i++){
            Room rL = loadedHouse.getRooms().get(i);
            Room rT = testHouse.getRooms().get(i);

            assertEquals(rL.getName(), rT.getName());
            assertEquals(rL.getWindows().size(), rT.getWindows().size());
            assertEquals(rL.getLights(), rT.getLights());
            for (int j = 0; j < rL.getDoors().size(); j++){
                assertEquals(rL.getDoors().get(j), rT.getDoors().get(j));
            }
        }
    }

    @Test
    void setHouseLocation() throws IOException, JSONException {
        Simulation simulation = new Simulation(new String(), java.sql.Time.valueOf(LocalTime.now()), 25, HOUSE_FILE, true);
        String location = "Software Development Hell";
        assertEquals("Set house location to " + location + "!",simulation.setHouseLocation(location));
        assertEquals(location, simulation.getHouseLocation());
    }

    @Test
    void setOutsideTemperature() throws IOException, JSONException {
        Simulation simulation = new Simulation(new String(), java.sql.Time.valueOf(LocalTime.now()), 25, HOUSE_FILE, true);
        String temperature = "42069.0";
        assertEquals("Setting simulation temperature to " + temperature + "!", simulation.setTemperature(temperature));
        assertEquals(temperature, String.valueOf(simulation.getTemperature()));
    }

    @Test
    void turnSimOnOff() throws IOException, JSONException {
        Simulation simulation = new Simulation(new String(), java.sql.Time.valueOf(LocalTime.now()), 25, HOUSE_FILE, true);
        // turn off then back on
        assertEquals("Simulation OFF", simulation.toggleRunning());
        assertEquals("Simulation ON", simulation.toggleRunning());
    }

    @Test
    void setTime() throws IOException, JSONException {
        Simulation simulation = new Simulation(new String(), java.sql.Time.valueOf(LocalTime.now()), 25, HOUSE_FILE, true);
        Time time =java.sql.Time.valueOf(LocalTime.now());
        assertEquals("Time set to: " + time + ".", simulation.setTime(time));
    }


    @Test
    void setDate() throws IOException, JSONException {
        Simulation simulation = new Simulation(new String(), java.sql.Time.valueOf(LocalTime.now()), 25, HOUSE_FILE, true);
        String date = "friday";
        assertEquals("Date set to: " + date + ".", simulation.setDate(date));
    }
}