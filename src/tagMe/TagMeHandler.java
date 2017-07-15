package tagMe;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

public class TagMeHandler {
	
	public static String getAnnotations(String txt) throws Exception{
		
		//TagMe Key
		String tagMe_key = "fd99161a-2ed9-458c-b2b9-f2ad12bd9ef7-843339462";
		
		//Text to process
		//String txt = "Schumacher%20won%20the%20race%20in%20Indianapolis";
		
		String url = "https://tagme.d4science.org/tagme/tag?";
		
		URL u = new URL(url);		
		HttpsURLConnection con = (HttpsURLConnection) u.openConnection();
		
		//Add request header
		con.setRequestMethod("POST");
		con.setRequestProperty("User-Agent", "Mozilla/5.0");
		con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
		
		String urlParameters = "lang=en&include_categories=true&gcube-token="+tagMe_key+"&text="+txt;
		
		//Send post request
		con.setDoOutput(true);
		DataOutputStream wr = new DataOutputStream(con.getOutputStream());
		wr.writeBytes(urlParameters);
		wr.flush();
		wr.close();
		
		int responseCode = con.getResponseCode();
		System.out.println("\nSending 'POST' request to URL : " + url);
		System.out.println("Post parameters : " + urlParameters);
		System.out.println("Response Code : " + responseCode);

		BufferedReader in = new BufferedReader(
		        new InputStreamReader(con.getInputStream()));
		String inputLine;
		StringBuffer response = new StringBuffer();

		while ((inputLine = in.readLine()) != null) {
			response.append(inputLine);
		}
		in.close();

		//print result
		System.out.println(response.toString());
		return response.toString();
		
	
	}
}
