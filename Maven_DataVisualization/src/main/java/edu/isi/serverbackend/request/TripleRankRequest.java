package edu.isi.serverbackend.request;

import edu.isi.serverbackend.linkedData.*;
import edu.isi.serverbackend.localDatabase.bean.PredicateBean;
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
	
	public TripleRankRequest(LinkedDataNode currentNode, ServletContext context) throws IOException{
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
	
	public TripleRankRequest(LinkedDataNode currentNode) throws IOException{
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
	
	public void rateInterestingness()throws IOException{
        //store relationship, weight and subject/weight
        Map<String,Double> relationshipMap = new HashMap<String,Double>();
        Map<String,Double> subjectMap = new HashMap<String,Double>();

        //read from csv file
        try {
            BufferedReader fileReader = null;
            fileReader = new BufferedReader(new FileReader("relationship.csv"));
            String line = "";
            while ((line = fileReader.readLine()) != null) {
                String[] content = line.split(",");
                relationshipMap.put(content[0],Double.parseDouble(content[1]));
                System.out.println(content[0]+ " "+Double.parseDouble(content[1]));
            }
            fileReader.close();
            fileReader = new BufferedReader(new FileReader("subject.csv"));
            while ((line = fileReader.readLine()) != null) {
                String[] content = line.split(",");
                subjectMap.put(content[0],Double.parseDouble(content[1]));
            }
            fileReader.close();
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
        //for testing
        /*
        relationshipMap.put("http://dbpedia.org/ontology/deathPlace",1.0);
        relationshipMap.put("http://dbpedia.org/ontology/birthPlace",1.0);
        subjectMap.put("http://dbpedia.org/resource/French_Third_Republic",1.0);
        subjectMap.put("http://dbpedia.org/resource/Netherlands",0.5);
        subjectMap.put("http://dbpedia.org/resource/Zundert",0.1);
		*/

        for(int i = 0 ; i < samples.size(); i++){
            // System.out.println("p: " + samples.get(i).getLink().getPredicate());
            // System.out.println("o: " + samples.get(i).getLink().getObject().getURI());
            // System.out.println("s: " + samples.get(i).getLink().getSubject().getURI());
            double factor1, factor2;
            factor1 = 0;
            factor2 = 0;

            //process predicate from url to single word
            String urlPredicate = samples.get(i).getLink().getPredicate();
            String[] predicateArr = urlPredicate.split("/");
            String predicate = predicateArr[predicateArr.length -1];

            if( relationshipMap.containsKey(predicate) ){
                factor1 = relationshipMap.get(predicate);
                 // System.out.println("f1: " + factor1);
            }
            //check sample
            if(samples.get(i).getLink().isSubjectConnection()){
                 // System.out.println("subject connection ");
                if( subjectMap.containsKey(samples.get(i).getLink().getObject().getURI())){
                    factor2 = subjectMap.get(samples.get(i).getLink().getObject().getURI());
                    // System.out.println("f2: " + factor2);
                }
            }
            else if(!samples.get(i).getLink().isSubjectConnection()){
                 // System.out.println("object connection ");
                if( subjectMap.containsKey(samples.get(i).getLink().getSubject().getURI())){
                    factor2 = subjectMap.get(samples.get(i).getLink().getSubject().getURI());
                    // System.out.println("f2: " + factor2);
                }
            }
            //set interestness
            samples.get(i).setInterestingness(factor1 * factor2);
            //System.out.println("int: " + samples.get(i).getInterestingness());
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
        
        
        //System.out.println("after ranking");
        /*
        for(int i=0;i<7;i++){
            if( samples.get(i).getLink().isSubjectConnection()){
                System.out.println(samples.get(i).getLink().getObject().getURI() + "  interestness: " + samples.get(i).getInterestingness());
            }
            else{
                System.out.println(samples.get(i).getLink().getSubject().getURI() + "  interestness: " + samples.get(i).getInterestingness());
            }

        }*/

        //remove some nodes
         for(int i = 0; i < samples.size(); i++){
	        if(samples.get(i).getLink().getPredicate().equals("http://dbpedia.org/ontology/wikiPageRedirects")
							|| samples.get(i).getLink().getPredicate().equals("http://dbpedia.org/ontology/wikiPageDisambiguates")
							|| samples.get(i).getLink().getPredicate().equals("http://dbpedia.org/ontology/wikiPageExternalLink")){
						samples.remove(i);
						i--;
					}	
		}

        //delete some nodes so that the same relationsship will not be displayed
        HashSet<String> relationSet = new HashSet<String>();
		relationSet.clear();
		int counter = 0;//make sure not removing to many nodes
        for(int i = 0; i < samples.size(); i++){
			if(!relationSet.contains(samples.get(i).getLink().getPredicate())){
				relationSet.add(samples.get(i).getLink().getPredicate());
				counter++;
			}
			else{
				samples.remove(i);
				i--;
			}
			if(counter >= 5){
				break;
			}
		}

		/*
		System.out.println("after removing nodes");
        for(int i=0;i<7;i++){
            if( samples.get(i).getLink().isSubjectConnection()){
                System.out.println(samples.get(i).getLink().getObject().getURI() + "  interestness: " + samples.get(i).getInterestingness());
            }
            else{
                System.out.println(samples.get(i).getLink().getSubject().getURI() + "  interestness: " + samples.get(i).getInterestingness());
            }

        }
        */

        if(samples.size() > 7){

        	//rank top 5 and randomly pick two
	        int index = 0; // the first one with interestness of 0
	        for(int i=0;i<samples.size();i++){
	        	if(samples.get(i).getInterestingness() == 0){
	        		index = i;
	        		break;
	        	}
	        }
	        
	        //make sure index >= 5, or the first node may be swapped 
	        index = Math.max(5,index);


	   		 int randomNum1 = index + (int)(Math.random() * (samples.size() - index));
	   		 int randomNum2 = index + (int)(Math.random() * (samples.size() - index));
	   		//make sure two random numbers are not equal
	   		 while(randomNum2 == randomNum1){
	   		 	randomNum2 = index + (int)(Math.random() * (samples.size() - index));
	   		 }

	   		 temp = samples.get(5);
	   		 samples.set(5, samples.get(randomNum1));
	   		 samples.set(randomNum1, temp);

	   		 temp = samples.get(6);
	   		 samples.set(6, samples.get(randomNum2));
	   		 samples.set(randomNum2, temp);
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
		//List<Sample> orderedSamples = reorderByRelation(num);
		//delete the logic of reorderbyrelation
		List<Sample> orderedSamples = samples;
		for(int i = 0; i < num; i++){
			if(i >= orderedSamples.size())
				break;
			JSONObject newNode = new JSONObject();

            String subject = orderedSamples.get(i).getLink().getSubject().getName();
            String object = orderedSamples.get(i).getLink().getObject().getName();
            String relation = PredicateBean.obtainPredicateName(orderedSamples.get(i).getLink().getPredicate());
			if(orderedSamples.get(i).getLink().isSubjectConnection()){
				newNode.put("name", object);
				newNode.put("uri", orderedSamples.get(i).getLink().getObject().getURI());
				newNode.put("relationship", relation);
				newNode.put("inverse", 0);
				newNode.put("rank", orderedSamples.get(i).getInterestingness());
                newNode.put("image", orderedSamples.get(i).getLink().getObject().getImage());
                newNode.put("relation", SentenceHashUtil.parseSentence(relation, 0, orderedSamples.get(i).getLink().getObject().getTypeURI()));
			}
			else{
				newNode.put("name", subject);
				newNode.put("uri", orderedSamples.get(i).getLink().getSubject().getURI());
				newNode.put("relationship", relation);
				newNode.put("inverse", 1);
				newNode.put("rank", orderedSamples.get(i).getInterestingness());
                newNode.put("image", orderedSamples.get(i).getLink().getSubject().getImage());
                newNode.put("relation", SentenceHashUtil.parseSentence(relation, 1, orderedSamples.get(i).getLink().getSubject().getTypeURI()));
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
