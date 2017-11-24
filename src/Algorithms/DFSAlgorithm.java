package Algorithms;

import org.graphstream.algorithm.Algorithm;
import org.graphstream.graph.Edge;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class DFSAlgorithm{
    Graph graph;
    public ArrayList<Node> lista=new ArrayList<>();
    public ArrayList<ArrayList<Node>> listas=new ArrayList<>();
    public String attrib=null;
    public boolean usarBloqueadas=false;

    int weight;

    public void init(Graph graph) {
        this.graph=graph;
        weight=-1;
        for (Node n:graph.getNodeSet())
            n.addAttribute("DFSVisited",false);

    }

    public void compute(Node s, Node d) {
        // Mark all the vertices as not visited
        ArrayList<Node> visited =new ArrayList<>();

        //Create an array to store paths
        ArrayList<Node>path = new ArrayList<>();

        //Call the recursive helper function to print all paths
        aux(s, d,visited, path);

    }

    private void aux(Node u, Node d, ArrayList<Node>visited, ArrayList<Node>path){

        // Mark the current node as visited and store in path
        path.add(u);
        visited.add(u);
        // If current vertex is same as destination, then print
        // current path[]
        if (u==d){
            lista=new ArrayList<>(path);
            listas.add(new ArrayList<>(path));
        }
        else {
            // If current vertex is not destination
            //Recur for all the vertices adjacent to this vertex
            for (Edge i : u.getLeavingEdgeSet()){
                if (!visited.contains(i.getTargetNode())
                        &&((!((boolean)i.getAttribute("estaBloqueada"))&&!usarBloqueadas)||usarBloqueadas)){
                    if(attrib!=null){
                        if(((String)i.getAttribute(attrib)).equals("in"))
                            aux(i.getTargetNode(), d, visited, path);
                    }
                    else
                        aux(i.getTargetNode(), d, visited, path);
                }
            }

        }
        // Remove current vertex from path[] and mark it as unvisited
        path.remove(u);
        visited.remove(u);

    }

    /**
     * Devuelve -1 si no hay camino
     * @return
     */
    public int obtenerCosto(){
        int res = -1;
        if(lista.size()>0){
            res=0;
            for(int i =0; i<lista.size()-1; i++)
                res+=(int)lista.get(i).getEdgeToward(lista.get(i+1)).getAttribute("pesoNormal");
        }
        return res;
    }

    public ArrayList<Edge> obtenerEdges(){
        ArrayList<Edge> res=null;
        res = new ArrayList<>();
        for(int i =0; i<lista.size()-1; i++)
            res.add(lista.get(i).getEdgeToward(lista.get(i+1)));

        return res;
    }
}
