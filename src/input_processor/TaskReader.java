package input_processor;

import java.util.ArrayList;

import node.Node;

public interface TaskReader {

	public ArrayList<Node> getGraph();
	public ArrayList<Boolean> getNextAvailableNodes();
	public int getNumberOfProcessors();
	
}
