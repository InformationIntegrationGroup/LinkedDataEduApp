package org.example.dbpedia;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashSet;
import java.util.Set;

import org.apache.http.client.ClientProtocolException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

public class DBPediaLink {

	public static void main(String[] args) throws JSONException, ClientProtocolException, IOException {
		JSONArray array = new JSONArray(new JSONTokener(new FileInputStream(new File(args[0]))));
		PrintWriter pw = new PrintWriter("rank.csv");
		Set<String> uris = new HashSet<String>();
		for (int i = 0; i < array.length(); i++) {
			JSONObject obj = array.getJSONObject(i);
			String uri = obj.getString("uri");
			String queryString = String.format("select ?s where {<%s> ?p ?s minus {<%s> a ?s}. filter (!isLiteral(?s)) }", uri, uri);
			String responseString = HTTPClient.postRequestToTripleStore(
					"http://dbpedia.org/sparql", queryString);
			try {
				JSONObject t = new JSONObject(responseString);
				JSONArray results = t.getJSONObject("results").getJSONArray("bindings");
				for (int j = 0; j < results.length(); j++) {
					String s = results.getJSONObject(j).getJSONObject("s").getString("value");
					if (s.contains("http://dbpedia.org/resource/") && !s.contains("Category")) {
						pw.format("\"%s\",\"%s\"\n",uri,s);
						//uris.add(s);
					}
				}
			}catch(Exception e) {
				
			}
		}
		for (String s : uris) {
			pw.println(s);
		}
		pw.close();
	}

}
