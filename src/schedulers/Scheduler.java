package schedulers;

import java.util.ArrayList;

import node.Node;

public interface Scheduler {
	
	public void schedule();
	public ArrayList<Node> getSchedule();
	
}