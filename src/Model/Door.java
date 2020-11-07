package Model;

import java.util.ArrayList;

/**
 * Represents a Door object within the Smart Home.
 * Doors are used in the rendering of the house layout visualization.
 * It is important for doors to store the Rooms that lay on both sides
 * for accurate identification of opposite doors.
 */
public class Door {
   private boolean open;
   private String from;
   private String to;
   private static ArrayList<Door> existingDoors = new ArrayList<Door>();

   /**
    * Creates a Door object given the names of the two
    * Rooms that lay on each side of it.
    * @param from the name of the first door
    * @param to the name of the second door
    */
   public Door(String from, String to) {
       this.from = from;
       this.to = to;
       this.open = false;
       existingDoors.add(this);
   }

   /**
    * Returns the open value of the door.
    * @return true if open, false otherwise
    */
   public boolean isOpen() {
      return open;
   }

   /**
    * Sets the open value of the door.
    * @param open the new value for open to be set to.
    */
   public void setOpen(boolean open) {
      this.open = open;
   }

   /**
    * Gets the from value of the door.
    * @return the name of the Room labelled from
    */
   public String getFrom() {
      return from;
   }

   /**
    * Sets the from value of the door.
    * @param from the new value to be set.
    */
   public void setFrom(String from) {
      this.from = from;
   }

   /**
    * Gets the to value of the door
    * @return the name of the Room labelled to
    */
   public String getTo() {
      return to;
   }

   /**
    * Sets the to value of the door.
    * @param to the new value to be set.
    */
   public void setTo(String to) {
      this.to = to;
   }

   /**
    * Toggles the open boolean value of this door.
    * Also performes the same toggle operation
    * for the opposite door.
    * @return String indicating the new state of this door
    */
   public String toggleOpen() {
      // toggling the opposite door
      Door oppositeDoor = this.getOppositeDoor();
      if (oppositeDoor != null) {
         if (oppositeDoor.isOpen())
             oppositeDoor.setOpen(false);
         else
             oppositeDoor.setOpen(true);
      }

      // toggling this door
      if (this.open) {
         this.open = false;
         return "Door is now closed!";
      } else {
         this.open = true;
         return "Door is now open!";
      }
   }

   // opposite is a door where from is to and to is from

   /**
    * Gets and returns the opposite Door of this door.
    * An oppossite door is one where the to and from attributes
    * are swapped.
    * These two Door objects model the same
    * real world door only from different perspectives.
    * @return the opposite Door if it exists, null otherwise
    */
   public Door getOppositeDoor() {
      for (Door existingDoor: existingDoors) {
         if (this.getFrom().equals(existingDoor.getTo()) && this.getTo().equals(existingDoor.getFrom()))
            return existingDoor;
      }
      return null;
   }

}
