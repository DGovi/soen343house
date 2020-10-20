package Tests;

import Controller.Simulation;
import org.json.JSONException;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.*;

class ModifySimulationParametersTest {
    private static final File HOUSE_FILE = new File("./houseinput.json");

    @Test
    void setHouseLocation() throws IOException, JSONException {
        Simulation simulation = new Simulation(new String(), java.sql.Time.valueOf(LocalTime.now()), 25, HOUSE_FILE, true);
        String location = "Software Development Hell";
        assertEquals("Set house location to " + location + "!",simulation.setHouseLocation(location));
    }
}