import java.io.*;
import java.util.*;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;


/**

 MANUAL GUIDELINES ON USING MY gameAlgorithm.java file:

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

 * @author  Manh Dao
 * @version 1.1
 * @since   2019-01-07
 */

class ColEdge
{
    int u;
    int v;
}

public class gameAlgorithm
{

    public static  int n = -1; // number of nodes
    private static int n2; // number of nodes which are connected to at least 1 node other than themselve
    private static int m = 0; // number of edges
    private final static boolean DEBUG = true;
    private final static String COMMENT = "//";
    private static ArrayList<ArrayList<Integer>> graphList = new ArrayList<ArrayList<Integer>>(); // stores all the edges of the graph here
    private static int[][] count ; // stores the nodes and corresponding degrees in an degree-ascending order
    private static String graphName;
    private static int[][] adjacencyMatrix;
    private static int[][] colorMatrixRLF; // for RLF
    private static int[][] colorMatrixWP; // for WP
    private static ArrayList<Integer> countList = new ArrayList<Integer>();
    private static ArrayList<Integer> countListWP = new ArrayList<Integer>();
    private static int[][] countUnsorted; // stores the nodes and corresponding degrees in an node-ascending order
    private static ArrayList<Integer> cliqueGraph; // biggest clique subgraph of the input graph. Need to call lowerBound() first to fill it up

    public gameAlgorithm() {
        readGraph();

    }

    public static void main(String[] args) {
        graphName = args[0];
        readGraph();
        //System.out.println(upperboundRLF(1,countList));
        //System.out.println(upperboundWP());
        System.out.println(lowerBound());
        System.out.println(cliqueGraph);



    }

    // CHECKER: PASS THE SET OF COLORED NODES BY THE USER AS INPUT THEN RETURN IF IT VIOLATES THE BIG RULE: NO CONNECTED NODES SHARE THE SAME COLOR

    public static boolean checker(int[][] colored) {

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

		/*
		//HINTING GAME MODE 1: SIMPLY RETURNS THE CHROMATIC NUMBER AND ONE WAY OF COLORING

		public static void hinting1() {

				System.out.println("The chromatic number: " + chromaticNum() + " Coloring: "+ Arrays.toString(coloring(new int[0][0])));
			}


		// HINTING GAME MODE 2.1: RETURNS THE MINIMUM NUMBER OF COLORS NEEDED AND ONE POSSIBLE COLORING GIVEN THE SET OF NODES COLORED BY THE USERS
		public static void hinting2_1(int[][]colored) {

			if (checker(colored)) {
				System.out.println("Invalid coloring!");
			}

			else {
				int min = minimum(colored);
				int[] result = coloring(colored);
				System.out.println("The minimum colors needed: " + min + " Coloring: " + Arrays.toString(result));
			}

			}


		//HINTING MODE 2.2: RECOMMEND A SET OF 3 NODES FOR THE USER TO CONSIDER

		public static void hinting2_2(int[][] colored) {

			if (checker(colored)) {
				System.out.println("Invalid coloring!");

			}

			else {


				ArrayList<Integer> consideredNodes = new ArrayList<Integer>();


				for (int i = n - 1; i >= 0; i--) {

					if (consideredNodes.size() == 3)
						break;
					int node = count[i][0];
					boolean contained = false;
					for (int j = 0; j < colored.length; j++) {
						if (colored[j][0] == node) {
							contained = true;
							break;
						}
					}

					if (contained == false) {
						consideredNodes.add(node);
					}
				}

				System.out.println("The 3 next nodes you should consider: " + consideredNodes);

			}
			}


		*/

    private static void readGraph() {


        String inputfile = "block3_2018_graph" +graphName+".txt";

        boolean seen[] = null;

        //! n is the number of vertices in the graph


        //! m is the number of edges in the graph


        //! e will contain the edges of the graph
        ColEdge e[] = null;

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

            e = new ColEdge[m];

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
                e[d] = new ColEdge();

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

        for( int x=1; x<=n; x++ )
        {
            if( seen[x] == false )
            {
                //if(DEBUG) System.out.println(COMMENT + " Warning: vertex "+x+" didn't appear in any edge : it will be considered a disconnected vertex on its own.");
            }
        }

        // Store all the edges in an 2-D int array named Graph
        int[][] Graph = new int[m][2];
        for (int i =0; i< m; i++) {
            Graph[i][0] = e[i].u;
            Graph[i][1] = e[i].v;
        }

        count = new int[n][2];

        adjacencyMatrix = new int[n][n];


        for (int i = 0; i<m; i++) {
            if (Graph[i][0] !=0 && Graph[i][1]!=0) {
                ArrayList<Integer> elm = new ArrayList<Integer>();
                elm.add(Graph[i][0]);
                elm.add(Graph[i][1]);

                graphList.add(elm);
                adjacencyMatrix[Graph[i][0]-1][Graph[i][1]-1]=1;
                adjacencyMatrix[Graph[i][1]-1][Graph[i][0]-1]=1;

            }
        }


        //! At this point e[0] will be the first edge, with e[0].u referring to one endpoint and e[0].v to the other
        //! e[1] will be the second edge...
        //! (and so on)
        //! e[m-1] will be the last edge
        //!
        //! there will be n vertices in the graph, numbered 1 to n

        for (int i=0; i<n;i++) {
            count[i][0] = i+1;
        }

        for (int i = 0; i< m; i++) {
            count[Graph[i][0]-1][1] ++;
            count[Graph[i][1]-1][1] ++;

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

        colorMatrixWP = colorMatrixRLF;

        int[][] countDescending = new int[n][2] ;

        for (int i = 0; i<= n-1; i++) {
            countDescending[i] = count[n-1-i];
        }
        for (int i = 0; i<= n-1; i++) {
            countList.add(countDescending[i][0]);
        }

        countListWP.addAll(countList);

    }


    // Welsh - Powell algorithm to calculate upper bound:


    private static int upperboundWP()

    {
        // Store the nodes and corresponding degrees in degree-descending order

        ArrayList<Integer> countListLocal = countListWP;
        int upperBound = 0;

        while (countListLocal.size() != 0) {

            upperBound ++;
            int highestNode = countListLocal.get(0);
            colorMatrixWP[highestNode-1][1] = upperBound;
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
                colorMatrixWP[selectedNode-1][1]=upperBound;
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

        return upperBound;



    }

    // Recursive Largest Algorithm method to compute the upper bound

    private static int upperboundRLF(int k, ArrayList<Integer> countListParameter) {

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

    private static ArrayList<Integer> buildCountList(ArrayList<Integer> nodeList) {
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

    private static int lowerBound()

    {

        int lowerBound = 2;
        int upperBound = upperboundWP();
        System.out.println(upperBound);

        //System.out.println(upperBound);

        boolean clique = true;

        while (clique == true && lowerBound <= upperBound)
        {
            if (clique(n,lowerBound).size()>0)
            {
                cliqueGraph = clique(n,lowerBound).get(0);
                lowerBound++;
            }

            else {
                clique = false;
            }


        }
        return lowerBound-1;

    }




    // Methods "combine" and "dfs2" are supposed to produce all the k-length combinations of the set of integers from 1 to n by DFS

    private static ArrayList<ArrayList<Integer>> combine(int n, int k)
    {
        ArrayList<ArrayList<Integer>> result = new ArrayList<ArrayList<Integer>>();

        if (n <= 0 || n < k)
            return result;

        ArrayList<Integer> item = new ArrayList<Integer>();
        dfs2(n, k, 1, item, result); // because it needs to begin from 1

        return result;
    }



    private static void dfs2(int n, int k, int start, ArrayList<Integer> item,
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

    private static ArrayList<ArrayList<Integer>> clique(int n, int k)
    {
        ArrayList<ArrayList<Integer>> result = new ArrayList<ArrayList<Integer>>();

        if (n <= 0 || n < k)
            return result;

        ArrayList<Integer> item = new ArrayList<Integer>();
        dfs3(n, k, 1, item, result); // because it need to begin from 1

        return result;

    }


    private static void dfs3(int n, int k, int start, ArrayList<Integer> item, ArrayList<ArrayList<Integer>> res)
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


    private static int[] coloring(int[][] colored) {

        colored = process(colored);

        int coloredNum = 0;

        for (int i = 0; i< colored.length;i++) {
            if (coloredNum < colored[i][1])
                coloredNum = colored[i][1];
        }

        int[] result = new int[n];

        int lower = Math.max(lowerBound(),coloredNum);
        int upperBound = upperboundWP();

        while (lower <= upperBound)

        {

            int[] permutation = per(lower,n,colored);


            if (permutation.length != 0)
            {
                result = permutation;
                break;

            }
            else {
                lower++;
            }
        }



        return result ;




    }

// Method "minimum" prints out the minimum number of colors needed and one way of coloring, given the set of nodes colored by the user(s).


    private static int minimum(int[][] colored)	{

        int[] result = coloring(colored);
        int min = 0;

        for (int i = 0; i<result.length;i++) {
            if (min < result[i])
                min = result[i];
        }

        return min;

    }

    public static int chromaticnumDFS() {


        boolean checkComplete  = true;

        boolean checkCycle = true;

        //Check if the graph is a complete graph:

        for (int i = 0; i<= n-1;i++) {
            if (count[i][1] != n-1) {
                checkComplete = false;
                break ;
            }
        }

        // Check if the graph is a cycle

        for (int i = 0; i<= n-1;i++) {
            if (count[i][1] != 2) {
                checkCycle = false;
                break ;
            }
        }

        // Produce the exact chromatic number if the input graph is a cycle or a complete graph, else produce the upper bound and lower bound



        if (checkCycle == true) {
            if (n % 2 != 0) {
                return 3;
            }
            else {
                return 2;
            }

        } else if (checkComplete == true) {
            return n; }


        else {

            if (upperboundWP() == lowerBound())
                return upperboundWP();
            else {
                return minimum(new int[0][0]);

            }
        }}




// Methods "per" and "dfs" are supposed to produce (if there is) a satisfying coloring of the input graph with n colors and k nodes by DFS


    private static  int[] per(int n, int k, int[][] colored) {

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

    private static int[] dfs(int n, int k, ArrayList<Integer> item, int[] itemArray , int[][] colored, ArrayList<Integer> index ) {

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


    // This method is supposed to "process" the "raw" set of colored nodes before manipulating it

    private static  int[][] process(int[][] colored) {
        if (colored.length == 0) {
            return colored;
        }

        else {
            int countZero = 0;

            if (colored.length >=2) {
                Arrays.sort(colored, (a, b) -> Integer.compare(a[1], b[1]));
            }
            int currentColor  = 0;
            int count = 0;
            for (int i = 0; i< colored.length; i++) {
                if (colored[i][1] == 0)
                    countZero++;
                else {
                    if (colored[i][1] != currentColor) {
                        count++;
                    }
                    currentColor = colored[i][1];
                    colored[i][1] = count;

                }
            }

            if (colored.length >=2) {
                Arrays.sort(colored, (a, b) -> Integer.compare(a[0], b[0]));
            }

            int[][] coloredNonZero = new int[colored.length-countZero][2];
            int counter = 0;
            for (int i = 0;i<colored.length;i++) {
                if (colored[i][1]!=0) {
                    coloredNonZero[counter][0] = colored[i][0];
                    coloredNonZero[counter][1] = colored[i][1];
                    counter++;
                }


            }

            return coloredNonZero;

        }


    }



}



