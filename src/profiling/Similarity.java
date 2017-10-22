package profiling;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.swing.plaf.synth.SynthSpinnerUI;

import model.Entity;
import model.User;

public class Similarity {

	public static List<User> CalculateSimilarity(Map<String,List<Entity>> entities, int time_fragment){
		List<User> users2Similarity = new ArrayList<User>();
		if(entities != null && entities.size() > 0) {
			Iterator<Entry<String, List<Entity>>> it = entities.entrySet().iterator();
			
			while(it.hasNext()) {
				List<String> similarity = new ArrayList<>();
				Map.Entry<String, List<Entity>> currentUser = it.next();
				User u = new User();
				u.setUser_id(currentUser.getKey());
				
				
				Iterator<Entry<String, List<Entity>>> it_2 = entities.entrySet().iterator();
				while(it_2.hasNext()){
					Map.Entry<String, List<Entity>> userToMatch = it_2.next();
					if(!userToMatch.getKey().equals(u.getUser_id())) {
						double simil = cosineSimilarity(currentUser.getValue(), userToMatch.getValue(), time_fragment);
						System.out.println(simil);
						similarity.add(userToMatch.getKey() + ":"+simil);
					}
					
				}
				u.setSimilarity(similarity);		
				users2Similarity.add(u);
			}			
		}
		return users2Similarity;
	}
	
	public static double cosineSimilarity(List<Entity> eLst1, List<Entity> eLst2, int time_fragment) {
		double result = 0.0;
		
		if(eLst1 != null && eLst2 != null && eLst1.size()>0 && eLst2.size()>0){
			double numerator = 0.0;
			double denominator_1 = 0.0;
			double denominator_2 = 0.0;
			
			for(Entity e1: eLst1){
				for(Entity e2: eLst2){
					//se le due entità sono nello stesso fragment e hanno stesso nome
					
					if(e1 != null && e2 != null && e1.getName().equals(e2.getName()) && UserProfiler.isSameFragment(e1, e2, time_fragment)) {
						numerator += e1.getTf_idf() * e2.getTf_idf();
					}
					
				}
				denominator_1 += Math.pow(e1.getTf_idf(), 2.0);

			}
			
			for(Entity e2: eLst2) {
				denominator_2 += Math.pow(e2.getTf_idf(),2.0);
			}
			
			result = numerator/Math.sqrt(denominator_1 * denominator_2);
		}		
		return result;
	}

}
