package input_processor;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;
import node.Node;

public class InputProcessor implements TaskReader{
		
		private String fileName;
		// Map stores node's name and their index in List_of_nodes
		private HashMap<String, Integer> map= new HashMap<>();	
		// Array stores all nodes/tasks
		private ArrayList<Node> listOfNodes= new ArrayList<Node>();		
		// Array stores starting nodes
		private ArrayList<Boolean> nextAvailableNodes = new ArrayList<Boolean>();/*COMMENT HERE*/
		
		/**
		 * Constructor
		 * @param fileName
		 */
		public InputProcessor(String fileName){
			this.fileName=fileName;
		}
		
		/**
		 * Process input from given file
		 * @throws FileNotFoundException
		 */
		public void processInput() throws FileNotFoundException{
			Scanner scan= new Scanner(new File(fileName));
			int index=0;
			while(scan.hasNext()){
				String line=scan.nextLine();
				// 	Ignore lines that contain "{" or "}" characters
				if(line.contains("{")|| line.contains("}")){			
					continue;
				}
				/*Split each line into parts by tab characters*/
				String[] parts=line.trim().split("\t");
				// Process lines that do not contain ">" characters, representing nodes
				if(!line.contains(">")){
					// Get names and weight of the nodes
					String name = parts[0].trim();
					parts[2]=parts[2].substring(parts[2].lastIndexOf("=")+1,parts[2].length()-3);
					int weight=Integer.parseInt(parts[2].trim());
					if(isNodeNew(name)){
						// 	If the node is not in the list_of_nodes then create a new node and add to the list
						Node n= new Node(name);
						n.setWeight(weight);
						listOfNodes.add(n);
						map.put(name, index);
						addAvaiableNode(n);
						index++;
					}else{
						// If the node is already in the list, assign the weight to the node
						Node n= listOfNodes.get(map.get(name));
						n.setWeight(weight);
					}
				// Process line that represent edges	
				}else{
					// Get the name of the parent node
					String parentName=parts[0].substring(0, parts[0].indexOf(">")-2).trim();
					Node p=null;
					// If the parent node does not exist create a new node, otherwise get the parent node and assign to p
					if(isNodeNew(parentName)){
						p=new Node(parentName);
						listOfNodes.add(p);
						map.put(parentName, index);
						addAvaiableNode(p);
						index++;
					}else{
						p=listOfNodes.get(map.get(parentName));
					}
					// Get the name of the child node
					String childName=parts[0].substring(parts[0].indexOf(">")+1).trim();
					Node c=null;
					// If the child node does not exist create a new node, otherwise get the child node and assign to c
					if(isNodeNew(childName)){
						c=new Node(childName);
						listOfNodes.add(c);
						map.put(childName, index);
						addAvaiableNode(c);
						removeAvailableNode(c);
						index++;
					}else{
						c=listOfNodes.get(map.get(childName));
						removeAvailableNode(c);
					}
					// Get the cost of communication 
					parts[2]=parts[2].substring(parts[2].lastIndexOf("=")+1,parts[2].length()-3);
					int weight=Integer.parseInt(parts[2].trim());
					//add the child node to the parent's list of children
					p.setChildren(c);
					//add the parent to the child's map of parent and 
					c.setParents(p,weight);
				}		
			}
			scan.close();
		}
		
		/**
		 * Check if the node with 'nodeName' is new
		 * @param nodeName
		 * @return boolean
		 */
		private boolean isNodeNew(String nodeName){
			Integer index=map.get(nodeName);
			if(index != null){
				return false;
			}else{
				return true;
			}
		}
		private void addAvaiableNode(Node n){
			int index = map.get(n.getName());
			nextAvailableNodes.add(true);
		}
		
		private void removeAvailableNode(Node n){
			int index = map.get(n.getName());
			nextAvailableNodes.add(index,false);
		}
		
		public HashMap<String,Integer> getMap(){
			return map;
		}
		/**
		 * 
		 */
		@Override
		public ArrayList<Node> getGraph() {
			return listOfNodes;
		}
		/**
		 * 
		 */
		@Override
		public ArrayList<Boolean> getNextAvailableNodes() {
			return nextAvailableNodes;
		}
		/**
		 * 
		 */
		@Override
		public int getNumberOfProcessors() {
			return 0;
		}
		
}
