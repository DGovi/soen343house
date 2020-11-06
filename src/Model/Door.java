package Model;

public class Door {
   private boolean open;

   public Door(boolean open) {
      this.open = open;
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
}
