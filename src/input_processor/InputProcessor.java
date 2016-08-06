package input_processor;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;
import node.Node;

public class InputProcessor {
		
		private String fileName;
		private HashMap<String, Integer> map= new HashMap<>();					/*COMMENT HERE*/
		private ArrayList<Node> listOfNodes= new ArrayList<Node>();				/*COMMENT HERE*/
		private ArrayList<Node> listOfAvailableNodes= new ArrayList<Node>();	/*COMMENT HERE*/
		
		public InputProcessor(String fileName){
			this.fileName=fileName;
		}
		
		public void processInput() throws FileNotFoundException{
			Scanner scan= new Scanner(new File(fileName));
			int index=0;
			while(scan.hasNext()){
				String line=scan.nextLine();
				//System.out.println(line);
				// 	Ignore lines that contain "{" or "}" characters
				if(line.contains("{")|| line.contains("}")){			
					continue;
				}
				/*COMMENT HERE*/
				String[] parts=line.trim().split("\t");
				//System.out.println(parts.length);
				if(!line.contains(">")){
					String name = parts[0].trim();
					parts[2]=parts[2].substring(parts[2].lastIndexOf("=")+1,parts[2].length()-3);
					int weight=Integer.parseInt(parts[2].trim());
					// Check if the node exists
					if(isNodeNew(name)){
						Node n= new Node(name);
						n.setWeight(weight);
						listOfNodes.add(n);
						map.put(name, index);
						addAvaiableNode(n);
						index++;
					}else{
						Node n= listOfNodes.get(map.get(name));
						n.setWeight(weight);
					}
				}else{
					String parentName=parts[0].substring(0, parts[0].indexOf(">")-2).trim();
					Node p=null;
					// If the parent node does not exist create a new node, otherwise get the parent node and assign to p
					if(isNodeNew(parentName)){
						p=new Node(parentName);
						listOfNodes.add(p);
						map.put(parentName, index);
						addAvaiableNode(p);
						//System.out.println();
						index++;
					}else{
						p=listOfNodes.get(map.get(parentName));
					}
					String childName=parts[0].substring(parts[0].indexOf(">")+1).trim();
					// If the child node does not exist create a new node, otherwise get the child node and assign to c
					Node c=null;
					if(isNodeNew(childName)){
						c=new Node(childName);
						listOfNodes.add(c);
						map.put(childName, index);
						index++;
					}else{
						c=listOfNodes.get(map.get(childName));
						removeAvailableNode(c);
					}
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
			for(Node availNode : listOfAvailableNodes){
				if (availNode.getName().equals(n.getName())){
					return;
				}
				
			}
			listOfAvailableNodes.add(n);
		}
		
		private void removeAvailableNode(Node n){
			for(Node availNode : listOfAvailableNodes){
				if (availNode.getName().equals(n.getName())){
					listOfAvailableNodes.remove(availNode);
					return;
				}
				
			}
		}
		
		public ArrayList<Node> getListOfAvailableNodes(){
			return listOfAvailableNodes;
		}
		
		public ArrayList<Node> getListOfNodes(){
			return listOfNodes;
		}
		
		public HashMap<String,Integer> getMap(){
			return map;
		}
		
}
