package node;

import java.util.ArrayList;
import java.util.HashMap;

/**
 *	Just a temporary Node class that doesn't store the actual Node objects for parents and children
 *	Need it so that IDA* can copy the optimal schedule without referencing old, changing node objects 
 * @author Kelvin
 *
 */
public class NodeTemp {

	private String name;
	private int weight = -1;
	// TODO: For the index, if is not set during input processing, then need a map saying what name maps to what index
	private int index = -1;	 /* Index of node in the main array list*/
	private HashMap<Integer,Integer> parents= new HashMap<>();		/* Parents node (index) and cost of communication from parent nodes*/
	private ArrayList<Integer> children= new ArrayList<>();    /* Contains reference to children node (index)*/
	private int processor = -1;										/* The processor that the node(i.e task) is assigned to*/
	private int finishTime = -1;										/* Finish time of the node*/
	private int startTime = -1;
	
	public NodeTemp(String name) {
		this.name = name;
	}
	
	public NodeTemp(NodeTemp node){
		this.name = node.getName();
		this.weight = node.getWeight();
		this.index = node.getIndex();
		//this.parents = null;
		this.parents = node.getParents();
		//this.children = null;
		this.children = node.getChildren();
		this.processor = node.getProcessor();
		this.finishTime = node.getFinishTime();
		this.startTime = node.getStartTime();
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getWeight() {
		return weight;
	}
	public void setWeight(int weight) {
		this.weight = weight;
	}
	public int getIndex() {
		return index;
	}
	public void setIndex(int index) {
		this.index = index;
	}
	public HashMap<Integer, Integer> getParents() {
		return parents;
	}
	public void setParents(HashMap<Integer, Integer> parents) {
		this.parents = parents;
	}
	public ArrayList<Integer> getChildren() {
		return children;
	}
	public void setChildren(ArrayList<Integer> children) {
		this.children = children;
	}
	public int getProcessor() {
		return processor;
	}
	public void setProcessor(int processor) {
		this.processor = processor;
	}
	public int getFinishTime() {
		return finishTime;
	}
	public void setFinishTime(int finishTime) {
		this.finishTime = finishTime;
	}
	public int getStartTime() {
		return startTime;
	}
	public void setStartTime(int startTime) {
		this.startTime = startTime;
	}	
	
}
