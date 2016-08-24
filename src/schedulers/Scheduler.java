package schedulers;

import java.util.ArrayList;

import dag.Dag;

import node.Node;

/**
 * Scheduler is an interface which methods should be implemented by all
 * schedulers
 * 
 */
public interface Scheduler extends Runnable {
	/**
	 * Method which runs an algorithm for producing an optimal schedule
	 */
	public void schedule();

	/**
	 * This method returns an optimal schedule
	 * 
	 * @return schedule
	 */
	public ArrayList<Node> getSchedule();

	/**
	 * Gives the scheduler a Graph Stream Graph for visualization purposes
	 * 
	 * @param visual
	 */
	public void setVisual(Dag visual);

}