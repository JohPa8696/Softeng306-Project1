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
import java.util.Map;
import java.util.Scanner;

import dag.Dag;

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
		
		
		System.out.println("The final list size is "+finalList.size());
		
		
		System.out.println("\n");
	
		for (Node node:finalList){
			System.out.println("The final node is "+node.getName()+" and the final processor is "+node.getProcessor());
		}
		
		for(Node n: list){
			System.out.println(n.getName()+ " weight: " +n.getWeight());
		}
		for(String name: map.keySet()){
			int index=map.get(name);
			System.out.println(name +" index: " + index);
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
