package utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import model.Entity;
import oracle.jrockit.jfr.events.DynamicValueDescriptor;

public class StringConverter {

	public static List<String> FromStringToStringList(String s){
		s = s.replace("[","");
		s = s.replace("]", "");  	
		List<String> lst = new ArrayList<String>(Arrays.asList(s.split(",")));
		
		return lst;
	}
	
	public static List<Entity> FromStringToEntityList(String s) {
		List<Entity> toReturn = new ArrayList<Entity>();
		
		s = s.replace("[","");
		s = s.replace("]", ""); 
		
		List<String> tmp = new ArrayList<String>(Arrays.asList(s.split("},")));
		
		for(String str: tmp){
			str = str.replace("{", "");
			str = str.replace("}", "");
			Entity e = new Entity();
			List<String> t = new ArrayList<String>(Arrays.asList(str.split(",")));
			for(String str1: t) {
				
				
				if(str1.contains("name")) {
					String value = str1.substring(str1.indexOf('=')+1);
					e.setName(value);
					System.out.println(value);
				} else if(str1.contains("type")) {
					String value = str1.substring(str1.indexOf('=')+1);
					e.setType(value);
				} else if (str1.contains("tf_idf")){
					String value = str1.substring(str1.indexOf('=')+1);
					Double d = Double.parseDouble(value);
					e.setTf_idf(d);
				} else if (str1.contains("date")){
					String value = str1.substring(str1.indexOf('=')+1);
					e.setDate(value);
				} else if(str1.contains("relevance")){
					String value = str1.substring(str1.indexOf('=')+1);
					Double d = Double.parseDouble(value);
					e.setRelevance(d);
				} else if(str1.contains("count")) {
					String value = str1.substring(str1.indexOf('=')+1);
					int d = Integer.parseInt(value);
					e.setCount(d);
				}
								
			}
			toReturn.add(e);
		}
		return toReturn;
	}

}
