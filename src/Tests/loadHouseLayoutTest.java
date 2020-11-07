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

class loadHouseLayoutTest {
    private static final File TEST_HOUSE = new File("./testInput.json");

    @Test
    void loadHouseLayout() throws IOException, JSONException {
        Simulation simulation = new Simulation("", java.sql.Time.valueOf(LocalTime.now()), 25, TEST_HOUSE);
        House loadedHouse = simulation.getHouse();

        ArrayList<Room> rooms =  new ArrayList<>();
        ArrayList<Window> windows =  new ArrayList<>();

        ArrayList<Door> roomsGarage = new ArrayList<Door>();
        ArrayList<Door> roomsKitchen = new ArrayList<Door>();

        roomsGarage.add(new Door("Garage", "Kitchen"));
        roomsKitchen.add(new Door("Kitchen", "Garage"));

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



}