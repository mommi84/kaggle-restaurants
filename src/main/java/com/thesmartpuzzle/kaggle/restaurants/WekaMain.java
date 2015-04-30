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
public class WekaMain {

	public static void main(String[] args) throws IOException {

		run("test-numericdate-dummy");

	}

	/**
	 * Add fake "revenue" column (all zeroes) for Weka.
	 * 
	 * @param prefix
	 * @throws IOException
	 */
	private static void run(String prefix) throws IOException {

		final String INPUT_PATH = "data/" + prefix + ".csv";
		final String OUTPUT_PATH = "data/" + prefix + "-weka.csv";

		CSVReader reader = new CSVReader(new FileReader(INPUT_PATH), ',', '"',
				CSVWriter.NO_ESCAPE_CHARACTER);
		CSVWriter writer = new CSVWriter(new FileWriter(OUTPUT_PATH), ',',
				CSVWriter.NO_QUOTE_CHARACTER);

		String[] nextLine = reader.readNext(); // column titles
		String[] writeLine = new String[nextLine.length + 1];
		for(int i=0; i<nextLine.length; i++)
			writeLine[i] = nextLine[i];
		writeLine[writeLine.length - 1] = "revenue";
		writer.writeNext(writeLine);

		while ((nextLine = reader.readNext()) != null) {
			for(int i=0; i<nextLine.length; i++)
				writeLine[i] = nextLine[i];
			writeLine[writeLine.length - 1] = "0";
			writer.writeNext(writeLine);
		}

		writer.close();
		reader.close();
	}

}
