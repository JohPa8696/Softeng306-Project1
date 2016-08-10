package main;
import node.Node;
import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

import output_processor.OutputProcessor;
import schedulers.BranchAnBound;
import schedulers.Scheduler;
import schedulers.SimpleScheduler;
import input_processor.InputProcessor;

public class Main {
	
	
	
	public static void main(String[] args) throws FileNotFoundException, UnsupportedEncodingException{
		
		ArrayList<Node> list= new ArrayList<Node> ();
		ArrayList<Boolean> available; 
		HashMap<String, Integer> map= new HashMap<>();	
		//Recieving command line arguments
		//int numProc=Integer.parseInt(args[1].trim()); 
		//String fileName=args[0];
		int numProc=2;
		String fileName="resources/input.dot";
		InputProcessor ip=new InputProcessor(fileName);
		ip.processInput();
		list=ip.getGraph();
		map=ip.getMap();	
		available=ip.getNextAvailableNodes();
		
		System.out.println("-----------------------------------------------------------------");
		System.out.println("Testing allocation class");
		BranchAnBound bnb = new BranchAnBound(4, ip.getGraph());
		bnb.testalloc();
		
		System.out.println("-----------------------------------------------------------------");
		System.out.println("Testing branch and bound permutation method");
		BranchAnBound bab=new BranchAnBound(2,list);
		ArrayList<ArrayList<Node>> permutations =bab.permutation();
		for(ArrayList<Node> al:permutations){
			for(Node n: al){
				System.out.print(n.getName());
			}
			System.out.println();
		}
		System.out.println("-----------------------------------------------------------------");
		System.out.println("Testing input processor class");
		for(Node n : list){
			System.out.println(n.getName()+ " weight: " +n.getWeight());
		}
		System.out.println("-----------------------------------------------------------------");
		System.out.println("Testing input processor class");
		for(String name: map.keySet()){
			int index=map.get(name);
			boolean isAvailable = available.get(index);
			System.out.println(name +" index: " + index + " isAvailable: " + isAvailable);
		}
		
		System.out.println("-----------------------------------------------------------------");
		System.out.println("Testing simple scheduler class");
		SimpleScheduler ss = new SimpleScheduler(list);
		ss.schedule();
		
		System.out.println("-----------------------------------------------------------------");
		System.out.println("Testing output processor class");
		OutputProcessor op = new OutputProcessor(fileName, ss.getSchedule());
		op.processOutput();
		
		
	}
	
	
	
}
