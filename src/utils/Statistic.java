package utils;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.HashMap;
import java.util.Map;

import org.bson.Document;

import com.mongodb.BasicDBObject;
import com.mongodb.MongoClient;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;

public class Statistic {

	public static void applyQuery(){
		//CONNECT TO DB
		MongoClient mongoClient = new MongoClient("localhost",27017);		
		 // Now connect to your databases
         MongoDatabase db = mongoClient.getDatabase("twitterDB");
         
         MongoCollection<Document> coll = db.getCollection("tweets");
         
         String year = "2016";
         BasicDBObject q = new BasicDBObject();
         q.put("created_at", java.util.regex.Pattern.compile(year));
         FindIterable<Document> myDoc = coll.find(q);
         
         HashMap<String, Integer> map = new HashMap<String,Integer>(); 
         
         MongoCursor<Document> cursor = myDoc.iterator();
         while(cursor.hasNext()){
        	 Document d = cursor.next();
        	 String urls = d.get("urls").toString();
        	 String user = d.get("id_user").toString();
        	 if(!urls.contains("//t.co") && !urls.contains("[]")) {
        		 System.out.println(urls);
        		 System.out.println(user);
//        		 System.out.println(d.toString());
        		 if(map.containsKey(user)){
        			 int n = map.get(user) +1;
        			 map.put(user, n);
        		 }
        		 else {
        			 map.put(user, 1);
        		 }
        	 }   	 
         }
         cursor.close();
         System.out.println(map.toString());
         
         for(Map.Entry<String, Integer> entry: map.entrySet()){
			 try {
				 FileWriter w;
				 w = new FileWriter("utenti.txt");
				 BufferedWriter b;
				 b = new BufferedWriter(w);
	        	 if(entry.getValue() >= 8){
	      			 
	        			 b.write(entry.getKey() + ": "+ entry.getValue());
	        			 b.write("\n");
	        	 }
    		 } catch(Exception ex){
    			 System.out.println(ex.toString());
    		 }
         }
	}

}
