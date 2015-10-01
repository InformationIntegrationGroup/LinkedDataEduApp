package org.example.dbpedia;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;

import org.json.JSONArray;
import org.json.JSONObject;

public class GenerateFeatures {

	public static void main(String[] args) throws IOException {
		PrintWriter pw = new PrintWriter("obj_features.csv");
		pw.println("2014-01,2014-02,2014-03,2014-04,2014-05,2014-06,2014-07,2014-08,2014-09,2014-10,2014-11,2014-12,Rarity,Object Property 1,Object Property 2");
		BufferedReader br = new BufferedReader(new FileReader(new File(args[0])));
		String line = null;
		while ((line = br.readLine()) != null) {
			line = "http://dbpedia.org/resource/" + line.replace(" ", "_");
			for (int i = 1; i <= 12; i++) {
				if (i == 1) {
					pw.print(WikipediaStat.getWikipediaCount(i, line.replace("http://dbpedia.org/resource/", "")));
				}
				else {
					pw.print("," + WikipediaStat.getWikipediaCount(i, line.replace("http://dbpedia.org/resource/", "")));
				}
			}
			String queryString = String.format("select distinct count(*) as ?num where { ?s ?p <%s>. }", line);
			String responseString = HTTPClient.postRequestToTripleStore(
					"http://dbpedia.org/sparql", queryString);
			try {
				JSONObject obj = new JSONObject(responseString);
				JSONArray results = obj.getJSONObject("results").getJSONArray("bindings");
				String num = results.getJSONObject(0).getJSONObject("num").getString("value");
				pw.print("," + num);	
			} catch(Exception e) {
				pw.print("," + 0);
			}
			queryString = String.format("SELECT ?y (COUNT(distinct ?o) AS ?num) WHERE{?y ?p <%s> . ?y ?q ?o.?y rdfs:label ?label.?y a owl:Thing.?q a owl:ObjectProperty.?p a owl:ObjectProperty.}GROUP BY ?y", line);
			responseString = HTTPClient.postRequestToTripleStore(
					"http://dbpedia.org/sparql", queryString);
			try {
				JSONObject obj = new JSONObject(responseString);
				JSONArray results = obj.getJSONObject("results").getJSONArray("bindings");
				int num = 0;
				for (int i = 0 ; i < results.length(); i++) {
					num += Integer.parseInt(results.getJSONObject(0).getJSONObject("num").getString("value"));
				}
				pw.print("," + num);	
			} catch(Exception e) {
				pw.print("," + 0);
			}
			queryString = String.format("SELECT ?y (COUNT(distinct ?s) AS ?num) WHERE{?y ?p <%s> .?s ?q ?y. ?y rdfs:label ?label. ?y a owl:Thing. ?q a owl:ObjectProperty. ?p a owl:ObjectProperty.}GROUP BY ?y", line);
			responseString = HTTPClient.postRequestToTripleStore(
					"http://dbpedia.org/sparql", queryString);
			try {
				JSONObject obj = new JSONObject(responseString);
				JSONArray results = obj.getJSONObject("results").getJSONArray("bindings");
				int num = 0;
				for (int i = 0 ; i < results.length(); i++) {
					num += Integer.parseInt(results.getJSONObject(0).getJSONObject("num").getString("value"));
				}
				pw.print("," + num);	
			} catch(Exception e) {
				pw.print("," + 0);
			}
			pw.println();
			pw.flush();
		}
		br.close();
		pw.close();
	}

}
