package com.thesmartpuzzle.kaggle.restaurants;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;

/**
 * @author Tommaso Soru <tsoru@informatik.uni-leipzig.de>
 *
 */
public class WekaToKaggle {

	public static void main(String[] args) throws IOException {

		run(2);

	}

	/**
	 * Convert the Weka output file (with ID = n) for submitting it to Kaggle.
	 * 
	 * @param n
	 * @throws IOException
	 */
	private static void run(int n) throws IOException {

		final String INPUT_PATH = "predictions/weka_" + n + ".csv";
		final String OUTPUT_PATH = "predictions/sent/weka_" + n + ".csv";

		CSVReader reader = new CSVReader(new FileReader(INPUT_PATH), ',', '"',
				CSVWriter.NO_ESCAPE_CHARACTER);
		CSVWriter writer = new CSVWriter(new FileWriter(OUTPUT_PATH), ',',
				CSVWriter.NO_QUOTE_CHARACTER);

		String[] nextLine = reader.readNext(); // column titles
		String[] writeLine = {"Id", "Prediction"};
		writer.writeNext(writeLine);

		for (int i=0; (nextLine = reader.readNext()) != null; i++) {
			if(nextLine.length == 1)
				break; // don't ask!
			writeLine[0] = "" + i;
			writeLine[1] = nextLine[2];
			writer.writeNext(writeLine);
		}

		writer.close();
		reader.close();
	}

}
