package com.thesmartpuzzle.kaggle.restaurants;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.thesmartpuzzle.kaggle.restaurants.crawlers.GeoNames;
import com.thesmartpuzzle.kaggle.restaurants.model.City;

/**
 * @author Tommaso Soru <tsoru@informatik.uni-leipzig.de>
 *
 */
public class GeoMain {

	public static void main(String[] args) throws IOException {
		
		final String INPUT_PATH = "data/test-numericdate.csv";
		final String OUTPUT_PATH = "data/test-numericdate-geonames.csv";
		
		final int column = 3;
		
		
		GeoNames geo = new GeoNames();
		geo.run(INPUT_PATH, column);
		
		HashMap<String, City> index = geo.getIndex();
		
		CSVReader reader = new CSVReader(new FileReader(INPUT_PATH), ',', '"', CSVWriter.NO_ESCAPE_CHARACTER);
		CSVWriter writer = new CSVWriter(new FileWriter(OUTPUT_PATH), ',', '"');
		
		String[] nextLine = reader.readNext(); // column titles
		
		String[] geoLine = new String[nextLine.length + 3];
		for(int i=0; i<=column; i++)
			geoLine[i] = nextLine[i];
		geoLine[column+1] = "geonames_lat";
		geoLine[column+2] = "geonames_lng";
		geoLine[column+3] = "geonames_pop";
		for(int i=column+4; i<geoLine.length; i++)
			geoLine[i] = nextLine[i-3];
		writer.writeNext(geoLine);
		
		while ((nextLine = reader.readNext()) != null) {
			
			City c = index.get(nextLine[column]);
						
			geoLine = new String[nextLine.length + 3];
			for(int i=0; i<=column; i++)
				geoLine[i] = nextLine[i];
			geoLine[column+1] = (c == null) ? "" : c.getLat();
			geoLine[column+2] = (c == null) ? "" : c.getLng();
			geoLine[column+3] = (c == null) ? "" : c.getPop() + "";
			for(int i=column+4; i<geoLine.length; i++)
				geoLine[i] = nextLine[i-3];
			
			writer.writeNext(geoLine);
		}
		
		writer.close();
		reader.close();
	}

}
