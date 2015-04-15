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

import javax.servlet.ServletContext;

public class TripleRankRequest {
	private LinkedDataNode currentNode;
	private List<Sample> samples;
	String ratingResponse = "";
    SentenceHashUtil sentenceHashUtil;
    ServletContext context;
	//private RepositoryConnection repoConnection;
	
	public TripleRankRequest(LinkedDataNode currentNode, ServletContext context){
        this.context = context;
        this.sentenceHashUtil = new SentenceHashUtil();
		this.currentNode = currentNode;
		//this.repoConnection = currentNode.getRepoConnection();
		this.samples = new ArrayList<Sample>();
		
		
		try {
			retrieveObjectExtensions();
			retrieveSubjectExtensions();
			
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
	
	public TripleRankRequest(LinkedDataNode currentNode){
        this.sentenceHashUtil = new SentenceHashUtil();
		this.currentNode = currentNode;
		//this.repoConnection = currentNode.getRepoConnection();
		this.samples = new ArrayList<Sample>();
		
		
		try {
			retrieveObjectExtensions();
			retrieveSubjectExtensions();
			
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
	
	public void retrieveObjectExtensions() throws RepositoryException, MalformedQueryException, QueryEvaluationException{
		currentNode.retrieveObjectExtensions(samples, false);
	}
	
	public void retrieveSubjectExtensions() throws RepositoryException, MalformedQueryException, QueryEvaluationException{
		currentNode.retrieveSubjectExtensions(samples, false);
	}
	
	public void rateInterestingness(){
		//RarityFeature.calculatePredicateRarity(samples);
		//ImportanceFeature.calculateImportance(samples);

		try {
			URL url = new URL("http://127.0.0.1:8080/LODStories/DemoServlet");//
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
			//e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
		}
	}
	
	public void sortConnections(){
		//algorithm: bubble sort // note from Dipa: WHY????
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
		int i = 0;
		while(i < samples.size()){
			String target;
			if(samples.get(i).getLink().isSubjectConnection())
				target= samples.get(i).getLink().getObject().getURI();
			else
				target = samples.get(i).getLink().getSubject().getURI();
			if(nodeSet.contains(target)){
				samples.remove(i);
				continue;
			}
			else{
				nodeSet.add(target);
				i++;
			}
				
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

            String subject = orderedSamples.get(i).getLink().getSubject().getName();
            String object = orderedSamples.get(i).getLink().getObject().getName();
            String relation = PredicateBean.obtainPredicateName( orderedSamples.get(i).getLink().getPredicate());
			if(orderedSamples.get(i).getLink().isSubjectConnection()){
				newNode.put("name", object);
				newNode.put("uri", orderedSamples.get(i).getLink().getObject().getURI());
				newNode.put("relationship", relation);
				newNode.put("inverse", 0);
				newNode.put("rank", orderedSamples.get(i).getInterestingness());
                newNode.put("relation", sentenceHashUtil.parseSentence(relation, 0, context));
			}
			else{
				newNode.put("name", subject);
				newNode.put("uri", orderedSamples.get(i).getLink().getSubject().getURI());
				newNode.put("relationship", relation);
				newNode.put("inverse", 1);
				newNode.put("rank", orderedSamples.get(i).getInterestingness());
                newNode.put("relation", sentenceHashUtil.parseSentence(relation, 1, context));
			}
			childrenArray.put(newNode);
		}
	
		result.put("name", currentNode.getName());
		result.put("uri", currentNode.getURI());
		result.put("relation", "none");
		result.put("children", childrenArray);
		//result.put("resultLine", ratingResponse);
		result.put("Size", orderedSamples.size());
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
		while (count < num && samples.size() > 0){
			relationSet.clear();
			for(int i = 0; i < samples.size(); i++){
				if(!relationSet.contains(samples.get(i).getLink().getPredicate())){
					if(samples.get(i).getLink().getPredicate().equals("http://dbpedia.org/ontology/wikiPageRedirects")
							|| samples.get(i).getLink().getPredicate().equals("http://dbpedia.org/ontology/wikiPageDisambiguates")
							|| samples.get(i).getLink().getPredicate().equals("http://dbpedia.org/ontology/wikiPageExternalLink")){
						samples.remove(i);
						continue;
					}	
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
