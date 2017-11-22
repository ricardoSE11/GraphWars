package Algorithms;

import org.graphstream.algorithm.Algorithm;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class DFSAlgorithm{
    Graph graph;
    ArrayList<ArrayList<Node>> lista;
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

        // If current vertex is same as destination, then print
        // current path[]
        if u ==d:
        print path
        else:
            # If current vertex is not destination
            #Recur for all the vertices adjacent to this vertex
        for i in self.graph[u]:
        if visited[i]==False:
        self.printAllPathsUtil(i, d, visited, path)

        # Remove current vertex from path[] and mark it as unvisited
        path.pop()
        visited[u]= False

    }
        /*Iterator<Node> iterator=s.getDepthFirstIterator(true);
        ArrayList<Node> route=new ArrayList<>();
        while (iterator.hasNext()){
            Node n = iterator.next();
            route.add(n);
            if(n==d)
                break;
        }

        if(route.contains(d))  {
            for (int i=route.size()-1;i>=0;i--){
                for (int j=i; j>0; j--){
                    if(!route.get(j).hasEdgeFrom(route.get(j-1)))
                        route.remove(j-1);
                }
            }
            return route;
        }
        return null;
    }*/
}
