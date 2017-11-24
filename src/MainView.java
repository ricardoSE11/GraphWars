import Algorithms.DFSAlgorithm;
import Classes.StatusEscudo;
import Classes.ThreadArista;
import org.graphstream.algorithm.Dijkstra;
import org.graphstream.algorithm.Kruskal;
import org.graphstream.algorithm.Prim;
import org.graphstream.algorithm.TarjanStronglyConnectedComponents;
import org.graphstream.graph.*;
import org.graphstream.graph.implementations.AdjacencyListGraph;
import org.graphstream.graph.implementations.SingleGraph;
import org.graphstream.ui.spriteManager.SpriteManager;
import org.graphstream.ui.swingViewer.ViewPanel;
import org.graphstream.ui.view.Viewer;

import javax.sound.sampled.*;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class MainView {
    private boolean juegoIniciado=false;
    private int precioArista;
    private int turno=-1;
    private Node currentNode;
    private JFrame frame;
    private Graph graph;

    private JPanel basePanel;
    private JButton agregarNodoButton;
    private JTextField txtNombreNodo;
    private JTextField txtDestino;
    private JTextField txtOrigen;
    private JButton agregarAristaButton;
    private JSpinner spnPesoNormal;
    private JSpinner spnPesoFastWay;
    private JButton iniciarJuegoButton;
    private JSpinner spnTiempoInactividadArista;
    private JSpinner spnDanioArista;
    private JSpinner spnTiempoMeditacion;
    private JSpinner spnDineroInicial;
    private JSpinner spnCostoDeArista;
    private JSpinner spnCostoDesbloqueo;
    private JLabel lblDineroInicial;
    private JPanel frameConfiguraciones;
    private JPanel frameAgregarNodo;
    private JButton terminarTurnoButton;
    private JLabel lblTurno;
    private JLabel lblDinero;
    private JPanel frameInfo;
    private JLabel lblPesoNormal;
    private JLabel lblPesoFastWay;
    private JComboBox cmbMensajeTipo;
    private JTextField txtMensajeOrigen;
    private JButton btnEnviarMensaje;
    private JPanel frameInteraccion;
    private JLabel lblTipo;
    private JButton SmokeWeedButton;
    private JPanel frameEscudos;
    private JButton EspejoButton;
    private JButton BombEffectButon;
    private JSpinner spnPorcentajeMeditacion;

    public int PRECIODEESCUDO=50;

    public MainView() {




        frame= new JFrame("MainView");
        frame.setContentPane(basePanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);


        graph = new SingleGraph("Juego");

        /*
         * TODO: Esta direccion hay que subirlo a un gist en github y sacarla desde ahi
         */
        //graph.addAttribute("ui.stylesheet", "url('--- INSERTE URL ---')");

        //*******************************************************
        //QUITAR COMENTARIO PARA HACER QUE SE VEA CON MAS CALIDAD
        //*******************************************************
        //graph.addAttribute("ui.quality");
        graph.addAttribute("ui.antialias");//Este se puede desactivar, pero se ve feo.

        /* Nodos de prueba
        graph.addNode("A");
        graph.addNode("B");
        graph.addNode("C");
        graph.addEdge("AB", "A", "B", true);
        graph.addEdge("BC", "B", "C", true);
        graph.addEdge("CA", "C", "A", true);

        for (Node node : graph) {

            //node.addAttribute("vida", 100);
        }*/

        Viewer viewer = new Viewer(graph, Viewer.ThreadingModel.GRAPH_IN_GUI_THREAD);
        viewer.enableAutoLayout();

        ViewPanel view = viewer.addDefaultView(false);
        frame.add(view);
        frame.pack();
        frame.setVisible(true);



        agregarNodoButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                agregarNodo();
            }
        });
        agregarAristaButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                agregarArista();
            }
        });
        iniciarJuegoButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                iniciarJuego();
            }
        });
        terminarTurnoButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                terminarTurno();
            }
        });
        btnEnviarMensaje.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                enviarMensaje();
            }
        });
        SmokeWeedButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                String soundName = "yes.wav";
                AudioInputStream audioInputStream = null;
                Clip clip=null;
                try {
                    audioInputStream = AudioSystem.getAudioInputStream(new File(soundName).getAbsoluteFile());
                    clip = AudioSystem.getClip();
                    clip.open(audioInputStream);
                    clip.start();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                StatusEscudo escudo=((StatusEscudo)currentNode.getAttribute("escudos"));
                if(!escudo.meditando)
                    if(gastarDinero(PRECIODEESCUDO))
                        escudo.meditar((int)spnTiempoMeditacion.getValue(),(int)spnPorcentajeMeditacion.getValue());
            }
        });
        EspejoButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                StatusEscudo escudo=((StatusEscudo)currentNode.getAttribute("escudos"));
                if(!escudo.tieneEspejo)
                    if(gastarDinero(PRECIODEESCUDO))
                        escudo.tieneEspejo=true;
            }
        });
        BombEffectButon.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                StatusEscudo escudo=((StatusEscudo)currentNode.getAttribute("escudos"));
                if(!escudo.tieneBombEffect)
                    if(gastarDinero(PRECIODEESCUDO))
                        escudo.tieneBombEffect=true;
            }
        });
    }

    private void enviarMensaje() {
        if(origenValido()){
            Node dest;
            try{
                Node orig = graph.getNode(txtOrigen.getText());
                dest = graph.getNode(txtDestino.getText());
                String tipo = String.valueOf(cmbMensajeTipo.getSelectedItem());
                float costo=0;
                int randomNum;

                switch (tipo){
                    case "Hit":

                        hit(orig, dest,costo, true,false);
                        break;

                    case "Teletransportacion":
                        System.out.println("Enviando un Dijkstra");
                        ArrayList<Edge> aristasDijkstra = dijkstra(graph , orig , dest);
                        if (aristasDijkstra.size()>0){
                            for (Edge e:aristasDijkstra) {
                                costo+=(int) e.getAttribute("pesoFastWay");
                            }
                            costo/=aristasDijkstra.size();
                            costo*=10;
                            if(gastarDinero((int)costo)){
                                desgastarAristas(aristasDijkstra);
                                recibirDmg(orig, dest,(int)costo);
                                System.out.println("Se envio un dijkstra de: " + orig.getId() + " a " + dest.getId() );
                            }
                        }
                        break;

                    case "Prim":
                        Prim prim=new Prim("pesoNormal","prim","in", "notin");
                        prim.init(graph);
                        prim.compute();
                        randomNum = ThreadLocalRandom.current().nextInt(2, 4+ 1);
                        costo =  randomNum*(((float)prim.getTreeWeight())/100f*60f);
                        if(gastarDinero((int)costo))
                        {
                            DFSAlgorithm dfsPrim=new DFSAlgorithm();
                            dfsPrim.init(graph);
                            dfsPrim.attrib="prim";
                            dfsPrim.compute(orig,dest);
                            costo -= dfsPrim.obtenerCosto();
                            if(dfsPrim.lista.size()>0){
                                ArrayList<Edge> aristasDFS = dfsPrim.obtenerEdges();
                                desgastarAristas(aristasDFS);
                                recibirDmg(orig, dest,(int)costo);
                                System.out.println("Se envio un prim de: " + orig.getId() + " a " + dest.getId() );
                            }else
                                System.out.println("Prim escogio caminos que no se pueden usar :<");

                        }
                        break;

                    case "Kruscal":
                        Kruskal kruskal = new Kruskal("pesoNormal","krusk","in", "notin");
                        kruskal.init(graph);
                        kruskal.compute();
                        randomNum = ThreadLocalRandom.current().nextInt(2, 4+ 1);
                        costo =  randomNum*(((float)kruskal.getTreeWeight())/100f*60f);
                        if(gastarDinero((int)costo))
                        {
                            DFSAlgorithm dfsKruskal = new DFSAlgorithm();
                            dfsKruskal.init(graph);
                            dfsKruskal.attrib="krusk";
                            dfsKruskal.compute(orig,dest);
                            costo -= dfsKruskal.obtenerCosto();
                            if(dfsKruskal.lista.size()>0){
                                ArrayList<Edge> aristasDFS = dfsKruskal.obtenerEdges();
                                desgastarAristas(aristasDFS);
                                recibirDmg(orig, dest,(int)costo);
                                System.out.println("Se envio un Kruskal de: " + orig.getId() + " a " + dest.getId() );
                            }else
                                System.out.println("Kruskal escogio caminos que no se pueden usar :<");

                        }
                        break;

                    case "Multishot":
                        costo = (pesosPonderados()[0]*10);
                        multihit(orig,dest,costo,false);
                        break;

                    case "Kamikaze":
                        costo = (pesosPonderados()[0]*20);
                        multihit(orig,dest,costo,true);
                        break;


                }
                actualizarEtiquetaDeNodo(orig);

                lblDinero.setText(currentNode.getAttribute("dinero").toString());

            }catch (ElementNotFoundException e){
                JOptionPane.showMessageDialog(frame,
                        "Revise los nombres de los nodos",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }

    }

    private void multihit(Node orig, Node dest, float costo, boolean usarBloq) {
        System.out.println("Enviando un multihit");
        if(!gastarDinero((int)costo))
            return;

        DFSAlgorithm algorithm = new DFSAlgorithm();
        algorithm.init(graph);
        algorithm.usarBloqueadas=usarBloq;
        algorithm.compute(orig,dest);
        for (ArrayList<Node> array:algorithm.listas) {
            costo -=-algorithm.obtenerCosto(array);
            if(array.size()>0){
                ArrayList<Edge> aristasDFS = algorithm.obtenerEdges(array);
                desgastarAristas(aristasDFS);
                recibirDmg(orig, dest,(int)costo);
                System.out.println("Se envio un multihit de: " + orig.getId() + " a " + dest.getId() );
            }
        }


    }

    private void hit(Node orig, Node dest, float costo, boolean cobrar, boolean sumar) {
        System.out.println("Enviando un hit");
        int randomNum = ThreadLocalRandom.current().nextInt(3, 5+ 1);
        costo = costo==0? randomNum*(pesosPonderados()[0]/100f*40f) : costo;
        if(cobrar)
            if(!gastarDinero((int)costo))
                return;

        DFSAlgorithm algorithm = new DFSAlgorithm();
        algorithm.init(graph);
        algorithm.compute(orig,dest);
        costo = sumar? costo+algorithm.obtenerCosto(): costo-algorithm.obtenerCosto();
        if(algorithm.lista.size()>0){
            ArrayList<Edge> aristasDFS = algorithm.obtenerEdges();
            desgastarAristas(aristasDFS);
            recibirDmg(orig, dest,(int)costo);
            System.out.println("Se envio un hit de: " + orig.getId() + " a " + dest.getId() );
        }


    }

    private void recibirDmg(Node origen, Node dest, int costo ){
        StatusEscudo escudo=((StatusEscudo)dest.getAttribute("escudos"));
        if(escudo.tieneBombEffect){
            efectoBomba(origen, dest, costo);
            escudo.tieneBombEffect=false;
        }
        if(escudo.tieneEspejo){
            efectoEspejo(origen,dest,costo);
        }
        else {
            int siguienteVida=(int)dest.getAttribute("vida")-costo;
            if (siguienteVida<0)
                siguienteVida=0;
            dest.addAttribute("vida",siguienteVida);
            actualizarEtiquetaDeNodo(dest);
        }
    }

    private boolean origenValido(){
        try{
            Node node = graph.getNode(txtOrigen.getText());

            return node==currentNode||((ArrayList) currentNode.getAttribute("hijos")).contains(node);

        }catch (ElementNotFoundException e){
            JOptionPane.showMessageDialog(frame,
                    "Revise los nombres de los nodos",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
        return false;
    }

    private void terminarTurno() {
        do{
            turno = turno + 1 < graph.getNodeCount()? turno + 1 : 0 ;
            currentNode = graph.getNode(turno);
        }while ((int)currentNode.getAttribute("vida") == 0);

        lblTurno.setText(currentNode.getId());
        lblDinero.setText(currentNode.getAttribute("dinero").toString());
    }

    private boolean gastarDinero(int n){
        boolean res = n <= (int)currentNode.getAttribute("dinero");
        if(!res){
            JOptionPane.showMessageDialog(frame,
                    "No tiene dinero suficiente",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            return res;
        }
        currentNode.addAttribute("dinero",(int)currentNode.getAttribute("dinero")-n);
        return res;

    }

    private void iniciarJuego() {
        if(grafoEsConexo()){
            juegoIniciado=true;
            inicializarNodos();

            lblDineroInicial.setVisible(false);
            spnDineroInicial.setVisible(false);
            spnCostoDeArista.setVisible(false);
            precioArista=(int)spnCostoDeArista.getValue();
            lblPesoFastWay.setVisible(false);
            lblPesoNormal.setVisible(false);
            spnPesoFastWay.setVisible(false);
            spnPesoNormal.setVisible(false);
            frameAgregarNodo.setVisible(false);
            iniciarJuegoButton.setVisible(false);
            lblTipo.setVisible(true);
            cmbMensajeTipo.setVisible(true);
            frameEscudos.setVisible(true);


            frameInfo.setVisible(true);
            terminarTurnoButton.setVisible(true);
            btnEnviarMensaje.setVisible(true);

            frameConfiguraciones.revalidate();
            //frameConfiguraciones.repaint();

            terminarTurno();
        }else{
            JOptionPane.showMessageDialog(frame,
                    "El grafo no es conexo",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }

    }

    private void actualizarEtiquetaDeNodo(Node n){
        n.addAttribute("ui.label", n.getId()+"\n"+
                "Vida:"+n.getAttribute("vida").toString()+
                "Escudos:"+n.getAttribute("escudos").toString()+
                "Dinero:"+n.getAttribute("dinero").toString()+
                "Hijos:"+((ArrayList) n.getAttribute("hijos")).size());
    }

    private void actualizarEtiquetaDeArista(Edge edge)
    {
        edge.addAttribute("ui.label","PN: " + String.valueOf((int) spnPesoNormal.getValue())+" - FW: "+
                String.valueOf((int) spnPesoFastWay.getValue()) + " - "  + "Vida: " + edge.getAttribute("vida") );
    }

    private void inicializarNodos() {
        for (Node n : graph.getEachNode()){
            n.addAttribute("escudos", new StatusEscudo(n));
            n.addAttribute("dinero",(int) spnDineroInicial.getValue());
            n.addAttribute("vida",100);
            n.addAttribute("hijos",new ArrayList<Node>());
            actualizarEtiquetaDeNodo(n);

            /*
                vecinos esta dentro de nodo (neighbourmap)
             */
        }
    }

    private boolean grafoEsConexo() {
        TarjanStronglyConnectedComponents tscc = new TarjanStronglyConnectedComponents();
        tscc.init(graph);
        tscc.compute();

        int max=-1;
        for (Node n : graph.getEachNode()){
            int group=Integer.parseInt((n.getAttribute(tscc.getSCCIndexAttribute()).toString()));
            max=(group)>max?group:max;
        }

        return max==0;
    }

    //D: Funcion que dado un grafo y dos Nodos, retorna la lista con el nombre de los Nodos que llevan al camino mas corto
    private ArrayList<Edge> dijkstra(Graph graph , Node origen ,  Node destino)
    {
        Dijkstra dijkstra = new Dijkstra(Dijkstra.Element.EDGE, null, "pesoFastWay");

        // Compute the shortest paths in g from A(origen) to all nodes
        dijkstra.init(graph);
        dijkstra.setSource(origen);
        dijkstra.compute();

        // Print the shortest path from A(origen) to B(destino)
        //System.out.println(dijkstra.getPath(graph.getNode(destino)));

        Iterator<Edge> iterator = dijkstra.getPathEdgesIterator(destino);
        ArrayList<Edge> res=new ArrayList<>();
        while (iterator.hasNext())
            res.add(iterator.next());

        return res;



        /*String lista = dijkstra.getPath(destino).toString().replace("[","");
        lista = lista.replace("]" , "");
        System.out.println(lista);
        List<String> listaNodos = new ArrayList<>(Arrays.asList(lista.split(",")));
        //System.out.println(listaNodos);*/

    }

    private void agregarArista() {
        String s = txtOrigen.getText();
        String d = txtDestino.getText();
        if(s.isEmpty()||(d.isEmpty()&&!juegoIniciado)){
            JOptionPane.showMessageDialog(frame,
                    "Debe ingresar los puntos de la arista",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }
        try {
            Edge edge= graph.addEdge(s+d,s,d,true); //Este true hace que las aristas sean dirigidas
            edge.addAttribute("vida",100);
            edge.addAttribute("estaBloqueada",false);
            if(!juegoIniciado){
                edge.addAttribute("pesoNormal", (int) spnPesoNormal.getValue());
                edge.addAttribute("pesoFastWay", (int) spnPesoFastWay.getValue());
                edge.addAttribute("ui.label", "PN: " + String.valueOf((int) spnPesoNormal.getValue())+" - FW: "+
                        String.valueOf((int) spnPesoFastWay.getValue()) + " - "  + "Vida: " + edge.getAttribute("vida"));


            }else if(gastarDinero(precioArista)){
                //Todo: solo dejar que se puedan comprar nodos si el currentNode es el source
                float[] pesos=pesosPonderados();
                edge.addAttribute("pesoNormal", (int) pesos[0]);
                edge.addAttribute("pesoFastWay", (int) pesos[1]);
                edge.addAttribute("ui.label", "PN: " + String.valueOf(pesos[0])+" - FW: "+String.valueOf(pesos[1]) + " - " + "Vida: " + edge.getAttribute("vida"));

                lblDinero.setText(currentNode.getAttribute("dinero").toString());
                actualizarEtiquetaDeNodo(currentNode);
            }
            txtOrigen.setText("");
            txtDestino.setText("");
        }catch (ElementNotFoundException e){
            JOptionPane.showMessageDialog(frame,
                    "Revise los nombres de los nodos",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
        catch (IdAlreadyInUseException e){
            JOptionPane.showMessageDialog(frame,
                    "La arista ya existe",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private float[] pesosPonderados() {
        float p1=0, p2=0;
        for(Edge e : graph.getEdgeSet()){
            if(e.hasAttribute("pesoNormal")){
                p1+=(int)e.getAttribute("pesoNormal");
                p2+=(int)e.getAttribute("pesoFastWay");
            }
        }
        p1/=graph.getEdgeCount();
        p2/=graph.getEdgeCount();
        return new float[]{p1, p2};

    }

    private void agregarNodo() {
        String nombre =txtNombreNodo.getText();

        if(nombre.isEmpty()){
            JOptionPane.showMessageDialog(frame,
                    "Debe ingresar un nombre para el nodo",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }
        try{
            Node node = graph.addNode(nombre);
            node.addAttribute("ui.label", node.getId());



            txtNombreNodo.setText("");
        }
        catch (IdAlreadyInUseException e){
            JOptionPane.showMessageDialog(frame,
                    "El nombre del nodo ya esta en uso",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void desgastarAristas(ArrayList<Edge> aristas)
    {
        int substractValue = (int)spnDanioArista.getValue();
        for (Edge currEdge: aristas)
        {
            int currLife = currEdge.getAttribute("vida");
            currLife -= substractValue;
            currEdge.changeAttribute("vida" , currLife);
            actualizarEtiquetaDeArista(currEdge);
            if (currLife <= 0)
                desactivarArista(currEdge);
        }
    }

    private void desactivarArista(Edge arista)
    {
        int tiempo = (int)spnTiempoInactividadArista.getValue();
        ThreadArista desactivar = new ThreadArista(tiempo , arista);
        desactivar.run();
    }

    //Origen es quien envio el mensaje originalmente
    private void efectoEspejo(Node origen , Node destino, float costo)
    {
        hit(destino , origen , costo , false , true);
    }

    //Origen es quien envio el mensaje originalmente
    private void efectoBomba(Node origen , Node afectado, float costo)
    {
        Iterator<Node> iterator = afectado.getNeighborNodeIterator();
        ArrayList<Node> vecinos = new ArrayList<>();
        while (iterator.hasNext())
            vecinos.add(iterator.next());

        for (Node currNode: vecinos) {
            if (afectado.hasEdgeToward(currNode))
            {
                hit(afectado , currNode , costo , false , false);
            }
        }
    }

    public static void main(String[] args) {
        System.setProperty("org.graphstream.ui.renderer", "org.graphstream.ui.j2dviewer.J2DGraphRenderer");
        MainView mainView = new MainView();

    }
}
