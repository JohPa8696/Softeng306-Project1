Branch and Bound algorithm


Task to complete the algorithm.

1. Order of the node

	Need to implement a permutation method to store all possible order. 
	Expected input: ArrayList<Node>
	Expected output: ArrayList<ArrayList<Node>>

	E.g. Using the input.dot, the possible order would be abcd,acbd. But adcb is not possible as d is dependent to c and b.
	The final output would be like: {
						{a,b,c,d};
						{a,c,b,d};
						...
					}

2. Allocation of the node to the processor
	
	Need to implement a method to allocate processors to each node in the arraylist that generated from 1. 
	E.g. Using the input.dot, the number of processors assigned is 2. The possible sequence of order {a,b,c,d} would be a1,b2,c2,d1 or a1,b1,c2,d2
	The final output would be like:{a1,b2,c2,d1} using node.setProcessor(int proc) method.

	Expected input: output from 1. 
	Expected output: ArrayList<Node>
	Could use a search tree or bitmap to traverse the allocation of the node. i.e. 1111,1112,1121,1122,1211,....


3. Calculate the finish time of a specific path and update the bound (almost completed)
	
	Need to implement a method getFinishTime(ArrayList<Node> nodelist) to calculate the finish time of a specific path (i.e. {a1,b2,c2,d1}).
	e.g. assume the upper bound is 10sec. If the finish time of the path exceed the bound, cut it and start another path.
	If the upper bound is not exceed, updated the bound and return the finish time for the path. 

	Expected input: ArrayList<Node> 
	Expected output: int finishTime

	Author: Jack Wong
	Date: 05/08/16