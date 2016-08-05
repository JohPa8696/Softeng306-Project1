package branch_and_bound;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import node.Node;


/**
 * Class: Algorithm
 * Description: Create branch and bound algorithm to solve the problem
 * Variable: numProc (number of processor)
 * @Author Jack Wong
 * Date: 05/08/16
 */
public class BranchAnBound {
	private int numProc;
	public BranchAnBound(int numProc){
		this.numProc = numProc;
	}
	
	/**
	 * @param nodelist
	 * @return the final finish time of the list of a specific order and allocation.
	 * This method is to calculate the finish time of the a specific path. It calculates the start time and finish time of each node in the list
	 * and return the final finish time of that path.
	 * 
	 */
	public int getFinishTime(ArrayList<Node> nodelist){
		int[] proc_time = new int[numProc];
		int finalFinishTime = 0;
		
		//Loop through all the node in the node list.
		for (Node node:nodelist){
			node.setStartTime(proc_time[node.getProcessor()-1]);
			int startTime = node.getStartTime();
			int finishTime;
			
			//Loop through every parents of the current node and calculate the start time.
			for (Map.Entry<Node, Integer> parent :node.getParents().entrySet()){
				//System.out.println("The current node is "+node.getName()+". The parent node is "+parent.getKey().getName() + " and the value is "+parent.getValue());
				if (node.getProcessor() != parent.getKey().getProcessor()){
					if(startTime < parent.getValue() + parent.getKey().getFinishTime()){
						startTime = parent.getValue() + parent.getKey().getFinishTime();
					}
				}
			}
			
			//Calculate the finish time of the current node and update the finish time
			finishTime = startTime + node.getWeight();
			node.setFinishTime(finishTime);
			proc_time[node.getProcessor()-1] = finishTime;
			
			//Not finished. It is to update the bound. If the finalFinishTime exceeds the current finish time, break the for loop and start with other path.
			if (finishTime > finalFinishTime){
				finalFinishTime = finishTime;
			}
			System.out.println("The current node is "+node.getName() + " the start time is "+startTime+" and the finishtime is "+finishTime+" and the processor is "+node.getProcessor());
		}
		
		return finalFinishTime;
		
	}
}
