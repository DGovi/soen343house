package Model;

import java.util.ArrayList;
import java.util.Objects;

public class Door {
   private boolean open;
   private String from;
   private String to;
   private static ArrayList<Door> existingDoors = new ArrayList<Door>();

   public Door(String from, String to) {
       this.from = from;
       this.to = to;
       this.open = false;
       existingDoors.add(this);
   }

   public boolean isOpen() {
      return open;
   }

   public void setOpen(boolean open) {
      this.open = open;
   }

   public String getFrom() {
      return from;
   }

   public void setFrom(String from) {
      this.from = from;
   }

   public String getTo() {
      return to;
   }

   public void setTo(String to) {
      this.to = to;
   }

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

   /*
   @Override
   public boolean equals(Object o) {
      if (o == null || getClass() != o.getClass()) return false;
      Door door = (Door) o;
      return (Objects.equals(this.from, door.from) && Objects.equals(this.to, door.to)) || (Objects.equals(this.from, door.to) && Objects.equals(this.to, door.from)) ;
   }
    */

   // opposite is a door where from is to and to is from
   public Door getOppositeDoor() {
      for (Door existingDoor: existingDoors) {
         if (this.getFrom().equals(existingDoor.getTo()) && this.getTo().equals(existingDoor.getFrom()))
            return existingDoor;
      }
      return null;
   }

}
