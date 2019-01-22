//program to implement greedy algorithm for graph coloring
import java.io.*;
import java.util.*;

class greedy {

    private static int nodeNum; //nr of vertices
    private static int edgeNum; //nr of edges
    private static LinkedList<Integer> adj[]; //adjacency list
    private static int[][] graph = new int[edgeNum][2];
    private static int max = 0;
    private static int bestRes = 0;
    private static String inputFile;
    private static ArrayList<Integer> order;
    private static ArrayList<Integer> degree;
    private static ArrayList<Integer> finalResult;
    private static int count = 0;

    //constructor for the graph
    public greedy(int v) {
        nodeNum = v;
        adj = new LinkedList[v];
        for (int i = 0; i < v; i++) {
            adj[i] = new LinkedList();
        }
    }

    //function to read the graph from txt file
    private static void readGraph(String inputFile) {
        try {
            FileReader fr = new FileReader(inputFile);
            Scanner sc = new Scanner(fr);
            String str1 = sc.nextLine();
            //System.out.println(str1);
            String[] part1 = str1.split("= ");
            nodeNum = Integer.parseInt(part1[1]); //nr of nodes
            String str2 = sc.nextLine();
            String[] part2 = str2.split("= ");
            edgeNum = Integer.parseInt(part2[1]); //nr of edges

            graph = new int[edgeNum][2];
            int i = 0;
            while (sc.hasNextLine()) {
                String edge = sc.nextLine();
                String[] nodes = edge.split(" ");
                graph[i][0] = Integer.parseInt(nodes[0]);
                graph[i][1] = Integer.parseInt(nodes[1]);
                i++;
            }


        }
        catch (Exception e) {
            System.out.println();
        }
    }

    //function to add an edge into the graph
    public static void addEdge(int v, int w) {
        adj[v-1].add(w);
        adj[w-1].add(v);
    }

    public static int greedyColoring(String inputFile) {

        //String inputFile = "graph01.txt";
        readGraph(inputFile); //read the graph from the txt file

        //add edges from graph array to graph g1
        for (int i = 0; i < edgeNum; i++) {
            addEdge(graph[i][0], graph[i][1]);
        }

        ArrayList<Integer> finalResult = new ArrayList<Integer>();

        for (int count = 1; count <= 1000; count++) {
            //array for storing the colors
            int[] result = new int[nodeNum];

            //initialize all vertices as unassigned
            Arrays.fill(result, -1);
            ArrayList<Integer> order = new ArrayList<Integer>();

            //this arraylist stores the degree of each vertex (from first to last)
            if (count == 1000) {
                ArrayList<Integer> degree = new ArrayList<Integer>();
                for (int i = 0; i < nodeNum; i++) {
                    int d = adj[i].size();
                    degree.add(d);
                }

                int maxDegree = Collections.max(degree);

                //this loop will find the index of the vertex with maximum degree and
                //will store it in the order array list
                while (maxDegree > 0) {
                    maxDegree = Collections.max(degree);
                    if (maxDegree == -1) break;
                    int ind = degree.indexOf(maxDegree) + 1; //because we will start from vertex 1
                    order.add(ind);
                    degree.set(ind-1, -1); //ind-1 , because the indexes in degree list start from 0
                }
            }

            //create an arrayList with random numbers from 1 to the nr of nodes
            //to use it for coloring the nodes in random order
            else {
                Random rd = new Random();
                for (int j = 0; j < nodeNum; j++) {
                    int randpos = rd.nextInt(nodeNum) + 1;
                    while (order.contains(randpos)) {
                        randpos = rd.nextInt(nodeNum) + 1;
                    }
                    order.add(randpos);
                }
            }

            //a temporary array to store the available colors. False value of
            // available[cr] would mean that the color cr is assigned to one of
            //its adjacent vertices
            boolean[] available = new boolean[nodeNum];

            //initially all colors are available
            Arrays.fill(available, true);

            //now assign colors to every vertex
            for (int j = 0; j < order.size(); j++) {
                int u = order.get(j); //random start
                //process all adjacent vertices and flag their colors as unavailable

                Iterator<Integer> it = adj[u-1].iterator();
                while (it.hasNext()) {
                    int i = it.next();
                    if (result[i-1] != -1) {
                        available[result[i-1]] = false;
                    }
                }
                //find the first available color
                int cr;
                for (cr = 0; cr < nodeNum; cr++) {
                    if (available[cr]) {
                        break;
                    }
                }
                //assign the found color
                result[u-1] = cr;

                //reset the values back to true for the next iteration
                Arrays.fill(available, true);
            }

            //count the number of used colors
            for (int u = 1; u < nodeNum + 1; u++) {
                if (result[u-1] > max) {
                    max = result[u-1];
                }
            }
            max = max + 1; //because first color had value 0
            finalResult.add(max);
            max = 0;
        }

        int upperboundGreedy = Collections.min(finalResult);
        return upperboundGreedy;

    }

}


