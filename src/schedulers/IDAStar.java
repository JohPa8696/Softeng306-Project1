package schedulers;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Map;
import java.util.Stack;
import java.util.concurrent.PriorityBlockingQueue;

import dag.Dag;
import node.Node;

public class IDAStar implements Scheduler {

	private ArrayList<Node> dag;
	private ArrayList<Boolean> nextAvailableNodes;
	private ArrayList<Boolean> scheduledNodes;
	private ArrayList<Stack<Node>> procFinishTimes;
	private ArrayList<Integer> hValues;

	private static volatile PriorityBlockingQueue<Integer> fCutOffQueue = new PriorityBlockingQueue<Integer>();
	private static volatile ArrayList<Integer> finishedFCutOffList = new ArrayList<Integer>();
	
	public static volatile boolean isSolved = false;
	private static volatile int stopCutOff = -1;
	
	private int numProc;
	private int fCutOff = 0;
	private ArrayList<Node> bestSchedule;
	private int bestFinishTime = -1;

	private boolean isVisual = false;
	private Dag visualDag;
	private int refresh_rate = 25000;
	private int refresh_value=0;

	public IDAStar(ArrayList<Node> dag, ArrayList<Boolean> nextAvailableNodes,
			int numProc) {
		
		this.dag = new ArrayList<Node>(dag.size());
		copyData(this.dag, dag);
		//this.dag = dag;
		
		this.nextAvailableNodes = new ArrayList<Boolean>(nextAvailableNodes.size());
		for (int i = 0; i < nextAvailableNodes.size(); i++){
			this.nextAvailableNodes.add(nextAvailableNodes.get(i) || false);
		}
		
		//this.nextAvailableNodes = nextAvailableNodes;
		this.numProc = numProc;

		scheduledNodes = new ArrayList<Boolean>(this.dag.size());

		for (int i = 0; i < this.dag.size(); i++) {
			scheduledNodes.add(false);
		}

		procFinishTimes = new ArrayList<Stack<Node>>(this.numProc);
		for (int i = 0; i < this.numProc; i++) {
			procFinishTimes.add(new Stack<Node>());
		}

		bestSchedule = new ArrayList<>(this.dag.size());

		hValues = new ArrayList<Integer>(this.dag.size());
		for (int i = 0; i < this.dag.size(); i++) {
			hValues.add(0);
		}

		for (Node n : this.dag) {
			if (n.getChildren().isEmpty()) {
				calculateH(n, 0);
			}
		}
		
		for(int i=0; i< hValues.size();i++){
			System.out.println("heuristic of "+dag.get(i).getName() +" is " + hValues.get(i));
		}
	}
	
	private void calculateH(Node n, int childH) {
		int hTemp = n.getWeight() + childH;

		// want the maximum H
		if (hTemp < hValues.get(n.getIndex()))
			return;
		// otherwise set values
		hValues.set(n.getIndex(), hTemp);

		// if no parents, it won't go thru this
		for (Node parent : n.getParents().keySet()) {
			calculateH(parent, hTemp);
		}

	}

	@Override
	public void run() {
		schedule();
	}

	@Override
	public void schedule() {
		for (int i = 0; i < nextAvailableNodes.size(); i++) {
			if (nextAvailableNodes.get(i)) {

				// get initial f cut off for starting node
				fCutOff = getHValue(dag.get(i));

				// reset procFinishTimes
				for (Stack<Node> s : procFinishTimes) {
					s.clear();
				}

				while (!isSolved) {
					System.out.println("fCutOff = " + fCutOff);
					buildTree(dag.get(i), 1);
					while (true){
						try {
							fCutOff = fCutOffQueue.take();
							if (!finishedFCutOffList.contains(fCutOff)) break;
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
					finishedFCutOffList.add(fCutOff);
				}
			}
		}
	}

	/**
	 * Recursive method to build and traverse the tree for determining the
	 * schedule. It depends on a f cut-off value, which increments as each task
	 * builds.
	 * 
	 * @param node
	 *            - the current node/task of the traversal
	 * @param pNo
	 *            - the assigned process number for the node/task
	 * @return true if a schedule has been made
	 */
	private boolean buildTree(Node node, int pNo) {
		if (isSolved && fCutOff > stopCutOff)
			return true;

		int nodeStartTime = getStartTime(node, pNo);

		int g = nodeStartTime /* + node.getWeight() */;
		int h = getHValue(node);
		int f = g + h;

		// if greater than cut off, we only store the next cut off, no need to
		// actually traverse it
		if (f > fCutOff) {
			if (!fCutOffQueue.contains(f)){
			// add f cut off values to a priority queue
				fCutOffQueue.put(f);
			}
			return false;

		} else {

			// If we need to visualize update the visuals
			if (isVisual && node.getName() != null) {
				node.incFrequency();
				if(refresh_value >= refresh_rate){
					visualDag.update(node);
					refresh_value=0;
				}else if(isVisual){
					refresh_value++;
				}
				//System.out.println("The added node is " + node.getName()
						//+ " and the frequency is " + node.getFrequency()+" and the processor is "+node.getProcessor());
			}

			node.setStartTime(nodeStartTime);
			node.setFinishTime(g + node.getWeight());
			node.setProcessor(pNo);

			scheduledNodes.set(node.getIndex(), true);

			procFinishTimes.get(pNo - 1).push(node);
			nextAvailableNodes.set(node.getIndex(), false);

			for (Node child : node.getChildren()) {
				// check dependencies of children, add them to available if they
				// can be visited
				boolean isResolved = checkDependencies(child);
				if (isResolved) {
					nextAvailableNodes.set(child.getIndex(), true);
				}
			}

			boolean isAvailable = checkAnyAvailable();

			// if the current node is a leaf (i.e. an ending task) AND there are
			// no more tasks
			if (node.getChildren().isEmpty() && !isAvailable) {

				// the last node to be scheduled is not necessarily the last to
				// finish
				int currentFinishTime = 0;
				for (Stack<Node> proc : procFinishTimes) {
					if (proc.isEmpty())
						continue;
					int endTime = proc.peek().getFinishTime();
					if (currentFinishTime < endTime) {
						currentFinishTime = endTime;
					}
				}

				// only copy if current solution has better finish time
				if (bestFinishTime > currentFinishTime || bestFinishTime == -1) {
					bestFinishTime = currentFinishTime;
					visualCopy(bestSchedule, dag);
				}

				isSolved = true;
				return isSolved;
			} else {
				// begin recursion
				boolean isSuccessful = false;
				for (int i = 0; i < nextAvailableNodes.size(); i++) {
					if (nextAvailableNodes.get(i)) {
						for (int j = 1; j <= numProc; j++) {
							isSuccessful = buildTree(dag.get(i), j);
							if (isSuccessful)
								break;
						}
						if (isSuccessful)
							break;
					}
				}

				scheduledNodes.set(node.getIndex(), false);

				// backtrack the procFinishTime
				procFinishTimes.get(pNo - 1).pop();

				// set child availability to false
				for (Node child : node.getChildren()) {
					nextAvailableNodes.set(child.getIndex(), false);
				}

				// set current node to available as we traverse back up
				nextAvailableNodes.set(node.getIndex(), true);

				return isSuccessful;
			}
		}
	}

	/**
	 * Calculates the earliest start time for a given node and process number
	 * The earliest start time will be the latest of two possible times: - time
	 * based on parent tasks - time based on the current processor task
	 * 
	 * @param node
	 *            - the node/task
	 * @param pNo
	 *            - process number
	 * @return integer of the earliest start time
	 */
	private int getStartTime(Node node, int pNo) {
		int parentFinishTime = 0;
		int traversalTime = 0;
		Map<Node, Integer> parents = node.getParents();

		for (Node parent : parents.keySet()) {
			if (parent.getProcessor() != pNo) {
				traversalTime = parents.get(parent);
			} else {
				traversalTime = 0;
			}

			if (parentFinishTime < parent.getFinishTime() + traversalTime) {
				parentFinishTime = parent.getFinishTime() + traversalTime;
			}

			traversalTime = 0;
		}

		int procFinishTime;
		if (!procFinishTimes.get(pNo - 1).isEmpty()) {
			procFinishTime = procFinishTimes.get(pNo - 1).peek()
					.getFinishTime();
		} else {
			procFinishTime = 0;
		}

		// return the latest of the two times
		return procFinishTime > parentFinishTime ? procFinishTime
				: parentFinishTime;

	}

	/**
	 * Heuristic calculation for a given node
	 * 
	 * @param node
	 *            - the node/task
	 * @return integer value for the estimation
	 */
	private int getHValue(Node node) {
		return hValues.get(node.getIndex());
	}

	/**
	 * Determines whether the dependencies for a given node/task has been
	 * resolved
	 * 
	 * @param node
	 *            - the node/task
	 * @return true if dependencies are resolved
	 */
	private boolean checkDependencies(Node node) {

		boolean isResolved = true;

		Map<Node, Integer> parents = node.getParents();

		for (Node parent : parents.keySet()) {
			isResolved = scheduledNodes.get(parent.getIndex()) && isResolved;
			if (!isResolved)
				break;
		}

		return isResolved;
	}

	private boolean checkAnyAvailable() {
		boolean isAvailable = false;
		for (Boolean b : nextAvailableNodes) {
			isAvailable = isAvailable || b;
			if (isAvailable)
				break;
		}

		return isAvailable;
	}

	private void copyData(ArrayList<Node> target, ArrayList<Node> source) {
		target.clear();
		for (int i = 0; i < source.size(); i++) {
			target.add(i, new Node(source.get(i)));
		}
		
		for (int i = 0; i < source.size(); i++) {
			for (Node parent: source.get(i).getParents().keySet()){
				target.get(i).setParents(target.get(parent.getIndex()), source.get(i).getParents().get(parent));
			}
			
			for (Node child: source.get(i).getChildren()){
				target.get(i).setChildren(target.get(child.getIndex()));
			}
		}
	}
	
	private void visualCopy(ArrayList<Node> target, ArrayList<Node> source) {
		target.clear();
		for (int i = 0; i < source.size(); i++) {
			target.add(i, new Node(source.get(i)));

			if (isVisual) {
				visualDag.updateProcGraph(source.get(i));
			}
		}
		
	}

	@Override
	public ArrayList<Node> getSchedule() {
		if (isVisual) {
			for (Node node : bestSchedule) {
				visualDag.changeNodeColor(node, Color.YELLOW);
			}
		}
		return bestSchedule;
	}

	public int getFinishTime() {
		return bestFinishTime;
	}

	public void setVisual(Dag visualDag) {
		this.isVisual = true;
		this.visualDag = visualDag;
	}
	
	public Dag getDag(){
		return visualDag;
	}

}
