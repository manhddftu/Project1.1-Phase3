# Project1.1-Phase3
Turn towards the prizes!
--------------------------

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

3. RUNNING ON CMD
When you run the file after compilation, just write the number of the graph instead of its fucking long name: block3.... For example: java gameAlgorithm 05







