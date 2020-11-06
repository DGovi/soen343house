package Tests;

import Controller.Simulation;
import org.json.JSONException;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

class BlockUnBlockWindowTest {
    private static final File HOUSE_FILE = new File("./houseinput.json");

    @Test
    void blockWindow() throws IOException, JSONException {
        Simulation simulation = new Simulation(new String(), java.sql.Time.valueOf(LocalTime.now()), 25, HOUSE_FILE);
        assertEquals(
                false,
                simulation.getHouse().getRooms().get(0).getWindows().get(0).getOpen(),
                "Window should be closed at the start."
        );

        simulation.getHouse().getRooms().get(0).getWindows().get(0).changeOpen();
        simulation.getHouse().getRooms().get(0).getWindows().get(0).changeObstructed();

        assertEquals(
                true,
                simulation.getHouse().getRooms().get(0).getWindows().get(0).getOpen(),
                "Window should be open after running changeOpen"
        );
        assertEquals(
                true,
                simulation.getHouse().getRooms().get(0).getWindows().get(0).getObstructed(),
                "Window should be blocked after running changeObstructed while the window is open."
        );

    }

    @Test
    void unBlockWindow() throws IOException, JSONException {
        Simulation simulation = new Simulation(new String(), java.sql.Time.valueOf(LocalTime.now()), 25, HOUSE_FILE);
        assertEquals(
                false,
                simulation.getHouse().getRooms().get(0).getWindows().get(0).getOpen(),
                "Window should be closed at the start."
        );

        simulation.getHouse().getRooms().get(0).getWindows().get(0).changeOpen();
        simulation.getHouse().getRooms().get(0).getWindows().get(0).changeObstructed();

        assertEquals(
                true,
                simulation.getHouse().getRooms().get(0).getWindows().get(0).getOpen(),
                "Window should be open after running changeOpen"
        );
        assertEquals(
                true,
                simulation.getHouse().getRooms().get(0).getWindows().get(0).getObstructed(),
                "Window should be blocked after running changeObstructed while the window is open."
        );

        simulation.getHouse().getRooms().get(0).getWindows().get(0).changeObstructed();

        assertEquals(
                false,
                simulation.getHouse().getRooms().get(0).getWindows().get(0).getObstructed(),
                "Window should be blocked after running changeObstructed again while the window is open."
        );
    }

}