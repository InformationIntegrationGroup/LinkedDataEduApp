package org.example.dbpedia;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.http.client.ClientProtocolException;
import org.json.JSONArray;
import org.json.JSONObject;

public class DBPediaQueryType {

	public static void main(String[] args) throws ClientProtocolException, IOException, InterruptedException {
		PrintWriter pw = new PrintWriter("obj_type.csv");
		pw.println("sub,type");
		BufferedReader br = new BufferedReader(new FileReader(new File(args[0])));
		String line = null;
		while ((line = br.readLine()) != null) {
			String uri = "http://dbpedia.org/resource/" + line.replace(" ", "_");
			String response = getQueryResponse(String.format("SELECT ?type \n WHERE \n{<%s> a ?type .}", uri));
			try {
				JSONObject obj = new JSONObject(response);
				JSONArray results = obj.getJSONObject("results").getJSONArray("bindings");
				Set<String> interested = new HashSet<String>();
				for (int i = 0; i < results.length(); i++) {
					String type = results.getJSONObject(i).getJSONObject("type").getString("value");
					if (type.startsWith("http://dbpedia.org/ontology/")) {
						interested.add(type);
					}
				}
				Map<String, Boolean> mapping = new HashMap<String, Boolean>();
				for (String t : interested) {
					if (!t.contains("Wikidata")) {
						mapping.put(t, true);
					}
				}
				for (String t1 : interested) {				
					for (String t2 : interested) {
						if (!t1.equals(t2)) {
							response = getQueryResponse(String.format("ASK {<%s> rdfs:subClassOf <%s>}", t1, t2));
							obj = new JSONObject(response);
							if (obj.getBoolean("boolean")) {
								mapping.put(t2, false);
							}
						}
					}
				}
				for (Entry<String, Boolean> entry : mapping.entrySet()) {
					if (entry.getValue()) {
						pw.format("%s,%s\n", line, entry.getKey());
					}
				}
				pw.flush();
			}catch(Exception e) {

			}
			Thread.sleep(100);
		}
		br.close();
		pw.close();

	}

	private static String getQueryResponse(String query) throws ClientProtocolException, IOException {
		String response = HTTPClient.postRequestToTripleStore(
				"http://dbpedia.org/sparql", query);
		return response;
	}



}
