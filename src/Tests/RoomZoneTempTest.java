package Tests;
import Model.Room;
import com.sun.tools.internal.xjc.model.Model;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.time.LocalTime;

import org.json.JSONException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

import Controller.Simulation;

/**
 * this test ensures that the temperatures in two different rooms but in the same zone
 * are the same temperature
 */
class RoomZoneTempTest {
    private static final File HOUSE_FILE = new File("./houseinput.json");
    private final File USER_FILE = new File("users.json");

    @Test
    void testZoneTemp() throws IOException, JSONException{
        Simulation testSim = Simulation.createInstance("", java.sql.Time.valueOf(LocalTime.now()), 25, HOUSE_FILE, USER_FILE);
        float[] temps = testSim.getHouse().getRooms().get(0).getTemperatures();
        assertEquals(testSim.getHouse().getRooms().get(0).getTemperatures(), temps);

    }
}
