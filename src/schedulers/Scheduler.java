package schedulers;

import java.util.ArrayList;

import dag.Dag;

import node.Node;

public interface Scheduler {
	
	public void schedule();
	public ArrayList<Node> getSchedule();
	public void setVisual(Dag visual);
	
}