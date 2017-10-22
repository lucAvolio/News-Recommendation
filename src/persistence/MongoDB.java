package persistence;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCursor;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.ServerAddress;

import com.mongodb.client.MongoDatabase;
import com.mongodb.client.DistinctIterable;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;

import org.bson.BasicBSONObject;
import org.bson.BsonDocument;
import org.bson.BsonObjectId;
import org.bson.BsonValue;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;
import org.json.JSONArray;
import org.json.JSONObject;

import com.mongodb.client.MongoCursor;
import static com.mongodb.client.model.Filters.*;
import com.mongodb.client.result.DeleteResult;
import static com.mongodb.client.model.Updates.*;
import com.mongodb.client.result.UpdateResult;

import model.Entity;
import model.Site;
import model.User;
import profiling.Similarity;
import profiling.UserProfiler;
import tagMe.OpenCalaisHandler;
import tagMe.TagMeHandler;
import utils.FileConfigReader;
import utils.StringConverter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;

import javax.rmi.ssl.SslRMIClientSocketFactory;
import javax.swing.plaf.synth.SynthSpinnerUI;

public class MongoDB {


	public static void pushAnnotations(String userId, MongoCollection<Document> coll) {
		try {

			//CONNECT TO DB

			BasicDBObject q = new BasicDBObject("id_user",userId);

			//Get documents
			//	        MongoCursor<Document> myHtml = coll.find(q).iterator();
			FindIterable<Document> myHtmlTemp = coll.find(q);
			MongoCursor<Document> myHtml = myHtmlTemp.iterator();


			while(myHtml.hasNext()){
				Document d = myHtml.next();
				String htmlToProcess = d.getOrDefault("html", null).toString();
				String url = d.getOrDefault("url", null).toString();
				String html_id = d.getOrDefault("_id", null).toString();

				String annotation = TagMeHandler.getAnnotations(htmlToProcess);
				System.out.println(annotation);          	

				BasicDBObject newDocument = new BasicDBObject();
				newDocument.put("annotations", annotation);
				BasicDBObject searchQuery = new BasicDBObject("_id", new ObjectId(html_id));

				coll.updateOne(searchQuery, new BasicDBObject("$set", newDocument));
				myHtml.close();
			}

			//Close Mongo connection
			//	        mongoClient.close();

		} catch (Exception ex) {
			System.out.println("Errore in pushAnnotations");
			System.out.println(ex.getMessage());
		}
	};

	public static void profileUser(){
		try {
			int time_fragment = Integer.parseInt(FileConfigReader.ReadKey("time_fragment"));
			//CONNECT TO DB
			MongoClient mongoClient = new MongoClient("localhost",27017);		
			// Now connect to databases
			MongoDatabase db = mongoClient.getDatabase("twitterDB");
			//Collection containng html
			MongoCollection<Document> coll = db.getCollection("html");

			MongoCursor<String> user_list = coll.distinct("id_user",String.class).iterator();
			List<List<Entity>> e = new ArrayList<>();
			HashMap<String,List<Entity>> entities = new HashMap<>();

			while(user_list.hasNext()) {
				String user = user_list.next();

				System.out.println(user.toString());

				List<Entity> lst = UserProfiler.profileUser(user,coll, time_fragment);
				entities.put(user, lst);
				

			} 
			user_list.close();
			mongoClient.close();
			saveEntityToDB(entities);
			List<User> users2Similarity = Similarity.CalculateSimilarity(entities, time_fragment);
			SaveSimilarityToDB(users2Similarity);
		} catch (Exception ex) {
			System.out.println("Errore in getUsersTopics: "+ex.toString());

		}


	}

	public static List<Site> getHtmlFromUser(String userId) {
		List<Site> urls = new ArrayList<Site>();
		try {
			//CONNECT TO DB
			MongoClient mongoClient = new MongoClient("localhost",27017);		
			// Now connect to your databases
			MongoDatabase db = mongoClient.getDatabase("twitterDB");
			//Collection containing html
			MongoCollection<Document> coll = db.getCollection("html");
			
			BasicDBObject q = new BasicDBObject("id_user",userId);

			//Get documents
			//        MongoCursor<Document> myHtml = coll.find(q).iterator();
			FindIterable<Document> myHtmlTemp = coll.find(q);
			MongoCursor<Document> myHtml = myHtmlTemp.iterator();
			
			while(myHtml.hasNext()) {
				Site site = new Site();
				Document html = myHtml.next();
				String url = html.getOrDefault("url", "").toString();
				
				if(!url.contains("http://t.co") && !url.contains("https://t.co") && !url.isEmpty()){
					String date = html.getOrDefault("created_at", "").toString();
					String topics = html.getOrDefault("topics", "{}").toString();
					
					List<Entity> entities = new ArrayList<Entity>();
					site.setUrl(url);
					site.setDate(date);
					
					JSONObject tmp = new JSONObject(topics);
					JSONArray tmpArray = new JSONArray();
					Iterator x = tmp.keys();
					
					while(x.hasNext()) {
						String key = (String) x.next();
						tmpArray.put(tmp.get(key));
					}
					
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
							e.setDate(date);
							entities.add(e);
				
						}	
					}
					
					site.setEntities(entities);
					
					urls.add(site);
				}	
			} 
			myHtml.close();

			mongoClient.close();
			

		}catch (Exception ex) {
			System.out.println("Errore in getHtmlFromUser");
			System.out.println(ex.getMessage());
			return null;
		}
		
		return urls;
	}

	public static void saveTopicsToDB() {
		try {
			//CONNECT TO DB
			MongoClient mongoClient = new MongoClient("localhost",27017);		
			// Now connect to your databases
			MongoDatabase db = mongoClient.getDatabase("twitterDB");
			//Collection containng html
			MongoCollection<Document> coll = db.getCollection("html");

			MongoCursor<String> user_list = coll.distinct("id_user",String.class).iterator();

			while(user_list.hasNext()) {
				String user = user_list.next();
				pushTopics(user, coll);
				System.out.println(user);


			} 
			user_list.close();

			mongoClient.close();



		} catch (Exception ex) {
			System.out.println("Errore in saveTopicsToDB");
			System.out.println(ex.getMessage());
		}
	}

	public static void pushTopics(String userId, MongoCollection<Document> coll) {
		try {
			//CONNECT TO DB

			BasicDBObject q = new BasicDBObject("id_user",userId);

			//Get documents
			//        MongoCursor<Document> myHtml = coll.find(q).iterator();
			FindIterable<Document> myHtmlTemp = coll.find(q);
			MongoCursor<Document> myHtml = myHtmlTemp.iterator();
			int i= 0;

			while(myHtml.hasNext()){

				Document d = myHtml.next();
				try {
					String htmlToProcess = d.getOrDefault("html", null).toString();
					String url = d.getOrDefault("url", null).toString();
					String html_id = d.getOrDefault("_id", null).toString();

					String annotation = OpenCalaisHandler.getTopics(htmlToProcess);
					System.out.println(i + ") "+ annotation);          	

					BasicDBObject newDocument = new BasicDBObject();
					newDocument.put("topics", annotation);
					BasicDBObject searchQuery = new BasicDBObject("_id", new ObjectId(html_id));

					coll.updateOne(searchQuery, new BasicDBObject("$set", newDocument));
				} catch (Exception ex) {
					System.out.println(ex.toString());
				}
				i++;

			}
			myHtml.close();


			//Close Mongo connection
			//        mongoClient.close();

		} catch(Exception ex) {
			System.out.println("Errore in pushTopics");
			System.out.println(ex.getMessage());
		}
	}

	public static void saveEntityToDB(HashMap<String, List<Entity>> map) {
		//CONNECT TO DB
		MongoClient mongoClient = new MongoClient("localhost",27017);		
		// Now connect to your databases
		MongoDatabase db = mongoClient.getDatabase("twitterDB");
		//Collection containng html
		MongoCollection<Document> coll = db.getCollection("users");

		if(map != null && map.size() > 0) {
			for(Map.Entry<String, List<Entity>> entry: map.entrySet()) {
				try {
					String user_id = entry.getKey();
					List<Entity> e = entry.getValue();
					FindIterable<Document> iterable = coll.find(new Document("id_user", user_id));
					if(iterable.first() != null) {
						BasicDBObject newDocument = new BasicDBObject();
						newDocument.put("topics", e.toString());
						BasicDBObject searchQuery = new BasicDBObject("id_user", user_id);
						coll.updateOne(searchQuery, new BasicDBObject("$set", newDocument));
					} else {
						Document document = new Document().append("id_user", user_id)
								.append("topics", e.toString());
						coll.insertOne(document);		
					}
				} catch (Exception ex) {
					System.out.println("Error in saveEntityToDB");
					System.out.println(ex.toString());
				}
			}
		}

		mongoClient.close();
	}

	public static void SaveSimilarityToDB(List<User> users2Similarity){
		if(users2Similarity != null && users2Similarity.size() > 0) {
			for(User u: users2Similarity){
				List<String> sim = u.getSimilarity();
				String user_id = u.getUser_id();
				try {
					//CONNECT TO DB
					MongoClient mongoClient = new MongoClient("localhost",27017);		
					// Now connect to your databases
					MongoDatabase db = mongoClient.getDatabase("twitterDB");
					//Collection containng html
					MongoCollection<Document> coll = db.getCollection("users");
					FindIterable<Document> iterable = coll.find(new Document("id_user", user_id));
					if(iterable != null) {
						BasicDBObject newDocument = new BasicDBObject();
						newDocument.put("similarity", sim.toString());
						BasicDBObject searchQuery = new BasicDBObject("id_user", user_id);
						coll.updateOne(searchQuery, new BasicDBObject("$set", newDocument));
					} else {
						Document document = new Document().append("id_user", user_id)
								.append("similarity", sim.toString());
						coll.insertOne(document);		
					}


				}catch(Exception ex) {
					System.out.println("ERROR in SaveSimilarityToDB");
					System.out.println(ex.toString());
				}
			}
		}

	}

	public static List<User> getUsersInCollection(){
		List<User> toReturn = new ArrayList<User>();
		try {
			
			//CONNECT TO DB
			MongoClient mongoClient = new MongoClient("localhost",27017);		
			// Now connect to your databases
			MongoDatabase db = mongoClient.getDatabase("twitterDB");

			MongoCollection<Document> usersColl = db.getCollection("users");

			MongoCursor<Document> myDoc = usersColl.find().iterator();
			

			while(myDoc.hasNext()) {
				//Crazione utente
				User u = new User();
				Document d = myDoc.next();
				String id_user = d.getOrDefault("id_user", "").toString();
				u.setUser_id(id_user);
				String s = d.getOrDefault("similarity", null).toString();	
				List<String> simil = StringConverter.FromStringToStringList(s);
				u.setSimilarity(simil);
				
				toReturn.add(u);
				
			}

			//Close Connection
			mongoClient.close();
			
			
		} catch (Exception ex) {
			System.out.println("Error in MongoDB.getUsersInCollection");
			System.out.println(ex.toString());
		}
		return toReturn;

	}

	public static User getUserById(String user_id){
		User toReturn = new User();
		try {
			
			//CONNECT TO DB
			MongoClient mongoClient = new MongoClient("localhost",27017);		
			// Now connect to your databases
			MongoDatabase db = mongoClient.getDatabase("twitterDB");

			MongoCollection<Document> usersColl = db.getCollection("users");
			

			Document myDoc = usersColl.find(new Document("id_user", user_id)).first();
			
			if(!myDoc.isEmpty()){
				String id_user = myDoc.getOrDefault("id_user", "").toString();
				toReturn.setUser_id(id_user);
				String s = myDoc.getOrDefault("similarity", null).toString();	
				List<String> simil = StringConverter.FromStringToStringList(s);
				toReturn.setSimilarity(simil);
				String topics = myDoc.getOrDefault("topics", "{}").toString();
				List<Entity> t = StringConverter.FromStringToEntityList(topics);
				System.out.println(t);
				toReturn.setTopics(t);
			}
			


			//Close Connection
			mongoClient.close();
			
			
		} catch (Exception ex) {
			System.out.println("Error in MongoDB.getUserById");
			System.out.println(ex.toString());
		}
		return toReturn;

	}

}
