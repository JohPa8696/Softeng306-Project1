package input_processor;

import java.util.ArrayList;

import node.Node;

/**
 * The TaskReader interface ensures that the input file returns the input graph
 * and available nodes for the schedulers
 * 
 */
public interface TaskReader {
	/**
	 * Returns an input graph in an arraylist
	 * 
	 * @return inputGraph
	 */
	public ArrayList<Node> getGraph();

	/**
	 * Returns an arraylist where each index represents whether a node is
	 * available or not
	 * 
	 * @return availableNodes
	 */
	public ArrayList<Boolean> getNextAvailableNodes();

	/**
	 * Returns the amount of processors which the inputGraph can run on
	 * 
	 * @return numProc
	 */
	public int getNumberOfProcessors();

}
