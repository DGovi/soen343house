package Model;

import java.util.ArrayList;
import java.util.Objects;

public class Door {
   private boolean open;
   private String name;
   private static ArrayList<Door> existingDoors = new ArrayList<Door>();

   public Door(String name) {
      this.name = name;
      this.open = false;
      existingDoors.add(this);
   }

   public boolean isOpen() {
      return open;
   }

   public void setOpen(boolean open) {
      this.open = open;
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

   public String getName() {
      return name;
   }

   public void setName(String name) {
      this.name = name;
   }

   @Override
   public boolean equals(Object o) {
      if (o == null || getClass() != o.getClass()) return false;
      Door door = (Door) o;
      return Objects.equals(name, door.name);
   }

   public static Door getExistingDoor(String name) {
      for (Door door : existingDoors) {
         if (door.getName().equals(name))
            return door;
      }
      return null;
   }

}
