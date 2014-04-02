package edu.isi.serverbackend.request;

import edu.isi.serverbackend.linkedData.*;
import edu.isi.serverbackend.localDatabase.bean.PredicateBean;
import edu.isi.serverbackend.feature.DifferentOccupationFeature;
import edu.isi.serverbackend.feature.EitherNotPlaceFeature;
import edu.isi.serverbackend.feature.ImportanceFeature;
import edu.isi.serverbackend.feature.RarityFeature;
import edu.isi.serverbackend.feature.SmallPlaceFeature;
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
		RarityFeature.calculatePredicateRarity(samples);
		//ImportanceFeature.calculateImportance(samples);
		
		try {
			URL url = new URL("http://127.0.0.1:8080/LODStories-1.0.0-SNAPSHOT/DemoServlet");//
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
						+ URLEncoder.encode(samples.get(i).getExtensionImportance()+",", "UTF-8")
				        + URLEncoder.encode(samples.get(i).getSmallPlace()+"\n", "UTF-8");
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
		eliminateSameNodeExtension();
	}
	
	/*Precondition: Sample is sorted*/
	private void eliminateSameNodeExtension(){
		HashSet<String> nodeSet = new HashSet<String>();
		for(Sample sample:samples){
			String target;
			if(sample.getLink().isSubjectConnection())
				target= sample.getLink().getObject().getURI();
			else
				target = sample.getLink().getSubject().getURI();
			if(nodeSet.contains(target))
				samples.remove(sample);
			else
				nodeSet.add(target);
		}
	}
	
	
	public JSONObject exportD3JSON(int num) throws JSONException{
		JSONObject result = new JSONObject();
		JSONArray childrenArray = new JSONArray();
		List<Sample> orderedSamples = reorderByRelation(num);
		for(int i = 0; i < num; i++){
			if(i >= orderedSamples.size())
				break;
			JSONObject newNode = new JSONObject();
			
			if(samples.get(i).getLink().isSubjectConnection()){
				newNode.put("name", orderedSamples.get(i).getLink().getObject().getName());
				newNode.put("uri", orderedSamples.get(i).getLink().getObject().getURI());
				newNode.put("relation", PredicateBean.obtainPredicateName( orderedSamples.get(i).getLink().getPredicate()));
				//newNode.put("importance", samples.get(i).getExtensionImportance());
				newNode.put("inverse", 1);
				newNode.put("rank", orderedSamples.get(i).getInterestingness());
			}
			else{
				newNode.put("name", orderedSamples.get(i).getLink().getSubject().getName());
				newNode.put("uri", orderedSamples.get(i).getLink().getSubject().getURI());
				newNode.put("relation", PredicateBean.obtainPredicateName(orderedSamples.get(i).getLink().getPredicate()));
				//newNode.put("importance", samples.get(i).getExtensionImportance());
				newNode.put("inverse", 0);
				newNode.put("rank", orderedSamples.get(i).getInterestingness());
			}
			childrenArray.put(newNode);
		}
	
		result.put("name", currentNode.getName());
		result.put("uri", currentNode.getURI());
		result.put("relation", "none");
		result.put("children", childrenArray);
		result.put("resultLine", ratingResponse);
		result.put("Size", samples.size());
		return result;
	}
	
	public List<Sample> getSamples(){
		return this.samples;
	}
	
	public int getNumbetConnections(){
		return samples.size();
	}
	
	private List<Sample> reorderByRelation(int num){
		List<Sample> result = new ArrayList<Sample>();
		int count = 0;
		HashSet<String> relationSet = new HashSet<String>();
		while (count < num){
			relationSet.clear();
			for(int i = 0; i < samples.size(); i++){
				if(!relationSet.contains(samples.get(i).getLink().getPredicate())){
					relationSet.add(samples.get(i).getLink().getPredicate());
					result.add(samples.get(i));
					samples.remove(i);
					count++;
					if(count == num)
						return result;
				}
			}
		}
		return result;
	}
}
