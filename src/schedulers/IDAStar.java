package schedulers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;

import node.Node;
import node.NodeTemp;

public class IDAStar implements Scheduler {

	private ArrayList<NodeTemp> dag;
	// TODO: extend the node class for IDA star (downcast?)
	// TODO: also merge the two following masks as fields for the extended node
	private ArrayList<Boolean> nextAvailableNodes;
	private ArrayList<Boolean> scheduledNodes;
	private ArrayList<Integer> procFinishTimes;
	
	private int numProc;
	private int fCutOff = 0;
	private int nextCutOff = -1;
	
	private ArrayList<NodeTemp> bestSchedule;
	private int bestFinishTime = -1;

	public IDAStar(ArrayList<NodeTemp> dag, ArrayList<Boolean> nextAvailableNodes, int numProc) {
		this.dag = dag;
		this.nextAvailableNodes = nextAvailableNodes;

		scheduledNodes = new ArrayList<Boolean>(dag.size());
		Collections.fill(scheduledNodes, Boolean.FALSE);
		
		this.numProc = numProc;
		procFinishTimes = new ArrayList<Integer>(numProc);
		
		bestSchedule = new ArrayList<>(dag.size());
	}

	@Override
	public void schedule() {
		for (int i = 0; i < nextAvailableNodes.size(); i++) {
			if (nextAvailableNodes.get(i)) {

				// get initial f cut off for starting node
				fCutOff= dag.get(i).getWeight() + getHValue(dag.get(i));
				
				// reset procFinishTimes
				Collections.fill(procFinishTimes, 0);
				
				boolean isSolved = false;
				while (!isSolved){
					isSolved = buildTree(dag.get(i), 1);
				}
			}
		}
	}

	/**
	 * Recursive method to build and traverse the tree for determining the schedule.
	 * It depends on a f cut-off value, which increments as each task builds.
	 * @param node - the current node/task of the traversal
	 * @param pNo - the assigned process number for the node/task
	 * @return true if a schedule has been made
	 */
	private boolean buildTree(NodeTemp node, int pNo) {
		int nodeStartTime = getStartTime(node, pNo);
		int g = nodeStartTime + node.getWeight();
		int h = getHValue(node);
		int f = g+h;
		
		// if greater than cut off, we only store the next cut off, no need to actually traverse it
		if (f > fCutOff ){
			if(f < nextCutOff || nextCutOff == -1){
				nextCutOff = f;
			}
			return false;
		} else {
			// TODO: add node to trail/path...
			node.setStartTime(nodeStartTime);
			node.setFinishTime(g);
			node.setProcessor(pNo);
			
			procFinishTimes.set(pNo-1, g);
			nextAvailableNodes.set(node.getIndex(), false);
			
			if(node.getChildren().isEmpty() /*TODO: and no more avaiable, and trail size = num of nodes*/){
				// TODO: copy solution to somewhere, store best time
				// NOTE: another way to copy the schedule would be to make a less heavy Node class which only has
				// can copy outside after buildTree finishes recursion, so long as i don't reset as going back up
				return true;
			} else {
				for (int childIndex:node.getChildren()){
					// check dependencies of children, add them to available if they can be visited
					boolean isResolved = checkDependencies(dag.get(childIndex));
					if (isResolved){
						nextAvailableNodes.set(childIndex, true);
					}
				}

				// begin recursion
				boolean isSuccessful = false;
				for (int i = 0;i<nextAvailableNodes.size();i++){
					if (nextAvailableNodes.get(i)){
						for (int j = 1; j<=numProc; j++){
							isSuccessful = buildTree(dag.get(i), j);
						}
					}
				}
				
				// TODO: remove node from trail/path
				
				// reset start and finish times and proc number of this node...
				// not exactly necessary
				/*node.setStartTime(-1);
				node.setFinishTime(-1);
				node.setProcessor(-1);*/
				
				// FIXME: backtrack the procFinishTime

				// set child availability to false
				for (int childIndex:node.getChildren()){
					nextAvailableNodes.set(childIndex, false);
				}
				
				// set current node to available as we traverse back up
				nextAvailableNodes.set(node.getIndex(), true);
				
				return isSuccessful;
			}
		}
	}

	/**
	 * Calculates the earliest start time for a given node and process number
	 * The earliest start time will be the latest of two possible times:
	 * 		- time based on parent tasks
	 * 		- time based on the current processor task
	 * @param node - the node/task
	 * @param pNo - process number
	 * @return integer of the earliest start time
	 */
	private int getStartTime(NodeTemp node, int pNo){
		int parentFinishTime = 0;
		int traversalTime = 0;
		Map<Integer, Integer> parents = node.getParents();
		
		for (int parentIndex : parents.keySet()){
			if (dag.get(parentIndex).getProcessor() != pNo){
				traversalTime = parents.get(parentIndex);
			}else{
				traversalTime = 0;
			}
			
			if (parentFinishTime < dag.get(parentIndex).getFinishTime() + traversalTime){
				parentFinishTime = dag.get(parentIndex).getFinishTime() + traversalTime;
			}

			traversalTime = 0;
		}
		
		int procFinishTime = procFinishTimes.get(pNo-1);
		
		// return the latest of the two times
		return procFinishTime > parentFinishTime ? procFinishTime : parentFinishTime;
		
	}
	
	/**
	 * Heuristic calculation for a given node
	 * @param node - the node/task
	 * @return integer value for the estimation
	 */
	private int getHValue(NodeTemp node){
		int totalRemainWeight = 0;
		// NOTE: can keep track of num remaining as we do recursion, as private field
		int numRemaining = 0;
		for (int i=0; i<scheduledNodes.size(); i++){
			// ignore the current node's weight
			if (i == node.getIndex()) continue;
			
			if (!scheduledNodes.get(i)){
				totalRemainWeight += dag.get(i).getWeight();
				numRemaining++;
			}
		}
		return totalRemainWeight/(numRemaining*numProc);
		
	}
	
	/**
	 * Determines whether the dependencies for a given node/task has been resolved
	 * @param node - the node/task
	 * @return true if dependencies are resolved
	 */
	private boolean checkDependencies(NodeTemp node){
		
		boolean isResolved = true;

		Map<Integer, Integer> parents = node.getParents();
		
		for (int parentIndex : parents.keySet()){
			isResolved = scheduledNodes.get(parentIndex) && isResolved;
			if (!isResolved) break;
		}
		
		return isResolved;
	}

	@Override
	public ArrayList<Node> getSchedule() {
		return null;
	}
	
	public ArrayList<NodeTemp> getScheduleTemp() {
		return bestSchedule;
	}
}
