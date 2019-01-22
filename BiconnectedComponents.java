
// A Java program to find biconnected components in a given
// undirected graph
import java.io.*;
import java.util.*;



// This class represents a directed graph using adjacency
// list representation
class BiconnectedComponents {
    static int V, E; // No. of vertices & Edges respectively
    static LinkedList<Integer> adj[]; // Adjacency List
    static ArrayList<ArrayList<Integer>> graphList = new ArrayList<ArrayList<Integer>>();
    static String graphName;
    public static ArrayList<Set<Integer>> components = new ArrayList<Set<Integer>>();



    // Count is number of biconnected components. time is
    // used to find discovery times
    static int count = 0, time = 0;

    class Edge {
        int u;
        int v;

        Edge() {
            this.u = 0;
            this.v = 0;
        }
        Edge(int u, int v)
        {
            this.u = u;
            this.v = v;
        }
    };

    // Constructor
    BiconnectedComponents(int v)
    {
        V = v;
        E = 0;
        adj = new LinkedList[v];
        for (int i = 0; i < v; ++i)
            adj[i] = new LinkedList();
    }

    // Function to add an edge into the graph
    void addEdge(int v, int w)
    {
        adj[v].add(w);
        E++;
    }

      void readGraph(String graphName) {


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
                V = Integer.parseInt( record.substring(11) );
                //if(DEBUG) System.out.println(COMMENT + " Number of vertices = "+n);
            }

            seen = new boolean[V+1];

            record = br.readLine();

            if( record.startsWith("EDGES = ") )
            {
                E = Integer.parseInt( record.substring(8) );
                //if(DEBUG) System.out.println(COMMENT + " Expected number of edges = "+m);
            }

            e = new Edge[E];

            for( int d=0; d<E; d++)
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
				/*
				if( surplus != null )
				{
					if( surplus.length() >= 2 ) if(DEBUG) System.out.println(COMMENT + " Warning: there appeared to be data in your file after the last edge: '"+surplus+"'");
				}
				*/

        }
        catch (IOException ex)
        {
            // catch possible io errors from readLine()
            System.out.println("Error! Problem reading file "+inputfile);
            System.exit(0);
        }

        for( int x=1; x<=V; x++ )
        {
            if( seen[x] == false )
            {
                //if(DEBUG) System.out.println(COMMENT + " Warning: vertex "+x+" didn't appear in any edge : it will be considered a disconnected vertex on its own.");
            }
        }

        // Store all the edges in an 2-D int array named Graph
        int[][] Graph = new int[E][2];
        for (int i =0; i< E; i++) {
            Graph[i][0] = e[i].u;
            Graph[i][1] = e[i].v;
        }


        for (int i = 0; i<E; i++) {
            if (Graph[i][0] !=0 && Graph[i][1]!=0) {
                ArrayList<Integer> elm = new ArrayList<Integer>();
                elm.add(Graph[i][0]);
                elm.add(Graph[i][1]);

                graphList.add(elm);
            }
        }


        //! At this point e[0] will be the first edge, with e[0].u referring to one endpoint and e[0].v to the other
        //! e[1] will be the second edge...
        //! (and so on)
        //! e[m-1] will be the last edge
        //!
        //! there will be n vertices in the graph, numbered 1 to n

    }


    // A recursive function that finds and prints strongly connected
    // components using DFS traversal
    // u --> The vertex to be visited next
    // disc[] --> Stores discovery times of visited vertices
    // low[] -- >> earliest visited vertex (the vertex with minimum
    // discovery time) that can be reached from subtree
    // rooted with current vertex
    // *st -- >> To store visited edges
    void BCCUtil(int u, int disc[], int low[], LinkedList<Edge> st,
                 int parent[],PrintWriter printWriter) throws IOException
    {

        // Initialize discovery time and low value
        disc[u] = low[u] = ++time;
        int children = 0;

        // Go through all vertices adjacent to this
        Iterator<Integer> it = adj[u].iterator();
        while (it.hasNext()) {
            int v = it.next(); // v is current adjacent of 'u'

            // If v is not visited yet, then recur for it
            if (disc[v] == -1) {
                children++;
                parent[v] = u;

                // store the edge in stack
                st.add(new Edge(u, v));
                BCCUtil(v, disc, low, st, parent,printWriter);

                // Check if the subtree rooted with 'v' has a
                // connection to one of the ancestors of 'u'
                // Case 1 -- per Strongly Connected Components Article
                if (low[u] > low[v])
                    low[u] = low[v];

                // If u is an articulation point,
                // pop all edges from stack till u -- v
                if ((disc[u] == 1 && children > 1) || (disc[u] > 1 && low[v] >= disc[u])) {
                    while (st.getLast().u != u || st.getLast().v != v) {
                        printWriter.print(st.getLast().u + "--" + st.getLast().v + " ");

                        st.removeLast();
                    }
                    printWriter.println(st.getLast().u + "--" + st.getLast().v + " ");
                    printWriter.println();
                    st.removeLast();

                    count++;
                }
            }

            // Update low value of 'u' only if 'v' is still in stack
            // (i.e. it's a back edge, not cross edge).
            // Case 2 -- per Strongly Connected Components Article
            else if (v != parent[u] && disc[v] < disc[u] ) {
                if (low[u] > disc[v])
                    low[u] = disc[v];

                st.add(new Edge(u, v));
            }
        }
    }

    // The function to do DFS traversal. It uses BCCUtil()
    void BCC (PrintWriter printWriter) throws IOException
    {

        int disc[] = new int[V];
        int low[] = new int[V];
        int parent[] = new int[V];
        LinkedList<Edge> st = new LinkedList<Edge>();

        // Initialize disc and low, and parent arrays
        for (int i = 0; i < V; i++) {
            disc[i] = -1;
            low[i] = -1;
            parent[i] = -1;
        }

        for (int i = 0; i < V; i++) {
            if (disc[i] == -1)
                BCCUtil(i, disc, low, st, parent,printWriter);

            int j = 0;

            // If stack is not empty, pop all edges from stack
            while (st.size() > 0) {
                j = 1;
                printWriter.print(st.getLast().u + "--" + st.getLast().v + " ");
                st.removeLast();
            }

            if (j == 1) {
                printWriter.println();
                printWriter.println();

                count++;
            }
        }
        //fileWriter.close();
    }

    public void loadBiconnected(PrintWriter printWriter) throws IOException {

        BCC(printWriter);
        printWriter.close();

        File file = new File("BiconnectedComponents.txt");

        Scanner sc = new Scanner(file);

        Set<Integer> set = new HashSet<Integer>();

        while (sc.hasNextLine()) {
            String line = sc.nextLine();
            if(line.length()==0) {
                components.add(set);
                set = new HashSet<Integer>();
            }
            else{

                String[] nodes = line.split(" ");
                //System.out.println(Arrays.toString(nodes));


                for (int i = 0; i< nodes.length;i++) {
                    String[] edge = nodes[i].split("--");
                    int node1 = Integer.parseInt(edge[0]);
                    int node2 = Integer.parseInt(edge[1]);
                    set.add(node1+1);
                    set.add(node2+1);

                }

            }


        }

    }


}


