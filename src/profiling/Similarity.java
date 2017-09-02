package profiling;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import model.Entity;
import model.User;

public class Similarity {

	public static void CalculateSimilarity(Map<String,List<Entity>> entities, int time_fragment){
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
						System.out.println("Utente " + u.getUser_id() + " - Utente " + userToMatch.getKey());
						double simil = cosineSimilarity(currentUser.getValue(), userToMatch.getValue(), time_fragment);
						System.out.println(simil);
						similarity.add(userToMatch.getKey() + ": " + simil);
						
						
					}
					
				}
//				System.out.println("_____________________");
				System.out.println(u.getUser_id());
//				System.out.println(similarity.toString());
//				u.setSimilarity(similarity);
				
				users2Similarity.add(u);
				System.out.println("_____________________");
			}
			
		}
	}
	
	private static double cosineSimilarity(List<Entity> eLst1, List<Entity> eLst2, int time_fragment) {
		double result = 0.0;
		
		if(eLst1 != null && eLst2 != null && eLst1.size()>0 && eLst2.size()>0){
			double numerator = 0.0;
			double denominator_1 = 0.0;
			double denominator_2 = 0.0;
			
			for(Entity e1: eLst1){
				for(Entity e2: eLst2){
					//se le due entità sono nello stesso fragment e hanno stesso nome
					
					if(e1 != null && e2 != null && e1.getName().equals(e2.getName()) && UserProfiler.isSameFragment(e1, e2, time_fragment)) {
						numerator += (double) e1.getTf_idf() * e2.getTf_idf();
					}
					denominator_2 += (double) Math.pow(e2.getTf_idf(), 2.0);				
				}
				denominator_1 += (double) Math.pow(e1.getTf_idf(), 2.0);
			}
			result = (float)numerator/Math.sqrt((float) denominator_1 * denominator_2);
		}

//		if(eLst1 != null && eLst1.size() > 0 && eLst2 != null && eLst2.size() > 0) {
//			double numerator = 0.0;
//			double denominator_1 = 0.0;
//			double denominator_2 = 0.0;
//			//per ogni entità, calcolo la coseno similarità
//			for(Entity e1: eLst1) {
//
//				for(Entity e2: eLst1) {
//					//se le due entità hanno lo stesso nome e sono nello stesso frame
//					if(e1.getName().equals(e2.getName()) && UserProfiler.isSameFragment(e1,e2, 1)) {
//						//calcolo la coseno similarità
//						numerator += (e1.getTf_idf() * e2.getTf_idf());
//						denominator_1 += Math.pow(e1.getTf_idf(), 2.0);
//						denominator_2 += Math.pow(e2.getTf_idf(), 2.0);
//					}
//				}
//				
//			}
////			System.out.println("numeratore:" + numerator);
////			System.out.println("denominatore1" + denominator_1);
////			System.out.println("denominatore2" + denominator_2);
//			result = (float)numerator/Math.sqrt((float) denominator_1 * denominator_2);
////			System.out.println("Risultato:" + result);
//		}
		
		
		
		return result;
	}

}
