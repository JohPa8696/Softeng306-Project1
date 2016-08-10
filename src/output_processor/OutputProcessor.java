package output_processor;

import node.Node;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Scanner;

public class OutputProcessor {
	private String fileName;
	private ArrayList<Node> schedule;
	private String outputFileName = null;

	public OutputProcessor(String fileName, ArrayList<Node> schedule) {
		this.fileName = fileName;
		this.schedule = schedule;
	}

	public OutputProcessor(String fileName, ArrayList<Node> schedule,
			String outFileName) {
		this.fileName = fileName;
		this.schedule = schedule;
		this.outputFileName = outFileName;
	}

	public void processOutput() throws FileNotFoundException,
			UnsupportedEncodingException {
		if (outputFileName == null) {
			outputFileName = fileName.substring(0, fileName.length() - 4)
					+ "-output.dot";
		}

		PrintWriter writer = new PrintWriter(outputFileName, "UTF-8");
		Scanner scan = new Scanner(new File(fileName));
		while (scan.hasNext()) {
			String line = scan.nextLine();
			if (line.contains("{")) {
				String[] parts = line.split("\\s+");
				String nameOfGraph = "output"
						+ parts[1].substring(1, (parts[1].length() - 1));
				line = parts[0] + " \"" + nameOfGraph + "\" {";
				writer.println(line);
			} else if (line.contains(">") || line.contains("}") || !line.contains("Weight=")) {
				writer.println(line);
			} else {
				String[] parts = line.split("\\s+");
				Node n = null;
				for (String s : parts) {
					if (s.matches("[a-zA-Z0-9]")) {
						for (Node node : schedule) {
							if (node.getName().equals(s)) {
								n = node;
								break;
							}
						}
						line = "\t" + parts[1] + "\t " + "[Weight="
								+ n.getWeight() + ",Start=" + n.getStartTime()
								+ ",Processor=" + n.getProcessor() + "];";
						writer.println(line);
						break;
					}
				}

			}
		}
		scan.close();
		writer.close();
	}
}
