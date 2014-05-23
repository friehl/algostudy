import java.util.HashMap;
//need to run 'isTeam' for inputs


public class BaseballElimination {
    private final int N; // # of teams
    private final HashMap<String, Integer> teams;
    private final HashMap<Integer, String> teamIdxToString;
    private final int[] wins;
    private final int[] losses;
    private final int[] remaining;
    private final int[][] g;
    private FlowNetwork net;
    
    
    public BaseballElimination(String filename) {
        In file = new In(filename);
        N = Integer.parseInt(file.readLine());
        teams = new HashMap<String, Integer>(N);
        teamIdxToString = new HashMap<Integer, String>(N);
        wins = new int[N];
        losses = new int[N];
        remaining = new int[N];
        g = new int[N][N];
        String[] line;
        int i = 0;
        String delims = "[ ]+";
        while (!file.isEmpty()) {
            line = file.readLine().trim().split(delims); 
            teams.put(line[0], i);
            teamIdxToString.put(i, line[0]);
            wins[i] = Integer.parseInt(line[1]);
            losses[i] = Integer.parseInt(line[2]);
            remaining[i] = Integer.parseInt(line[3]);
            for (int j = 0; j < N; j++) {
                g[i][j] = Integer.parseInt(line[4 + j]);
            }
            i++; 
        }
    }
    
    public int numberOfTeams() { return N; }
    
    public Iterable<String> teams() {
        return teams.keySet();
    }
    
    public int wins(String team) { 
        checkTeam(team);
        return wins[teamIndex(team)];
    }
    
    public int losses(String team) {
        checkTeam(team);
        return losses[teamIndex(team)];
    }
    
    public int remaining(String team) {
        checkTeam(team);
        return remaining[teamIndex(team)];
    }
    
    public int against(String team1, String team2) {
        checkTeam(team1, team2);
        int idx1 = teamIndex(team1);
        int idx2 = teamIndex(team2);
        return g[idx1][idx2];
    }
    
    private int teamIndex(String team) {
        checkTeam(team);
        if (teams.containsKey(team))
            return teams.get(team);
        else
            throw new java.lang.IllegalArgumentException();
    }
    
    public boolean isEliminated(String team) {
        checkTeam(team);
        //test trivial elimination
        int teamIdx = teamIndex(team);
        if (checkTrivial(team))
            return true;
        //test non-trivial elimination
        FordFulkerson ff = createFlowNetwork(teamIdx);
        int maxFlowVal = computeMaxFlowVal();
        if (maxFlowVal > ff.value() )
            return true;
        else
            return false;
    }
       
    private boolean checkTrivial(String team) {
        checkTeam(team);
        int teamIdx = teamIndex(team);
        int maxWins = wins[teamIdx] + remaining[teamIdx];
        for (String i : teams() ) {
            if (wins(i) > maxWins) 
                return true;
        }       
        return false;
    }
    private int computeMaxFlowVal() {
        int result = 0;
        for (int i = 0; i < N; i++) {
            for (int j = i + 1; j < N; j++) {
                result += g[i][j];
            }
        }
        return result;
    }
    
    public Iterable<String> certificateOfElimination(String team) {
        checkTeam(team);
        int teamIdx = teamIndex(team);
        FordFulkerson ff = createFlowNetwork(teamIdx);
        Queue<String> qq = new Queue<String>();
        String firstPlace = "";
        int mostWins = -1;
        if (checkTrivial(team)) {
            for (String i : teams()) {
                if (wins(i) >= mostWins) {
                    mostWins = wins(i);
                    firstPlace = i;
                }
            }
            qq.enqueue(firstPlace);
        }
        
        String teamToAdd;
        for (int i = 0; i < N; i++) {
            if (ff.inCut(i) ) {
                teamToAdd = teamIdxToString.get(i);
                qq.enqueue(teamToAdd);
            }
        }
        return qq;
    }
        
    private FordFulkerson createFlowNetwork(int idx) {
        int netSize = networkSize();
        net = new FlowNetwork(netSize);
        int source = netSize - 2;
        int sink = netSize - 1;
        int gameIndex = N;
        int edgeCap;
        for (int i = 0; i < N; i++) {
            for (int j = i + 1; j < N; j++) {
                FlowEdge e = new FlowEdge(source, gameIndex, g[i][j]);
                FlowEdge f = new FlowEdge(gameIndex, i, Double.POSITIVE_INFINITY);
                FlowEdge g = new FlowEdge (gameIndex, j, Double.POSITIVE_INFINITY);
                gameIndex++;
                net.addEdge(e);
                net.addEdge(f);
                net.addEdge(g);                           
            }
            if (wins[idx] + remaining[idx] - wins[i] < 0)
                edgeCap = 0;
            else
                edgeCap = wins[idx] + remaining[idx] - wins[i];
            FlowEdge e = new FlowEdge(i, sink, edgeCap);
            net.addEdge(e);
        }
        FordFulkerson ff = new FordFulkerson(net, source, sink);
        return ff;
    }

    private int networkSize() {
        return N * (N-1) / 2 + N + 2;
    }
   
    private int teamNetIndex(int n, int netSize) {
        return netSize - 2 - N + n;
    }
    
    private void checkTeam(String i) {
        if (!teams.containsKey(i) )
            throw new java.lang.IllegalArgumentException();
    }
    
    private void checkTeam(String i, String j) {
        if (!teams.containsKey(i) || !teams.containsKey(j) )
            throw new java.lang.IllegalArgumentException();
    }
                                          
    public static void main(String[] args) {
    BaseballElimination division = new BaseballElimination(args[0]);
    StdOut.print(division.teams());
    for (String team : division.teams()) {
        if (division.isEliminated(team)) {
            StdOut.print(team + " is eliminated by the subset R = { ");
            for (String t : division.certificateOfElimination(team))
                StdOut.print(t + " ");
            StdOut.println("}");
        }
        else {
            StdOut.println(team + " is not eliminated");
        }
    }
    }
}