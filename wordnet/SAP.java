public class SAP {
    private final Digraph G;
    private final DepthFirstOrder gorder;
    
    //constructor
    public SAP(Digraph G) {
        this.G = new Digraph(G);
        this.gorder = new DepthFirstOrder(G);
    }
    
    //shortest ancestral path between v and w
    public int length(int v, int w) {
        checkVals(v, w);
        int min = Integer.MAX_VALUE;
        int dist = -1;
        boolean updated = false;
            
        BreadthFirstDirectedPaths vpath = new BreadthFirstDirectedPaths(G, v);
        BreadthFirstDirectedPaths wpath = new BreadthFirstDirectedPaths(G, w);
        
        for (int node : gorder.pre()) {
            if (vpath.hasPathTo(node) && wpath.hasPathTo(node)) {
                dist = vpath.distTo(node) + wpath.distTo(node);
                if (dist < min) {
                    min = dist;
                    updated = true;
                }
            }
        }
        if (!updated) return -1;
        return min;
    }
    
    public int length(Iterable<Integer> v, Iterable<Integer> w) {
        checkVals(v, w);
        int min = Integer.MAX_VALUE;
        int dist = -1;
        boolean updated = false;
            
        BreadthFirstDirectedPaths vpath = new BreadthFirstDirectedPaths(G, v);
        BreadthFirstDirectedPaths wpath = new BreadthFirstDirectedPaths(G, w);
        
        for (int node : gorder.pre()) {
            if (vpath.hasPathTo(node) && wpath.hasPathTo(node)) {
                dist = vpath.distTo(node) + wpath.distTo(node);
                if (dist < min) {
                    min = dist;
                    updated = true;
                }
            }    
        }
        if (!updated) return -1;
        return min;
    }
             
    //common ancestor that participates in shortest ancestral path
    public int ancestor(int v, int w) {
        checkVals(v, w);
        int min = Integer.MAX_VALUE;
        int dist = -1;
        int minNode = -1;
        
        BreadthFirstDirectedPaths vpath = new BreadthFirstDirectedPaths(G, v);
        BreadthFirstDirectedPaths wpath = new BreadthFirstDirectedPaths(G, w);
        for (int node : gorder.pre()) {
            if (vpath.hasPathTo(node) && wpath.hasPathTo(node)) {
                dist = vpath.distTo(node) + wpath.distTo(node);
                if (dist < min) {
                    min = dist;
                    minNode = node;
                }
            }
        }
        return minNode;
    }
    
    // length of shortest ancestral path between any vertex in v and any vertex in w; -1 if no such path
    public int ancestor(Iterable<Integer> v, Iterable<Integer> w) {
        checkVals(v, w);
        int min = Integer.MAX_VALUE;
        int dist = -1;
        int minNode = -1;
        
        BreadthFirstDirectedPaths vpath = new BreadthFirstDirectedPaths(G, v);
        BreadthFirstDirectedPaths wpath = new BreadthFirstDirectedPaths(G, w);
        for (int node : gorder.pre()) {
            if (vpath.hasPathTo(node) && wpath.hasPathTo(node)) {
                dist = vpath.distTo(node) + wpath.distTo(node);
                if (dist < min) {
                    min = dist;
                    minNode = node;
                }
            }
        }
        return minNode;
    }
    
    private void checkVals(int v, int w) {
        if (v < 0 || v > G.V() || w < 0 || w > G.V())
            throw new IndexOutOfBoundsException();
    }
    
    private void checkVals(Iterable<Integer> v, Iterable<Integer> w) {
        for (int x : v) {
            if (x < 0 || x > G.V())
                throw new IndexOutOfBoundsException();
        }
        for (int y : w) {
            if (y < 0 || y > G.V())
                throw new IndexOutOfBoundsException();
        }
    }
    
    public static void main(String[] args) {
        In in = new In(args[0]);
        Digraph G = new Digraph(in);
        SAP sap = new SAP(G);
        while (!StdIn.isEmpty()) {
            int v = StdIn.readInt();
            int w = StdIn.readInt();
            int length = sap.length(v, w);
            int ancestor = sap.ancestor(v, w);
            StdOut.printf("length = %d, ancestor = %d\n", length, ancestor);
        }
    }
}
    
        
    