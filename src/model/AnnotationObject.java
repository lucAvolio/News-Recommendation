package model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AnnotationObject {
	private String url;
	private Map<String, Integer> spot = new HashMap<String,Integer>();
	private Map<String,List<String>> dbpedia_categories = new HashMap<String,List<String>>();

	public AnnotationObject() {
		// TODO Auto-generated constructor stub
	}
	
	public AnnotationObject(String url, Map<String,Integer> spot, Map<String,List<String>> dbpedia_categories) {
		this.url = url;
		this.spot = spot;
		this.dbpedia_categories = dbpedia_categories;		
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public Map<String, Integer> getSpot() {
		return spot;
	}

	public void setSpot(Map<String, Integer> spot) {
		this.spot = spot;
	}

	public Map<String, List<String>> getDbpedia_categories() {
		return dbpedia_categories;
	}

	public void setDbpedia_categories(Map<String, List<String>> dbpedia_categories) {
		this.dbpedia_categories = dbpedia_categories;
	}
	
	public void addSingleSpotToAnnotation(String singleSpot){
		int num = 1;
		if(this.spot.containsKey(singleSpot)) {
			num = this.spot.get(singleSpot)+1;
			this.spot.replace(singleSpot, num-1, num);		
		} else
			this.spot.put(singleSpot, num);	
	}

	public void addCategoryBySpot(String spot, List<String> cat) {
		if(!this.dbpedia_categories.containsKey(spot)) {
			this.dbpedia_categories.put(spot, cat);
		}
		
	}
	
	public String toString() {
		return url.toString() + System.lineSeparator() + this.spot.toString() + System.lineSeparator() + this.dbpedia_categories.toString();
		
	}

}
