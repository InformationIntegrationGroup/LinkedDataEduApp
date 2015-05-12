package org.example.dbpedia;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

public class WikipediaStat {

	public static void main(String[] args) throws IOException {	
		BufferedReader br = new BufferedReader(new FileReader(new File(args[0])));
		String line = null;
		PrintWriter pw = new PrintWriter("subjects_wikipedia.csv");
		while ((line = br.readLine()) != null) {
			pw.print(line);
			for (int i = 1; i <= 12;i++) {
				pw.print("," + getWikipediaCount(i, line));
			}
			pw.println();
			pw.flush();
		}
		pw.close();
		br.close();
	}
	
	public static int getWikipediaCount(int month, String line) throws JSONException, IOException {
		String tmp = String.format("http://stats.grok.se/json/en/%d%02d/%s",2014,month,line.replace(" ", "_"));
		URL url = new URL(tmp);
		JSONObject obj = new JSONObject(new JSONTokener(url.openStream())).getJSONObject("daily_views");
		int sum = 0;
		for (Object key : obj.keySet()) {
			sum += obj.getInt(key.toString());
		}
		return sum;
	}

}
