import persistence.MongoDB;
import profiling.UserProfiling;
import tagMe.TagMeHandler;

public class Main {

	public static void main(String[] args) {
		try {
//			MongoDB.getUsersAnnotations();
			UserProfiling.profileUser("19299339");
			
			//TagMeHandler.getAnnotations("Schumacher%20won%20the%20race%20in%20Indianapolis");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
