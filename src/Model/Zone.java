package Model;
import java.util.ArrayList;

/**
 * Represents a heating zone in the house
 */
public class Zone {
    private ArrayList<Room> rooms;
    private float[] temperatures;

    /**
     * Constructor with list of rooms in the zone
     * @param rooms
     */
    public Zone(ArrayList<Room> rooms) {
        this.rooms = rooms;
        this.temperatures = new float[3];
        this.temperatures[0] = 24;
        this.temperatures[1] = 24;
        this.temperatures[2] = 24;
    }

    /**
     * Constructor with a single starting room
     * @param room
     */
    public Zone(Room room) {
        this.rooms = new ArrayList<>();
        this.rooms.add(room);
        this.temperatures = room.getTemperatures();
    }

    /**
     * Takes in a list of zones and returns the index of the zone that contains the room
     * @param zones list of heating zones in the system
     * @param r room that is being looked for
     * @return if one of the zones has the room, returns index of the zone, otherwise returns -1
     */
    static public int indexOf(ArrayList<Zone> zones, Room r) {
        for (Zone z : zones) {
            if (z.rooms.contains(r)) {
                return zones.indexOf(z);
            }
        }
        return -1;
    }

    /**
     * Removes a room from the zone and returns a message of success of error
     * @param r
     * @return message of success or error
     */
    public String removeRoom(Room r) {
        int indexOfRoom = this.rooms.indexOf(r);

        if (indexOfRoom != -1) {
            this.rooms.remove(indexOfRoom);
            return "Successfully removed room from zone.";
        }
        return "Room is not in the zone that the system is trying to remove it from.";
    }

    /**
     * Adds a room to the zone and returns a message of success or error
     * @param r
     * @return message of success or error
     */
    public String addRoom(Room r) {
        int indexOfRoom = this.rooms.indexOf(r);

        if (indexOfRoom == -1) {
            this.rooms.add(r);
            return "Successfully added room to zone.";
        }
        return "Room is already in this zone. (Room should have been removed from zone and added back...)";
    }

    /**
     * Get desired temperatures of zone
     * @return float array with temperatures
     */
    public float[] getTemperatures() {
        return temperatures;
    }

    /**
     * Set desired period temperatures of zone
     * @param morning float desired temperature in the morning
     * @param day float desired temperature during the day
     * @param night float desired tmperature at night
     * @return message stating success
     */
    public String setTemperatures(float morning, float day, float night) {
        this.temperatures[0] = morning;
        this.temperatures[1] = day;
        this.temperatures[2] = night;

        return "Successfully set the period temperatures of the selected zone.";
    }
}
