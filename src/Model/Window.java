package Model;

/**
 * Represents a window in the simulation
 */
public class Window {

    private Boolean obstructed;
    private ArbitraryObject obstruction;

    /**
     * creates a window
     */
    public Window() {
        obstructed = false; obstruction = null;
    }

    /**
     * creates a window with an obstruction
     * @param obs object that blocks a window
     */
    public Window(ArbitraryObject obs) {
        obstruction = obs;
        obstructed = (obs != null);
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
        setObstructed(obs, "Arbitrary Object");
    }

    /**
     * set and obstruction on a window and displays a message
     * @param obs object that is obstructing the window
     * @param s message
     */
    public void setObstructed(Boolean obs, String s) {
        if (obs) {
            obstruction = new ArbitraryObject(s);
            obstructed = true;
        }
        else {
            obstruction = null;
            obstructed = false;
        }
    }
}