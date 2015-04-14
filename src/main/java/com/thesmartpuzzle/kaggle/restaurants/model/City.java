package com.thesmartpuzzle.kaggle.restaurants.model;

/**
 * @author Tommaso Soru <t.soru@informatik.uni-leipzig.de>
 *
 */
public class City {
	
	private String name, lat, lng;
	private Long pop;
	
	public City() {
		super();
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getLat() {
		return lat;
	}
	public void setLat(String lat) {
		this.lat = lat;
	}
	public String getLng() {
		return lng;
	}
	public void setLng(String lng) {
		this.lng = lng;
	}
	public Long getPop() {
		return pop;
	}
	public void setPop(Long pop) {
		this.pop = pop;
	}
	
	

}
