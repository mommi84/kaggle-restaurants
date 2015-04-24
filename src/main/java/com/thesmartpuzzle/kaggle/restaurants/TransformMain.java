package com.thesmartpuzzle.kaggle.restaurants;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;

/**
 * @author Tommaso Soru <tsoru@informatik.uni-leipzig.de>
 *
 */
public class TransformMain {

	public static void main(String[] args) throws IOException {

		run("train");
		run("test");

	}

	private static void run(String prefix) throws IOException {

		final String INPUT_PATH = "data/" + prefix + "-numericdate.csv";
		final String OUTPUT_PATH = "data/" + prefix + "-numericdate-out.csv";

		CSVReader reader = new CSVReader(new FileReader(INPUT_PATH), ',', '"',
				CSVWriter.NO_ESCAPE_CHARACTER);
		CSVWriter writer = new CSVWriter(new FileWriter(OUTPUT_PATH), ',',
				CSVWriter.NO_QUOTE_CHARACTER);

		String[] nextLine = reader.readNext(); // column titles
		int l = nextLine.length - 5;

		double[] mins = new double[l];
		double[] maxs = new double[l];
		for (int i = 0; i < maxs.length; i++) {
			mins[i] = Double.POSITIVE_INFINITY;
			maxs[i] = Double.NEGATIVE_INFINITY;
		}

		ArrayList<String[]> lines = new ArrayList<String[]>();
		while ((nextLine = reader.readNext()) != null) {
			String[] trLine = new String[l];
			// numeric date
			trLine[0] = nextLine[2];
			// numeric values
			for (int i = 6; i < nextLine.length; i++)
				trLine[i - 5] = nextLine[i];

			for (int i = 0; i < trLine.length; i++) {
				double val = Double.parseDouble(trLine[i]);
				if (val < mins[i])
					mins[i] = val;
				if (val > maxs[i])
					maxs[i] = val;
			}

			lines.add(trLine);

		}

		for (String[] line : lines) {
			for (int i = 0; i < line.length - 1; i++) {
				if (i == 0) { // normalize date only
					double val = (Double.parseDouble(line[i]) - mins[i])
							/ (maxs[i] - mins[i]);
					line[i] = "" + val;
				}
			}
			writer.writeNext(line);
		}

		writer.close();
		reader.close();
	}

}
