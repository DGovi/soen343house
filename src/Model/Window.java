package Model;

/**
 * Represents a window in the simulation
 * a window can be opened and closed and
 * can also be obstructed by a random object
 */
public class Window {
    private Boolean open;
    private Boolean obstructed;

    /**
     * Creates a window that is by default not open and not obstructed.
     */
    public Window() {
        this.open = false;
        this.obstructed = false;
    }

    /**
     * Gets the state of the window if obstructed or not.
     *
     * @return true if window is obstructed, false otherwise
     */
    public Boolean getObstructed() {
        return obstructed;
    }

    /**
     * Sets an obstruction on a window.
     * Can only obstruct if the window is open.
     *
     * @param obs boolean that sets the blocked state of the window
     */
    public void setObstructed(Boolean obs) {
        // Can only perform obstruction if window is open
        if (open) {
            obstructed = obs;
        } else {
            // ensure it's not obstructed when closed
            obstructed = false;
        }
    }

    /**
     * Gets the state of the window if open or not.
     *
     * @return true if window is open, false otherwise
     */
    public Boolean getOpen() {
        return open;
    }

    /**
     * Sets the state of the window open or not.
     * Can only close the window if it is not obstructed.
     *
     * @param open boolean representing the requested state of the window (true if open)
     */
    public void setOpen(Boolean open) {
        if (!obstructed) {
            this.open = open;
        }
    }

    /**
     * Changes the current state of the window (open or closed).
     * Can only close the window if it is not obstructed.
     * Can always open the window.
     *
     * @return message String to be printed when the operation is complete or cancelled
     */
    public String changeOpen() {
        if (obstructed && open) {
            return "ERROR: Cannot change from open while blocked.";
        } else {
            open = !open;
            return "Successfully changed state of window.";
        }
    }

    /**
     * Changes the current blocked state of blocking the window.
     * Can only block the window if it is open.
     * Can always unblock the window.
     *
     * @return message String to be printed when the operation is complete or cancelled
     */
    public String changeObstructed() {
        if (!open) {
            // ensure it is not obstructed while closed
            obstructed = false;
            return "ERROR: Can't block window if it's not open.";
        } else {
            obstructed = !obstructed;
            return "Successfully changed the blocking state of the window.";
        }
    }
}