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

import org.graphstream.ui.swingViewer.*;
import org.graphstream.ui.view.*;
import org.graphstream.graph.*;
import org.graphstream.ui.*;
import javax.swing.*;
import java.awt.*;

/**
 * This class implements an application that finds a schedules with the shortest
 * schedule length
 * 
 */
public class Main {

	public static void main(String[] args) throws FileNotFoundException,
			UnsupportedEncodingException, InvalidArgumentException {

		ArrayList<JFrame> frames = new ArrayList<>();
		long StartTime = System.currentTimeMillis();
		Dag dag = null;
		ArrayList<Node> list = new ArrayList<Node>();
		ArrayList<Boolean> available;
		int numProc;

		// Process Input.
		InputProcessor ip = new InputProcessor(args);
		ip.processInput();
		list = ip.getGraph();
		available = ip.getNextAvailableNodes();
		numProc = ip.getNumberOfProcessors();

		// Sets up JFrames for visualization
		if (ip.getVisualisation()) {
			for (int i = 0; i < ip.getNumThread(); i++) {
				frames.add(new JFrame("Running on processor: " + (i+1)));
				if (i > 0) {
					frames.get(i).setLocation(
							frames.get(i - 1).getX()
									+ frames.get(i - 1).getWidth(),
							frames.get(i - 1).getY());
				}
				frames.get(i).setSize(450, 600);

			}
		}
		int numThreads = ip.getNumThread();
		ArrayList<Thread> threadList = new ArrayList<Thread>(numThreads);
		ArrayList<Scheduler> schedulerList = new ArrayList<Scheduler>(
				numThreads);

		// Creates Schedule for num threads and start them
		for (int i = 0; i < numThreads; i++) {
			Scheduler s = new IDAStar(list, available, numProc);
			schedulerList.add(s);

			// Real time visuals are displayed if set to true
			if (ip.getVisualisation()) {
				dag = new Dag(list, numProc);
				Viewer viewer = new Viewer(dag.createDag(),
						Viewer.ThreadingModel.GRAPH_IN_ANOTHER_THREAD);
				View view = viewer.addDefaultView(false);
				frames.get(i).add((Component) view);
				frames.get(i).setVisible(true);
				viewer.enableAutoLayout();
				s.setVisual(dag);
			}

			threadList.add(new Thread(s));
			threadList.get(i).start();
		}

		for (int i = 0; i < numThreads; i++) {
			try {
				threadList.get(i).join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		IDAStar s = null;
		for (int i = 0; i < numThreads; i++) {
			IDAStar currentS = (IDAStar) schedulerList.get(i);
			if (currentS.getFinishTime() != -1) {
				if (s == null) {
					s = currentS;
				} else {
					if (s.getFinishTime() > currentS.getFinishTime()) {
						s = currentS;
					}
				}
			}
		}
		
		long EndTime = System.currentTimeMillis();
		System.out.print(EndTime - StartTime);

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

		// Show final schedule for visualization
		if (ip.getVisualisation()) {
			s.getDag().createProcessorGraph();

		}


	}
}
