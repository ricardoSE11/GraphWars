package Classes;

import org.graphstream.graph.Edge;

public class ThreadArista implements Runnable {

    private int tiempo;
    private Edge arista;

    public ThreadArista(int segundos , Edge arista)
    {
        this.tiempo = segundos;
        this.arista = arista;
    }

    @Override
    public void run() {
        try
        {
            Thread.sleep(1000 * tiempo);
            this.arista.addAttribute("vida" , 100);
        }

        catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
