package Tests;

import Controller.Simulation;
import org.json.JSONException;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.sql.Time;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.*;

class ModifySimulationParametersTest {
    private static final File HOUSE_FILE = new File("./houseinput.json");
    private final File USER_FILE = new File("users.json");

    @Test
    void setHouseLocation() throws IOException, JSONException {
        Simulation simulation = Simulation.createInstance("", java.sql.Time.valueOf(LocalTime.now()),  HOUSE_FILE, USER_FILE);
        String location = "Software Development Hell";
        assertEquals("Set house location to " + location + "!", simulation.setHouseLocation(location));
        assertEquals(location, simulation.getHouseLocation());
    }

    @Test
    void setOutsideTemperature() throws IOException, JSONException {
        Simulation simulation = Simulation.createInstance("", java.sql.Time.valueOf(LocalTime.now()),  HOUSE_FILE, USER_FILE);
        String temperature = "42069.0";
        assertEquals("Setting simulation temperature to " + temperature + "!", simulation.setTemperature(temperature));
        assertEquals(temperature, String.valueOf(simulation.getTemperature()));
    }

    @Test
    void turnSimOnOff() throws IOException, JSONException {
        Simulation simulation = Simulation.createInstance("", java.sql.Time.valueOf(LocalTime.now()),  HOUSE_FILE, USER_FILE);
        // turn off then back on
        assertEquals("Simulation OFF", simulation.toggleRunning());
        assertEquals("Simulation ON", simulation.toggleRunning());
    }

    @Test
    void setTime() throws IOException, JSONException {
        Simulation simulation = Simulation.createInstance("", java.sql.Time.valueOf(LocalTime.now()), HOUSE_FILE, USER_FILE);
        Time time = java.sql.Time.valueOf(LocalTime.now());
        assertEquals("Time set to: " + time + ".", simulation.getSimulationTimes().setTime(time));
    }


    @Test
    void setDate() throws IOException, JSONException {
        Simulation simulation = Simulation.createInstance("", java.sql.Time.valueOf(LocalTime.now()), HOUSE_FILE, USER_FILE);
        String date = "friday";
        assertEquals("Date set to: " + date + ".", simulation.getSimulationTimes().setDate(date));
    }
}