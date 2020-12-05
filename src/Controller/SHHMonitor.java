package Controller;

public class SHHMonitor extends Thread{
    private final int SLEEP_TIME = 1000; //ms

    public void run() {
        while (true) {
            System.out.println("fizz pop bang");
            try {
                Thread.sleep(SLEEP_TIME);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
