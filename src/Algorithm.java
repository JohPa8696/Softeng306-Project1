import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;


/**
 * Class: Algorithm
 * Description: Create ida* algorithm to search through the tree and find out the optimal path using the pseudocode. (Github Wiki)
 * Variable: ArrayList<Node> availableNodeList (to store all available nodes)
 * @Author Jack Wong
 * Date: 04/08/16
 */
public class Algorithm {
	private ArrayList<Node> availableNodeList = new ArrayList<Node>();
	
	/**
	 * @param node
	 * @param assignedProc
	 * This method is used to build a search tree.
	 */
	public void buildTree(Node node, int assignedProc){
		double g = 0;
		double h = 0;
		double f = g+h;
		
	}
	
	
	/**
	 * @param node
	 * @param assignedProc
	 * @return latestStartTime
	 * This method is to calculate the latest start time in the processor.
	 */
	public int getLatestStartTime(Node node, int assignedProc){
		int latestStartTimeFromParent = 0;
		int traversalTime = 0;
		HashMap<Node, Integer> parent = node.getParents();
		for (HashMap.Entry<Node, Integer> entry : parent.entrySet()){
			if (entry.getKey().getProcessor() != assignedProc){
			
			}
		}
		
		return latestStartTimeFromParent;
	}
	
	/**
	 * @param node
	 * This method is to add the available node into an array list.
	 */
	public void addAvailable(Node node){
		availableNodeList.add(node);
	}
	
	
	/**
	 * @param node
	 * This method is to remove the node from the Available list.
	 */
	public void removeFromAvailable(Node node){
		int i = 0;
		for (Node availableNode:availableNodeList){
			if (node.getIndex() == availableNode.getIndex()){
				availableNodeList.remove(i);
				break;
			}else{
				i++;
			}
		}
	}
}
