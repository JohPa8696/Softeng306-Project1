package tests;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Collections;

import org.junit.Test;

import node.Node;
import schedulers.IDAStar;

public class IDAStarTest {

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
		
		IDAStar awesome = new IDAStar(dag, nextAvailableNodes, numProc, null);
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
		dag.add(c);
		dag.add(b);
		
		ArrayList<Boolean> nextAvailableNodes = new ArrayList<Boolean>(3);
		for(int i = 0; i < 3; i++){
			nextAvailableNodes.add(false);
		}
		nextAvailableNodes.set(0, true);
		nextAvailableNodes.set(2, true);
		
		int numProc = 2;
		
		IDAStar awesome = new IDAStar(dag, nextAvailableNodes, numProc, null);
		System.out.println("running0");
		awesome.schedule();
		System.out.println("running1");
		int finishTime = awesome.getFinishTime();
		System.out.println(finishTime);		
		assertEquals(finishTime, 5);

	}

	@Test
	public void testDisjointGraphs(){
	
	}

}
