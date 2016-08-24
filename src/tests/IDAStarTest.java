package tests;

import static org.junit.Assert.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Scanner;

import org.junit.Test;

import input_processor.InputProcessor;
import junit.framework.TestCase;
import node.Node;
import output_processor.OutputProcessor;
import schedulers.IDAStar;
import schedulers.Scheduler;
import utils.InvalidArgumentException;

public class IDAStarTest extends TestCase {
	
	// Stores the input processors
	@Test
	public void testSimple() {
		
		ArrayList<Node> dag = new ArrayList<Node>();
		
		Node a = new Node("a");
		Node b = new Node("b");
		Node c = new Node("c");
		Node d = new Node("c");
		
		a.setWeight(2);
		a.setIndex(0);
		
		b.setWeight(3);
		b.setIndex(1);
		
		c.setWeight(3);
		c.setIndex(2);
		
		d.setWeight(2);
		d.setIndex(3);
		
		a.getChildren().add(b);
		a.getChildren().add(c);
		b.getChildren().add(d);
		c.getChildren().add(d);
		
		b.getParents().put(a, 1);
		c.getParents().put(a, 2);
		d.getParents().put(b, 2);
		d.getParents().put(c, 1);
		
		dag.add(a);
		dag.add(b);
		dag.add(c);
		dag.add(d);
		
		ArrayList<Boolean> nextAvailableNodes = new ArrayList<Boolean>(4);
		for(int i = 0; i < 4; i++){
			nextAvailableNodes.add(false);
		}
		nextAvailableNodes.set(0, true);
		
		int numProc = 2;
		
		IDAStar awesome = new IDAStar(dag, nextAvailableNodes, numProc);
		System.out.println("running0");
		awesome.schedule();
		System.out.println("running1");
		int finishTime = awesome.getFinishTime();
		//System.out.println("test simple: " + finishTime);	
		assertEquals(finishTime, 8);
	}
	
	@Test
	public void testMultiStarts(){
		
		ArrayList<Node> dag = new ArrayList<Node>();
		
		Node a = new Node("a");
		Node b = new Node("b");
		Node c = new Node("c");
		
		a.setWeight(2);
		a.setIndex(0);
		
		b.setWeight(3);
		b.setIndex(1);
		
		c.setWeight(3);
		c.setIndex(2);
	
		a.getChildren().add(b);
		c.getChildren().add(b);
		
		b.getParents().put(a, 2);
		b.getParents().put(c, 2);
		
		dag.add(a);
		dag.add(b);
		dag.add(c);
		
		ArrayList<Boolean> nextAvailableNodes = new ArrayList<Boolean>(3);
		for(int i = 0; i < 3; i++){
			nextAvailableNodes.add(false);
		}
		nextAvailableNodes.set(0, true);
		nextAvailableNodes.set(2, true);
		
		int numProc = 2;
		
		IDAStar awesome = new IDAStar(dag, nextAvailableNodes, numProc);
		System.out.println("running0");
		awesome.schedule();
		System.out.println("running1");
		int finishTime = awesome.getFinishTime();
		System.out.println(finishTime);		
		assertEquals(finishTime, 7);

	}

	@Test
	public void testDisjointGraphs() throws InvalidArgumentException, FileNotFoundException{
		String args[]={"resources/disjoint_graph.dot","3"};
		InputProcessor ip= new InputProcessor(args);
		ip.processInput();
		IDAStar s=new IDAStar(ip.getListOfNodes(), 
				ip.getNextAvailableNodes(),
				ip.getNumberOfProcessors());
		s.schedule();
		int finishTime=s.getFinishTime();
		System.out.println(finishTime);
		assertEquals(finishTime,104);
	}
	@Test
	public void testNodes8() throws InvalidArgumentException, FileNotFoundException{
		String args[]={"resources/Nodes_8_Random.dot","5"};
		InputProcessor ip= new InputProcessor(args);
		ip.processInput();
		IDAStar s=new IDAStar(ip.getListOfNodes(), 
				ip.getNextAvailableNodes(),
				ip.getNumberOfProcessors());
		s.schedule();
		int finishTime=s.getFinishTime();
		System.out.println(finishTime);
		assertEquals(finishTime,581);
	}
	@Test
	public void test5BranchesGraph() throws InvalidArgumentException, FileNotFoundException{
		String args[]={"resources/5_branches.dot","4"};
		InputProcessor ip= new InputProcessor(args);
		ip.processInput();
		IDAStar s=new IDAStar(ip.getListOfNodes(), 
				ip.getNextAvailableNodes(),
				ip.getNumberOfProcessors());
		s.schedule();
		int finishTime=s.getFinishTime();
		System.out.println(finishTime);
		assertEquals(finishTime,131);
	}
	@Test
	public void testNodes10() throws InvalidArgumentException, FileNotFoundException{
		String args[]={"resources/Nodes_10_Random.dot","4"};
		InputProcessor ip= new InputProcessor(args);
		ip.processInput();
		IDAStar s=new IDAStar(ip.getListOfNodes(), 
				ip.getNextAvailableNodes(),
				ip.getNumberOfProcessors());
		s.schedule();
		int finishTime=s.getFinishTime();
		System.out.println(finishTime);
		assertEquals(finishTime,50);
	}
	/*@Test  TAKING TOO LONG TO BE INCLUDED
	public void testNodes11() throws InvalidArgumentException, FileNotFoundException{
		String args[]={"resources/Nodes_11_OutTree.dot","3"};
		InputProcessor ip= new InputProcessor(args);
		ip.processInput();
		IDAStar s=new IDAStar(ip.getListOfNodes(), 
				ip.getNextAvailableNodes(),
				ip.getNumberOfProcessors(), 
				ip.getNumThread());
		s.schedule();
		int finishTime=s.getFinishTime();
		System.out.println(finishTime);
		assertEquals(finishTime,104);
	}*/
	@Test
	public void testNodes7() throws InvalidArgumentException, FileNotFoundException{
		String args[]={"resources/Nodes_7_OutTree.dot","3"};
		InputProcessor ip= new InputProcessor(args);
		ip.processInput();
		IDAStar s=new IDAStar(ip.getListOfNodes(), 
				ip.getNextAvailableNodes(),
				ip.getNumberOfProcessors());
		s.schedule();
		int finishTime=s.getFinishTime();
		System.out.println(finishTime);
		assertEquals(finishTime,27);
	}
	@Test
	public void testNodes9() throws InvalidArgumentException, FileNotFoundException{
		String args[]={"resources/Nodes_9_SeriesParallel.dot","6"};
		InputProcessor ip= new InputProcessor(args);
		ip.processInput();
		IDAStar s=new IDAStar(ip.getListOfNodes(), 
				ip.getNextAvailableNodes(),
				ip.getNumberOfProcessors());
		s.schedule();
		int finishTime=s.getFinishTime();
		System.out.println(finishTime);
		assertEquals(finishTime,55);
	}

}
