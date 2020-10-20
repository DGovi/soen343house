package Model;

public class Window {

    private Boolean open;
    private Boolean obstructed;

    public Window() {
        open = false; obstructed = false;
    }

    public Boolean getObstructed() { return obstructed; }

    public void setObstructed(Boolean obs) {
        // Can only perform obstruction if window is open
        if (open) {
            obstructed = obs;
        }
        else {
            // ensure it's not obstructed when closed
            obstructed = false;
        }
    }

    public Boolean getOpen() { return open; }

    public void setOpen(Boolean open) {
        if (!obstructed) {
            this.open = open;
        }
    }

    public String changeOpen() {
        if (obstructed && open) {
            return "ERROR: Cannot change from open while blocked.";
        }
        else {
            open = !open;
            return "Successfully changed state of window.";
        }
    }

    public String changeObstructed() {
        if (!open) {
            // ensure it is not obstructed while closed
            obstructed = false;
            return "ERROR: Can't block window if it's not open.";
        }
        else {
            obstructed = !obstructed;
            return "Successfully changed the blocking state of the window.";
        }
    }
}