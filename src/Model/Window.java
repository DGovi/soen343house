package Model;

public class Window {

    private Boolean obstructed;
    private ArbitraryObject obstruction;

    public Window() {
        obstructed = false; obstruction = null;
    }

    public Window(ArbitraryObject obs) {
        obstruction = obs;
        obstructed = (obs != null);
    }

    public Boolean getObstructed() { return obstructed; }

    public void setObstructed(Boolean obs) {
        setObstructed(obs, "Arbitrary Object");
    }

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