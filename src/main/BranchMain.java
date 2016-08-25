package main;

import schedulers.BranchAndBound;
import utils.InvalidArgumentException;
import input_processor.InputProcessor;
import node.Node;
import output_processor.OutputProcessor;

import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;


import dag.Dag;

/**
 * @author Jack Wong
 * 
 * 
 * Testing class used to generate output from branch and bound and use the 
 * output file to compare with the output from ida* in order to make sure the 
 * schedule is optimal. 
 * Expected run time for 11 nodes with 4 proc: 2min
 *
 */
public class BranchMain {
	
	
	public static void main(String[] args) throws FileNotFoundException, UnsupportedEncodingException, InvalidArgumentException{
		
		ArrayList<Node> list= new ArrayList<Node> ();
		ArrayList<Node> finalList = new ArrayList<Node>();
		HashMap<String, Integer> map= new HashMap<>();	
		int finalFinishTime = Integer.MAX_VALUE;
		
		int numProc=Integer.parseInt(args[1]);
		
		
		list.clear();
		InputProcessor ip=new InputProcessor(args);
		ip.processInput();
		list=ip.getListOfNodes();
		map=ip.getMap();
		
		Dag dag = new Dag(list,numProc);
		dag.createDag();
		
		BranchAndBound b = new BranchAndBound(numProc, list);
		
		b.setFinalList(finalList);
		b.testalloc();
		for (Node node:finalList){
			dag.updateProcGraph(node);
		}
		
		if(ip.getOutputFileName() != null){
			OutputProcessor op = new OutputProcessor(ip.getFileName(), finalList, ip.getOutputFileName());
			op.processOutput();
		}else{
			OutputProcessor op = new OutputProcessor(ip.getFileName(), finalList);
			op.processOutput();
		}
		dag.createProcessorGraph();
		
	}
	
	
	
}
