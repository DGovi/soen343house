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
      if (this.open) {
         this.open = false;
         return "Door is now closed!";
      } else {
         this.open = true;
         return "Door is now open!";
      }
   }

   @Override
   public boolean equals(Object o) {
      if (o == null || getClass() != o.getClass()) return false;
      Door door = (Door) o;
      return (Objects.equals(this.from, door.from) && Objects.equals(this.to, door.to)) || (Objects.equals(this.from, door.to) && Objects.equals(this.to, door.from)) ;
   }

   /**
   public static Door getExistingDoor(Door newDoor) {

      for (Door existingDoor: existingDoors) {
         if (existingDoor.equals(newDoor))
            return existingDoor;
      }
      return newDoor;
   }
    */

}
