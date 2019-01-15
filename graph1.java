// A Java program to implement greedy algorithm for graph coloring 
import java.io.*; 
import java.util.*; 
//import java.util.LinkedList; 
  
// This class represents an undirected graph using adjacency list 
class graph1 { 
	
	private static int nodeNum;   // No. of vertices 
    private static int edgeNum;   //no of edges
    private LinkedList<Integer> adj[]; //Adjacency List 
    private static int[][] graph = new int[edgeNum][2];
  
    //Constructor 
    public graph1(int v) { 
        nodeNum = v; 
        adj = new LinkedList[v]; 
        for (int i=0; i<v; i++) { 
        	adj[i] = new LinkedList();}
    } 
    
    
  
    //Function to add an edge into the graph 
    void addEdge(int v,int w) 
    { 
        adj[v-1].add(w); 
        adj[w-1].add(v); //Graph is undirected 
    } 
  
    // Assigns colors (starting from 0) to all vertices and 
    // prints the assignment of colors 
    void greedyColoring() 
    { 
        int result[] = new int[nodeNum]; 
  
        // Initialize all vertices as unassigned 
        Arrays.fill(result, -1); 
  
        // Assign the first color to first vertex 
        result[0]  = 0; 
  
        // A temporary array to store the available colors. False 
        // value of available[cr] would mean that the color cr is 
        // assigned to one of its adjacent vertices 
        boolean available[] = new boolean[nodeNum]; 
          
        // Initially, all colors are available 
        Arrays.fill(available, true); 
  
        // Assign colors to remaining V-1 vertices 
        for (int u = 1; u < nodeNum+1; u++) 
        { 
            // Process all adjacent vertices and flag their colors 
            // as unavailable 
            Iterator<Integer> it = adj[u-1].iterator() ; 
            while (it.hasNext()) 
            { 
                int i = it.next(); 
                if (result[i-1] != -1) 
                    available[result[i-1]] = false; 
            } 
  
            // Find the first available color 
            int cr; 
            for (cr = 0; cr < nodeNum; cr++){ 
                if (available[cr]) 
                    break; 
            } 
  
            result[u-1] = cr; // Assign the found color 
  
            // Reset the values back to true for the next iteration 
            Arrays.fill(available, true); 
        } 
  
        // print the result 
        for (int u = 1; u < nodeNum+1; u++) 
            System.out.println("Vertex " + u + " --->  Color "
                                + result[u-1]); 
    } 
  
    private static void readGraph() {
    	try {		
    		String inputFile = "GeneratedGraph.txt";
    		//File inputFile = new File(inputFileName);
   	 		FileReader fr = new FileReader(inputFile);
   	 		Scanner sc = new Scanner(fr);
    		//BufferedReader br = new BufferedReader(fr);
	  
    		String str1 = sc.nextLine();
    		System.out.println(str1);
    		String[] part1 = str1.split("= ");
    		nodeNum = Integer.parseInt(part1[1]);
    		System.out.println(nodeNum);
    		String str2 = sc.nextLine();
    		String[] part2 = str2.split("= ");
    		edgeNum = Integer.parseInt(part2[1]);
    		System.out.println(edgeNum);

    		int[][] graph = new int[edgeNum][2];
    		int i = 0;
    		//String line;
    		//while ((line = br.readLine()) != null) {
    		while (sc.hasNextLine()) {
    			String edge = sc.nextLine();
    			System.out.println(edge);
    			String[] nodes = edge.split(" ");
    			graph[i][0] = Integer.parseInt(nodes[0]);
    			graph[i][1] = Integer.parseInt(nodes[1]);
    			i++;
    		}
    	}
    	catch (Exception e) {
    		System.out.println("fve");

    	}
	}     

    public static void main(String args[]) {  
    	
    	Integer x1 = Integer.parseInt(args[0]);
    	graph1 g1 = new graph1(x1);
        g1.readGraph();
        for (int i = 0; i < g1.edgeNum; i++) {
        	g1.addEdge(g1.graph[i][0], g1.graph[i][1]);
        }
        //g1.greedyColoring(); 
    } 
} 
 