package main;

import node.Node;

import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import output_processor.OutputProcessor;
import schedulers.IDAStar;
import schedulers.Scheduler;
import utils.InvalidArgumentException;
import input_processor.InputProcessor;
import dag.Dag;

/**
 * This class implements an application that finds a schedules with the shortest
 * schedule length
 *
 */
public class Main {

	public static void main(String[] args) throws FileNotFoundException,
			UnsupportedEncodingException, InvalidArgumentException {

		long StartTime = System.currentTimeMillis();

		ArrayList<Node> list = new ArrayList<Node>();
		ArrayList<Boolean> available;
		int numProc;

		// Process Input.
		InputProcessor ip = new InputProcessor(args);
		ip.processInput();
		list = ip.getGraph();
		available = ip.getNextAvailableNodes();
		numProc = ip.getNumberOfProcessors();
		
		int numThreads =  ip.getNumThread();
		ArrayList<Thread> threadList = new ArrayList<Thread>(numThreads);
		ArrayList<Scheduler> schedulerList = new ArrayList<Scheduler>(numThreads);
		
		// Creates Schedule for num threads and start them
		for (int i=0; i < numThreads; i++){
			Scheduler s = new IDAStar(list, available, numProc);
			schedulerList.add(s);
			
			// visuals are displayed if set to true
			if (ip.getVisualisation()) {
				Dag dag = new Dag(list, numProc);
				dag.createDag();
				s.setVisual(dag);
			}
			
			threadList.add(new Thread(s));
			threadList.get(i).start();
		}
		
		for (int i=0; i < numThreads; i++){
			try {
				threadList.get(i).join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		IDAStar s = null;
		for (int i=0; i < numThreads; i++){
			IDAStar currentS = (IDAStar)schedulerList.get(i);
			if (currentS.getFinishTime() != -1){
				if (s==null){
					s = currentS;
				} else {
					if (s.getFinishTime() > currentS.getFinishTime()){
						s = currentS;
					}
				}
			}
		}

		// Create output file
		if (ip.getOutputFileName() != null) {
			OutputProcessor op = new OutputProcessor(ip.getFileName(),
					s.getSchedule(), ip.getOutputFileName());
			op.processOutput();
		} else {
			OutputProcessor op = new OutputProcessor(ip.getFileName(),
					s.getSchedule());
			op.processOutput();
		}

		long EndTime = System.currentTimeMillis();
		System.out.print(EndTime - StartTime);

	}
}
