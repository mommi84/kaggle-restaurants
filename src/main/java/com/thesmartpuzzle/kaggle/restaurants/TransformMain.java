package com.thesmartpuzzle.kaggle.restaurants;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;

/**
 * @author Tommaso Soru <t.soru@informatik.uni-leipzig.de>
 *
 */
public class TransformMain {

	public static void main(String[] args) throws IOException {
		
		final String INPUT_PATH = "data/train-numericdate.csv";
		final String OUTPUT_PATH = "data/train-numericdate-norm.csv";
				
		CSVReader reader = new CSVReader(new FileReader(INPUT_PATH), ',', '"', CSVWriter.NO_ESCAPE_CHARACTER);
		CSVWriter writer = new CSVWriter(new FileWriter(OUTPUT_PATH), ',', CSVWriter.NO_QUOTE_CHARACTER);
		
		String[] nextLine = reader.readNext(); // column titles
		int l = nextLine.length - 5;
		
		double[] mins = new double[l];
		double[] maxs = new double[l];
		for(int i=0; i<maxs.length; i++) {
			mins[i] = Double.MAX_VALUE;
			maxs[i] = Double.MIN_VALUE;
		}
		
		ArrayList<String[]> lines = new ArrayList<String[]>();
		while ((nextLine = reader.readNext()) != null) {
			String[] trLine = new String[l];
			// numeric date
			trLine[0] = nextLine[2];
			// numeric values
			for(int i=6; i<nextLine.length; i++)
				trLine[i-5] = nextLine[i];
			
			for(int i=0; i<trLine.length; i++) {
				double val = Double.parseDouble(trLine[i]);
				if(val < mins[i])
					mins[i] = val;
				if(val > maxs[i])
					maxs[i] = val;
			}
			
			lines.add(trLine);
			
		}
		
		
		for(int i=0; i<maxs.length; i++) {
			System.out.println(mins[i]+"\t"+maxs[i]);
		}
		
		for(String[] line : lines) {
			for(int i=0; i<line.length; i++) {
				double val = (Double.parseDouble(line[i]) - mins[i]) / (maxs[i] - mins[i]);
				line[i] = "" + val;
			}
			writer.writeNext(line);
		}
		
		writer.close();
		reader.close();
	}

}
