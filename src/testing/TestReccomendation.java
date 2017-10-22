package testing;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import javax.swing.plaf.synth.SynthSpinnerUI;

import org.apache.commons.collections4.bag.SynchronizedSortedBag;
import org.bson.Document;
import org.json.JSONArray;
import org.json.JSONObject;

import com.mongodb.BasicDBObject;
import com.mongodb.MongoClient;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;

import model.Entity;
import profiling.HtmlAnnotation;
import profiling.UserProfiler;
import utils.FileConfigReader;

public class TestReccomendation {

	public static void TestReccomendation(){
		//CONNECT TO DB
		MongoClient mongoClient = new MongoClient("localhost",27017);		
		// Now connect to databases
		MongoDatabase db = mongoClient.getDatabase("twitterDB");
		//Collection containng html
		MongoCollection<Document> coll = db.getCollection("html");
		
		
		
		//GET USERS IN COLLECTION
		MongoCursor<String> user_list = coll.distinct("id_user",String.class).iterator();
		
		List<String> users = new ArrayList<String>();
		
		while(user_list.hasNext()){
			String u1 = user_list.next();
			users.add(u1);		
		}
		
		users = new ArrayList<String>();
		users.add("17826013");
		
		
		//For each user in users
		for(String u1: users){
			//Get documents in test months
			List<Document> u1_docs = getDocsInTestMonths(u1,coll);		
			List<Entity> entitiesInTestMonths_u1 = calculateTfIdf(u1,coll);
			
			
			//for user 2
			for(String u2: users){
				System.out.println("_____________________________________________________");
				System.out.println(u1+"-"+u2);
				double u1_u2 = 0.0;
				List<Document> u2_docs = getDocsInTestMonths(u2,coll);		
				List<Entity> entitiesInTestMonths_u2 = calculateTfIdf(u2,coll);
				int i= 0;
				for(Document d: u1_docs){
					double d1_u2 = 0.0;
					List<Entity> toMatch = new ArrayList<Entity>();
					List<Entity> topics = getTopicsByNews(d);
					
					for(Entity e: topics)
						if(e.getName() != null && e.getName()!= "" && entitiesInTestMonths_u1.contains(e)) {
							Entity et= entitiesInTestMonths_u1.get(entitiesInTestMonths_u1.indexOf(e));
							toMatch.add(et);
						}
					int j=0;
					for(Document d2: u2_docs){
						List<Entity> toMatch_2 = new ArrayList<Entity>();
						List<Entity> topics_2 = getTopicsByNews(d2);
						
						for(Entity e: topics_2)
							if(e.getName() != null && e.getName()!= "" && entitiesInTestMonths_u2.contains(e)) {
								Entity et= entitiesInTestMonths_u2.get(entitiesInTestMonths_u2.indexOf(e));
								toMatch_2.add(et);
							}
//						System.out.println("d"+i+ "-d"+j+": "+ CalculateNewsSimilarity(toMatch, toMatch_2));
						d1_u2 += CalculateNewsSimilarity(toMatch, toMatch_2)/u2_docs.size();	
						j++;
					}
					u1_u2 += d1_u2;
				}
//				u1_u2 += u1_u2;
				System.out.println(u1+"-"+u2+": "+u1_u2);
			}					
		}
		
	
		
//		//For each user in user_list
//		while(user_list.hasNext()){
//			String u1 = user_list.next();
//			
//			
//			//for each user in user_toMatch
//			while(users_toMatch.hasNext()){
//				String u2 = users_toMatch.next();
//				System.out.println(u1 + "-" + u2);
//				
//				
//				//Get u1 documents
//				BasicDBObject q = new BasicDBObject("id_user",u1);
//				FindIterable<Document> myHtmlTemp_u1 = coll.find(q);
//				MongoCursor<Document> myHtml_u1 = myHtmlTemp_u1.iterator();
//				
//				//Entities u1 talked about during test months
//				List<Entity> entitiesInTestMonths_u1 = calculateTfIdf(u1, coll);
////				System.out.println(u1+" "+entitiesInTestMonths_u1.size());
//				
//				while(myHtml_u1.hasNext()){
//					Document d1 = myHtml_u1.next();
//					
//					//get the date and check if is in testMonths
//					String date1 = d1.getOrDefault("created_at", "").toString();
//					if(date1 != null && date1 != "" && isInTestMonths(date1)){
//						//get topics in news
//						List<Entity> entities1 = getTopicsByNews(d1);
//						
//						
//						if(entities1 != null && entities1.size()>0){
//							List<Entity> tmp_1 = new ArrayList<Entity>();
//							for(Entity e1: entities1){
//								if(entitiesInTestMonths_u1.contains(entitiesInTestMonths_u1.indexOf(e1)))
//									tmp_1.add(entitiesInTestMonths_u1.get(entitiesInTestMonths_u1.indexOf(e1)));								
//							}
////							System.out.println(tmp_1.size());
//						}
//						
//					}
//				}
				
//			}
			
			
//		}
		
		
		
	}
	
	
	public static List<Entity> getTopicsByNews(Document d){
		try {
			List<Entity> entities = new ArrayList<Entity>();
			String topics = d.getOrDefault("topics", null).toString();
			JSONObject tmp = new JSONObject(topics);
			JSONArray tmpArray = new JSONArray();
			Iterator x = tmp.keys();
//			
			while(x.hasNext()) {
				String key = (String) x.next();
				tmpArray.put(tmp.get(key));
			}
//			
			for(int i=0; i<tmpArray.length(); i++) {
				JSONObject obj = tmpArray.getJSONObject(i);
				if(obj.has("_typeGroup") && obj.has("name") && !obj.get("name").equals("")) {
					Entity e = new Entity();
					if(obj.get("_typeGroup").equals("topics")) {
						e.setType("topics");
						e.setName(obj.get("name").toString());
						e.setRelevance( Double.valueOf(obj.get("score").toString()).doubleValue());
					}
					if(obj.get("_typeGroup").equals("socialTag")) {
						e.setType("socialTag");
						e.setName(obj.get("name").toString());
						e.setRelevance( Double.valueOf(obj.get("importance").toString()).doubleValue());
					}
					e.setDate(d.getOrDefault("created_at", null).toString());
					entities.add(e);				
				}	
				
			}
			return entities;
		}catch(Exception ex){
			System.out.println(ex.toString());
			return new ArrayList<Entity>();
			
		}
		
	}
	
	public static double CalculateNewsSimilarity(List<Entity> e1Lst, List<Entity> e2Lst){
		double result = 0.0;
		if(e1Lst.size() != 0 && e2Lst.size() != 0){
			double den_1 = 0.0;
			double den_2 = 0.0;
			double numerator = 0.0;
			for(Entity e: e1Lst){
				den_1 += Math.pow(e.getTf_idf(), 2);
				
				for(Entity e2: e2Lst){
					
					if(e.getName() != null && e2.getName()!= null && e.getName().equals(e2.getName())){
						numerator += (double) e.getTf_idf() * (double) e2.getTf_idf();
					}
				}
				
			}
			for(Entity e2: e2Lst){
				den_2 += Math.pow(e2.getTf_idf(), 2);
			}
			if(den_1 != 0.0 && den_2 != 0.0)
				result = numerator/Math.sqrt(den_1 * den_2);
		}
		return result;
	}
	
	
	public static List<Entity> calculateTfIdf(String user, MongoCollection<Document> coll){
//		//CONNECT TO DB
//		MongoClient mongoClient = new MongoClient("localhost",27017);		
//		// Now connect to databases
//		MongoDatabase db = mongoClient.getDatabase("twitterDB");
//		//Collection containng html
//		MongoCollection<Document> coll = db.getCollection("html");
//		List<Double> tfIdf = new ArrayList<Double>();
		
//		MongoCursor<String> user_list = coll.distinct("id_user",String.class).iterator();
		
		List<Entity> testEntities = new ArrayList<Entity>();
		
//		while(user_list.hasNext()) {
//			String user = user_list.next();
			List<Entity> entities = HtmlAnnotation.getTopicsByUser(user, coll);
			List<Entity> toReturn = new ArrayList<Entity>();
			
			for(Entity e: entities){
				if(e != null && isInTestMonths(e.getDate())){
					testEntities.add(e);				
				}				
			}
			
			double tf;
			double idf = UserProfiler.calculateIDF(testEntities);
			
			for(Entity e: testEntities){
				tf = Collections.frequency(testEntities,e);
				e.setTf_idf(tf/idf);
				if(!toReturn.contains(e))
					toReturn.add(e);				
			}						
//		}
		
		return toReturn;
	}
	
	public static boolean isInTestMonths(String date){
		String testMonths= FileConfigReader.ReadKey("testMonths");
		String[] arr = testMonths.split(",");
		
		String month = date.substring(date.indexOf("-")+1, date.lastIndexOf("-"));
		for(int i=0; i<arr.length; i++){
			if(arr[i].equals(month))
				return true;
		}
		return false;
	}

	public static List<Document> getDocsInTestMonths(String user, MongoCollection<Document> coll){
		try{
			
			List<Document> toReturn = new ArrayList<Document>();
//			//Get u1 documents
			BasicDBObject q = new BasicDBObject("id_user",user);
			FindIterable<Document> myHtmlTemp = coll.find(q);
			MongoCursor<Document> myHtml = myHtmlTemp.iterator();
			
			while(myHtml.hasNext()){
				Document d = myHtml.next();
				
				String date = d.getOrDefault("created_at", "").toString();
				if(date != null && date != "" && isInTestMonths(date))
					toReturn.add(d);				
			}
			
			return toReturn;			
		} catch(Exception ex){
			return new ArrayList<Document>();
		}
	}
}
