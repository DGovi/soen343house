package Model;

/**
 * this class represents an intruder.
 * An intruder's sole purpose is to enter a room and
 * be detected by a motion sensor in a room.
 */
public class Intruder {
    private Room room;

    public Intruder(Room room){
        this.room = room;
    }

    /**
     * gets the room in the house that the intruder is in
     * @return room object
     */
    public Room getRoom(){
        return room;
    }
}
