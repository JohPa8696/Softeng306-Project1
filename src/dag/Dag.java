package dag;

import java.awt.Color;
import java.util.ArrayList;
import node.Node;

import org.graphstream.graph.ElementNotFoundException;
import org.graphstream.graph.Graph;
import org.graphstream.graph.IdAlreadyInUseException;
import org.graphstream.graph.implementations.*;
import org.graphstream.ui.view.Viewer;
import org.graphstream.ui.view.ViewerListener;
import org.graphstream.ui.view.ViewerPipe;



/**
 * @author Jack Wong
 * Create a DAG based on the input.dot file
 * Create a processor graph after the scheduling finish
 * library: GraphStream
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

	
	/**
	 * @param nodelist
	 * @param numProc
	 * Constructor
	 */
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
	 * Create a visualized DAG graph and processor graph.
	 */
	public Graph createDag(){
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
		//g.display();
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
		
		return g;
	}
	/**
	 * Create a processor graph which shows the order and the allocation of the node. User could 
	 * click on a node to see the starting time.
	 */
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
		graphnode.addAttribute("ui.label", node.getName()+""+" P"+node.getProcessor());
		
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
	}
	/**
	 * @param n
	 * Set the color of the root node as green
	 */
	public void setRoot(Node n){
		g.getNode(n.getName()).setAttribute("ui.style", "fill-color: green;");
	}
	public void unRoot(Node n){
		g.getNode(n.getName()).setAttribute("ui.style", "fill-color: white,black,yellow;");
		g.getNode(n.getName()).setAttribute("ui.color", 0);
	}
	/**
	 * @param n - Passing the node which is determined
	 * Update the processor graph and store the information inside the node.
	 */
	public void updateProcGraph(Node n){
		int proc = n.getProcessor();
		String name = n.getName();
		
		proc_graph.addNode(name);
		
		//Store node name and starting time and finish time inside the node
		proc_graph.getNode(name).addAttribute("ui.label", name);
		proc_graph.getNode(name).setAttribute("Name", n.getName());
		proc_graph.getNode(name).setAttribute("Time", "S:"+n.getStartTime()+" F:"+n.getFinishTime());
		proc_graph.getNode(name).setAttribute("Clicked", true);
		
		//update node
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
	}
	
	@Override
	public void buttonPushed(String id) {
		try{
			if ((boolean) proc_graph.getNode(id).getAttribute("Clicked")){
				proc_graph.getNode(id).addAttribute("ui.label", (Object)proc_graph.getNode(id).getAttribute("Time"));
				proc_graph.getNode(id).addAttribute("Clicked",false);
				
			}else{
				proc_graph.getNode(id).addAttribute("ui.label", (Object)proc_graph.getNode(id).getAttribute("Name"));
				proc_graph.getNode(id).addAttribute("Clicked",true);
			}
		}catch(NullPointerException e){}		
	}
	
	@Override
	public void buttonReleased(String id) {
	}
	
	@Override
	public void viewClosed(String arg0) {
		looped = false;
	}
	
	public void mouseLeft(String arg0) {
	}
	
	public void mouseOver(String arg0) {		
	}
	
}
