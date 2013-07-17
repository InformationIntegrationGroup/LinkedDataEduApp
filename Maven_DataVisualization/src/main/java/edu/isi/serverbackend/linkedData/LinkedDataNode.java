package edu.isi.serverbackend.linkedData;


import java.util.*;

import edu.isi.serverbackend.linkedData.LinkedDataConnection.CurrentNode;
import edu.isi.serverbackend.feature.util.*;
import org.openrdf.model.Literal;
import org.openrdf.query.*;
import org.openrdf.repository.*;


public class LinkedDataNode {
	private String name;
	private String uri;
	private RepositoryConnection repoConnection;
	public enum NodeType{person, organization, place, work};
	
	public LinkedDataNode(String uri, RepositoryConnection connection) throws RepositoryException, MalformedQueryException, QueryEvaluationException{
		this.uri = uri;
		this.repoConnection = connection;
		//retrieveName();
	}
	
	public LinkedDataNode(String uri, String name, RepositoryConnection connection){
		this.uri = uri;
		this.name = name;
		this.repoConnection = connection;
	}
	
	public void retrieveName() throws RepositoryException, MalformedQueryException, QueryEvaluationException{
		String queryString = "SELECT ?label WHERE { "
				+ "<" + uri + "> rdfs:label ?label ." 
				+" FILTER(langMatches(lang(?label), \"EN\")) }";
		System.out.println(queryString);
		TupleQuery query = repoConnection.prepareTupleQuery(QueryLanguage.SPARQL, queryString);
		TupleQueryResult result = query.evaluate();
		
		BindingSet bindingSet = result.next();
		Literal literal = (Literal)bindingSet.getValue("label");
		
		System.out.println(literal.getLanguage());
		System.out.println(literal.stringValue());
		this.name = literal.stringValue();
	}
	
	public void retrieveSubjectConnections(List<Sample> samples) throws RepositoryException, MalformedQueryException, QueryEvaluationException{
		String queryString = "SELECT ?predicate ?object ?label WHERE{ "
                   + "<"+ uri + "> ?predicate ?object ."
                   + "?object a owl:Thing ."
                   + "?object rdfs:label ?label ."
                  // + "?s ?predicate ?object . "
                  // + "FILTER(langMatches(lang(?label), \"EN\")) "
                   + "}";
		System.out.println(queryString);
		TupleQuery query = repoConnection.prepareTupleQuery(QueryLanguage.SPARQL, queryString);
		TupleQueryResult result = query.evaluate();
		
		while(result.hasNext()){
			BindingSet bindingSet = result.next();
			Literal objectLiteral = (Literal)bindingSet.getValue("label");
			String language = objectLiteral.getLanguage();
			if(language != null){
				if(objectLiteral.getLanguage().equals("en")){
					LinkedDataNode objectNode = new LinkedDataNode(bindingSet.getValue("object").stringValue(), objectLiteral.stringValue(), repoConnection);
					LinkedDataConnection newConnection = new LinkedDataConnection(this, objectNode, bindingSet.getValue("predicate").stringValue(), CurrentNode.subject, repoConnection);
					//newConnection.setConnectionParam(Integer.parseInt(bindingSet.getValue("count").stringValue()));
					samples.add(new Sample(newConnection));
				}
			}
		}
		
	}
	
	public void retrieveObjectConnections(List<Sample> samples) throws RepositoryException, MalformedQueryException, QueryEvaluationException{
		String queryString = "SELECT ?predicate ?subject ?label WHERE{ "
                             + "?subject ?predicate <"+ uri + "> ."
                             + "?subject a owl:Thing ."
                             + "?subject rdfs:label ?label ."
                            // + "?subject ?predicate ?o . "
                           //  + "FILTER(langMatches(lang(?label), \"EN\")) "
                             + "}";
		System.out.println(queryString);
		TupleQuery query = repoConnection.prepareTupleQuery(QueryLanguage.SPARQL, queryString);
		TupleQueryResult result = query.evaluate();
		
		while(result.hasNext()){
			BindingSet bindingSet = result.next();
			Literal subjectLiteral = (Literal)bindingSet.getValue("label");
			String language = subjectLiteral.getLanguage();
			if(language != null){
				if(subjectLiteral.getLanguage().equals("en")){
					LinkedDataNode subjectNode = new LinkedDataNode(bindingSet.getValue("subject").stringValue(), subjectLiteral.stringValue(), repoConnection);
					LinkedDataConnection newConnection = new LinkedDataConnection(subjectNode, this, bindingSet.getValue("predicate").stringValue(), CurrentNode.object, repoConnection);
					//newConnection.setConnectionParam(Integer.parseInt(bindingSet.getValue("count").stringValue()));
					samples.add(new Sample(newConnection));
				}
			}
		}
	}
	
	public String getName(){
		return name;
	}
	
	public String getURI(){
		return uri;
	}
	
	public RepositoryConnection getRepoConnection(){
		return this.repoConnection;
	}
}
