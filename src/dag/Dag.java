package dag;

import java.awt.Color;
import java.util.ArrayList;

import node.Node;

import org.graphstream.graph.ElementNotFoundException;
import org.graphstream.graph.Graph;
import org.graphstream.graph.IdAlreadyInUseException;
import org.graphstream.graph.implementations.*;

/**
 * @author Jack Wong
 * Create a DAG based on the input.dot file
 * 
 */
public class Dag {
	/**
	 * Stores every node from the input.dot file
	 */
	private ArrayList<Node> nodelist;
	private Graph g;
	
	public Dag(ArrayList<Node> nodelist){
		this.nodelist = nodelist;
		g = new SingleGraph("DAG");
	}
	/**
	 * Create a visualized DAG graph
	 */
	public void createDag(){
		g.addAttribute("ui.stylesheet", "url('resources/style.css')");
		
		for (Node node : nodelist){
			//System.out.println("The node name is "+node.getName());	
			try{
				g.addNode(node.getName());
			}catch (IdAlreadyInUseException e){}
			//changeNodeColor(node,Color.BLUE);
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
		g.setStrict(false);
		g.setAutoCreate(true);
		g.display();
		// Add label of the node
		for (org.graphstream.graph.Node node : g) {
	        node.addAttribute("ui.label", node.getId());
	    }
	}
	/**
	 * @param node
	 * @param color
	 * Change the node color after the node's processor is determined.
	 */
	public void changeNodeColor(Node node,Color color){
		org.graphstream.graph.Node graphnode = g.getNode(node.getName());
		//graphnode.setAttribute("ui.color", color);
		graphnode.addAttribute("ui.class", "final");
		graphnode.addAttribute("ui.label", "Processor: "+node.getProcessor());
		
	}
	/**
	 * Calls other function which update the DAG
	 * @param n - node that has been visited
	 */
	public void update(Node n){
		// TODO update the visuals of the dag
		int freq = n.getFrequency();
		double color = freq/100000.0;
		g.getNode(n.getName()).setAttribute("ui.color", color);
		
	}
}
