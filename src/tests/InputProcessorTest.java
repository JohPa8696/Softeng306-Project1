package tests;

import static org.junit.Assert.*;

import java.io.FileNotFoundException;
import java.util.ArrayList;

import org.junit.Test;

import input_processor.InputProcessor;
import node.Node;

public class InputProcessorTest {
	private InputProcessor ip1;
	private InputProcessor ip2;
	private ArrayList<Node> list1 ;
	private ArrayList<Node> list2 ;
	void setup(){
		ip1= new InputProcessor("input.dot");
		ip2= new InputProcessor("largeinput.dot");
		try {
			ip1.processInput();
			ip2.processInput();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		list1 =ip1.getGraph();
		 list2 =ip2.getGraph();
	}
	@Test
	public void testSmallInput1() {
		ArrayList<Node> uL=new ArrayList<Node>();
	}

}
