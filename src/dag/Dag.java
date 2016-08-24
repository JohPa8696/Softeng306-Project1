package dag;

import java.awt.Color;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Iterator;

import node.Node;

import org.graphstream.graph.Edge;
import org.graphstream.graph.ElementNotFoundException;
import org.graphstream.graph.Graph;
import org.graphstream.graph.IdAlreadyInUseException;
import org.graphstream.graph.implementations.*;
import org.graphstream.ui.graphicGraph.GraphicGraph;
import org.graphstream.ui.view.View;
import org.graphstream.ui.view.Viewer;
import org.graphstream.ui.view.ViewerListener;
import org.graphstream.ui.view.ViewerPipe;
import org.graphstream.ui.view.util.MouseManager;


/**
 * @author Jack Wong
 * Create a DAG based on the input.dot file
 * 
 */
public class Dag implements ViewerListener{
	/**
	 * Stores every node from the input.dot file
	 */
	private ArrayList<Node> nodelist;
	private Graph g;
	private Graph proc_graph;
	private int numProc;
	private Node prevNode = null;
	private ArrayList<Node> procList;
	private Viewer viewer;
	private boolean looped = true;

	
	public Dag(ArrayList<Node> nodelist,int numProc){
		this.nodelist = nodelist;
		g = new SingleGraph("DAG");
		proc_graph = new SingleGraph("Processor Graph");
		this.numProc = numProc;
		procList = new ArrayList<Node>(numProc);
		for (int i=0;i<numProc;i++){
			procList.add(null);
		}
		
	}
	/**
	 * Create a visualized DAG graph
	 */
	public void createDag(){
		System.setProperty("gs.ui.renderer",
                "org.graphstream.ui.j2dviewer.J2DGraphRenderer");
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
					g.getEdge(node.getName()+ child.getName()).addAttribute("layout.weight", 3);
				}catch (ElementNotFoundException e){
					g.addNode(child.getName());
					g.addEdge(node.getName()+ child.getName(), node.getName(), child.getName(),true);
					g.getEdge(node.getName()+ child.getName()).addAttribute("layout.weight", 2);
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
		
		proc_graph.addAttribute("ui.stylesheet", "url('resources/style.css')");
		proc_graph.addAttribute("ui.class", "ProcessorGraph");
		for (int i=1; i<=numProc;i++){
			String proc = "P"+i;
			proc_graph.addNode(proc);
			proc_graph.getNode(proc).addAttribute("ui.class", "Processor");
			proc_graph.getNode(proc).setAttribute("xyz", 1, 3, 0);
		}
		for (org.graphstream.graph.Node node : proc_graph) {
	        node.addAttribute("ui.label", node.getId());
	    }
		proc_graph.setStrict(false);
		proc_graph.setAutoCreate(true);
	}
	public void createProcessorGraph(){
		
		viewer = proc_graph.display();
		viewer.setCloseFramePolicy(Viewer.CloseFramePolicy.HIDE_ONLY);
		ViewerPipe fromViewer = viewer.newViewerPipe();
		fromViewer.addViewerListener(this);
		fromViewer.addSink(proc_graph);
		while (looped){
			fromViewer.pump();
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
		//graphnode.addAttribute("ui.class", "final");
		graphnode.addAttribute("ui.label", node.getName()+"-"+"Processor: "+node.getProcessor());
		
	}
	/**
	 * Calls other function which update the DAG
	 * @param n - node that has been visited
	 */
	public void update(Node n){
		// TODO update the visuals of the dag
		if (prevNode == null){
			prevNode = n;
		}else if(!n.getName().equals(prevNode.getName())){
			int freq = prevNode.getFrequency();
			double color = (freq/2000000.0);
			if (color > 0.5){
				color = 0.5;
			}
			g.getNode(prevNode.getName()).setAttribute("ui.color", color);
			prevNode = n;

			g.getNode(n.getName()).setAttribute("ui.color", 1);
		}
		//

		
	}
	
	public void updateProcGraph(Node n){
		int proc = n.getProcessor();
		String name = n.getName();
		//System.out.println(procList.size());
		
		proc_graph.addNode(name);
	
		//System.out.println(n.getName());
		proc_graph.getNode(name).addAttribute("ui.label", name);
		proc_graph.getNode(name).setAttribute("Name", n.getName());
		proc_graph.getNode(name).setAttribute("Start", n.getStartTime());
		proc_graph.getNode(name).setAttribute("Clicked", true);
		
		//System.out.println(proc_graph.getNode(n.getName()).getId());
		if (procList.get(proc-1)==null){
			try{
				proc_graph.addEdge("P"+proc+name, "P"+proc, name,true);
				proc_graph.getEdge("P"+n.getProcessor()+n.getName()).addAttribute("layout.weight", 4);
			}catch(ElementNotFoundException e){
				
			}
			
		}else{
			String fromName = procList.get(proc-1).getName();
			proc_graph.addEdge(fromName+name, fromName, name,true);
			proc_graph.getEdge(fromName+name).addAttribute("layout.weight", 4);
		}
		procList.set(proc-1, n);
		
		/*try{
			proc_graph.addNode(n.getName());
			proc_graph.addEdge("P"+n.getProcessor()+n.getName(), "P"+n.getProcessor(), n.getName(),true);
		}catch(IdAlreadyInUseException e){
			//proc_graph.removeEdge("P"+n.getProcessor()+n.getName());
			//proc_graph.addEdge("P"+n.getProcessor()+n.getName(), "P"+n.getProcessor(), n.getName(),true);
			Iterable<Edge> edges = proc_graph.getNode(n.getName()).getEachEdge();
			for (Edge edge : edges){
				proc_graph.removeEdge(edge);
			}
		}*/
	}
	@Override
	public void buttonPushed(String id) {
		// TODO Auto-generated method stub
		try{
			if ((boolean) proc_graph.getNode(id).getAttribute("Clicked")){
				proc_graph.getNode(id).addAttribute("ui.label", "Start Time: "+proc_graph.getNode(id).getAttribute("Start"));
				proc_graph.getNode(id).addAttribute("Clicked",false);
				
			}else{
				proc_graph.getNode(id).addAttribute("ui.label", proc_graph.getNode(id).getAttribute("Name"));
				proc_graph.getNode(id).addAttribute("Clicked",true);
			}
		}catch(NullPointerException e){}
		
		
	}
	@Override
	public void buttonReleased(String id) {
		// TODO Auto-generated method stub
		
	

	}
	@Override
	public void viewClosed(String arg0) {
		// TODO Auto-generated method stub
		looped = false;
	}
	
}
