package org.example.dbpedia;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;


public class HTTPClient {
	public static String postRequestToTripleStore(String serviceURL, String query) 
			throws ClientProtocolException, IOException {

		List<NameValuePair> formParams = new ArrayList<NameValuePair>();
		formParams.add(new BasicNameValuePair("query", query));
		formParams.add(new BasicNameValuePair("queryLn", "SPARQL"));
		HttpPost httpPost = new HttpPost(serviceURL);
		httpPost.setEntity(new UrlEncodedFormEntity(formParams, "UTF-8"));
		HttpClient httpClient = new DefaultHttpClient();
		httpPost.setHeader("Accept", "application/sparql-results+json");
		httpPost.setHeader("charset", "UTF-8");
		HttpResponse response = httpClient.execute(httpPost);
		HttpEntity entity = response.getEntity();
		StringBuilder responseString = new StringBuilder();
		if (entity != null) {
			BufferedReader buf = new BufferedReader(new InputStreamReader(entity.getContent(),"UTF-8"));
			String line = buf.readLine();
			while(line != null) {
				responseString.append(line);
				responseString.append('\n');
				line = buf.readLine();
			}
		}
		return responseString.toString();
	}
}
