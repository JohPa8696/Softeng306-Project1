package schedulers;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Map;
import java.util.Stack;
import java.util.concurrent.PriorityBlockingQueue;

import dag.Dag;
import node.Node;

/**
 * Main implementation of Iterative Deepening A* algorithm
 * 
 * @author Amy and Kelvin
 *
 */
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
	private int totalComputationTime = 0;
	private int idleTime = 0;

	private boolean isVisual = false;
	private Dag visualDag;
	private int refresh_rate = 25000;
	private int refresh_value = 0;

	private Node root = null;

	/**
	 * Constructor creates a deep copy of ArrayList<Node> dag and ArrayList<Boolean> nextAvailableNodes
	 * @param dag - the tasks to be scheduled
	 * @param nextAvailableNodes - a true/false mask for whether a node is available to be expanded
	 * @param numProc - the number of processors
	 */
	public IDAStar(ArrayList<Node> dag, ArrayList<Boolean> nextAvailableNodes,
			int numProc) {

		this.dag = new ArrayList<Node>(dag.size());
		copyData(this.dag, dag);

		this.nextAvailableNodes = new ArrayList<Boolean>(
				nextAvailableNodes.size());
		for (int i = 0; i < nextAvailableNodes.size(); i++) {
			this.nextAvailableNodes.add(nextAvailableNodes.get(i) || false);
		}

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

		// initialise bottom level hValues
		hValues = new ArrayList<Integer>(this.dag.size());
		for (int i = 0; i < this.dag.size(); i++) {
			hValues.add(0);
		}

		// calculate bottom level hValues for every node
		for (Node n : this.dag) {
			if (n.getChildren().isEmpty()) {
				calculateH(n, 0);
			}
		}

		getTotalComputationTime();
	}

	private void getTotalComputationTime() {
		for (int i = 0; i < dag.size(); i++) {
			totalComputationTime += dag.get(i).getWeight();
		}
	}

	/**
	 * Calculates the heuristic for node n in bottom level cost function
	 * @param n - the current node
	 * @param childH - heuristic of child
	 */
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

	/**
	 * Called by computation thread when it is started. It will schedule the
	 * input tasks using buildTree.
	 */
	@Override
	public void schedule() {
		for (int i = 0; i < nextAvailableNodes.size(); i++) {
			if (nextAvailableNodes.get(i)) {

				// get initial f cut off for starting node
				fCutOff = Math.max(getHValue(dag.get(i)),
						(totalComputationTime / numProc));

				// reset procFinishTimes
				for (Stack<Node> s : procFinishTimes) {
					s.clear();
				}
				
				// isSolved is checked by all threads
				while (!isSolved) {
					if (isVisual) {
						if (root == null) {
							visualDag.setRoot(dag.get(i));
							root = dag.get(i);
						} else if (!dag.get(i).getName().equals(root.getName())) {
							visualDag.unRoot(root);
							visualDag.setRoot(dag.get(i));
							root = dag.get(i);
						}
					}
					
					buildTree(dag.get(i), 1);
					
					// a thread should only try to get a new fValue if it isn't solved
					while (!isSolved) {
						try {
							fCutOff = fCutOffQueue.take();
							if (!finishedFCutOffList.contains(fCutOff))
								break;
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
		// each thread should be constantly check if a solution has been found
		// by any thread
		// if solved and fcutoff is greater, it wont have optimal solution, but
		// if fcutoff is less then it should be allowed to continue and find a
		// solution for its own fcutoff (if it exists)
		if (isSolved && fCutOff > stopCutOff)
			return true;

		int nodeStartTime = getStartTime(node, pNo);
		int currentIdleTime = 0;

		int g = nodeStartTime;
		int h = getHValue(node);
		int fBottomLevel = g + h;

		// current idle time is just for this iteration

		if (procFinishTimes.get(pNo - 1).isEmpty()) {
			currentIdleTime = nodeStartTime;
		} else {
			currentIdleTime = nodeStartTime
					- procFinishTimes.get(pNo - 1).peek().getFinishTime();
		}
		idleTime += currentIdleTime;
		int fIdle = (totalComputationTime + idleTime) / numProc;

		// use the max of either bottom level f or idle time f as our cost
		// function
		int f = Math.max(fBottomLevel, fIdle);

		// if greater than cut off, we only store the next cut off, no need to
		// actually traverse it
		if (f > fCutOff) {
			if (!fCutOffQueue.contains(f)) {
				// add f cut off values to a priority queue
				fCutOffQueue.put(f);
			}
			// subtract currentIdleTime from totalIdleTime
			idleTime -= currentIdleTime;
			return false;

		} else {

			// If we need to visualize update the visuals
			if (isVisual && node.getName() != null) {
				node.incFrequency();
				if (refresh_value >= refresh_rate) {
					visualDag.update(node);
					refresh_value = 0;
				} else if (isVisual) {
					refresh_value++;

				}
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

				// only copy if current solution has better finish time (in case
				// of multiple entry points)
				if (bestFinishTime > currentFinishTime || bestFinishTime == -1) {
					bestFinishTime = currentFinishTime;
					visualCopy(bestSchedule, dag);
				}

				// set is solved immediately so all threads know a solution is
				// found
				isSolved = true;
				return isSolved;
			} else {
				// begin recursion
				boolean isSuccessful = false;
				for (int i = 0; i < nextAvailableNodes.size(); i++) {
					if (nextAvailableNodes.get(i)) {

						// used for pruning by avoiding duplicate node expansion
						// for empty processors
						boolean zeroChecked = false;

						for (int j = 1; j <= numProc; j++) {

							if (zeroChecked)
								continue;

							// empty processors found, set zeroChecked to true
							// and expand
							if (procFinishTimes.get(j - 1).isEmpty())
								zeroChecked = true;

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

				// subtract current idle time from total idle time
				idleTime -= currentIdleTime;
				return isSuccessful;
			}
		}

	}

	/**
	 * Calculates the earliest start time for a given node and process number
	 * The earliest start time will be the latest of two possible times: 
	 * - time based on parent tasks 
	 * - time based on the current processor task
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

	/**
	 * Creates a copy of ArrayList<Node> from target to source, using correct
	 * object references
	 * 
	 * @param target
	 * @param source
	 */
	private void copyData(ArrayList<Node> target, ArrayList<Node> source) {
		target.clear();
		for (int i = 0; i < source.size(); i++) {
			target.add(i, new Node(source.get(i)));
		}

		for (int i = 0; i < source.size(); i++) {
			for (Node parent : source.get(i).getParents().keySet()) {
				target.get(i).setParents(target.get(parent.getIndex()),
						source.get(i).getParents().get(parent));
			}

			for (Node child : source.get(i).getChildren()) {
				target.get(i).setChildren(target.get(child.getIndex()));
			}
		}
	}

	/**
	 * Creates a copy of ArrayList<Node> ignoring references to other nodes
	 * 
	 * @param target
	 * @param source
	 */
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

	public Dag getDag() {
		return visualDag;
	}

}
