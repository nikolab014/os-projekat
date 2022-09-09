package proces;

import cpu.ThreadHandler;

import java.util.LinkedList;
import java.util.Queue;

public class ProcesScheduler {

    private static Queue<Proces> readyQueue = new LinkedList<>();
    private static Proces aktivniProces = null;

    public static synchronized void schedule() {

        if(!readyQueue.isEmpty() && aktivniProces == null) {
                Proces proces = readyQueue.remove();
                aktivniProces = proces;
                proces.setState("RUNNING");

                new ThreadHandler(proces);

        }
    }

    public static Queue<Proces> getReadyQueue(){
        return readyQueue;
    }

    public static Proces getAktivniProces(){
        return aktivniProces;
    }
    public static void removeRunningProcess() {
        aktivniProces=null;
    }
    public static void setAktivniProces(Proces proces){
        aktivniProces = proces;
    }


}
