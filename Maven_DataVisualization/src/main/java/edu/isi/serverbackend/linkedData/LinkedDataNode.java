package edu.isi.serverbackend.linkedData;


import java.util.*;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import edu.isi.serverbackend.linkedData.LinkedDataConnection.CurrentNode;
import edu.isi.serverbackend.feature.util.*;

import org.openrdf.model.Literal;
import org.openrdf.query.*;
import org.openrdf.repository.*;


public class LinkedDataNode {
	private String name;
	private String uri;
	private RepositoryConnection repoConnection;
	private String typeURI;
	
	public LinkedDataNode(String uri, RepositoryConnection connection) throws RepositoryException, MalformedQueryException, QueryEvaluationException{
		this.uri = uri;
		this.repoConnection = connection;
		//retrieveNameAndType();
	}
	
	public LinkedDataNode(String uri, String name, RepositoryConnection connection){
		this.uri = uri;
		this.name = name;
		this.repoConnection = connection;
	}
	
	public LinkedDataNode(String uri, String name, String typeURI, RepositoryConnection connection){
		this.uri = uri;
		this.name = name;
		this.typeURI = typeURI;
		this.repoConnection = connection;
	}
	
	public void retrieveNameAndType() throws RepositoryException, MalformedQueryException, QueryEvaluationException{
		String queryString = "SELECT ?label ?type WHERE { "
				+ "<" + uri + "> rdfs:label ?label ." 
				+ "<" + uri + "> a ?type."
                + "FILTER(?type = <http://dbpedia.org/ontology/Person> OR "
                + "?type = <http://dbpedia.org/ontology/Place> OR "
                + "?type = <http://dbpedia.org/ontology/Organisation> OR "
                + "?type = <http://dbpedia.org/ontology/Work> OR "
                + "?type =<http://dbpedia.org/ontology/Event>)."
				+" FILTER(langMatches(lang(?label), \"EN\")) }";
		System.out.println(queryString);
		TupleQuery query = repoConnection.prepareTupleQuery(QueryLanguage.SPARQL, queryString);
		TupleQueryResult result = query.evaluate();
		
		BindingSet bindingSet = result.next();
		Literal literal = (Literal)bindingSet.getValue("label");
		
		System.out.println(literal.getLanguage());
		System.out.println(literal.stringValue());
		this.name = literal.stringValue();
		this.typeURI = bindingSet.getValue("type").stringValue();
	}
	
	public void retrieveSubjectConnections(List<Sample> samples) throws RepositoryException, MalformedQueryException, QueryEvaluationException{
		String queryString = "SELECT ?predicate ?object ?label ?type WHERE{ "
                   + "<"+ uri + "> ?predicate ?object ."
                   + "?predicate rdf:type owl:ObjectProperty ."
                   + "?object a owl:Thing ."
                   + "?object rdfs:label ?label ."
                   + "?object a ?type."
                   + "FILTER(?type = <http://dbpedia.org/ontology/Person> OR "
                   + "?type = <http://dbpedia.org/ontology/Place> OR "
                   + "?type = <http://dbpedia.org/ontology/Organisation> OR "
                   + "?type = <http://dbpedia.org/ontology/Work> OR "
                   + "?type =<http://dbpedia.org/ontology/Event>)."
                   + "FILTER(langMatches(lang(?label), \"EN\")) "
                   + "} GROUP BY ?object";
		System.out.println(queryString);
		TupleQuery query = repoConnection.prepareTupleQuery(QueryLanguage.SPARQL, queryString);
		TupleQueryResult result = query.evaluate();
		
		while(result.hasNext()){
			BindingSet bindingSet = result.next();
			Literal objectLiteral = (Literal)bindingSet.getValue("label");
			String language = objectLiteral.getLanguage();
			if(language != null){
				if(objectLiteral.getLanguage().equals("en")){
					retrieveNameFromURI(bindingSet.getValue("object").stringValue());
					LinkedDataNode objectNode = new LinkedDataNode(bindingSet.getValue("object").stringValue(), objectLiteral.stringValue(), bindingSet.getValue("type").stringValue(), repoConnection);
					LinkedDataConnection newConnection = new LinkedDataConnection(this, objectNode, bindingSet.getValue("predicate").stringValue(), CurrentNode.subject, repoConnection);
					//newConnection.setConnectionParam(Integer.parseInt(bindingSet.getValue("count").stringValue()));
					samples.add(new Sample(newConnection));
				}
			}
		}
		
	}
	
	public void retrieveObjectConnections(List<Sample> samples) throws RepositoryException, MalformedQueryException, QueryEvaluationException{
		String queryString = "SELECT ?predicate ?subject ?label ?type WHERE{ "
                             + "?subject ?predicate <"+ uri + "> ."
                             + "?predicate rdf:type owl:ObjectProperty ."
                             + "?subject a owl:Thing ."
                             + "?subject rdfs:label ?label ."
                             + "?subject a ?type."
                             + "FILTER(?type = <http://dbpedia.org/ontology/Person> OR "
                             + "?type = <http://dbpedia.org/ontology/Place> OR "
                             + "?type = <http://dbpedia.org/ontology/Organisation> OR "
                             + "?type = <http://dbpedia.org/ontology/Work> OR "
                             + "?type =<http://dbpedia.org/ontology/Event>)."
                             + "FILTER(langMatches(lang(?label), \"EN\")). "
                             + "} GROUP BY ?subject";
		System.out.println(queryString);
		TupleQuery query = repoConnection.prepareTupleQuery(QueryLanguage.SPARQL, queryString);
		TupleQueryResult result = query.evaluate();
		
		while(result.hasNext()){
			BindingSet bindingSet = result.next();
			Literal subjectLiteral = (Literal)bindingSet.getValue("label");
			String language = subjectLiteral.getLanguage();
			if(language != null){
				if(subjectLiteral.getLanguage().equals("en")){
					LinkedDataNode subjectNode = new LinkedDataNode(bindingSet.getValue("subject").stringValue(), subjectLiteral.stringValue(), bindingSet.getValue("type").stringValue(), repoConnection);
					LinkedDataConnection newConnection = new LinkedDataConnection(subjectNode, this, bindingSet.getValue("predicate").stringValue(), CurrentNode.object, repoConnection);
					//newConnection.setConnectionParam(Integer.parseInt(bindingSet.getValue("count").stringValue()));
					samples.add(new Sample(newConnection));
				}
			}
		}
	}
	
	public String retrieveNameFromURI(String uri){
		String temp = uri;
		int index = 0;
		try {
			for(int i = 0; i < temp.length(); i++){
				if(temp.charAt(i) == '/')
					index = i;
			}
			temp = temp.substring(index + 1, temp.length());
			temp = URLDecoder.decode(temp, "UTF-8");
			temp = temp.replace("_", " ");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println(temp);
		return temp;
	}
	public void setName(String name){
		this.name = name;
	}
	public String getName(){
		return name;
	}
	public String getURI(){
		return uri;
	}
	
	public String getTypeURI(){
		return this.typeURI;
	}
	
	public RepositoryConnection getRepoConnection(){
		return this.repoConnection;
	}
}
