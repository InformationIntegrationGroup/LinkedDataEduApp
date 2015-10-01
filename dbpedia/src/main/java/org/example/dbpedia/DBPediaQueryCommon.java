package org.example.dbpedia;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;

import org.apache.http.client.ClientProtocolException;
import org.json.JSONArray;
import org.json.JSONObject;

public class DBPediaQueryCommon {

	public static void main(String[] args) throws ClientProtocolException, IOException, InterruptedException {
		PrintWriter pw = new PrintWriter("pre_domain_range.csv");
		pw.println("pre,domain,range");
		BufferedReader br = new BufferedReader(new FileReader(new File(args[0])));
		String line = null;
		while ((line = br.readLine()) != null) {
			String uri = "dbpedia-owl:" + line;
			String domain = getQueryResponse(String.format("SELECT ?domain \n WHERE \n{%s rdfs:domain ?domain .}", uri));
			String range = getQueryResponse(String.format("SELECT ?range \n WHERE \n{%s rdfs:range ?range .}", uri));
			String domainType = "";
			String rangeType = "";
			try {
				JSONObject obj = new JSONObject(domain);
				JSONArray results = obj.getJSONObject("results").getJSONArray("bindings");
				domainType = results.getJSONObject(0).getJSONObject("domain").getString("value");
			}catch(Exception e) {

			}
			try{
				JSONObject obj = new JSONObject(range);
				JSONArray results = obj.getJSONObject("results").getJSONArray("bindings");
				rangeType = results.getJSONObject(0).getJSONObject("range").getString("value");
			}catch(Exception e) {
				
			}
			pw.format("%s,%s,%s\n", line, domainType, rangeType);
			pw.flush();
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
