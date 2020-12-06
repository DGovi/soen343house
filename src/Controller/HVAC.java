package Controller;

import Model.Room;

import java.sql.Time;

public class HVAC extends  Thread{
    private Simulation masterSim;

    public HVAC(Simulation masterSim){
        super();
        this.masterSim = masterSim;
    }

    public void run() {
        while (true) {
            //System.out.println("hello world");

            // sleep
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }


}
