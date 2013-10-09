package edu.isi.serverbackend.request;

import edu.isi.serverbackend.linkedData.*;
import edu.isi.serverbackend.feature.util.*;

import java.io.*;
import java.net.*;
import java.util.*;

import org.openrdf.query.*;
import org.openrdf.repository.*;
import org.json.*;

public class ConnectionRankRequest {
	private LinkedDataNode currentNode;
	private List<Sample> samples;
	String ratingResponse = "";
	//private RepositoryConnection repoConnection;
	
	public ConnectionRankRequest(LinkedDataNode currentNode){
		this.currentNode = currentNode;
		//this.repoConnection = currentNode.getRepoConnection();
		this.samples = new ArrayList<Sample>();
		
		
		try {
			retrieveSubjectConnections();
			retrieveObjectConnections();
			
		} catch (RepositoryException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (MalformedQueryException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (QueryEvaluationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} /*catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
		
	}
	
	public void retrieveSubjectConnections() throws RepositoryException, MalformedQueryException, QueryEvaluationException{
		currentNode.retrieveSubjectConnections(samples);
	}
	
	public void retrieveObjectConnections() throws RepositoryException, MalformedQueryException, QueryEvaluationException{
		currentNode.retrieveObjectConnections(samples);
	}
	
	public void rateInterestingness(){
		for(int i = 0; i < samples.size(); i++){
			samples.get(i).evalutateFeature();
		}
		try {
			URL url = new URL("http://127.0.0.1:8080/Maven_DataVisualization-0.0.1-SNAPSHOT/DemoServlet");//
			URLConnection modelConn = url.openConnection();
			modelConn.setDoInput(true);
			modelConn.setDoOutput(true);
			modelConn.setUseCaches(false);
			modelConn.setRequestProperty("Content-Type", 
						   "application/x-www-form-urlencoded");
			DataOutputStream modelInput = new DataOutputStream(modelConn.getOutputStream());
			//String header = "1,"+"2,"+"3\n";
			String content = "features=";
			for(int i = 0 ; i < samples.size(); i++){
				content += URLEncoder.encode(samples.get(i).getRarity()+",", "UTF-8")
						+ URLEncoder.encode(samples.get(i).getEitherNotPlace()+",", "UTF-8" )
						+ URLEncoder.encode(samples.get(i).getDifferentOccupation()+",", "UTF-8")
						+ URLEncoder.encode(samples.get(i).getRarity()+"\n", "UTF-8");
			}
			modelInput.writeBytes(content);
			modelInput.flush();
			modelInput.close();
			
			BufferedReader modelOutput = new BufferedReader(new InputStreamReader(modelConn.getInputStream()));
			String line = modelOutput.readLine();
			int index = 0;
				while (line != null){
					this.ratingResponse += line+"::";
					if(index < samples.size()){
						samples.get(index).setInterestingness(Double.parseDouble(line));
						index++;
					}
					line = modelOutput.readLine();
				}
				
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void sortConnections(){
		//algorithm: bubble sort
		boolean swap = true;
		Sample temp = null;
		
		while(swap){
			swap = false;
			for(int i = 0;i < samples.size()-1;i++){
				if(samples.get(i).getInterestingness() < samples.get(i+1).getInterestingness()){
					temp = samples.get(i+1);
					samples.set(i+1, samples.get(i));
					samples.set(i, temp);
					swap = true;
				}
			}
		}
	}
	
	public JSONObject exportD3JSON(int num) throws JSONException{
		JSONObject result = new JSONObject();
		JSONArray childrenArray = new JSONArray();
		
		for(int i = 0; i < num; i++){
			if(i >= samples.size())
				break;
			JSONObject newNode = new JSONObject();
			if(samples.get(i).getLink().isSubjectConnection()){
				newNode.put("name", samples.get(i).getLink().getObject().getName());
				newNode.put("uri", samples.get(i).getLink().getObject().getURI());
				newNode.put("relation", samples.get(i).getLink().getPredicate());
				newNode.put("rank", samples.get(i).getInterestingness());
			}
			else{
				newNode.put("name", samples.get(i).getLink().getSubject().getName());
				newNode.put("uri", samples.get(i).getLink().getSubject().getURI());
				newNode.put("relation", samples.get(i).getLink().getPredicate());
				newNode.put("rank", samples.get(i).getInterestingness());
			}
			childrenArray.put(newNode);
		}
	
		result.put("name", currentNode.getName());
		result.put("uri", currentNode.getURI());
		result.put("relation", "none");
		result.put("children", childrenArray);
		result.put("resultLine", ratingResponse);
		return result;
	}
	
	public List<Sample> getSamples(){
		return this.samples;
	}
	
	public int getNumbetConnections(){
		return samples.size();
	}
	
}
