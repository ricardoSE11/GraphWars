package Algorithms;

import org.graphstream.algorithm.Algorithm;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;

import java.util.Iterator;
import java.util.List;

public class DFSAlgorithm{
    Graph graph;
    List<Node> lista;
    int weight;

    public void init(Graph graph) {
        this.graph=graph;
        weight=-1;
        for (Node n:graph.getNodeSet())
            n.addAttribute("DFSVisited",false);


    }

    public void compute(Node s, Node d) {
        Iterator<Node> iterator=s.getDepthFirstIterator(true);
        while (iterator.hasNext()){
            Node n = iterator.next();
            System.out.println(n.getId());
        }

    }
}
