package Tests;

import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.time.LocalTime;

import org.json.JSONException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

import Controller.Simulation;

/**
 * This is a JUnit class that tests the singleton pattern implemented.
 * <p>
 * It verifies if the single instance of a simulation is returned when
 * we try to call the constructor again.
 */
class CreateInstanceOfInstanceTest {
    private static final File TEST_HOUSE = new File("./testInput.json");
    private final File USER_FILE = new File("users.json");

    @Test
    void testCreateInstance() throws IOException, JSONException {
        Simulation test = Simulation.createInstance("", java.sql.Time.valueOf(LocalTime.now()),  TEST_HOUSE, USER_FILE);

        assertEquals(Simulation.createInstance("", java.sql.Time.valueOf(LocalTime.now()),  TEST_HOUSE, USER_FILE), test);
    }
}
