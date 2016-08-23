package main;

import schedulers.BranchAndBound;
import input_processor.InputProcessor;
import node.Node;
import output_processor.OutputProcessor;

import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class BranchMain {
	
	
	public static void main(String[] args) throws FileNotFoundException{
		
		ArrayList<Node> list= new ArrayList<Node> ();
		ArrayList<Node> finalList = new ArrayList<Node>();
		HashMap<String, Integer> map= new HashMap<>();	
		int finalFinishTime = Integer.MAX_VALUE;
		
		int numProc=Integer.parseInt(args[1]);
		
		
		list.clear();
		String fileName = "resources/"+args[0];
		InputProcessor ip1=new InputProcessor(fileName);
		ip1.processInput();
		list=ip1.getListOfNodes();
		map=ip1.getMap();
		
		
		BranchAndBound b = new BranchAndBound(numProc, list);
		
		b.setFinalList(finalList);
		b.testalloc();
		System.out.println("The final list size is "+finalList.size());
		/*if (b.getFinalFinishTime() < finalFinishTime){
			finalFinishTime = b.getFinalFinishTime();
			finalList.clear();
			finalList = b.getFinalList();
		}*/
		
		
		System.out.println("\n");
		//bab2.setFinalList(finalList);
		//ArrayList<ArrayList<Node>> perm = bab2.permutation();
		//System.out.println("The two array is "+perm.size());
		//bab2.testalloc();
		/*for (ArrayList<Node> array : perm){
			BranchAnBound b = new BranchAnBound(numProc, array);
			b.setFinalList(finalList);
			b.testalloc();
			System.out.println("The final list size is "+finalList.size());
			if (b.getFinalFinishTime() < finalFinishTime){
				finalFinishTime = b.getFinalFinishTime();
				finalList.clear();
				finalList = b.getFinalList();
			}
			
			for (Node node:array){
				System.out.print(node.getName()+" ");
				
			}
			System.out.println("\n");
		}*/
		System.out.println("The final list size is "+finalList.size());
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
		
		
		
		OutputProcessor out = new OutputProcessor(fileName,finalList);
		try {
			out.processOutput();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	
}
