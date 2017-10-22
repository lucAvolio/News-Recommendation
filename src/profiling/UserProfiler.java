package profiling;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import org.bson.Document;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import model.Entity;
import persistence.MongoDB;
import utils.DateComparator;
import utils.EntityComparator;
import utils.ExcelOutputWriter;

public class UserProfiler {

	public static List<Entity> profileUser(String user_id, MongoCollection<Document> coll, int time_fragment){
		/*It creates a list of interests per user*/
		/*This list is TOPIC and SOCIAL TAG entities extracted from open calais*/
		List<Entity> entities = HtmlAnnotation.getTopicsByUser(user_id, coll);

//		MongoDB.saveEntityToDB(entities, user_id);
		entities = calculateSignal(entities, time_fragment, user_id);
		return entities;
//		System.out.println(entities);
	}
	
	public static int calculateIDF(List<Entity> entities) {
		
		int idf = 0;
		
		for(Entity e: entities) {
			int tmpIdf = Collections.frequency(entities, e);
			if(tmpIdf > idf)
				idf = tmpIdf;
		}
		return idf;
	}
	
	private static List<Entity> calculateTF_IDF(List<Entity> entities, int time_fragment){
		//time_fragment is in days
		int tf;
		int idf = calculateIDF(entities);
		List<Entity> tmpList = new ArrayList<Entity>();
		if(time_fragment != 0 && entities.size() != 0) {
			for(int i=0 ; i<=entities.size()-1; i++) {
				tf=1;
				Entity e = entities.get(i);
				Entity e3 = e;
				for(int j=i+1; j<=entities.size()-1; j++) {
					Entity e2 = entities.get(j);
					if(e.getName().equals(e2.getName())) {
						if(isSameFragment(e,e2,time_fragment)){
							tf++;
						}
					}					
				}
				double tf_idf = (double)tf / (double)idf;
				e3.setTf_idf(tf_idf);
				int index = getIndexEntityByFragmentInList(tmpList, e3, time_fragment);
				if(index > -1) {
					if(!isSameFragment(e3,tmpList.get(index), time_fragment)) {
						tmpList.add(e3);
					}
				}
				else {
					tmpList.add(e3);
				}
			}
		}	
		return tmpList;
	}
	
	public static boolean isSameFragment(Entity e1, Entity e2, int time_fragment){
		//1 giorno, 7 giorni, 14 giorni, intero mese
		
		if(e1 != null && e2 != null){
			DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
			Date c1Date = new Date();
			Date c2Date = new Date();
			Calendar cal1 = Calendar.getInstance();
			Calendar cal2 = Calendar.getInstance();

			try {
				c1Date = format.parse(e1.getDate());
				cal1.setTime(c1Date);
				c2Date = format.parse(e2.getDate());
				cal2.setTime(c2Date);
				
				//prendo il mese di e1 e lo divido in fragment 
				int mese = c1Date.getMonth();
				
				if(c1Date.getYear() == c2Date.getYear() && c1Date.getMonth() == c2Date.getMonth()) {
					if(time_fragment == 1) {
						//raccomandazione per giorno
						if(cal1.get(Calendar.DAY_OF_MONTH) == cal2.get(Calendar.DAY_OF_MONTH)) {
							return true;
						} else {
							return false;
						}
					} else if(time_fragment > 1 && time_fragment <= 7) {
						//raccomandazione per settimana
					    cal1.setMinimalDaysInFirstWeek(7);
					    int wk1 = cal1.get(Calendar.WEEK_OF_MONTH);
					    cal2.setMinimalDaysInFirstWeek(7);
					    int wk2 = cal2.get(Calendar.WEEK_OF_MONTH);
					    if(wk1 == wk2) {
					    	return true;
					    } else {
					    	return false;
					    }
					} else if(time_fragment > 7 && time_fragment <= 14) {
						//raccomandazione per mezzo mese (2 settimane)
					    cal1.setMinimalDaysInFirstWeek(7);
					    int wk1 = cal1.get(Calendar.WEEK_OF_MONTH);
					    cal2.setMinimalDaysInFirstWeek(7);
					    int wk2 = cal2.get(Calendar.WEEK_OF_MONTH);
					    if((wk1 <= 3 && wk1 <= 3) || (wk1 > 3 && wk2 > 3)) {
					    	return true;
					    } else {
					    	return false;
					    }
					} else if(time_fragment > 14) {
						//raccomandazione per mese
						//Ritorno true perchè sto già all'interno dello stesso anno e dello stesso mese
						return true;
					}
				}
			 }
			catch(Exception ex) {
				System.out.println("Error in parsing data - Method: isSameFragment");
				return false;
			}
			
			
		}
		return false;
	}
	
	public static List<Entity> calculateSignal(List<Entity> entities, int frame,String user) {
		entities = calculateTF_IDF(entities, frame);
		//Save entities in DB
		if(ExcelOutputWriter.ExcelOutputWriter(entities, user)){
			System.out.println("Excel file created");
		} else {
			System.out.println("Error while creating Excel file");
		}
		
		return entities;
	}
	
	private static int getIndexEntityByFragmentInList(List<Entity> entities, Entity e, int time_fragment) {
		if(entities != null && entities.size() != 0 && e != null){
			for(int i=0; i<=entities.size()-1; i++) {
				Entity ent = entities.get(i);
				if(e.getName().equals(ent.getName())) {
					if(isSameFragment(e,ent,time_fragment)) {
						return i;
					}
				}
			}
		}
		return -1;
	}

}
