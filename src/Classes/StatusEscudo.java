package Classes;

import org.graphstream.graph.Node;

public class StatusEscudo implements Runnable{
    public int tiempo;
    public int porcentajeDeCuracion;
    public Node node;
    public boolean tieneEspejo;
    public boolean tieneBombEffect;
    public boolean meditando;
    public StatusEscudo(Node node) {
        this.node=node;
        tieneEspejo=false;
        tieneBombEffect=false;
        meditando=false;
    }

    public void meditar(int tiempo, int porcentajeDeCuracion){
        this.tiempo=tiempo;
        this.porcentajeDeCuracion=porcentajeDeCuracion;
        new Thread(this).start();
    }

    @Override
    public String toString() {
        return (tieneEspejo?"E ":"") +
                (tieneBombEffect?"B ":"") +
                (meditando?"M":"");
    }

    @Override
    public void run() {
        try {
            meditando=true;
            Thread.sleep(tiempo*1000);
            int nuevaVida=((int)node.getAttribute("vida")+porcentajeDeCuracion)<=100?
                    ((int)node.getAttribute("vida")+porcentajeDeCuracion):
                    100;
            node.addAttribute("vida",nuevaVida);
            meditando=false;
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
