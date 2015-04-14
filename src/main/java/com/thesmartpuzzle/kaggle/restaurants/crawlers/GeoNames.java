package com.thesmartpuzzle.kaggle.restaurants.crawlers;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.TreeSet;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.thesmartpuzzle.kaggle.restaurants.model.City;

/**
 * @author Tommaso Soru <mommi84@gmail.com>
 *
 */
public class GeoNames {
	
	private static final Logger LOGGER = LogManager.getLogger();
	
	private static final String GEONAMES_QUERY_PREFIX = "http://api.geonames.org/searchJSON?"
			+ "formatted=true&lang=en&username=mommi84&style=full&country=TR&maxRows=1&featureClass=P&q=";
	
//	private HashMap<String, String> jsonindex = new HashMap<String, String>();

	private HashMap<String, City> index = new HashMap<String, City>();
	
	private TreeSet<String> notFound = new TreeSet<String>();

	public HashMap<String, City> getIndex() {
		return index;
	}

	public static void main(String[] args) throws IOException {
		new GeoNames().run("data/train-numericdate.csv", 3);
	}

	public void run(String path, int column) throws IOException {
		
		CSVReader reader = new CSVReader(new FileReader(path), ',', '"', CSVWriter.NO_ESCAPE_CHARACTER);
		
		String[] nextLine = reader.readNext(); // column titles
		while ((nextLine = reader.readNext()) != null) {
			String city = nextLine[column];
			LOGGER.debug(city);
			
			if(index.containsKey(city) || notFound.contains(city))
				continue;
			
			InputStream is;
			try {
				is = new URL(GEONAMES_QUERY_PREFIX + city).openStream();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				continue;
			}
			try {
				
				BufferedReader rd = new BufferedReader(new InputStreamReader(
						is, Charset.forName("UTF-8")));
				String jsonText = readAll(rd);
				LOGGER.debug(jsonText);
				JSONObject obj = (JSONObject) JSONValue.parse(jsonText);
				Long tot = (Long) obj.get("totalResultsCount");
				
				if(tot == 0) {
					notFound.add(city);
					LOGGER.warn(city+" not found.");
				} else {
					
					City c = new City();
					c.setName(city);
					
					JSONObject geo = (JSONObject) ((JSONArray) obj.get("geonames")).get(0);
					
					Long pop = (Long) geo.get("population");
					c.setPop(pop);
					String lat = (String) geo.get("lat");
					c.setLat(lat);
					String lng = (String) geo.get("lng");
					c.setLng(lng);
					
//					jsonindex.put(city, jsonText);
					index.put(city, c);
					
					LOGGER.info(city+" saved (first entry among "+tot+" results)");
					LOGGER.info("pop = \t"+pop);
					LOGGER.info("lat = \t"+lat);
					LOGGER.info("lng = \t"+lng);
					
				} 
				
			} finally {
				is.close();
			}
		}
		
		if(!notFound.isEmpty())
			LOGGER.warn("Cities not found: "+notFound);
		
		reader.close();
	}
	

	private static String readAll(Reader rd) throws IOException {
		StringBuilder sb = new StringBuilder();
		int cp;
		while ((cp = rd.read()) != -1) {
			sb.append((char) cp);
		}
		return sb.toString();
	}


}
