package schedulers;

import java.util.ArrayList;
import java.util.Map;
import java.util.Set;

import node.Node;

public class SimpleScheduler implements Scheduler {
	ArrayList<Node> nodes;
	static ArrayList<Node> schedule = new ArrayList<Node>();
	
	public SimpleScheduler(ArrayList<Node> nodes){
		this.nodes=nodes;
	}
	
	public static ArrayList<Node> permutation(ArrayList<Node> nodes) { 
		permutation(schedule, nodes); 
		return schedule;
	}
	
	private static boolean permutation(ArrayList<Node> schedule, ArrayList<Node> nodes) {
		if (nodes.isEmpty()) {
	    	for(Node n: schedule){
            	System.out.print(n.getName() + " ");
            }System.out.println();
            return true;
	    }
	    else {
	        for (int i = 0; i < nodes.size(); i++){
	        	Node n = nodes.get(i);
	        	
	        	if (!isValid(n, schedule)){
	        		continue;
	        	}else if(schedule.size()==0){
	        		n.setStartTime(0);
	        	}else{
	        		n.setStartTime(schedule.get(schedule.size()-1).getStartTime()+schedule.get(schedule.size()-1).getWeight());
	        	}
	        	schedule.add(n);
	            nodes.remove(i);
	            if(permutation(schedule, nodes)){
	            	return true;
	            }
	            nodes.add(i,schedule.get(schedule.size()-1));
	            schedule.remove(schedule.size()-1);
			}
	    }
		return false;
	}
	private static boolean isValid(Node n, ArrayList<Node> schedule){
		final Set<Map.Entry<Node, Integer>> entries = n.getParents().entrySet();
		if (entries.size()==0){
			return true;
		}
		for(Map.Entry<Node, Integer> entry : entries){
			Node parent = entry.getKey();
			if(schedule.indexOf(parent) < 0){
				return false;
			}
		}
		return true;
	}
	public ArrayList<Node> getSchedule(){
		return SimpleScheduler.schedule;
	}
	
	@Override
	public void schedule() {
		SimpleScheduler.permutation(nodes);
	}
	
}
