package Model;

/**
 * represents an object that will be blocking a window
 * or a door in the simulation
 */
public class ArbitraryObject {

    private String name;

    /**
     * creates an arbitrary object with name s
     * @param s name of the object
     */
    public ArbitraryObject(String s) { name = s; }

    /**
     * creates an arbitrary object
     */
    public ArbitraryObject() { name = "Random Object"; }

    /**
     *
     * @return string that is the name of the object
     */
    public String getName() { return name; }
}