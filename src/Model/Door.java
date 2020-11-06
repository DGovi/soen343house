package Model;

import java.util.Objects;

public class Door {
   private boolean open;
   private String name;

   public Door(String name) {
      this.name = name;
      this.open = false;
   }

   public boolean isOpen() {
      return open;
   }

   public void setOpen(boolean open) {
      this.open = open;
   }

   public void toggleOpen() {
      if (this.open)
         this.open = false;
      else
         this.open = true;
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

}
