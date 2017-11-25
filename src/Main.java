import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.bson.Document;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import model.Entity;
import model.Site;
import persistence.MongoDB;
import profiling.HtmlAnnotation;
import profiling.Similarity;
import profiling.UserProfiler;
import prompter.Recommender;
import tagMe.OpenCalaisHandler;
import tagMe.TagMeHandler;
import testing.TestReccomendation;
import utils.FileConfigReader;
import utils.Statistic;

public class Main {

	public static void main(String[] args) {
		try {
//			MongoDB.saveTopicsToDB(); //Salva i topic sul db, per ogni pagina scaricata
//			MongoDB.profileUser();
//			Recommender.RecommendNews();
			TestReccomendation.TestReccomendation();
			
			
//			Entity e1 = new Entity();
//			e1.setTf_idf(9);
//			e1.setName("A");
//			Entity e2 = new Entity();
//			e2.setTf_idf(20);
//			e2.setName("B");
//			Entity e3 = new Entity();
//			e3.setTf_idf(30);
//			e3.setName("C");
//			Entity e4 = new Entity();
//			e4.setTf_idf(40);
//			e4.setName("D");
//			Entity e5 = new Entity();
//			e5.setTf_idf(48);
//			e5.setName("E");
//			Entity e6 = new Entity();
//			e6.setTf_idf(55);
//			e6.setName("F");
//			Entity e7 = new Entity();
//			e7.setTf_idf(61);
//			e7.setName("G");
//			Entity e8 = new Entity();
//			e8.setName("H");
//			e8.setTf_idf(66);
//			
//			Entity e9 = new Entity();
//			e9.setTf_idf(8);
//			e9.setName("A");
//			Entity e10 = new Entity();
//			e10.setTf_idf(20);
//			e10.setName("B");
//			Entity e11 = new Entity();
//			e11.setTf_idf(31);
//			e11.setName("C");
//			Entity e12 = new Entity();
//			e12.setTf_idf(42);
//			e12.setName("D");
//			Entity e13 = new Entity();
//			e13.setTf_idf(51);
//			e13.setName("E");
//			Entity e14 = new Entity();
//			e14.setTf_idf(61);
//			e14.setName("F");
//			Entity e15 = new Entity();
//			e15.setTf_idf(68);
//			e15.setName("G");
//			Entity e16 = new Entity();
//			e16.setTf_idf(74);
//			e16.setName("H");
//			
//			List<Entity> eLst1 = new ArrayList<Entity>();
//			eLst1.add(e1);
//			eLst1.add(e2);
//			eLst1.add(e3);
//			eLst1.add(e4);
//			eLst1.add(e5);
//			eLst1.add(e6);
//			eLst1.add(e7);
//			eLst1.add(e8);
//			List<Entity> eLst2 = new ArrayList<Entity>();
//			eLst2.add(e9);
//			eLst2.add(e10);
//			eLst2.add(e11);
//			eLst2.add(e12);
//			eLst2.add(e13);
//			eLst2.add(e14);
//			eLst2.add(e15);
//			eLst2.add(e16);
//			double val = TestReccomendation.CalculateSimilarityPearson(eLst1, eLst2);
//			System.out.println(val);
		
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
