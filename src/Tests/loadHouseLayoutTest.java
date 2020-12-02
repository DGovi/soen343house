package Tests;

import Controller.Simulation;
import Model.Door;
import Model.House;
import Model.Room;
import Model.Window;
import org.json.JSONException;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.time.LocalTime;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class loadHouseLayoutTest {
    private static final File TEST_HOUSE = new File("./testInput.json");
    private final File USER_FILE = new File("users.json");

    @Test
    void loadHouseLayout() throws IOException, JSONException {
        Simulation simulation = Simulation.createInstance("", java.sql.Time.valueOf(LocalTime.now()), 25, TEST_HOUSE, USER_FILE);
        House loadedHouse = simulation.getHouse();

        ArrayList<Room> rooms = new ArrayList<>();
        ArrayList<Window> windows = new ArrayList<>();

        ArrayList<Door> doorsGarage = new ArrayList<Door>();
        ArrayList<Door> doorsKitchen = new ArrayList<Door>();

        doorsGarage.add(new Door("Garage", "Outside"));
        doorsGarage.add(new Door("Garage", "Kitchen"));
        doorsKitchen.add(new Door("Kitchen", "Garage"));

        windows.add(new Window());
        rooms.add(new Room("Garage", windows, 1, doorsGarage));
        rooms.add(new Room("Kitchen", new ArrayList<>(), 0, doorsKitchen));

        House testHouse = new House(rooms, "Test location");

        for (int i = 0; i < loadedHouse.getRooms().size(); i++) {
            Room rL = loadedHouse.getRooms().get(i);
            Room rT = testHouse.getRooms().get(i);
            System.out.println(rL.getName());

            assertEquals(rL.getName(), rT.getName());
            assertEquals(rL.getWindows().size(), rT.getWindows().size());
            assertEquals(rL.getLights(), rT.getLights());
            for (int j = 0; j < rL.getDoors().size(); j++) {
                assertTrue(rT.getDoors().get(j).equals(rT.getDoors().get(j)));
            }
        }
    }


}