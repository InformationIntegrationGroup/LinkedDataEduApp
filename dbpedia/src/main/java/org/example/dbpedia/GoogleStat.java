package org.example.dbpedia;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import java.net.URLEncoder;

import org.json.JSONObject;
import org.json.JSONTokener;

public class GoogleStat {

	public static void main(String[] args) throws IOException, InterruptedException {	
		BufferedReader br = new BufferedReader(new FileReader(new File(args[0])));
		String line = null;
		PrintWriter pw = new PrintWriter("subjects_google.csv");
		while ((line = br.readLine()) != null) {
			pw.print(line);
			String tmp = String.format("http://ajax.googleapis.com/ajax/services/search/web?v=1.0&q=%s", URLEncoder.encode(line, "UTF-8"));
			URL url = new URL(tmp);
			JSONObject obj = new JSONObject(new JSONTokener(url.openStream())).getJSONObject("responseData").getJSONObject("cursor");
			pw.println("," + obj.get("estimatedResultCount"));
			pw.flush();
			Thread.sleep(100);
		}
		pw.close();
		br.close();
	}

}
