import java.util.ArrayList;


public class Algorithm {
	private ArrayList<Node> availableNodeList = new ArrayList<Node>();
	
	public void buildTree(Node node, int assignedProc){
		double g = 0;
		double h = 0;
		double f = g+h;
		
	}
	public int getLatestStartTime(){
		int latestStartTimeFromParent = 0;
		int traversalTime = 0;
		
		return latestStartTimeFromParent;
	}
	public void addAvailable(Node node){
		availableNodeList.add(node);
	}
	public void removeFromAvailable(Node node){

	}
}
