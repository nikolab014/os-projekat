package cpu;
import proces.Proces;

public class ThreadHandler extends Thread {
    Proces proces;

    public ThreadHandler(Proces proces) {
        this.proces = proces;
        this.start();

    }
    public void run(){

            CPU.execute(proces);
    }
}
