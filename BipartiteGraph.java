/*THIS CODE WAS MY OWN WORK, 
IT WAS WRITTEN WITHOUT CONSULTING ANYSOURCES 
OUTSIDE OF THOSE APPROVED BY THE INSTRUCTOR. 
[Wali Khan, 2308097]*/
import java.util.ArrayList;
import java.util.HashMap;

public class BipartiteGraph {
    // to get the edges incident with a vertex v, just call adjacencyMap.get(v)
    private HashMap<Vertex, ArrayList<Edge>> adjacencyMap;
    private ArrayList<Vertex> leftVertices;
    private ArrayList<Vertex> rightVertices;

    public BipartiteGraph(int nLeft, int nRight, ArrayList<int[]> edges) {
        leftVertices = new ArrayList<>();
        rightVertices = new ArrayList<>();
        adjacencyMap = new HashMap<>();
        for (int i = 0; i < nLeft; i++) {
            Vertex v = new Vertex(i, true);
            leftVertices.add(v);
            adjacencyMap.put(v, new ArrayList<Edge>());
        }
        for (int i = 0; i < nRight; i++) {
            Vertex v = new Vertex(i, false);
            rightVertices.add(v);
            adjacencyMap.put(v, new ArrayList<Edge>());
        }
        for (int[] edge : edges) {
            Vertex u = leftVertices.get(edge[0]);
            Vertex v = rightVertices.get(edge[1]);
            Edge e = new Edge(leftVertices.get(edge[0]), rightVertices.get(edge[1]));
            adjacencyMap.get(u).add(e);
            adjacencyMap.get(v).add(e);
        }
    }

    public ArrayList<Vertex> getVertices() {
        ArrayList<Vertex> verts = new ArrayList<>();
        for (Vertex v : leftVertices)
            verts.add(v);
        for (Vertex v : rightVertices)
            verts.add(v);
        return verts;
    }

    public ArrayList<Edge> getEdges() {
        ArrayList<Edge> edgeList = new ArrayList<>();
        for (Vertex v : leftVertices) {
            for (Edge e : adjacencyMap.get(v)) {
                edgeList.add(e);
            }
        }
        return edgeList;
    }

    public boolean augmentFlow(ArrayList<Edge> augmentingPath) {
        if (augmentingPath == null || augmentingPath.size() == 0) {
            return false;
        }
        for (int i = 0; i < augmentingPath.size(); i++) {
            if ((i % 2 == 0) == augmentingPath.get(i).isInMatching()) {
                // just a sanity check to make sure it alternates blue/red
                return false;
            }
        }
        // as long as the order of the edges is correct, augmenting a flow
        // can be done in this way
        for (int i = 0; i < augmentingPath.size(); i++) {
            augmentingPath.get(i).setInMatching(i % 2 == 0);
        }
        return true;
    }

    // ** This is a helper method to check if a head of a edge is used in matches */
    protected static boolean containsHead(Edge target, ArrayList<Edge> edges) {
        for (Edge walk : edges) {
            if (walk.getHead() == target.getHead()) {
                return true;
            }
        }
        return false;
    }
    // ** This a helper method to check if the tail of the edge has been used in
    // ** mathches */
    protected static boolean containsTail(Edge target, ArrayList<Edge> edges) {
        for (Edge walk : edges) {
            if (walk.getTail() == target.getTail()) {
                return true;
            }
        }
        return false;
    }
    //** In this method we return a list of the edges that are connected to any given edge.
    //** will return a list with edges that share head or tail values  */ 
    protected ArrayList<Edge> path(ArrayList<Edge> path, ArrayList<Edge> edges, int y, Edge x){
        ArrayList<Edge> list = new ArrayList<>();
        list.add(x);
        boolean c = true;
        Vertex head = x.getHead();
        Vertex tail = x.getTail();
        if(y % 2 == 0){
            c = false;
        }
        if(containsTail(x, path) != false){
            return list;
        } else {
            for(Edge e : edges){
                if(c == false){
                    if(head == e.getHead() && tail != e.getTail()){
                        list.add(e);
                        x = e;
                    }
                } else {
                    if(head != e.getHead() && tail == e.getTail()){
                        list.add(e);
                        x = e;
                    }
                }
            }
        }
        return list;
    }
    // ** We go over a list given by the path method searching the edges.
    // ** Searches for an available tour if one is not found initially */
    protected ArrayList<Edge> tour(ArrayList<Edge> path, ArrayList<Edge> edges, Edge x) {
        Edge walk = x;
        Vertex t = walk.getTail(); // tail
        Vertex h = walk.getHead(); // head
        ArrayList<Edge> answer = new ArrayList<>();
        answer.add(walk);
        ArrayList<Edge> list = new ArrayList<>();
        list.add(walk);
        int k = 0;
        while (list.size() != 0) {
            if (k > edges.size()) {
                return null;
            }
            list = path(path, edges, answer.size(), walk);
            t = walk.getTail();
            h = walk.getHead();
            Edge prev = walk;
            if (containsTail(walk, path) == false) {
                answer.add(walk);
                return answer;
            } else if (walk.isInMatching()) {
                boolean exit = false;
                for (Edge e : edges) {
                    if (e.getHead() == h && t != e.getTail()) {
                        if (e.isInMatching() == false) {
                            list.remove(walk);
                            walk = e;
                            list.add(walk);
                            answer.add(walk);
                            exit = true;
                        }
                    }
                }
                if(exit != true){
                    for(Edge e : edges){
                        if(e.getHead() != h && t == e.getTail()){
                            if(e.isInMatching() != true){
                                list.remove(walk);
                                walk = e;
                                list.add(walk);
                                answer.add(walk);
                                exit = true;
                            }
                        }
                    }
                }
                if(exit != true){
                    list.remove(walk);
                    k++;
                }
            } else {
                for(Edge e : edges){
                    if(e.getHead() != h && t == e.getTail()){
                        if(e.isInMatching()){
                            walk.setInMatching(true);
                            list.remove(walk);
                            walk = e;
                            list.add(walk);
                        }
                    }
                }

            }
            if(list.size() == 0 && containsTail(answer.get(answer.size() -1), path) == true){
                if(prev == walk){
                    list = path(path, edges, answer.size(), walk);
                    list.remove(prev);
                }

            }

        }
        if(containsTail(answer.get(answer.size() -1 ), path) == false){
            for(Edge e : answer){
                e.setInMatching(true);
                return answer;
            }
        }
        return null;
    }

    public ArrayList<Edge> findAugmentingPath() {
        int r = 0;
        int l = 0;
        ArrayList<Edge> augPath = new ArrayList<>(); 
        ArrayList<Edge> answer = new ArrayList<>();
        ArrayList<Vertex> v = getVertices();
        ArrayList<Edge> edges = getEdges();
        boolean explore = false;
        for(Edge e : getEdges()){
            if(e.isInMatching()){
                augPath.add(e);
            }
        }
        for(Vertex vertex : v){
            if(vertex.isLeftVertex()){
                l++;
            } else {
                r++;
            }
        }
        if(edges.size() == augPath.size() || r == augPath.size() || l == augPath.size()){
            return null;
        }
        int j = 0;
        while(j < edges.size() && explore == false){
            if(containsHead(edges.get(j), augPath)){
                j++;
            } else if(edges.get(j).isInMatching()){
                j++;
            } else if (containsTail(edges.get(j), augPath) == false && containsHead(edges.get(j), augPath) != true){
                answer.add(edges.get(j));
                edges.get(j).setInMatching(true);
                return answer;
            } else if(containsTail(edges.get(j), augPath) && containsHead(edges.get(j), augPath) != true){
                Edge walk = null;
                for(int i = j; i < edges.size(); i++){
                    if(containsTail(edges.get(i),augPath) == false && edges.get(j).getHead() == edges.get(i).getHead()){
                        walk = edges.get(i);
                    }
                }
                if(walk != null){
                    explore = true;
                    walk.setInMatching(true);
                    answer.add(walk);
                    j++;
                    return answer;
                } else {
                    j++;
                }
            }  
        } 
        for(Edge e : edges){
            if(containsHead(e, augPath) == false){
                answer = tour(augPath, edges, e);
                if(answer == null){
                    return null;
                }
                if(answer != null){
                   int i = 0;
                   while(i < answer.size()){
                       answer.get(i).setInMatching(true);
                       i++;
                   }
                   return answer;
                }
            }
        }
        return answer;
    }
}
