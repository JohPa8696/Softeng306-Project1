package schedulers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;

import node.Node;

public class IDAStar implements Scheduler {

	private ArrayList<Node> dag;
	private ArrayList<Boolean> nextAvailableNodes;
	private ArrayList<Boolean> scheduledNodes;
	private ArrayList<Integer> procFinishTimes;
	private int numProc;
	private int fCutOff = 0;
	private int nextCutOff = -1;

	public IDAStar(ArrayList<Node> dag, ArrayList<Boolean> nextAvailableNodes, int numProc) {
		this.dag = dag;
		this.nextAvailableNodes = nextAvailableNodes;

		scheduledNodes = new ArrayList<Boolean>(dag.size());
		Collections.fill(scheduledNodes, Boolean.FALSE);
		
		this.numProc = numProc;
		procFinishTimes = new ArrayList<Integer>(numProc);
	}

	@Override
	public void schedule() {
		for (int i = 0; i < nextAvailableNodes.size(); i++) {
			if (nextAvailableNodes.get(i)) {

				// get initial f cut off for starting node
				fCutOff= dag.get(i).getWeight() + getHValue(dag.get(i));
				
				boolean isSolved = false;
				while (!isSolved){
					isSolved = buildTree(dag.get(i), 1);
				}
			}
		}
	}

	private boolean buildTree(Node node, int pNo) {
		int nodeStartTime = getStartTime(node, pNo);
		int g = nodeStartTime + node.getWeight();
		int h = getHValue(node);
		int f = g+h;
		
		// if greater than cut off, we only store the next cut off, no need to actually traverse it
		if (f > fCutOff ){
			if(f < nextCutOff || f == -1){
				nextCutOff = f;
			}
			return false;
		} else {
			// TODO: add node to trail/path
			node.setStartTime(nodeStartTime);
			node.setFinishTime(g);
			node.setProcessor(pNo);
			
			procFinishTimes.set(pNo-1, g);
			
			// TODO: remove from available
			
			if(node.getChildren().isEmpty() /*TODO: and no more avaiable, and trail size = num of nodes*/){
				// TODO: copy solution to somewhere
				return true;
			} else {
				for (Node child:node.getChildren()){
					// TODO: check dependencies and add them to available
				}
				
				boolean isSuccessful = false;
				
				for (int i = 0;i<nextAvailableNodes.size();i++){
					if (nextAvailableNodes.get(i)){
						for (int j = 1; j<=numProc; j++){
							isSuccessful = buildTree(dag.get(i), j);
						}
					}
				}
				
				// TODO: remove node from trail/path
				
				// TODO: reset start and finish times and proc number of this node...

				for (Node child:node.getChildren()){
					// TODO: set child availability to false
				}
				
				// TODO: set current node to available
				
				return isSuccessful;
			}
		}
	}

	private int getStartTime(Node node, int pNo){
		int parentFinishTime = 0;
		int traversalTime = 0;
		Map<Node, Integer> parents = node.getParents();
		
		for (Node parent : parents.keySet()){
			if (parent.getProcessor()  != pNo){
				traversalTime = parents.get(parent);
			}else{
				traversalTime = 0;
			}
			
			if (parentFinishTime < parent.getFinishTime() + traversalTime){
				parentFinishTime = parent.getFinishTime() + traversalTime;
			}

			traversalTime = 0;
		}
		
		int procFinishTime = procFinishTimes.get(pNo-1);
		
		return procFinishTime > parentFinishTime ? procFinishTime : parentFinishTime;
		
	}
	
	private int getHValue(Node node){
		int totalRemainWeight = 0;
		// TODO:can get num remaining by subtracting trail size by dag size
		int numRemaining = 0;
		for (int i=0; i<scheduledNodes.size(); i++){
			if (!scheduledNodes.get(i)){
				totalRemainWeight += dag.get(i).getWeight();
				numRemaining++;
			}
		}
		return totalRemainWeight/numRemaining;
		
	}
}
