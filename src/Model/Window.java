package Model;

/**
 * Represents a window in the simulation
 */
public class Window {

    private Boolean open;
    private Boolean obstructed;

    /**
     * creates a window
     */
    public Window() {
        this.open = false;
        this.obstructed = false;
    }

    /**
     * Gets the state of the window if obstructed or not
     * @return true if window is obstructed, false otherwise
     */
    public Boolean getObstructed() { return obstructed; }

    /**
     * sets an obstruction on a window
     * @param obs object that blocks a window
     */
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