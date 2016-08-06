package input_processor;

import java.util.ArrayList;

import node.Node;

public interface GraphReader {

	public ArrayList<Node> getGraph();
	public ArrayList<Boolean> getNextAvailableNodes();
	
}
