package request;

import linkedData.*;
import linkedData.LinkedDataConnection.CurrentNode;

import java.util.*;
import java.util.concurrent.Semaphore;

import org.openrdf.query.BindingSet;
import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.query.QueryLanguage;
import org.openrdf.query.TupleQuery;
import org.openrdf.query.TupleQueryResult;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.http.HTTPRepository;
import org.json.*;

public class ConnectionRankRequest {
	private LinkedDataNode currentNode;
	private List<LinkedDataConnection> subjectConnections;
	private List<LinkedDataConnection> objectConnections;
	//private RepositoryConnection repoConnection;
	
	public ConnectionRankRequest(LinkedDataNode currentNode){
		this.currentNode = currentNode;
		//this.repoConnection = currentNode.getRepoConnection();
		this.subjectConnections = new ArrayList<LinkedDataConnection>();
		this.objectConnections =  new ArrayList<LinkedDataConnection>();
		
		try {
			retrieveSubjectConnections();
			retrieveObjectConnections();
			
			System.out.println("get to acquire() statement");
			//subjectRetrieving.acquire();
			System.out.println("finished two retrieving");
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
	
	/*public void retrieveSubjectConnections() throws RepositoryException, MalformedQueryException, QueryEvaluationException{
		String queryString = "SELECT DISTINCT ?object ?predicate WHERE{ "
				+ "<" + currentNode.getURI() + "> ?predicate ?object . "
				+ "?object foaf:name ?o . "
				+ " }";
		System.out.println(queryString);

		TupleQuery query = repoConnection.prepareTupleQuery(QueryLanguage.SPARQL, queryString);
		TupleQueryResult result = query.evaluate();
		
		while(result.hasNext()){
			BindingSet bindingSet = result.next();
			subjectConnections.add(new LinkedDataConnection(currentNode, new LinkedDataNode(bindingSet.getValue("object").stringValue(),repoConnection), bindingSet.getValue("predicate").stringValue(), CurrentNode.subject, repoConnection));
			subjectConnections.get(subjectConnections.size() - 1 ).evaluateConnection();
		}
	}
	
	public void retrieveObjectConnections() throws RepositoryException, MalformedQueryException, QueryEvaluationException{
		String queryString = "SELECT DISTINCT ?subject ?predicate WHERE{ "
				+ "?subject ?predicate " + "<" + currentNode.getURI() + "> . "
				+ "?subject foaf:name ?o . "
				+ " }";
		System.out.println(queryString);

		TupleQuery query = repoConnection.prepareTupleQuery(QueryLanguage.SPARQL, queryString);
		TupleQueryResult result = query.evaluate();
		
		while(result.hasNext()){
			BindingSet bindingSet = result.next();
			objectConnections.add(new LinkedDataConnection(new LinkedDataNode(bindingSet.getValue("subject").stringValue(), repoConnection), this.currentNode, bindingSet.getValue("predicate").stringValue(), CurrentNode.object, repoConnection));
			objectConnections.get(objectConnections.size() - 1 ).evaluateConnection();
		}
	}*/
	
	public void retrieveSubjectConnections() throws RepositoryException, MalformedQueryException, QueryEvaluationException{
		
		subjectConnections = currentNode.retrieveSubjectConnections();
	}
	
	public void retrieveObjectConnections() throws RepositoryException, MalformedQueryException, QueryEvaluationException{
		objectConnections = currentNode.retrieveObjectConnections();
	}
	
	public void sortConnections(){
		//algorithm: bubble sort
		boolean swap = true;
		LinkedDataConnection tempConnect;
		//sort subjectConnecitons list
		while(swap){
			swap = false;
			for(int i = 0;i < subjectConnections.size()-1;i++){
				if(subjectConnections.get(i).getConnectionParam() > subjectConnections.get(i+1).getConnectionParam()){
					tempConnect = subjectConnections.get(i+1);
					subjectConnections.set(i+1, subjectConnections.get(i));
					subjectConnections.set(i, tempConnect);
					swap = true;
				}
			}
		}
		
		//sort objectConnecitons list
		tempConnect = null;
		swap = true;
		while(swap){
			swap = false;
			for(int i = 0;i < objectConnections.size()-1;i++){
				if(objectConnections.get(i).getConnectionParam() > objectConnections.get(i+1).getConnectionParam()){
					tempConnect = objectConnections.get(i+1);
					objectConnections.set(i+1, objectConnections.get(i));
					objectConnections.set(i, tempConnect);
					swap = true;
				}
			}
		}
	}
	
	public JSONArray exportJSONList(int num){
		JSONArray fullJSONArray = new JSONArray();
		JSONArray result = new JSONArray();
		int i = 0;
		int j = 0;
		
		while(i < subjectConnections.size() && j < objectConnections.size()){
			JSONObject JObject = new JSONObject();
			if(subjectConnections.get(i).getConnectionParam() < objectConnections.get(j).getConnectionParam()){
				JObject.put("uri", subjectConnections.get(i).getObject().getURI());
				JObject.put("predicate", subjectConnections.get(i).getPredicate());
				JObject.put("name", subjectConnections.get(i).getObject().getName());
				fullJSONArray.put(JObject);
				i++;
			}
			else{
				JObject.put("uri", objectConnections.get(j).getSubject().getURI());
				JObject.put("predicate", objectConnections.get(j).getPredicate());
				JObject.put("name", objectConnections.get(j).getSubject().getName());
				fullJSONArray.put(JObject);
				j++;
			}
		}
		
		if(i == subjectConnections.size()){
			while(j < objectConnections.size()){
				JSONObject JObject = new JSONObject();
				JObject.put("uri", objectConnections.get(j).getSubject().getURI());
				JObject.put("predicate", objectConnections.get(j).getPredicate());
				JObject.put("name", objectConnections.get(j).getSubject().getName());
				fullJSONArray.put(JObject);
				j++;
			}
		}
		else if(j == objectConnections.size()){
			while(i < subjectConnections.size()){
				JSONObject JObject = new JSONObject();
				JObject.put("uri", subjectConnections.get(i).getObject().getURI());
				JObject.put("predicate", subjectConnections.get(i).getPredicate());
				JObject.put("name", subjectConnections.get(i).getObject().getName());
				fullJSONArray.put(JObject);
				i++;
			}
		}
		
		for(int k = 0; k < num; k++){
			if(k < fullJSONArray.length()){
				result.put(fullJSONArray.get(k));
			}
			else{
				break;
			}
		}
		return result;
	}
	
	public JSONObject exportD3JSON(int num){
		JSONObject result = new JSONObject();
		JSONArray nodeArray = new JSONArray();
		JSONArray linkArray = new JSONArray();
		
		int i = 0;
		int j = 0;
		int k = 0;
		JSONObject currentNodeJSON = new JSONObject();
		currentNodeJSON.put("name", currentNode.getName());
		currentNodeJSON.put("uri", currentNode.getURI());
		nodeArray.put(currentNodeJSON);
		
		while(i < subjectConnections.size() && j < objectConnections.size() && k < num){
			JSONObject node = new JSONObject();
			JSONObject link = new JSONObject();
			if(subjectConnections.get(i).getConnectionParam() < objectConnections.get(j).getConnectionParam()){
				boolean found = false;
				int count = 0;
				node.put("name", subjectConnections.get(i).getObject().getName());
				node.put("uri", subjectConnections.get(i).getObject().getURI());
				node.put("group", 1);
				for(count = 0;count < nodeArray.length();count++){
					if(nodeArray.get(count).toString().equals(node.toString())){
						found = true;
						break;
					}
				}
				if(found){
					link.put("source", 0);
					link.put("target", count);
					link.put("value", subjectConnections.get(i).getPredicate());
				}
				else{
					nodeArray.put(node);
					link.put("source", 0);
					link.put("target", nodeArray.length()-1);
					link.put("value", subjectConnections.get(i).getPredicate());
					linkArray.put(link);
				}
				i++;
			}
			else{
				boolean found = false;
				int count = 0;
				node.put("name", objectConnections.get(j).getSubject().getName());
				node.put("uri", objectConnections.get(j).getSubject().getURI());
				node.put("group", 1);
				for(count = 0;count < nodeArray.length();count++){
					if(nodeArray.get(count).toString().equals(node.toString())){
						found = true;
						break;
					}
				}
				if(found){
					link.put("source", count);
					link.put("target", 0);
					link.put("value", objectConnections.get(j).getPredicate());
					linkArray.put(link);
				}
				else{
					nodeArray.put(node);
					link.put("source", nodeArray.length()-1);
					link.put("target", 0);
					link.put("value", objectConnections.get(j).getPredicate());
					linkArray.put(link);
				}
				j++;
			}
			k++;
		}
		
		if(i == subjectConnections.size()){
			while(j < objectConnections.size() && k < num){
				JSONObject node = new JSONObject();
				JSONObject link = new JSONObject();
				boolean found = false;
				int count = 0;
				node.put("name", objectConnections.get(j).getSubject().getName());
				node.put("uri", objectConnections.get(j).getSubject().getURI());
				node.put("group", 1);
				for(count = 0;count < nodeArray.length();count++){
					if(nodeArray.get(count).toString().equals(node.toString())){
						found = true;
						break;
					}
				}
				if(found){
					link.put("source", count);
					link.put("target", 0);
					link.put("value", objectConnections.get(j).getPredicate());
					linkArray.put(link);
				}
				else{
					nodeArray.put(node);
					link.put("source", nodeArray.length()-1);
					link.put("target", 0);
					link.put("value", objectConnections.get(j).getPredicate());
					linkArray.put(link);
				}
				j++;
				k++;
			}
		}
		else if(j == objectConnections.size()){
			while(i < subjectConnections.size() && k < num){
				JSONObject node = new JSONObject();
				JSONObject link = new JSONObject();
				boolean found = false;
				int count = 0;
				node.put("name", subjectConnections.get(i).getObject().getName());
				node.put("uri", subjectConnections.get(i).getObject().getURI());
				node.put("group", 1);
				for(count = 0;count < nodeArray.length();count++){
					if(nodeArray.get(count).toString().equals(node.toString())){
						found = true;
						break;
					}
				}
				if(found){
					link.put("source", 0);
					link.put("target", count);
					link.put("value", subjectConnections.get(i).getPredicate());
				}
				else{
					nodeArray.put(node);
					link.put("source", 0);
					link.put("target", nodeArray.length()-1);
					link.put("value", subjectConnections.get(i).getPredicate());
					linkArray.put(link);
				}
				i++;
				k++;
			}
		}
		
		result.put("nodes", nodeArray);
		result.put("links", linkArray);
		
		return result;
	}
	
	public List<LinkedDataConnection> getSubjectConnections(){
		return this.subjectConnections;
	}
	
	public List<LinkedDataConnection> getObjectConnections(){
		return this.objectConnections;
	}
	
	public int getNumbetConnections(){
		return subjectConnections.size() + objectConnections.size();
	}
	
}
