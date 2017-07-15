 	package persistence;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCursor;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.ServerAddress;

import com.mongodb.client.MongoDatabase;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;

import org.bson.BasicBSONObject;
import org.bson.BsonDocument;
import org.bson.BsonObjectId;
import org.bson.BsonValue;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;

import com.mongodb.client.MongoCursor;
import static com.mongodb.client.model.Filters.*;
import com.mongodb.client.result.DeleteResult;
import static com.mongodb.client.model.Updates.*;
import com.mongodb.client.result.UpdateResult;

import tagMe.TagMeHandler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;

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
	
	public static void getUsersAnnotations(){
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
	       		pushAnnotations(user, coll);
	       		System.out.println(user);
	       		
	       		user_list.close();
	       	} 
	       	
	       	mongoClient.close();
	       	
	       	
			
		} catch (Exception ex) {
			System.out.println("Errore in getUsersAnnotations");
			System.out.println(ex.getMessage());
		}
	}
	
	public static void getHtmlFromUser(String userId) {
		
		try {
			
		}catch (Exception ex) {
			System.out.println("Errore in getHtmlFromUser");
			System.out.println(ex.getMessage());
		}
	}
	
	
	
}
