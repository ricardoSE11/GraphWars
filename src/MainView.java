import Algorithms.DFSAlgorithm;
import Classes.StatusEscudo;
import org.graphstream.algorithm.Dijkstra;
import org.graphstream.algorithm.TarjanStronglyConnectedComponents;
import org.graphstream.graph.*;
import org.graphstream.graph.implementations.SingleGraph;
import org.graphstream.ui.spriteManager.SpriteManager;
import org.graphstream.ui.swingViewer.ViewPanel;
import org.graphstream.ui.view.Viewer;
//Todo:Arreglar etiqueta de egde para mostrar todos los datos
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class MainView {
    private boolean juegoIniciado=false;
    private int precioArista;
    private int turno=-1;
    private Node currentNode;
    private JFrame frame;
    private Graph graph;
    private SpriteManager sman;

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

    public MainView() {

        frame= new JFrame("MainView");
        frame.setContentPane(basePanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);


        graph = new SingleGraph("Juego");
        sman = new SpriteManager(graph);
        
        /*
         * TODO: Esta direccion hay que subirlo a un gist en github y sacarla desde ahi
         */
        //graph.addAttribute("ui.stylesheet", "url('--- INSERTE URL ---')");

        //*******************************************************
        //QUITAR COMENTARIO PARA HACER QUE SE VEA CON MAS CALIDAD
        //*******************************************************
        //graph.addAttribute("ui.quality");
        //graph.addAttribute("ui.antialias");//Este se puede desactivar, pero se ve feo.

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
    }

    private void enviarMensaje() {
        if(origenValido()){
            Node dest;
            try{
                Node orig = graph.getNode(txtOrigen.getText());
                dest = graph.getNode(txtDestino.getText());
                String tipo = String.valueOf(cmbMensajeTipo.getSelectedItem());
                switch (tipo){
                    case "Hit":

                        System.out.println("Enviando un hit");
                        int randomNum = ThreadLocalRandom.current().nextInt(3, 5+ 1);
                        float costo = randomNum*(pesosPonderados()[0]/100*40);
                        if(gastarDinero((int)costo)){
                            DFSAlgorithm algorithm = new DFSAlgorithm();
                            algorithm.init(graph);
                            algorithm.compute(orig,dest);
                            costo -= algorithm.obtenerCosto();
                            //todo: encontrar el camino correcto (DFS)
                            Edge aristaADesgastar = orig.getEdgeToward(dest);
                            ArrayList<Edge> aristasADesgastar = new ArrayList<>();
                            aristasADesgastar.add(aristaADesgastar);
                            desgastarAristas(aristasADesgastar);
                            actualizarEtiquetaDeArista(aristaADesgastar);

                            //todo: hacer un mensaje mas especifico
                            System.out.println("Se envio un hit de: " + orig.getId() + " a " + dest.getId() );
                            //todo: thread para volver a levantar la arista
                            //todo: otros mensajes
                        }
                        break;

                    case "Teletransportacion":
                        System.out.println("Enviando un Dijkstra");
                        System.out.println(dijkstra(graph , orig.getId() , dest.getId()));
                        break;

                }


            }catch (ElementNotFoundException e){
                JOptionPane.showMessageDialog(frame,
                        "Revise los nombres de los nodos", //Todo: hacer mas bonito este mensaje
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }

    }

    private boolean origenValido(){
        try{
            Node node = graph.getNode(txtOrigen.getText());

            return node==currentNode||((ArrayList) currentNode.getAttribute("hijos")).contains(node);

        }catch (ElementNotFoundException e){
            JOptionPane.showMessageDialog(frame,
                    "Revise los nombres de los nodos", //Todo: hacer mas bonito este mensaje
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
        boolean res = n >= (int)currentNode.getAttribute("dinero");
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
    private List<String> dijkstra(Graph graph , String origen ,  String destino)
    {
        Dijkstra dijkstra = new Dijkstra(Dijkstra.Element.EDGE, null, "pesoFastWay");

        // Compute the shortest paths in g from A(origen) to all nodes
        dijkstra.init(graph);
        dijkstra.setSource(graph.getNode(origen));
        dijkstra.compute();

        // Print the shortest path from A(origen) to B(destino)
        //System.out.println(dijkstra.getPath(graph.getNode(destino)));

        String lista = dijkstra.getPath(graph.getNode(destino)).toString().replace("[","");
        lista = lista.replace("]" , "");
        System.out.println(lista);
        List<String> listaNodos = new ArrayList<>(Arrays.asList(lista.split(",")));
        //System.out.println(listaNodos);

        return listaNodos;
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
                int[] pesos=pesosPonderados();
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
                    "Revise los nombres de los nodos", //Todo: hacer mas bonito este mensaje
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

    private int[] pesosPonderados() {
        int  p1=0, p2=0;
        for(Edge e : graph.getEdgeSet()){
            if(e.hasAttribute("pesoNormal")){
                p1+=(int)e.getAttribute("pesoNormal");
                p2+=(int)e.getAttribute("pesoFastWay");
            }
        }
        p1/=graph.getEdgeCount();
        p2/=graph.getEdgeCount();
        return new int[]{p1, p2};

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
        }
    }








    public static void main(String[] args) {
        System.setProperty("org.graphstream.ui.renderer", "org.graphstream.ui.j2dviewer.J2DGraphRenderer");
        MainView mainView = new MainView();

    }
}
