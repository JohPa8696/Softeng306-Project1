import java.util.ArrayList;

public class Node {
		private String name;
		private int weight;
		private ArrayList<Node> children= new ArrayList<Node> ();
		private ArrayList<Node> dependencies= new ArrayList<Node>();
		private ArrayList<Integer> weightOfDepencies= new ArrayList<Integer>();
		private int processor;
		private int finishTime;
		private int startTime;
		
		public Node(String name,int weight){
			this.weight=weight;
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

		public ArrayList<Node> getChildren() {
			return children;
		}

		public void setChildren(Node child) {
			this.children.add(child);
		}

		public ArrayList<Node> getDependencies() {
			return dependencies;
		}

		public void setDependencies(Node dependency) {
			this.dependencies.add(dependency);
		}
		
		public ArrayList<Integer> getWeightOfDepencies() {
			return weightOfDepencies;
		}

		public void setWeightOfDepencies(int weight) {
			this.weightOfDepencies.add(weight);
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
