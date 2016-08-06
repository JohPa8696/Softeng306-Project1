package schedulers;

import java.util.ArrayList;

import node.Node;

public class IDAStar implements Scheduler {

	private ArrayList<Node> dag = new ArrayList<Node>();
	private ArrayList<Boolean> nextAvailableNodes = new ArrayList<Boolean>();

	public IDAStar(ArrayList<Node> dag, ArrayList<Boolean> nextAvailableNodes) {
		this.dag = dag;
		this.nextAvailableNodes = nextAvailableNodes;
	}

	@Override
	public void schedule() {
		for (int i = 0; i < nextAvailableNodes.size(); i++) {
			if (nextAvailableNodes.get(i)) {
				buildTree(dag.get(i), 1);
			}
		}
	}

	private void buildTree(Node node, int pNo) {

	}

}
