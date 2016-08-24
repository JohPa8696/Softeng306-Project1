package schedulers;

import java.util.ArrayList;
import java.util.Map;
import java.util.Set;

import dag.Dag;

import node.Node;

/**
 * Class: SimpleScheduler Description: Creates a schedule for a single processor
 * only.
 * 
 * @author vincent
 */
public class SimpleScheduler implements Scheduler {
	ArrayList<Node> nodes = new ArrayList<Node>(); // The input array
	static ArrayList<Node> schedule = new ArrayList<Node>();// The output array

	/**
	 * Constructor, takes in a list of nodes.
	 * 
	 * @param nodes
	 */
	public SimpleScheduler(ArrayList<Node> nodes) {
		this.nodes = nodes;
	}

	/**
	 * This is the entry of the permutation method Creates a valid schedule for
	 * a single processor.
	 * 
	 * @param nodes
	 * @return schedule
	 */
	public static ArrayList<Node> permutation(ArrayList<Node> nodes) {
		permutation(schedule, nodes);
		return schedule;
	}

	/**
	 * The recursive part of the permutation method Creates a valid schedule for
	 * a single processor.
	 * 
	 * @param schedule
	 * @param nodes
	 * @return boolean
	 */
	private static boolean permutation(ArrayList<Node> schedule,
			ArrayList<Node> nodes) {
		// Base case
		if (nodes.isEmpty()) {
			return true;
		} else {
			// Selects a node from the list of nodes
			for (int i = 0; i < nodes.size(); i++) {
				Node n = nodes.get(i);
				if (!isValid(n, schedule)) {
					// Skips if the nodes parents/dependencies are not present
					continue;
				} else if (schedule.size() == 0) {
					n.setStartTime(0);
					n.setProcessor(1);
				} else {
					n.setStartTime(schedule.get(schedule.size() - 1)
							.getStartTime()
							+ schedule.get(schedule.size() - 1).getWeight());
					n.setProcessor(1);
				}
				schedule.add(n);
				nodes.remove(i);
				if (permutation(schedule, nodes)) {
					return true;
				}
				nodes.add(i, schedule.get(schedule.size() - 1));
				schedule.remove(schedule.size() - 1);
			}
		}
		return false;
	}

	/**
	 * Checks if a schedule contains all the parents/dependencies for a child
	 * node.
	 * 
	 * @return boolean
	 */
	private static boolean isValid(Node n, ArrayList<Node> schedule) {
		final Set<Map.Entry<Node, Integer>> entries = n.getParents().entrySet();
		if (entries.size() == 0) {
			return true;
		}
		for (Map.Entry<Node, Integer> entry : entries) {
			Node parent = entry.getKey();
			if (schedule.indexOf(parent) < 0) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Returns a schedule.
	 */
	public ArrayList<Node> getSchedule() {
		return SimpleScheduler.schedule;
	}

	/**
	 * Produces a schedule from the list of nodes.
	 */
	@Override
	public void schedule() {
		SimpleScheduler.permutation(nodes);
	}

	@Override
	public void setVisual(Dag visual) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		
	}

}
