package input_processor;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

import utils.InvalidArgumentException;
import utils.StringUtils;
import node.Node;

/**
 * 
 * @author John + Vincent
 */
public class InputProcessor implements TaskReader{
		
		private String fileName;
		private int numProc=3;
		private int numThread=1;
		private boolean visualisation=true;
		private String outputFileName=null;
		// Map stores node's name and their index in List_of_nodes
		private HashMap<String, Integer> map= new HashMap<>();	
		// Array stores all nodes/tasks
		private ArrayList<Node> listOfNodes= new ArrayList<Node>();		
		// Array stores starting nodes
		private ArrayList<Boolean> nextAvailableNodes = new ArrayList<Boolean>();/*COMMENT HERE*/
		
		/**
		 * Constructor also handle command line arguments
		 * @param args
		 */
		public InputProcessor(String[] args) throws InvalidArgumentException{
			
			// Check if the input exists
			if( new File(args[0]).isFile()){
				this.fileName=args[0];
			}else{
				throw new InvalidArgumentException("\nCannot find input file: "+ args[0]+"!");
			}
			
			if(StringUtils.isNumeric(args[1])){
				this.numProc=Integer.parseInt(args[1].trim());
			}else{
				throw new InvalidArgumentException("\n"+ args[1]+" is not a valid number of processors!");
			}
			//Loop through the rest of the command line arguments to get the optional arguments
			for( int i=2; i<args.length; i++){
				
				//Thread option
				if( args[i].equals("-p")){
			
					if (StringUtils.isNumeric(args[i+1])){
						numThread=Integer.parseInt( args[i+1].trim());
						i++;
					}else{
						numThread=Runtime.getRuntime().availableProcessors();
					}
					
				//Visualization option	
				}else if( args[i].equals("-v")){
					visualisation=true;
					
				//Output file name option
				}else if( args[i].equals("-o")){
					outputFileName=args[i+1];
					
					if(new File(outputFileName).isFile()){ 	
						throw new InvalidArgumentException("\nFile's name " + outputFileName + 
								" already exists.\nPlease choose a different name");
					}else{
						outputFileName=args[i+1];
						i++;
					}
					
				//Violate command argument format, throw invalid exception	
				}else{
					throw new InvalidArgumentException("\nInvalid command line argument: " + args[i] );
				}
			}
		}
		
		/**
		 * Second constructor
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
				if(line.contains("{")|| line.contains("}") || !line.contains("Weight=")){			
					continue;
				}
				/*Split each line into parts by tab characters*/
				String[] parts=line.trim().split("\t");
				// Process lines that do not contain ">" characters, representing nodes
				if(!line.contains(">")){
					// Get names and weight of the nodes
					String name="";
					int weight=0;
					for(String subString: parts){
						if(subString.equals("")){
							continue;
						}else if(!subString.contains(";")){
							name=subString.trim();
						}else{
							subString=subString.substring(subString.lastIndexOf("=")+1,subString.lastIndexOf("]"));
							weight=Integer.parseInt(subString.trim());
						}
					}
					if(isNodeNew(name)){
						// 	If the node is not in the list_of_nodes then create a new node and add to the list
						Node n= new Node(name);
						n.setWeight(weight);
						listOfNodes.add(n);
						map.put(name, index);
						n.setIndex(index);
						addAvailableNode(n);
						index++;
					}else{
						// If the node is already in the list, assign the weight to the node
						Node n= listOfNodes.get(map.get(name));
						n.setWeight(weight);
					}
				// Process line that represent edges	
				}else{
					// Get the name of the parent and child node
					String parentName="";
					String childName="";
					int weight=0;
					for(String subString: parts){
						if(subString.equals("")){
							continue;
						}else if(!subString.contains(";")){
							parentName=subString.substring(0, subString.indexOf(">")-2).trim();
							childName=subString.substring(parts[0].indexOf(">")+1).trim();
						}else{// Get the cost of communication
							subString=subString.substring(subString.lastIndexOf("=")+1,subString.lastIndexOf("]"));
							weight=Integer.parseInt(subString.trim());
						}
					}
					Node p=null;
					// If the parent node does not exist create a new node, otherwise get the parent node and assign to p
					if(isNodeNew(parentName)){
						p=new Node(parentName);
						listOfNodes.add(p);
						map.put(parentName, index);
						p.setIndex(index);
						addAvailableNode(p);
						index++;
					}else{
						p=listOfNodes.get(map.get(parentName));
					}
					// Get the child node
					Node c=null;
					// If the child node does not exist create a new node, otherwise get the child node and assign to c
					if(isNodeNew(childName)){
						c=new Node(childName);
						listOfNodes.add(c);
						map.put(childName, index);
						c.setIndex(index);
						addAvailableNode(c);
						removeAvailableNode(c);
						index++;
					}else{
						c=listOfNodes.get(map.get(childName));
						removeAvailableNode(c);
					} 
					
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
		private void addAvailableNode(Node n){
			int index = map.get(n.getName());
			nextAvailableNodes.add(true);
		}
		
		private void removeAvailableNode(Node n){
			int index = map.get(n.getName());
			nextAvailableNodes.set(index,false);
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
			return numProc;
		}
		
		public boolean getVisualisation(){
			return visualisation;
		}
		public ArrayList<Node> getListOfNodes(){
			return listOfNodes;
		}
		public String getOutputFileName(){
			return this.outputFileName;
		}
		public String getFileName(){
			return this.fileName;
		}
}
