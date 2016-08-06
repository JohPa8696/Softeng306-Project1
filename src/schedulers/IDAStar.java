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

	public IDAStar(ArrayList<Node> dag, ArrayList<Boolean> nextAvailableNodes, int noProc) {
		this.dag = dag;
		this.nextAvailableNodes = nextAvailableNodes;

		scheduledNodes = new ArrayList<Boolean>(dag.size());
		Collections.fill(scheduledNodes, Boolean.FALSE);
		
		procFinishTimes = new ArrayList<Integer>(noProc);
	}

	@Override
	public void schedule() {
		for (int i = 0; i < nextAvailableNodes.size(); i++) {
			if (nextAvailableNodes.get(i)) {

				// get initial f cut off for starting node
				int fCutoff = dag.get(i).getWeight() + getHValue(dag.get(i));
				
				boolean isSolved = false;
				while (!isSolved){
					isSolved = buildTree(dag.get(i), 1, fCutoff);
				}
			}
		}
	}

	private boolean buildTree(Node node, int pNo, int fCutoff) {
		int g = getStartTime(node, pNo) + node.getWeight();
		int h = getHValue(node);
		int f = g+h;
		
		return true;
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
