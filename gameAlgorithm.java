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
		private static int[][] colorMatrix; // for RLF
		private static ArrayList<Integer> countList = new ArrayList<Integer>();

		public gameAlgorithm() {
			readGraph();
		}


		public static void main(String[] args) {
			graphName = args[0];
			readGraph();
			System.out.println(chromaticnumDFS());
			System.out.println(upperboundWP());
			System.out.println(lowerBound());

		}

		// CHECKER: PASS THE SET OF COLORED NODES BY THE USER AS INPUT THEN RETURN IF IT VIOLATES THE BIG RULE: NO CONNECTED NODES SHARE THE SAME COLOR

		public static boolean checker(int[][] colored) {

			boolean checker = false;

			for (int i = 0; i<graphList.size();i++) {
				int node1 = graphList.get(i).get(0);
				int node2 = graphList.get(i).get(1);
				if (colored[node1-1][1] != 0 && colored[node2-1][1] != 0 && colored[node1-1][1] == colored[node2-1][1]) {
					checker = true;
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
			int[][] countUnsorted = new int[n][2];
			System.arraycopy(count,0,countUnsorted,0,count.length);
			Arrays.sort(count, (a, b) -> Integer.compare(a[1], b[1]));

			for (int i = 0; i<n; i++) {
				if (count[i][1]!=0)
					n2++;


			}

			colorMatrix = new int[n][2];
			for (int i = 0; i<n;i++) {
				colorMatrix[i][0] = i+1;
			}

			int[][] countDescending = new int[n][2] ;

			for (int i = 0; i<= n-1; i++) {
				countDescending[i] = count[n-1-i];
			}
			for (int i = 0; i<= n-1; i++) {
				countList.add(countDescending[i][0]);
			}

		}


		// Welsh - Powell algorithm to calculate upper bound:
			    

		private static int upperboundWP()

		{
			// Store the nodes and corresponding degrees in degree-descending order

			int[][] countDescending = new int[n][2] ;	

			for (int i = 0; i<= n-1; i++) {
			countDescending[i] = count[n-1-i];
			}

			ArrayList<Integer> countList = new ArrayList<Integer>();
		    for (int i = 0; i<= n-1; i++) {
		    countList.add(countDescending[i][0]);   
		    }
		    
		    
		    int upperBound = 0;
		    
		    boolean colorable = true;

		    
		    while (countList.size() != 0) {

				int highestNode = countList.get(0);
				countList.remove(countList.indexOf(countList.get(0)));
				ArrayList<Integer> coloredNodes = new ArrayList<Integer>();
				coloredNodes.add(highestNode);
				for (int j = 0; j<= countList.size()-1; j++) {
					int v = 0;
					while (v<= coloredNodes.size()-1) {

						int node1 = coloredNodes.get(v);
						int node2 = countList.get(j);
						if (adjacencyMatrix[node1-1][node2-1]==1) {
							colorable = false;
							break;
						}

						else{
							colorable = true;
							v++;
						}

					}

					if (colorable == true) {
						coloredNodes.add(countList.get(j));

					}

				}

				for (int u = 0; u<= coloredNodes.size()-1;u++) {

					if (countList.contains(coloredNodes.get(u))){
						countList.remove(countList.indexOf(coloredNodes.get(u)));
					}
				}


				upperBound ++;
		    	
		    }
		    
		    return upperBound;
		    
			
	
		}


      // Produce the lower bound by searching for the clique number of a given graph
      
      private static int lowerBound()
      
      {

		  int lowerBound = 2;
		  int upperBound = upperboundWP();

		  //System.out.println(upperBound);

		  boolean clique = true;

		  while (clique == true && lowerBound <= upperBound)
			  {
				  if (clique(n2,lowerBound).size()>0)
					  {
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


		

