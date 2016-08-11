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

/**
 * This class implements an application that finds a schedules with the shortest schedule length
 *
 */
public class Main {
	

	public static void main(String[] args) throws FileNotFoundException, UnsupportedEncodingException{
		
		ArrayList<Node> list= new ArrayList<Node> ();
		ArrayList<Boolean> available; 
		HashMap<String, Integer> map= new HashMap<>();	
		
		//Process Input.
		InputProcessor ip=new InputProcessor(args);
		ip.processInput();
		list=ip.getGraph();
		map=ip.getMap();	
		available=ip.getNextAvailableNodes();
		
		// Creates Schedule
		Scheduler ss = new SimpleScheduler(list);
		ss.schedule();
		
		// Create output file
		OutputProcessor op = new OutputProcessor(args[0], ss.getSchedule());
		op.processOutput();
		
		
	}
	
	
	
}
