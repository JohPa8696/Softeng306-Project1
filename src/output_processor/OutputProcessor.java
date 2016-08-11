package output_processor;

import node.Node;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * OutputProcessor produces an output file from a schedule and input file.
 * @author vincent
 *
 */
public class OutputProcessor {
	private String fileName;
	private ArrayList<Node> schedule;
	private String outputFileName = null;
	/**
	 * Constructor.
	 * @param fileName
	 * @param schedule
	 */
	public OutputProcessor(String fileName, ArrayList<Node> schedule) {
		this.fileName = fileName;
		this.schedule = schedule;
	}
	/**
	 * Constructor.
	 * @param fileName
	 * @param schedule
	 * @param outFileName
	 */
	public OutputProcessor(String fileName, ArrayList<Node> schedule,
			String outFileName) {
		this.fileName = fileName;
		this.schedule = schedule;
		this.outputFileName = outFileName;
	}
	/**
	 * The method processOutput generates an output file from a schedule and input file.
	 * @throws FileNotFoundException
	 * @throws UnsupportedEncodingException
	 */
	public void processOutput() throws FileNotFoundException,
			UnsupportedEncodingException {
		// Check if output file has specific name
		if (outputFileName == null) {
			outputFileName = fileName.substring(0, fileName.length() - 4)
					+ "-output.dot";
		}

		PrintWriter writer = new PrintWriter(outputFileName, "UTF-8");
		Scanner scan = new Scanner(new File(fileName));
		
		// Scans input file line-by-line.
		while (scan.hasNext()) {
			String line = scan.nextLine();
			if (line.contains("{")) {
				// Prepends "output" to the existing graphs name.
				String[] parts = line.split("\\s+");
				String nameOfGraph = "output"
						+ parts[1].substring(1, (parts[1].length() - 1));
				line = parts[0] + " \"" + nameOfGraph + "\" {";
				writer.println(line);
			} else if (line.contains(">") || line.contains("}") || !line.contains("Weight=")) {
				// If the line does not contain a single node print it.
				writer.println(line);
			} else {
				// Print the nodes name, weight, start time and processor.
				String[] parts = line.split("\\s+");
				Node n = null;
				for (String s : parts) {
					if (s.matches("^[a-zA-Z0-9]*$") && s.length() > 0) {
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
