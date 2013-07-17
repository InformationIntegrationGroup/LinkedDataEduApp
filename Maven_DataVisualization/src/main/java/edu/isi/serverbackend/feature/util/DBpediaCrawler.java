package edu.isi.serverbackend.feature.util;

import edu.isi.serverbackend.linkedData.*;
import edu.isi.serverbackend.linkedData.LinkedDataConnection.CurrentNode;

import java.util.*;

import org.openrdf.query.*;

import org.openrdf.repository.RepositoryException;

public class DBpediaCrawler {
	
	ArrayList<LinkedDataConnection> connections;
	Queue<String> nodes;
	HashMap<String, String> nodesMap;
	LinkedDataNode seed;
	
	public DBpediaCrawler(LinkedDataNode seed){
		this.seed = seed;
		this.nodes = new PriorityQueue<String>();
		this.nodesMap = new HashMap<String, String>();
		this.connections = new ArrayList<LinkedDataConnection>();
	}
	
	public void startExplore(int max){
		int num = 0;
		try {
			nodes.add(seed.getURI());
			while(num < max && nodes.size() != 0){
				String currentURI = nodes.remove();
				String queryString = "SELECT ?p ?o{ "
						+ "<" + currentURI + "> ?p ?o. "
						+ "?o a owl:Thing. "
						+ "?o rdfs:label ?label. "
						+ "?p rdf:type owl:ObjectProperty. "
						+ "FILTER(langMatches(lang(?label), \"EN\")) "
						+ "}";
				System.out.println(queryString);
				TupleQuery query = seed.getRepoConnection().prepareTupleQuery(QueryLanguage.SPARQL, queryString);
				TupleQueryResult result = query.evaluate();
				while(result.hasNext()){
					BindingSet bindingSet = result.next();
					String newURI = bindingSet.getValue("o").stringValue();
					String predicate = bindingSet.getValue("p").stringValue();
					if(nodesMap.get(newURI) == null){
						LinkedDataNode subject = new LinkedDataNode(currentURI, seed.getRepoConnection());
						LinkedDataNode object = new LinkedDataNode(newURI, seed.getRepoConnection());
						LinkedDataConnection newLink = new LinkedDataConnection(subject, object, predicate, CurrentNode.subject, seed.getRepoConnection());
						connections.add(newLink);
						nodesMap.put(newURI, currentURI);
						nodes.add(newURI);
						num++;
					}
				}
			}
			System.out.println("Finish Exploration!!!");
		} catch (RepositoryException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (MalformedQueryException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (QueryEvaluationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public ArrayList<LinkedDataConnection> exportLinks(){
		
		return this.connections;
	}
}
