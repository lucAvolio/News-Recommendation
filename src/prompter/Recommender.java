package prompter;

import java.util.ArrayList;
import java.util.List;

import model.Entity;
import model.Site;
import model.User;
import persistence.MongoDB;
import utils.ExcelOutputWriter;
import utils.FileConfigReader;

public class Recommender {

	public static void RecommendNews(){
		double minSimilarityValue = Double.parseDouble(FileConfigReader.ReadKey("minSimilarityValue"));
		//Prendo gli utenti
		List<User> usersLst = MongoDB.getUsersInCollection();
		
		if(usersLst != null && usersLst.size() > 0) {
			//Per ogni utente
			for(User u: usersLst) {
				//get html for user
				List<Site> thisUserUrls = MongoDB.getHtmlFromUser(u.getUser_id().toString());			
				
				for(String s: u.getSimilarity()) {
					String userToCompare = s.substring(0, s.indexOf(":"));
					userToCompare = userToCompare.replaceAll(" ", "");
					Double similarityToUser = Double.parseDouble(s.substring(s.indexOf(":")+1));
					//Valore oltre una certa soglia
					if(similarityToUser >= minSimilarityValue) {
						//get html
						List<Site> toCompareUserUrls = MongoDB.getHtmlFromUser(userToCompare);
						//suggest news
						List<Site> res = SuggestNews(thisUserUrls,toCompareUserUrls, userToCompare);
						ExcelOutputWriter.WriteRecommendation(res, u.getUser_id(),userToCompare);
					}
				}
				
			}
		}
		
	}
	
	public static List<Site> SuggestNews(List<Site> thisUser, List<Site> toCompareUser, String userToCompare) {
		
		List<Site> intersect = new ArrayList<Site>(thisUser);

		List<Site> diff = new ArrayList<Site>(thisUser);
		intersect.retainAll(toCompareUser);
		diff.removeAll(intersect);
		
		if(Integer.parseInt(FileConfigReader.ReadKey("suggestionType")) == 1) {
			diff = RefineSuggestionWithUserInterest(diff, userToCompare);
		}
		System.out.println(diff);
		return diff;
		
		
		
	}
	
	public static List<Site> RefineSuggestionWithUserInterest(List<Site> lst, String userToCompare) {
		List<Site> sites = new ArrayList<Site>();
		//CONFRONTARE CHE OGNI SITO INTERNET ABBIA DEI TOPIC CON TF_IDF > DI UNA CERTA SOGLIA PER L'UTENTE USERTOCOMPARE
		
		
		User u =  MongoDB.getUserById(userToCompare);
		System.out.println(sites);
		System.out.println(u.getTopics());
		if(u.getTopics() != null) {
			List<Entity> entities = u.getTopics();
			

		}
		
		
		
		
		return sites;
	}

}
