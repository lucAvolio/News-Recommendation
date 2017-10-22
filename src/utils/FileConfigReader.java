package utils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class FileConfigReader {

	private static final String FILENAME = "app.config";

	public static String ReadKey(String key) {

		BufferedReader br = null;
		FileReader fr = null;
		String res = "";

		try {

			//br = new BufferedReader(new FileReader(FILENAME));
			fr = new FileReader(FILENAME);
			br = new BufferedReader(fr);

			String sCurrentLine;

			while ((sCurrentLine = br.readLine()) != null) {
				if(sCurrentLine.contains(key)){
					res =  sCurrentLine.substring(sCurrentLine.indexOf('=')+1);
					break;
				}
			}

		} catch (IOException e) {
			
			e.printStackTrace();
			return null;

		} finally {

			try {

				if (br != null)
					br.close();

				if (fr != null)
					fr.close();

			} catch (IOException ex) {
				
				ex.printStackTrace();

			}

		}
		
		return res;

	}
}
