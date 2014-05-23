import java.util.HashMap;

public class WordNet {
    private final HashMap<Integer, String> idMap; //id to noun map
    private final HashMap<String, Bag<Integer>> nounMap; //noun to id map (can have multiple defns or ids)
    private final Digraph gHyp;  //directed graph by id
    private final SAP sapG;
    
    public WordNet(String synsets, String hypernms) {
        idMap = new HashMap<Integer, String>();
        nounMap = new HashMap<String, Bag<Integer>>();
        makeMaps(synsets);  //create synset hashmaps
        int size = idMap.size();
        gHyp = new Digraph(size);
        makeHyp(hypernms);
        findRoot(gHyp);
        sapG = new SAP(gHyp);
    }
    
    private void makeMaps(String synsets) {
        In file = new In(synsets);
        String[] line;
        int id;
        Bag<Integer> bag;
        while (!file.isEmpty()) {
            line = file.readLine().split(",");
            id = Integer.parseInt(line[0]);
            idMap.put(id, line[1]);
            for (String noun : line[1].split(" ")) {
                bag = nounMap.get(noun);
                if (bag == null) {
                    bag = new Bag<Integer>();
                    bag.add(id);
                    nounMap.put(noun, bag);
                } else
                    bag.add(id);
            }
        }
    }
    
    private void makeHyp(String hypernms) {
        In file = new In(hypernms);
        String[] ids;
        int id;
        while (!file.isEmpty()) {
            ids = file.readLine().split(",");
            id = Integer.parseInt(ids[0]);
            for (int i = 1; i < ids.length; i++) 
                gHyp.addEdge(id, Integer.parseInt(ids[i]));
        }
    }
    
    private void findRoot(Digraph G) {
        int roots = 0;
        for (int i = 0; i < G.V(); i++) {
            if (!G.adj(i).iterator().hasNext())
                roots += 1;
        }
        if (roots != 1)
            throw new IllegalArgumentException();
    }
    
    //Is the word a wordnet noun?
    public boolean isNoun(String word) {
        return nounMap.containsKey(word);
    }
    
    //distance between nounA and nounB
    public int distance(String nounA, String nounB) {
        checkNouns(nounA, nounB);
        return sapG.length(nounMap.get(nounA), nounMap.get(nounB));
    }
    
    //a synset that is a common ancestor in the shortest ancestral path
    public String sap(String nounA, String nounB) {
        checkNouns(nounA, nounB);
        int node = sapG.ancestor(nounMap.get(nounA), nounMap.get(nounB));
        return idMap.get(node);
    }
    
    private void checkNouns(String a, String b) {
        if (nounMap.get(a) == null || nounMap.get(b) == null)
            throw new java.lang.IllegalArgumentException();
    }
    
    public Iterable<String> nouns() {
        return nounMap.keySet();
    }
        
                      
    public static void main(String[] args) {
        WordNet wn = new WordNet("synsets.txt", "hypernyms.txt");
        System.out.println(wn.distance("Black_Plague", "black_marlin"));
        System.out.println(wn.sap("municipality", "region"));
        System.out.println(wn.nouns());
    }
}