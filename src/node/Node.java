package node;

import java.util.ArrayList;
import java.util.HashMap;

public class Node {
		
		private String name;
		private int weight=0;
		private int index;											/* Index of node in the main array list*/
		private HashMap<Node,Integer> parents= new HashMap<>();		/* Parents node and cost of communication from parent nodes*/
		private ArrayList<Node> children= new ArrayList<Node>();    /* Contains reference to children node*/
		private int processor=0;										/* The processor that the node(i.e task) is assigned to*/
		private int finishTime;										/* Finish time of the node*/
		private int startTime=0;										/* Time when node is being executed*/
		
		/**
		 * 
		 */
		public Node(String name){
			this.name=name;
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
		public HashMap<Node, Integer> getParents() {
			return parents;
		}
		public void setParents(Node parent, Integer weight) {
			this.parents.put(parent, weight);
		}
		public ArrayList<Node> getChildren() {
			return children;
		}
		public void setChildren(Node child) {
			this.children.add(child);
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