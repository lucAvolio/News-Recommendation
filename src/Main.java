import java.util.Calendar;
import java.util.List;

import model.Entity;
import persistence.MongoDB;
import profiling.HtmlAnnotation;
import profiling.UserProfiler;
import tagMe.OpenCalaisHandler;
import tagMe.TagMeHandler;

public class Main {

	public static void main(String[] args) {
		try {
//			MongoDB.getUsersAnnotations();
//			MongoDB.saveTopicsToDB(); //Salva i topic sul db, per ogni pagina scaricata
			MongoDB.profileUser();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
