package testing;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

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

import jdk.nashorn.internal.runtime.regexp.RegExp;
import jdk.nashorn.internal.runtime.regexp.joni.Regex;
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

//		users = new ArrayList<String>();
//		users.add("17826013");
//		users.add("17826013");
		
//		// MODALITA' ENTITA'
//		List<String> similarity_1 = TestEntità(users,coll);
//		System.out.println(similarity_1.toString());
		
//		//MODALITA' UTENTI
//		List<String> similarity_2 = TestUtenti(users,coll);
//		System.out.println(similarity_2.toString());
		
		//MODALITA' NEWS
		List<String> similarity_3 = TestNews(users, coll);
		System.out.println(similarity_3.toString());
		
	}
	
	
	//MODALITA' ENTITà	
	public static List<String> TestEntità(List<String> users, MongoCollection<Document> coll){
		try{
			// MODALITA' ENTITA'
			List<Entity> totalEntities = GetTotalEntitiesInTestMonth(users,coll);
			HashMap<String, List<Entity>> userToEntity = new HashMap<String, List<Entity>>();
			
			//For each user in users
			for(String ul: users){
				//Modalità ENTITA'
				List<Entity> tf  =Tf_Modalità1(ul, coll);
				
				for(Entity e: tf){
					if(e.getName() != null){
						Entity tmp = totalEntities.get(totalEntities.indexOf(e));
						double idf = (double)totalEntities.size()/(double)tmp.getCount();
						e.setTf_idf(e.getTf_idf() * Math.log(idf));
					}
				}
				userToEntity.put(ul,tf);
			}
			
			List<String> sim = new ArrayList<String>();
			Iterator<Entry<String, List<Entity>>> it = userToEntity.entrySet().iterator();
			
			while(it.hasNext()) {
				Map.Entry<String, List<Entity>> currentUser = it.next();
				Iterator<Entry<String, List<Entity>>> it_2 = userToEntity.entrySet().iterator();
				
				while(it_2.hasNext()) {
					
					Map.Entry<String, List<Entity>> userToMatch = it_2.next();
					double val = CalculateNewsSimilarity(currentUser.getValue(),userToMatch.getValue());
					sim.add(currentUser.getKey() + "-" + userToMatch.getKey() + " " + val);
				}
			}
			System.out.println(sim.toString());
			return sim;
			
		}
		catch(Exception ex){
			return new ArrayList<String>();
		}
	}
	//Numero di volte che l'utente ha referenziato una news con entità E1
	//diviso (/)
	//Numero totale di entità astratte da tutte le news dell'utente
	public static List<Entity> Tf_Modalità1(String user,MongoCollection<Document> coll){		
		
	
		//TF
		List<Entity> testEntities = new ArrayList<Entity>();
		List<Entity> entities = HtmlAnnotation.getTopicsByUser(user, coll);
		List<Entity> toReturn = new ArrayList<Entity>();

		for(Entity e: entities){
			if(e != null && isInTestMonths(e.getDate())){
				testEntities.add(e);				
			}				
		}
		double tf_num;		
		double tf_den = new ArrayList(new HashSet(entities)).size();
		for(Entity e: testEntities){
			tf_num = Collections.frequency(testEntities,e);
			e.setTf_idf(tf_num/tf_den);
			if(!toReturn.contains(e))
				toReturn.add(e);				
		}
		return toReturn;
	}
	
	//MODALITA' ENTITA'
	//numero totale di entità referenziate da tutti gli utenti (per ogni entità
	//con count = numero di volte che l'entità è presente nella collezione (mesi di test)
	public static List<Entity> GetTotalEntitiesInTestMonth(List<String> users, MongoCollection<Document> coll){
		List<Entity> totalEntities = new ArrayList<Entity>();
		if(users != null && users.size()>0){
			for(String u: users){
				List<Entity> userEntity = HtmlAnnotation.getTopicsByUser(u, coll);
				for(Entity e: userEntity){
					if(e.getDate() != null && isInTestMonths(e.getDate()))
						totalEntities.add(e);
				}
			}
			
			for(Entity e: totalEntities){
				int count = Collections.frequency(totalEntities,e);
				e.setCount(count);
			}
		}
		
		return new ArrayList( new HashSet(totalEntities));
	}
	
	// MODALITA' UTENTI
	public static List<String> TestUtenti(List<String> users, MongoCollection<Document> coll){
		try{
			HashMap<String, List<Entity>> userToEntity = new	 HashMap<String,List<Entity>>();
			List<List<Entity>> s = new ArrayList<List<Entity>>();
			for(String user: users)
			{
				userToEntity.put(user,TF_Modalità2(user,coll));
			}
			
			userToEntity = IDF_M2(userToEntity);

			List<String> sim = new ArrayList<String>();
			Iterator<Entry<String, List<Entity>>> it = userToEntity.entrySet().iterator();
			
			while(it.hasNext()) {
				Map.Entry<String, List<Entity>> currentUser = it.next();
				Iterator<Entry<String, List<Entity>>> it_2 = userToEntity.entrySet().iterator();
				
				while(it_2.hasNext()) {
					
					Map.Entry<String, List<Entity>> userToMatch = it_2.next();
					double val = CalculateNewsSimilarity(currentUser.getValue(),userToMatch.getValue());
					sim.add(currentUser.getKey() + "-" + userToMatch.getKey() + " " + val);
				}
			}
			return sim;
			
		} catch(Exception ex){
			System.out.println(ex.toString());
			return new ArrayList<String>();
		}
	}
	
	//Numero di news in cui user ha parlato dell'entità
	//diviso
	//numero totale di news di cui ha parlato l'utente
	public static List<Entity> TF_Modalità2(String user, MongoCollection<Document> coll){
		try{
			List<Entity> toReturn = new ArrayList<Entity>();
			if(user != null && coll != null){
				List<Entity> testEntities = new ArrayList<Entity>();
				List<Entity> entities = HtmlAnnotation.getTopicsByUser(user, coll);
				
				for(Entity e: entities){
					if(e != null && isInTestMonths(e.getDate())){
						testEntities.add(e);				
					}				
				}
				double tf_num;
				
//				List<Document> documents = getDocsInTestMonths(user,coll);		
				double tf_den = getDocsInTestMonths(user,coll).size();
				
				for(Entity e: testEntities){
					tf_num = Collections.frequency(testEntities,e);
					e.setTf_idf(tf_num/tf_den);
					if(!toReturn.contains(e))
						toReturn.add(e);				
				}	
			}
			return toReturn;
		}
		catch (Exception ex){
			System.out.println(ex.toString());
		}
		return new ArrayList<Entity>();
	}
	
	//MODALITà UTENTI
	//LOG(numero totale di utenti / numero di utenti che hanno parlato dell'entità)
	public static HashMap<String,List<Entity>> IDF_M2(Map<String, List<Entity>> map){
		try{
			HashMap<String, List<Entity>> toReturn = new HashMap<String,List<Entity>>();
			
			Iterator<Entry<String, List<Entity>>> it = map.entrySet().iterator();
			double idf_num = map.size();
			while(it.hasNext()) {
				List<Entity> listaEntita = new ArrayList<Entity>();
				Map.Entry<String, List<Entity>> currentUser = it.next();
				List<Entity> eLst = currentUser.getValue();
				if(eLst != null){
					for(Entity e: eLst){
						double idf_den = 0.0;
						Iterator<Entry<String,List<Entity>>> it_2 = map.entrySet().iterator();
						while(it_2.hasNext()){
							Map.Entry<String, List<Entity>> userToMatch = it_2.next();
							List<Entity> eLstToMatch= userToMatch.getValue();
							if(eLstToMatch.contains(e)){
								idf_den += 1.1;
							}
						}
						double tf = e.getTf_idf();
						e.setTf_idf(tf * Math.log(idf_num*idf_den));
						if(!listaEntita.contains(e)){
							listaEntita.add(e);
						}
					}
				}
				System.out.println("___________________");
				System.out.println(currentUser.getKey());
				System.out.println(listaEntita);
				toReturn.put(currentUser.getKey(), listaEntita);
			}
			return toReturn;
		}
		catch (Exception ex){
			System.out.println(ex.toString());
			return new HashMap<String,List<Entity>>();
		}
	}
	public static List<Entity> IDF_Modalità2(List<Entity> eLst,MongoCollection<Document> coll, List<String> users){
		try {
			if(eLst != null && coll != null && users != null){
				double idf_num = users.size();
				
				for(Entity e: eLst){
					double idf_den = 0.0;
					if(e != null){
						for(String u : users){
							List<Entity> entitiesByUser = HtmlAnnotation.getTopicsByUser(u, coll);
							if(entitiesByUser.contains(e) && isInTestMonths(entitiesByUser.get(entitiesByUser.indexOf(e)).getDate())){
								idf_den += 1.0;
								System.out.println(idf_den);
							}
						}
					}
					if(idf_den != 0){
						double tf = e.getTf_idf();
						e.setTf_idf(tf * Math.log(idf_num/idf_den));
					}
					else
						e.setTf_idf(0);
				}
			}
			System.out.println(eLst);
			return eLst;
		}
		catch(Exception ex){
			System.out.println(ex.toString());
			return new ArrayList<Entity>();
		}
	}

	//MODALITA' NEWS
	public static List<String> TestNews(List<String> users, MongoCollection<Document> coll){
		try{
			
			HashMap<String, List<Entity>> userToEntity = new	 HashMap<String,List<Entity>>();
			List<List<Entity>> s = new ArrayList<List<Entity>>();
			List<Entity> testEntities = new ArrayList<Entity>();
			int idf_num = 0;
			
			for(String user: users)
			{
				userToEntity.put(user,TF_Modalità2(user,coll));
				idf_num += getDocsInTestMonths(user, coll).size();
				
				List<Entity> entities = HtmlAnnotation.getTopicsByUser(user, coll);
				
				for(Entity e: entities){
					if(e != null && isInTestMonths(e.getDate()) && !testEntities.contains(e)){
						e.setCount(1);
						testEntities.add(e);				
					}
					if(e != null && isInTestMonths(e.getDate()) && testEntities.contains(e)){
						testEntities.get(testEntities.indexOf(e)).setCount(testEntities.get(testEntities.indexOf(e)).getCount()+1);
					}
				}
			}
			
			Iterator<Entry<String, List<Entity>>> it = userToEntity.entrySet().iterator();
			while(it.hasNext()) {
				Map.Entry<String, List<Entity>> currentUser = it.next();
				
				for(Entity e: currentUser.getValue()){
					double tf = e.getTf_idf();
					
					double idf_den = testEntities.get(testEntities.indexOf(e)).getCount();
					double idf =(tf*Math.log(idf_num/idf_den));
					e.setTf_idf(idf);
					
				}
			}
			List<String> sim = new ArrayList<String>();
			it = userToEntity.entrySet().iterator();
			
			while(it.hasNext()) {
				Map.Entry<String, List<Entity>> currentUser = it.next();
				Iterator<Entry<String, List<Entity>>> it_2 = userToEntity.entrySet().iterator();
				
				while(it_2.hasNext()) {
					
					Map.Entry<String, List<Entity>> userToMatch = it_2.next();
					double val = CalculateNewsSimilarity(currentUser.getValue(),userToMatch.getValue());
					sim.add(currentUser.getKey() + "-" + userToMatch.getKey() + " " + val);
				}
			}
			return sim;
				
		} catch(Exception ex){
			System.out.println(ex.toString());
			return new ArrayList<String>();
		}
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
