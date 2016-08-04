import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;


public class Algorithm {
	private ArrayList<Node> availableNodeList = new ArrayList<Node>();
	
	public void buildTree(Node node, int assignedProc){
		double g = 0;
		double h = 0;
		double f = g+h;
		
	}
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
	public void addAvailable(Node node){
		availableNodeList.add(node);
	}
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
