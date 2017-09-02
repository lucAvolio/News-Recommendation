package profiling;

import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;

import org.bson.Document;
import org.bson.types.ObjectId;
import org.json.*;

import com.mongodb.BasicDBObject;
import com.mongodb.MongoClient;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.util.JSON;

import jdk.nashorn.internal.parser.JSONParser;
import model.AnnotationObject;
import model.Entity;
import tagMe.TagMeHandler;

public class HtmlAnnotation {

	public static void getAnnotationByUser(String user_id) {
		AnnotationObject annotationObject = new AnnotationObject();


		/*Get user's html */

		//CONNECT TO DB
		MongoClient mongoClient = new MongoClient("localhost",27017);		
		// Now connect to your databases
		MongoDatabase db = mongoClient.getDatabase("twitterDB");
		//Collection containng html
		MongoCollection<Document> coll = db.getCollection("html");

		BasicDBObject q = new BasicDBObject("id_user",user_id);


		//Get documents
		FindIterable<Document> myHtmlTemp = coll.find(q);
		MongoCursor<Document> myHtml = myHtmlTemp.iterator();

		while(myHtml.hasNext()){
			Document d = myHtml.next();
			try {
				String annotations = d.getOrDefault("annotations", null).toString();
				JSONObject tmp = new JSONObject(annotations);

				//Get annotation field  		    		
				JSONArray m = (JSONArray) tmp.get("annotations");

				String url = d.getOrDefault("html", "").toString();

				for (int j=0; j<m.length(); j++){
					Object spots = new Object();

					JSONObject myObject = m.getJSONObject(j);
					if(myObject.has("spot"))
						spots= myObject.get("spot");

					annotationObject.addSingleSpotToAnnotation(spots.toString());

					if(myObject.has("dbpedia_categories")){
						List<String> cat = new ArrayList<String>();

						JSONArray arr = new JSONArray(myObject.get("dbpedia_categories").toString());
						//      				dbpedia_categories = myObject.get("dbpedia_categories");

						for(int i=0; i<arr.length(); i++)
							cat.add(arr.getString(i).toString());

						annotationObject.addCategoryBySpot(spots.toString(), cat);
					}


					annotationObject.setUrl(url);
				}


			} catch(NullPointerException e) {
				System.out.println("annotation is null");
			}
		}

		//Close collection
		myHtml.close();
		//Close Mongo connection
		mongoClient.close();
	}

	public static List<Entity> getTopicsByUser(String user_id, MongoCollection<Document> coll) {		

		/*Get user's html */

		//		//CONNECT TO DB
		//		MongoClient mongoClient = new MongoClient("localhost",27017);		
		//		// Now connect to your databases
		//		MongoDatabase db = mongoClient.getDatabase("twitterDB");
		//Collection containng html
		//		MongoCollection<Document> coll = db.getCollection("html");

		BasicDBObject q = new BasicDBObject("id_user",user_id);


		//Get documents
		FindIterable<Document> myHtmlTemp = coll.find(q);
		MongoCursor<Document> myHtml = myHtmlTemp.iterator();
		List<Entity> entities = new ArrayList<Entity>();

		while(myHtml.hasNext()){
			Document d = myHtml.next();
			try {
				String url = d.getOrDefault("url", null).toString();
				if(!url.contains("https://t.co")) {
					String topics = d.getOrDefault("topics", null).toString();
					JSONObject tmp = new JSONObject(topics);
					JSONArray tmpArray = new JSONArray();
					Iterator x = tmp.keys();

					while(x.hasNext()) {
						String key = (String) x.next();
						tmpArray.put(tmp.get(key));
					}
					//Get topics and social tags

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
				}
			} catch(NullPointerException e) {
				System.out.println("Error in getTopicsByUser, probably annotation is null: " + e.toString());
			}



		}
		//Close collection
		myHtml.close();
		
		//Delete null values if any
		List<Entity> toRemove = new ArrayList<Entity>();
		for(Entity e: entities) {
			if(e.getName().equals(""))
				toRemove.add(e);				
		}
		entities.removeAll(toRemove);
		return entities;
	}	
}
