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
			
			//Statistiche
//			Statistic.applyQuery();
		
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
