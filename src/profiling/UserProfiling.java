package profiling;

import java.util.ArrayList;
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
import tagMe.TagMeHandler;

public class UserProfiling {

	public static void profileUser(String user_id) {
		
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
        
      	int i = 1;
      	while(myHtml.hasNext()){
      		Document d = myHtml.next();
      		try {
      		String annotations = d.getOrDefault("annotations", null).toString();
      		JSONObject tmp = new JSONObject(annotations);
      		
      		//Get annotation field  		    		
      		JSONArray m = (JSONArray) tmp.get("annotations");
      		
      		for (int j=0; j<m.length(); j++){
      			Object spots = new Object();
      			Object dbpedia_categories = new Object();
      			
      			JSONObject myObject = m.getJSONObject(j);
//      			System.out.println(i+")" + myObject.toString());

      			if(myObject.has("spot"))
      				spots= myObject.get("spot");
      			if(myObject.has("dbpedia_categories"))
      				dbpedia_categories = myObject.get("dbpedia_categories");
      			System.out.println("___Link "+i + "___");
      			System.out.println("Spot: " + spots.toString());
      			System.out.println("Dbpedia_categories: " + dbpedia_categories.toString());
      			
      		}
      		
      		
      		} catch(NullPointerException e) {
      			System.out.println("annotation is null");
      		}
      		i++;
      	}
      	
      	//Close collection
      	myHtml.close();
      	//Close Mongo connection
      	mongoClient.close();
		
		
		/*Get annotations from user*/
		
		
	}
	
	
	

}
