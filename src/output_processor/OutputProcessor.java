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

	public OutputProcessor(String fileName, ArrayList<Node> schedule) {
		this.fileName = fileName;
		this.schedule = schedule;
	}

	public void processOutput() throws FileNotFoundException,
			UnsupportedEncodingException {
		PrintWriter writer = new PrintWriter("output.dot", "UTF-8");

		Scanner scan = new Scanner(new File(fileName));
		while (scan.hasNext()) {
			String line = scan.nextLine();
			if (line.contains("{")) {
				String[] parts = line.split("\\s+");
				String nameOfGraph = "output"
						+ parts[1].substring(1, (parts[1].length() - 1));
				line = parts[0] + " \"" + nameOfGraph + "\" {";
				writer.println(line);
			} else if (line.contains(">") || line.contains("}")) {
				writer.println(line);
			} else {
				String[] parts = line.split("\\s+");
				Node n = null;
				int i = 0;
				for (String s : parts) {
					if (s.matches("[a-zA-Z0-9]")) {
						for (Node node : schedule) {
							if (node.getName().equals(parts[1])) {
								n = node;
							}
						}
						line = "\t" + parts[1] + "\t\t" + "[Weight="
								+ n.getWeight() + ",Start=" + n.getStartTime()
								+ ",Processor=" + n.getProcessor() + "];";
						writer.println(line);

					}
				}

			}
		}
		scan.close();
		writer.close();
	}
}
