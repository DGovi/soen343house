package Model;

public class Window {

    private Boolean obstructed;
    private ArbitraryObject obstruction;

    public Window() {
        obstructed = false; obstruction = null;
    }

    public Window(ArbitraryObject obs) {
        if (obs == null) {
            obstruction = null;
            obstructed = false;
        }
        else {
            obstruction = obs;
            obstructed = true;
        }
    }

    public Boolean getObstructed() { return obstructed; }

    public void setObstructed(Boolean obs) {
        setObstructed(obs, "Arbitrary Object");
    }

    public void setObstructed(Boolean obs, String s) {
        if (obs == true) {
            obstruction = new ArbitraryObject(s);
            obstructed = true;
        }
        else {
            obstruction = null;
            obstructed = false;
        }
    }
}