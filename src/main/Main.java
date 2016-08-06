package main;
import node.Node;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

import input_processor.InputProcessor;

public class Main {
	
	
	
	public static void main(String[] args) throws FileNotFoundException{
		
		ArrayList<Node> list= new ArrayList<Node> ();
		ArrayList<Node> available; 
		HashMap<String, Integer> map= new HashMap<>();	
		//int numProc=args[1];
		//String fileName=args[0];
		int numProc=2;
		String fileName="input.dot";
		InputProcessor ip=new InputProcessor(fileName);
		ip.processInput();
		list=ip.getListOfNodes();
		map=ip.getMap();
		available=ip.getListOfAvailableNodes();
		
		for(Node n: list){
			System.out.println(n.getName()+ " weight: " +n.getWeight());
		}
		for(String name: map.keySet()){
			int index=map.get(name);
			System.out.println(name +" index: " + index);
		}
		for(Node n: available){
			System.out.println(n.getName()+ " weight: " +n.getWeight() + " is available");
		}
		
	}
	
	
	
}
