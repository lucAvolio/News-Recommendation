package tagMe;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.PostMethod;

public class OpenCalaisHandler {
	private static final String CALAIS_URL = "https://api.thomsonreuters.com/permid/calais";
	private static String uniqueAccessKey = "DJvFRk5H7XOq0QFDt7a5BdA1SjVPvKUh";
	private static HttpClient client;

	public static String getTopics(String txt) throws Exception {
		String url = "https://api.thomsonreuters.com/permid/calais";
		
		
		
		URL u = new URL(url);		
		HttpsURLConnection con = (HttpsURLConnection) u.openConnection();
		
		//Add request header
		con.setRequestMethod("POST");
		con.setRequestProperty("User-Agent", "Mozilla/5.0");
		con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
		con.setRequestProperty("Content-Type", "text/xml");
		con.setRequestProperty("outputFormat", "application/json");
		con.setRequestProperty("x-ag-access-token", uniqueAccessKey);
		
		String urlParameters = txt;
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
