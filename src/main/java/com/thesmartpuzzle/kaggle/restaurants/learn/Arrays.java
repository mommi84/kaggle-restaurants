package com.thesmartpuzzle.kaggle.restaurants.learn;

/**
 * @author Tommaso Soru <tsoru@informatik.uni-leipzig.de>
 *
 */
public class Arrays {

	public static String toString(int[] predict) {
		String s = "";
		if(predict.length == 0)
			return s;
		for(int i : predict)
			s += i + ",";
		return s.substring(0, s.length()-1);
	}

}
