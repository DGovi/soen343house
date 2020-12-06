package Controller;

public class HVAC extends  Thread{
    private Simulation master_sim;

    public HVAC(Simulation master_sim){
        super();
        this.master_sim = master_sim;
    }

    public void run() {
        while (true) {
            System.out.println("hello world");

            // sleep
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

}
