package com.thesmartpuzzle.kaggle.restaurants;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.TreeSet;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;

/**
 * @author Tommaso Soru <tsoru@informatik.uni-leipzig.de>
 *
 */
public class CityCheck {

	public static void main(String[] args) throws IOException {

		run();

	}

	/**
	 * Check which cities are not in the training set.
	 * 
	 * @param prefix
	 * @throws IOException
	 */
	private static void run() throws IOException {

		final String TRAIN_PATH = "data/train-numericdate.csv";
		final String TEST_PATH = "data/test-numericdate.csv";

		CSVReader trainReader = new CSVReader(new FileReader(TRAIN_PATH), ',', '"',
				CSVWriter.NO_ESCAPE_CHARACTER);
		CSVReader testReader = new CSVReader(new FileReader(TEST_PATH), ',', '"',
				CSVWriter.NO_QUOTE_CHARACTER);

		String[] nextLine = trainReader.readNext(); // column titles
		
		TreeSet<String> train = new TreeSet<String>();
		TreeSet<String> test = new TreeSet<String>();
		
		while ((nextLine = trainReader.readNext()) != null) {
			train.add(nextLine[3]);
		}

		while ((nextLine = testReader.readNext()) != null) {
			test.add(nextLine[3]);
		}

		testReader.close();
		trainReader.close();
		
		System.out.println("Train cities = "+train.size());
		System.out.println(train);
		System.out.println("Test cities = "+test.size());
		System.out.println(test);
		
		TreeSet<String> testMinusTrain = new TreeSet<String>(test);
		testMinusTrain.removeAll(train);
		System.out.println("testMinusTrain cities = "+testMinusTrain.size());
		
		System.out.println(testMinusTrain);
		
	}

}
