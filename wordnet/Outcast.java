public class Outcast {
    private final WordNet wordnet;
    
    public Outcast(WordNet wordnet) {
        this.wordnet = wordnet;
    }
    
    //Given an array of wordnet nouns, return an outcast
    public String outcast(String[] nouns) {
        int max = 0;
        String maxNoun = "";
        for (String itemA : nouns) {
            int totalDist = 0;
            for (String itemB : nouns) {
                totalDist += wordnet.distance(itemA, itemB);
            }
            if (totalDist > max) {
                max = totalDist;
                maxNoun = itemA;
            }
        }
        return maxNoun;   
    }
    
    public static void main(String[] args) {
        WordNet wordnet = new WordNet(args[0], args[1]);
        Outcast outcast = new Outcast(wordnet);
        for (int t = 2; t < args.length; t++) {
            String[] nouns = In.readStrings(args[t]);
            StdOut.println(args[t] + ": " + outcast.outcast(nouns));
        }
    }
}
    