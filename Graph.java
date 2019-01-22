import java.io.*;
import java.util.*;
import java.lang.*;


/**

 MANUAL GUIDELINES ON USING THIS FILE:

 1.VARIABLE:

 - n: number of nodes
 - n2: number of nodes that are connected to at least 1 other node
 - m: number of edges
 - graphList: a 2-D ArrayList of edges. It looks like this: [[1,3],[2,5]...]
 - count: a 2-D integer array of nodes and corresponding degrees in degree-ascending order. It looks like this: {{2,1},{5,1},{3,3},{4,4},{1,4}}.
 - countList: an ArrayList of (only) nodes in degree-descending order. It looks like this for the same example as above: [1,4,3,5,2]
 - adjacencyMatrix: a 2-D n x n array representing the whole graph. The entry in i th row and j th column is 1 if nodes i,j are connected, otherwise it is 0. By convention, all the entries of the diagonal are 0, which means a node is not connected to itself.

 2. METHODS:

 - readGraph(): call it in the main method to create and fill up all the variables above.
 - upperboundWP(): the heuristic algorithm for coloring we already have from phase 2. I add "WP" to differentiate it from other algorithms (it is based on Welsh - Powell algorithm). You should follow this naming convention. For example: chromaticnumABC(): the exact algorithm named ABC.
 - chromaticnumDFS(): the exact algorithm we already have from phase 2. It based on Depth First Search with pruning or Backtracking (they are the same).
 - Other methods are unecessary. It's nice to take a look but please dont'try understanding the code.

 3.RUNNING FILE ON CMD

 When you run the file after compilation, just write the number of the graph instead of its fucking long name: block3.... For example: java gameAlgorithm 05

 * @author  Manh, Paula, Mathias, Stan, Simon
 * @version 1.2
 * @since   2019-01-21
 */

class Edge
{
    int u;
    int v;

    Edge(int u, int v)
    {
        this.u = u;
        this.v = v;
    }

    Edge() {
        this.u = 0;
        this.v = 0;
    }
}

public class Graph
{
    public int n = -1; // number of nodes
    public int n2; // number of nodes which are connected to at least 1 node other than themselve
    public int m = 0; // number of edges
    private final static boolean DEBUG = true;
    private final static String COMMENT = "//";
    public ArrayList<ArrayList<Integer>> graphList = new ArrayList<ArrayList<Integer>>(); // stores all the edges of the graph here
    public int[][] count ; // stores the nodes and corresponding degrees in an degree-ascending order
    private static String graphName;
    public int[][] adjacencyMatrix;
    private  int[][] colorMatrixRLF; // for RLF
    private int[][] colorMatrixWP; // for WP
    private  ArrayList<Integer> countList = new ArrayList<Integer>();
    private ArrayList<Integer> countListWP = new ArrayList<Integer>();
    private  int[][] countUnsorted; // stores the nodes and corresponding degrees in an node-ascending order
    private ArrayList<Integer> cliqueGraph; // biggest clique subgraph of the input graph. Need to call lowerBound() first to fill it up
    private int upperBound;
    private int lowerBound;
    private ArrayList<ArrayList<Integer>> connectedComponents = new ArrayList<ArrayList<Integer>>();
    private ArrayList<Integer>[] adjListArray;
    private ArrayList<ArrayList<Integer>> biconnectedComponents = new ArrayList<ArrayList<Integer>>();
    private int counter = 0, time = 0;
    private LinkedList<Integer> adj[]; // Adjacency List


    public Graph() {
    }

    public Graph(Graph g, ArrayList<Integer> component) {
        Collections.sort(component);
        this.n = component.size();
        this.adjacencyMatrix = new int[this.n][this.n];
        this.count = new int[this.n][2];
        for (int i=0; i<this.n;i++) {
            this.count[i][0] = i+1;
        }

        adj = new LinkedList[n];
        for (int i = 0; i < n; ++i)
            adj[i] = new LinkedList();
        for (int i = 0; i<this.n-1;i++) {
            for (int j = i+1;j<this.n;j++) {
                if (g.adjacencyMatrix[component.get(i)-1][component.get(j)-1]==1) {
                    ArrayList<Integer> elm = new ArrayList<Integer>();
                    this.m++;
                    elm.add(i+1);
                    elm.add(j+1);
                    this.graphList.add(elm);
                    this.adjacencyMatrix[i][j]=1;
                    this.adjacencyMatrix[j][i]=1;
                    count[i][1]++;
                    count[j][1]++;
                    this.addEdge(i+1,j+1);
                    this.addEdge(j+1,i+1);
                }
            }
        }

        countUnsorted = new int[n][2];
        System.arraycopy(count,0,countUnsorted,0,count.length);
        Arrays.sort(count, (a, b) -> Integer.compare(a[1], b[1]));

        for (int i = 0; i<n; i++) {
            if (count[i][1]!=0)
                n2++;

        }

        colorMatrixRLF = new int[n][2];
        for (int i = 0; i<n;i++) {
            colorMatrixRLF[i][0] = i+1;
        }

        colorMatrixWP = new int[n][2];
        for (int i = 0; i<n;i++) {
            colorMatrixWP[i][0] = i+1;
        }


        int[][] countDescending = new int[n][2] ;

        for (int i = 0; i<= n-1; i++) {
            countDescending[i] = count[n-1-i];
        }
        for (int i = 0; i<= n-1; i++) {
            countList.add(countDescending[i][0]);
        }

        countListWP.addAll(countList);
        upperBound = Math.min(upperboundWP(),upperboundRLF(0,countList));
        lowerBound = lowerBound();

    }

    void addEdge(int v, int w)
    {
        adj[v-1].add(w);
    }



    public static void main(String[] args) throws IOException {

        Graph.graphName = args[0];
        Graph big = new Graph();
        big.readGraph(graphName);
        big.chromaticNum(big);


    }



    private void printAdj(Graph g) {
        for (int j =0; j< g.adjacencyMatrix.length;j++) {
            System.out.println(Arrays.toString(g.adjacencyMatrix[j]));
        }

    }

    private int chromaticNumberBC(Graph big) throws IOException {

        int chromaticNumber  = 0;
        FileWriter  writer      = new FileWriter("BiconnectedComponents.txt");
        PrintWriter printWriter = new PrintWriter(writer);

        BiconnectedComponents g = new BiconnectedComponents(big.n);
        g.readGraph(big.graphName);
        for (int i = 0; i< graphList.size();i++) {
            g.addEdge(big.graphList.get(i).get(0)-1,big.graphList.get(i).get(1)-1);
            g.addEdge(big.graphList.get(i).get(1)-1,big.graphList.get(i).get(0)-1);
        }

        g.loadBiconnected(printWriter);

        for (int i =0; i< g.components.size();i++) {
            Set<Integer> component = g.components.get(i);
            ArrayList<Integer> componentArr = new ArrayList<Integer>();
            componentArr.addAll(component);

            Graph componentGraph = new Graph(big,componentArr);
            int k = componentGraph.chromaticnumDFS();
            if (chromaticNumber<k)
                chromaticNumber=k;

        }
        return chromaticNumber;

    }

    // CHECKER: PASS THE SET OF COLORED NODES BY THE USER AS INPUT THEN RETURN IF IT VIOLATES THE BIG RULE: NO CONNECTED NODES SHARE THE SAME COLOR

    public boolean checker(int[][] colored) {

        boolean checker = false;

        for (int i = 0; i<graphList.size();i++) {
            int node1 = graphList.get(i).get(0);
            int node2 = graphList.get(i).get(1);
            if (colored[node1-1][1] != 0 && colored[node2-1][1] != 0 && colored[node1-1][1] == colored[node2-1][1]) {
                checker = true;
                System.out.println(node1 + " and " + node2);
                break;
            }

        }

        return checker;
    }



    private boolean isBipartite(Graph big) {
        big.connectedComponents();
        for (int i = 0; i< big.connectedComponents.size();i++) {
            Graph component = new Graph(big, big.connectedComponents.get(i));
            if (component.containsOdd(component.adjacencyMatrix,0))
                return false;
        }

        return true;
    }

    private void readGraph(String graphName) {


        String inputfile = graphName;

        boolean seen[] = null;

        //! n is the number of vertices in the graph


        //! m is the number of edges in the graph


        //! e will contain the edges of the graph
        Edge e[] = null;

        try 	{
            FileReader fr = new FileReader(inputfile);
            BufferedReader br = new BufferedReader(fr);

            String record = new String();

            //! THe first few lines of the file are allowed to be comments, staring with a // symbol.
            //! These comments are only allowed at the top of the file.

            //! -----------------------------------------
            while ((record = br.readLine()) != null)
            {
                if( record.startsWith("//") ) continue;
                break; // Saw a line that did not start with a comment -- time to start reading the data in!
            }

            if( record.startsWith("VERTICES = ") )
            {
                n = Integer.parseInt( record.substring(11) );
                //if(DEBUG) System.out.println(COMMENT + " Number of vertices = "+n);
            }

            seen = new boolean[n+1];

            record = br.readLine();

            if( record.startsWith("EDGES = ") )
            {
                m = Integer.parseInt( record.substring(8) );
                //if(DEBUG) System.out.println(COMMENT + " Expected number of edges = "+m);
            }

            e = new Edge[m];

            for( int d=0; d<m; d++)
            {
                //if(DEBUG) System.out.println(COMMENT + " Reading edge "+(d+1));
                record = br.readLine();
                //record = record.substring(2);
                String data[] = record.split(" ");
                if( data.length != 2 )
                {
                    System.out.println("Error! Malformed edge line: "+record);
                    System.exit(0);
                }
                e[d] = new Edge();

                e[d].u = Integer.parseInt(data[0]);
                e[d].v = Integer.parseInt(data[1]);
                if (e[d].u > e[d].v) {
                    int a = e[d].u;
                    e[d].u = e[d].v;
                    e[d].v = a;
                }

                seen[ e[d].u ] = true;
                seen[ e[d].v ] = true;

                //if(DEBUG) System.out.println(COMMENT + " Edge: "+ e[d].u +" "+e[d].v);

            }

            String surplus = br.readLine();
        }
        catch (IOException ex)
        {
            // catch possible io errors from readLine()
            System.out.println("Error! Problem reading file "+inputfile);
            System.exit(0);
        }

        for( int x=1; x<=n; x++ )
        {
            if( seen[x] == false )
            {
                //if(DEBUG) System.out.println(COMMENT + " Warning: vertex "+x+" didn't appear in any edge : it will be considered a disconnected vertex on its own.");
            }
        }

        // Store all the edges in an 2-D int array named Graph
        int[][] GraphArray = new int[m][2];
        for (int i =0; i< m; i++) {
            GraphArray[i][0] = e[i].u;
            GraphArray[i][1] = e[i].v;
        }


        adjacencyMatrix = new int[n][n];


        adjListArray = new ArrayList[n];

        adj = new LinkedList[n];
        for (int i = 0; i < n; ++i)
            adj[i] = new LinkedList();

        // Create a new list for each vertex
        // such that adjacent nodes can be stored

        for(int i = 0; i < n ; i++){
            adjListArray[i] = new ArrayList<Integer>();
        }


        for (int i = 0; i<m; i++) {
            if (GraphArray[i][0] !=0 && GraphArray[i][1]!=0) {
                ArrayList<Integer> elm = new ArrayList<Integer>();
                elm.add(GraphArray[i][0]);
                elm.add(GraphArray[i][1]);
                graphList.add(elm);
                adjacencyMatrix[GraphArray[i][0]-1][GraphArray[i][1]-1]=1;
                adjacencyMatrix[GraphArray[i][1]-1][GraphArray[i][0]-1]=1;
                adjListArray[GraphArray[i][0]-1].add(GraphArray[i][1]);
                adjListArray[GraphArray[i][1]-1].add(GraphArray[i][0]);
            }
        }


        //! At this point e[0] will be the first edge, with e[0].u referring to one endpoint and e[0].v to the other
        //! e[1] will be the second edge...
        //! (and so on)
        //! e[m-1] will be the last edge
        //!
        //! there will be n vertices in the graph, numbered 1 to n

        count = new int[n][2];
        for (int i=0; i<this.n;i++) {
            this.count[i][0] = i+1;
        }
        for (int i = 0; i< m; i++) {
            this.count[GraphArray[i][0]-1][1] ++;
            this.count[GraphArray[i][1]-1][1] ++;

        }
        countUnsorted = new int[n][2];
        System.arraycopy(count,0,countUnsorted,0,count.length);
        Arrays.sort(count, (a, b) -> Integer.compare(a[1], b[1]));

        for (int i = 0; i<n; i++) {
            if (count[i][1]!=0)
                n2++;

        }

        colorMatrixRLF = new int[n][2];
        for (int i = 0; i<n;i++) {
            colorMatrixRLF[i][0] = i+1;
        }

        colorMatrixWP = new int[n][2];
        for (int i = 0; i<n;i++) {
            colorMatrixWP[i][0] = i+1;
        }

        int[][] countDescending = new int[n][2] ;

        for (int i = 0; i<= n-1; i++) {
            countDescending[i] = count[n-1-i];
        }
        for (int i = 0; i<= n-1; i++) {
            countList.add(countDescending[i][0]);
        }

        countListWP.addAll(countList);


    }

    // This method is to check if a graph contains odd cycle

    public boolean containsOdd(int G[][], int src)
    {
        // Create a color array to store colors assigned
        // to all vertices. Vertex number is used as
        // index in this array. The value '-1' of
        // colorArr[i] is used to indicate that no color
        // is assigned to vertex 'i'.  The value 1 is
        // used to indicate first color is assigned and
        // value 0 indicates second color is assigned.
        int colorArr[] = new int[n];
        for (int i = 0; i < n; ++i)
            colorArr[i] = -1;

        // Assign first color to source
        colorArr[src] = 1;

        // Create a queue (FIFO) of vertex numbers and
        // enqueue source vertex for BFS traversal
        LinkedList<Integer> q = new LinkedList<Integer>();
        q.add(src);

        // Run while there are vertices in queue
        // (Similar to BFS)
        while (q.size() != 0)
        {
            // Dequeue a vertex from queue
            int u = q.poll();

            // Return true if there is a self-loop
            if (G[u][u] == 1)
                return true;

            // Find all non-colored adjacent vertices
            for (int v = 0; v < n; ++v)
            {
                // An edge from u to v exists and
                // destination v is not colored
                if (G[u][v] == 1 && colorArr[v] == -1)
                {
                    // Assign alternate color to this
                    // adjacent v of u
                    colorArr[v] = 1 - colorArr[u];
                    q.add(v);
                }

                // An edge from u to v exists and
                // destination v is colored with same
                // color as u
                else if (G[u][v] == 1 && colorArr[v] ==
                        colorArr[u])
                    return true;
            }
        }

        //System.out.println(Arrays.toString(colorArr));

        // If we reach here, then all adjacent
        // vertices can be colored with alternate
        // color
        return false;
    }



    // Welsh - Powell algorithm to calculate upper bound:


    private int upperboundWP()

    {
        // Store the nodes and corresponding degrees in degree-descending order

        ArrayList<Integer> countListLocal = countListWP;
        int upperBoundLocal = 0;

        while (countListLocal.size() != 0) {
            upperBoundLocal ++;
            int highestNode = countListLocal.get(0);
            colorMatrixWP[highestNode-1][1] = upperBoundLocal;
            countListLocal.remove(0);
            ArrayList<Integer> coloredNodes = new ArrayList<Integer>();
            coloredNodes.add(highestNode);
            ArrayList<Integer> nonadjacent = new ArrayList<Integer>();
            for (int j = 0; j<= countListLocal.size()-1; j++) {
                if (adjacencyMatrix[highestNode-1][countListLocal.get(j)-1]==0) {
                    nonadjacent.add(countListLocal.get(j));
                }
            }

            while(nonadjacent.size()!=0) {
                int selectedNode = nonadjacent.get(0);
                nonadjacent.remove(0);
                colorMatrixWP[selectedNode-1][1]=upperBoundLocal;
                coloredNodes.add(selectedNode);
                ArrayList<Integer> nonadjacentTemp = new ArrayList<Integer>();
                for (int i = 0; i<nonadjacent.size();i++ ) {
                    if (adjacencyMatrix[selectedNode-1][nonadjacent.get(i)-1]==0)
                        nonadjacentTemp.add(nonadjacent.get(i));
                }
                nonadjacent = nonadjacentTemp;
            }

            ArrayList<Integer> countListTemp = new ArrayList<Integer>();
            for (int i = 0; i<= countListLocal.size()-1;i++) {
                if (!coloredNodes.contains(countListLocal.get(i))) {
                    countListTemp.add(countListLocal.get(i));
                }
            }

            countListLocal = countListTemp;
            //System.out.println(countListLocal);
        }

        return upperBoundLocal;
    }

    // Recursive Largest Algorithm method to compute the upper bound

    private int upperboundRLF(int k, ArrayList<Integer> countListParameter) {

        if (countListParameter.size() == 0) {
            return k;
        }

        else {
            ArrayList<Integer> countListLocal = countListParameter;
            int selectedNode = countListLocal.get(0);
            countListLocal.remove(countListLocal.indexOf(selectedNode));
            ArrayList<Integer> adjacent = new ArrayList<Integer>();
            ArrayList<Integer> nonadjacent = new ArrayList<Integer>();
            for (int i = 0; i < countListLocal.size(); i++) {
                if (adjacencyMatrix[selectedNode - 1][countListLocal.get(i)-1] == 1)
                    adjacent.add(countListLocal.get(i));
                else
                    nonadjacent.add(countListLocal.get(i));
            }
            int adjacentCount = adjacent.size();
            int nonadjacentCount = nonadjacent.size();
            colorMatrixRLF[selectedNode - 1][1] = k;
            //System.out.println("Color " + selectedNode + "with " + k);

            int[][] nonadjacentDegree = new int[nonadjacentCount][2];
            for (int i = 0; i < nonadjacentCount; i++) {
                nonadjacentDegree[i][0] = nonadjacent.get(i);
            }
            for (int i = 0; i < nonadjacentCount; i++) {
                for (int j = 0; j < adjacentCount; j++) {
                    if (adjacencyMatrix[nonadjacent.get(i) - 1][adjacent.get(j) - 1] == 1)
                        nonadjacentDegree[i][1]++;

                }
            }

            Arrays.sort(nonadjacentDegree, (a, b) -> Integer.compare(a[1], b[1]));

            ArrayList<Integer> nonadjacentDegreeDescending = new ArrayList<Integer>();
            for (int i = nonadjacentCount - 1; i >= 0; i--) {
                nonadjacentDegreeDescending.add(nonadjacentDegree[i][0]);
            }
            //System.out.println("non:" + nonadjacentDegreeDescending);
            //System.out.println("adj:" + adjacent);


            while (nonadjacentDegreeDescending.size()!= 0) {
                int nextNode = nonadjacentDegreeDescending.get(0);
                colorMatrixRLF[nextNode - 1][1] = k;
                //System.out.println("Color " + nextNode + "with " + k);
                nonadjacentDegreeDescending.remove(nonadjacentDegreeDescending.indexOf(nextNode));
                ArrayList<Integer> nonadjacentTemp = new ArrayList<Integer>();
                for (int i = 0; i < nonadjacentDegreeDescending.size(); i++) {
                    if (adjacencyMatrix[nextNode - 1][nonadjacentDegreeDescending.get(i) - 1] == 1) {
                        adjacent.add(nonadjacentDegreeDescending.get(i));
                        //nonadjacentDegreeDescending.set(nonadjacentDegreeDescending.indexOf(nonadjacentDegreeDescending.get(i)),null);
                    }
                    else {
                        nonadjacentTemp.add(nonadjacentDegreeDescending.get(i));
                    }

                }

                nonadjacentDegreeDescending = nonadjacentTemp;


                int[][] nonadjacentDegreeLoop = new int[nonadjacentDegreeDescending.size()][2];
                for (int i = 0; i < nonadjacentDegreeDescending.size(); i++) {
                    nonadjacentDegreeLoop[i][0] = nonadjacentDegreeDescending.get(i);
                    for (int j = 0; j < adjacent.size(); j++) {
                        if (adjacencyMatrix[nonadjacentDegreeDescending.get(i) - 1][adjacent.get(j) - 1] == 1)
                            nonadjacentDegreeLoop[i][1]++;
                    }
                }
                Arrays.sort(nonadjacentDegreeLoop, (a, b) -> Integer.compare(a[1], b[1]));
                ArrayList<Integer> nonadjacentDegreeDescendingLoop = new ArrayList<Integer>();
                for (int i = 0; i < nonadjacentDegreeDescending.size(); i++) {
                    nonadjacentDegreeDescendingLoop.add(nonadjacentDegreeLoop[i][0]);
                }
                nonadjacentDegreeDescending = nonadjacentDegreeDescendingLoop;


            }

            ArrayList<Integer> newNodeList = new ArrayList<Integer>();
            for (int i =0;i<n;i++) {
                if(colorMatrixRLF[i][1]==0)
                    newNodeList.add(i+1);
            }
            if (newNodeList.size()==0)
                return k;
            else {
                ArrayList<Integer> newCountList = buildCountList(newNodeList);
                //System.out.println(newCountList);
                return upperboundRLF(k + 1, newCountList);
            }


        }


    }

    private  ArrayList<Integer> buildCountList(ArrayList<Integer> nodeList) {
        ArrayList<Integer> countList = new ArrayList<Integer>();
        int[][] countAscending = new int[nodeList.size()][2];
        for (int i =0; i<nodeList.size();i++) {
            countAscending[i][0]= nodeList.get(i);
        }
        for(int i = 0; i<nodeList.size()-1;i++) {
            for (int j = i+1; j<nodeList.size();j++) {
                if(adjacencyMatrix[nodeList.get(i)-1][nodeList.get(j)-1]==1) {
                    countAscending[i][1]++;
                    countAscending[j][1]++;
                }
            }
        }
        Arrays.sort(countAscending, (a, b) -> Integer.compare(a[1], b[1]));
        for (int i = countAscending.length-1;i>=1;i--) {
            for (int j = i-1;j>=0;j--) {
                if (countAscending[i][1] == countAscending[j][1]) {
                    if (countUnsorted[countAscending[i][0]-1][1] < countUnsorted[countAscending[j][0]-1][1]) {
                        int temp = countAscending[i][0];
                        countAscending[i][0] = countAscending[j][0];
                        countAscending[j][0]= temp;
                    }

                }
            }
        }
        for (int i = countAscending.length-1;i>=0;i--) {
            countList.add(countAscending[i][0]);
        }
        return countList;

    }


    // Produce the lower bound by searching for the clique number of a given graph

    private int lowerBound()

    {
        double startTime = System.nanoTime()/1000000000;
        //System.out.println(startTime);
        int lowerBound = 2;
        boolean clique = true;
        double currentTime = System.nanoTime()/1000000000;
        //System.out.println(currentTime);
        while (clique == true && lowerBound <= upperBound && currentTime-startTime<105)
        {
            currentTime = System.nanoTime()/1000000000;

                //System.out.println(currentTime);
                if (clique(n, lowerBound).size() > 0) {
                    cliqueGraph = clique(n, lowerBound).get(0);
                    lowerBound++;
                } else {
                    clique = false;
                }


        }
        if(currentTime-startTime>105){
            System.out.println("The lower bound algorithm was timed out.");
            System.out.println("NEW BEST LOWER BOUND = " + (lowerBound-1));
            //return lowerBound-1;
        }
        return lowerBound-1;

    }




    // Methods "combine" and "dfs2" are supposed to produce all the k-length combinations of the set of integers from 1 to n by DFS

    private ArrayList<ArrayList<Integer>> combine(int n, int k)
    {
        ArrayList<ArrayList<Integer>> result = new ArrayList<ArrayList<Integer>>();

        if (n <= 0 || n < k)
            return result;

        ArrayList<Integer> item = new ArrayList<Integer>();
        dfs2(n, k, 1, item, result); // because it needs to begin from 1

        return result;
    }



    private void dfs2(int n, int k, int start, ArrayList<Integer> item,
                      ArrayList<ArrayList<Integer>> res) {
        if (item.size() == k) {
            res.add(new ArrayList<Integer>(item));
            return;
        }

        for (int i = start; i <= n; i++) {
            item.add(i);
            dfs2(n, k, i + 1, item, res);
            item.remove(item.size() - 1);
        }
    }

    // Methods "clique" and "dfs3" are supposed to search for a clique subgraph of size k in a given graph of size n by DFS

    private ArrayList<ArrayList<Integer>> clique(int n, int k)
    {
        ArrayList<ArrayList<Integer>> result = new ArrayList<ArrayList<Integer>>();

        if (n <= 0 || n < k)
            return result;

        ArrayList<Integer> item = new ArrayList<Integer>();
        dfs3(n, k, 1, item, result); // because it need to begin from 1

        return result;

    }


    private void dfs3(int n, int k, int start, ArrayList<Integer> item, ArrayList<ArrayList<Integer>> res)
    {

        if (item.size() == k) {
            res.add(new ArrayList<Integer>(item));
            return;
        }

        for (int i = start; i <= n; i++) {

            if (res.size()>0)
                break;

            item.add(i);
            boolean clique = true;

            if(item.size()>=2) {
                ArrayList<ArrayList<Integer>> combination = combine(item.size(),2);
                for (int j = 0; j< combination.size();j++)

                {
                    ArrayList<Integer> pair = new ArrayList<Integer>();

                    pair.add(item.get(combination.get(j).get(0)-1));
                    pair.add(item.get(combination.get(j).get(1)-1));

                    if (!graphList.contains(pair))
                    {
                        clique = false;
                        break;

                    }

                }

            }

            if (clique == true){
                dfs3(n, k, i + 1, item, res);

            }

            item.remove(item.size() - 1);
        }
    }





//Figure out one way of coloring the graph with the minimum number of colors used, given a set of nodes colored by the user(s).


    private int[] coloring(int[][] colored) {

        //colored = process(colored);


        int[] result = new int[n];


        while (lowerBound <= upperBound)

        {

            int[] permutation = per(lowerBound,n,colored);

            if (permutation.length != 0)
            {
                result = permutation;
                break;

            }
            else {
                lowerBound++;
            }
        }



        return result ;




    }

// Method "minimum" prints out the minimum number of colors needed and one way of coloring, given the set of nodes colored by the user(s).


    private int minimum(int[][] colored)	{

        int[] result = coloring(colored);
        int min = 0;

        for (int i = 0; i<result.length;i++) {
            if (min < result[i])
                min = result[i];
        }

        return min;

    }

    public int chromaticnumDFS() {
        if (upperBound == lowerBound)
            return upperBound;
        else {
            return minimum(new int[0][0]);

        }
    }

    public void chromaticNum(Graph big) throws IOException {

        if (big.m >= 100000 && !containsOdd(big.adjacencyMatrix,0))
            System.out.println("CHROMATIC NUMBER = 2");


        else if (isBipartite(big)) {
            System.out.println("CHROMATIC NUMBER = 2");

        }

        else
        {
            greedy Greedy = new greedy(big.n);
            int upperboundGreedy = Greedy.greedyColoring(big.graphName);
            big.upperBound = Math.min(Math.min(big.upperboundWP(),big.upperboundRLF(0,big.countList)),upperboundGreedy);
            System.out.println("NEW BEST UPPER BOUND = " + big.upperBound);
            big.lowerBound = big.lowerBound();
            System.out.println("NEW BEST LOWER BOUND = " + big.lowerBound);

            boolean checkComplete = true;

            boolean checkCycle = true;

            //Check if the graph is a complete graph:

            for (int i = 0; i <= n - 1; i++) {
                if (count[i][1] != n - 1) {
                    checkComplete = false;
                    break;
                }
            }

            // Check if the graph is a cycle

            for (int i = 0; i <= n2 - 1; i++) {
                if (count[i][1] != 2) {
                    checkCycle = false;
                    break;
                }
            }

            // Produce the exact chromatic number if the input graph is a cycle or a complete graph or a bipartite graph, else produce the upper bound and lower bound and try to compute the chromatic number


            if (checkCycle == true) {
                if (n2 % 2 != 0) {
                    System.out.println("CHROMATIC NUMBER = 3");
                } else {
                    System.out.println("CHROMATIC NUMBER = 2");
                }

            } else if (checkComplete == true) {
                System.out.println("CHROMATIC NUMBER = " + n2);
            } else if (big.upperBound == big.lowerBound) {
                System.out.println("CHROMATIC NUMBER = " + big.upperBound);

            } else {
                System.out.println("CHROMATIC NUMBER = " + chromaticNumberBC(big));
            }

        }

    }




// Methods "per" and "dfs" are supposed to produce (if there is) a satisfying coloring of the input graph with n colors and k nodes by DFS


    private int[] per(int n, int k, int[][] colored) {

        if (n <= 0)
            return new int[0];

        ArrayList<Integer> item = new ArrayList<Integer>();
        int[] itemArray = new int[k];

        for (int i = 0; i<colored.length;i++) {
            itemArray[colored[i][0]-1] = colored[i][1];

        }


        ArrayList<Integer> index = new ArrayList<Integer>();
        for (int abc = 0; abc < itemArray.length;abc++) {
            if (itemArray[abc] == 0)
                index.add(abc);
        }


        return  dfs(n, k, item, itemArray,colored,index);
    }

    private int[] dfs(int n, int k, ArrayList<Integer> item, int[] itemArray , int[][] colored, ArrayList<Integer> index ) {

        if (item.size() == k-colored.length) {

            return itemArray;

        }


        for (int i = 1; i <= n; i++) {

            if (item.size() == k-colored.length) {

                return itemArray;

            }

            item.add(i);



            int countCount = 0;
            for (int o = 0; o< index.size(); o++) {
                if (o < item.size())
                {
                    itemArray[index.get(o)] = item.get(countCount);
                    countCount++;

                }
                else {
                    break;
                }


            }


            boolean color = true;
            for (int j = 0; j< graphList.size();j++)
            {
                if (itemArray.length>= graphList.get(j).get(1)){
                    if (itemArray[(graphList.get(j).get(0)-1)] != 0 && itemArray[(graphList.get(j).get(1)-1)] !=0 && itemArray[(graphList.get(j).get(0)-1)] == itemArray[(graphList.get(j).get(1)-1)])
                    {
                        color = false;
                        break;
                    }
                }
            }


            if (color == true) {

                if (item.size() == k-colored.length) {

                    return itemArray;

                }

                dfs(n, k, item, itemArray,colored,index);
            }

            item.remove(item.size() - 1);

            for (int o = 0; o<index.size();o++) {
                itemArray[index.get(o)] = 0;
            }



        }

        return new int[0];

    }


    void DFSUtil(int v, boolean[] visited,ArrayList<Integer> component) {
        // Mark the current node as visited and print it
        visited[v-1] = true;
        component.add(v);
        // Recur for all the vertices
        // adjacent to this vertex
        for (int x : adjListArray[v-1]) {
            if(!visited[x-1]) DFSUtil(x,visited,component);
        }

    }
    void connectedComponents() {
        // Mark all the vertices as not visited
        boolean[] visited = new boolean[n];
        for(int v = 1; v <= n; ++v) {
            if(!visited[v-1]) {
                // print all reachable vertices
                // from v
                ArrayList<Integer> component = new ArrayList<Integer>();
                DFSUtil(v,visited,component);
                connectedComponents.add(component);

            }
        }
    }



    // A recursive function that finds and prints biconnected
    // components using DFS traversal
    // u --> The vertex to be visited next
    // disc[] --> Stores discovery times of visited vertices
    // low[] -- >> earliest visited vertex (the vertex with minimum
    // discovery time) that can be reached from subtree
    // rooted with current vertex
    // *st -- >> To store visited edges
    void BCCUtil(int u, int disc[], int low[], LinkedList<Edge> st,
                 int parent[], Set<Integer> component)
    {

        // Initialize discovery time and low value
        disc[u-1] = low[u-1] = ++time;
        int children = 0;

        // Go through all vertices adjacent to this
        Iterator<Integer> it = adj[u-1].iterator();
        while (it.hasNext()) {
            int v = it.next(); // v is current adjacent of 'u'

            // If v is not visited yet, then recur for it
            if (disc[v-1] == -1) {
                children++;
                parent[v-1] = u;

                // store the edge in stack
                st.add(new Edge(u, v));
                BCCUtil(v, disc, low, st, parent,component);

                // Check if the subtree rooted with 'v' has a
                // connection to one of the ancestors of 'u'
                // Case 1 -- per Strongly Connected Components Article
                if (low[u-1] > low[v-1])
                    low[u-1] = low[v-1];

                // If u is an articulation point,
                // pop all edges from stack till u -- v
                if ((disc[u-1] == 1 && children > 1) || (disc[u-1] > 1 && low[v-1] >= disc[u-1])) {
                    while (st.getLast().u != u || st.getLast().v != v) {
                        //System.out.print(st.getLast().u + "--" + st.getLast().v + " ");

                        component.add(st.getLast().u);

                        component.add(st.getLast().u);
                        st.removeLast();
                    }
                    ArrayList<Integer> componentArr = new ArrayList<Integer>();
                    componentArr.addAll(component);

                    if (!biconnectedComponents.contains(componentArr))
                        biconnectedComponents.add(componentArr);
                    //System.out.print(st.getLast().u + "--" + st.getLast().v + " ");

                    //component = new ArrayList<Integer>();
                    component = new HashSet<Integer>();
                    component.add(st.getLast().u);
                    component.add(st.getLast().v);
                    componentArr = new ArrayList<Integer>();

                    if (!biconnectedComponents.contains(componentArr))
                        biconnectedComponents.add(componentArr);
                    st.removeLast();
                    counter++;
                }
            }

            // Update low value of 'u' only if 'v' is still in stack
            // (i.e. it's a back edge, not cross edge).
            // Case 2 -- per Strongly Connected Components Article
            else if (v != parent[u-1] && disc[v-1] < disc[u-1] ) {
                if (low[u-1] > disc[v-1])
                    low[u-1] = disc[v-1];

                st.add(new Edge(u, v));
            }
        }
    }

    // The function to do DFS traversal. It uses BCCUtil()
    void BCC(Set<Integer> component)
    {
        int disc[] = new int[n];
        int low[] = new int[n];
        int parent[] = new int[n];
        LinkedList<Edge> st = new LinkedList<Edge>();


        // Initialize disc and low, and parent arrays
        for (int i = 0; i < n; i++) {
            disc[i] = -1;
            low[i] = -1;
            parent[i] = -1;
        }

        for (int i = 0; i < n; i++) {
            if (disc[i] == -1)
                BCCUtil(i+1, disc, low, st, parent,component);

            int j = 0;

            //component = new ArrayList<Integer>();

            // If stack is not empty, pop all edges from stack
            while (st.size() > 0) {
                j = 1;
                int u = st.getLast().u;
                int v = st.getLast().v;
                //System.out.print(st.getLast().u + "--" + st.getLast().v + " ");
                component.add(u);
                component.add(v);
                st.removeLast();
            }


            if (j == 1) {
                ArrayList<Integer> componentArr = new ArrayList<Integer>();
                componentArr.addAll(component);
                if (!biconnectedComponents.contains(componentArr))
                    biconnectedComponents.add(componentArr);
                componentArr = new ArrayList<Integer>();
                component = new HashSet<Integer>();
                //System.out.println();
                counter++;
            }
        }
    }



}


