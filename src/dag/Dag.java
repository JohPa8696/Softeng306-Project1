package dag;

import java.util.ArrayList;


import node.Node;

import org.graphstream.graph.ElementNotFoundException;
import org.graphstream.graph.Graph;
import org.graphstream.graph.IdAlreadyInUseException;
import org.graphstream.graph.implementations.*;


public class Dag {
	private ArrayList<Node> nodelist;
	public Dag(){
		
	}
	public Dag(ArrayList<Node> nodelist){
		this.nodelist = nodelist;
	}
	public void createDag(){
		Graph g = new DefaultGraph("g");
		for (Node node : nodelist){
			//System.out.println("The node name is "+node.getName());
			try{
				g.addNode(node.getName());
			}catch (IdAlreadyInUseException e){
				
			}
			
			for (Node child : node.getChildren()){
				//System.out.println(node.getName() + " 's children is " +child.getName());
				try{
					g.addEdge(node.getName()+ child.getName(), node.getName(), child.getName(),true);
				}catch (ElementNotFoundException e){
					g.addNode(child.getName());
					g.addEdge(node.getName()+ child.getName(), node.getName(), child.getName(),true);
				}
			}
		}
		
		
		g.display();
		
	}
	
}
