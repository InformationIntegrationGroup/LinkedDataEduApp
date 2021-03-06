package edu.isi.serverbackend.linkedData;

import java.util.*;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import edu.isi.serverbackend.linkedData.LinkedDataTriple.CurrentNode;
import edu.isi.serverbackend.feature.util.*;

import org.openrdf.model.Literal;
import org.openrdf.query.BindingSet;
import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.query.QueryLanguage;
import org.openrdf.query.TupleQuery;
import org.openrdf.query.TupleQueryResult;
import org.openrdf.repository.*;

import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.query.*;
import com.hp.hpl.jena.sparql.core.Var;
import com.hp.hpl.jena.sparql.engine.binding.Binding;

public class LinkedDataNode {
	private String name;
	private String uri;
	private RepositoryConnection repoConnection;
	private String typeURI;
    private String image;
	
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
	
    public LinkedDataNode(String uri, String name, String typeURI, RepositoryConnection connection, String image){
        this.uri = uri;
        this.name = name;
        this.typeURI = typeURI;
        this.repoConnection = connection;
        this.image = image;
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
				+" FILTER(langMatches(lang(?label), \"EN\")) } LIMIT 500";
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
	
	public void retrieveObjectExtensions(List<Sample> samples, boolean remote) throws RepositoryException, MalformedQueryException, QueryEvaluationException{
		if(remote){
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
	                   + "}GROUP BY ?object LIMIT 500";
			System.out.println(queryString);
			TupleQuery query = repoConnection.prepareTupleQuery(QueryLanguage.SPARQL, queryString);
			TupleQueryResult result = query.evaluate();
			
			while(result.hasNext()){
				BindingSet bindingSet = result.next();
				Literal objectLiteral = (Literal)bindingSet.getValue("label");
				String language = objectLiteral.getLanguage();
				if(language != null){
					if(objectLiteral.getLanguage().equals("en")){
						LinkedDataNode objectNode = new LinkedDataNode(bindingSet.getValue("object").stringValue(), objectLiteral.getLabel(), bindingSet.getValue("type").stringValue(), repoConnection);
						LinkedDataTriple newTriple = new LinkedDataTriple(this, objectNode, bindingSet.getValue("predicate").stringValue(), CurrentNode.subject, repoConnection);
						samples.add(new Sample(newTriple));
					}
				}
			}
		}
		else{
			String queryStr = "SELECT ?image ?predicate ?object ?label ?type WHERE {"
					+ "GRAPH ?g { "
	    			+"{  SELECT ?predicate ?object WHERE { <"+ uri +"> ?predicate ?object } }"
	    			+ "?object <http://www.w3.org/2000/01/rdf-schema#label> ?label. "
                    + "?object <http://dbpedia.org/ontology/thumbnail> ?image. "
	    			+ "OPTIONAL { ?object <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> ?type. "
	    			+ "FILTER(?type=  <http://dbpedia.org/ontology/Person> "
	    			+ "|| ?type=  <http://dbpedia.org/ontology/Place> "
	    			+ "|| ?type=  <http://dbpedia.org/ontology/Organisation> "
	    			+ "|| ?type = <http://dbpedia.org/ontology/Work>)"
	    			+ "}}} LIMIT 500";
			System.out.println("RUN SPARQL: " + queryStr);
	    	Query query = QueryFactory.create(queryStr);
	    	QueryExecution qExe = QueryExecutionFactory.sparqlService( "http://lodstories.isi.edu:3030/integrated_dbpedia/query", query );
	    	ResultSet results = qExe.execSelect();
	    	//ResultSetFormatter.out(System.out, results, query) ;
	    	while(results.hasNext()){
	    		Binding binding = results.nextBinding();
	    		Iterator<Var> vars = binding.vars();
	    		String predicate = "";
	    		String object = "";
	    		String label = "";
	    		String type = "";
                String image = "";
	    		while(vars.hasNext()){
	    			Var var = vars.next();
	    			Node node = binding.get(var);
	    			String name = var.getVarName();
	    			String value;
	    			if(node.isURI())
	    				value = node.getURI();
	    			else
	    				value = node.getLiteralValue().toString();
	    			if(name.equals("predicate"))
	    				predicate = value;
	    			else if(name.equals("object"))
	    				object = value;
	    			else if(name.equals("label"))
	    				label = value;
	    			else if(name.equals("type"))
	    				type = value;
                    else if(name.equals("image"))
                        image = value;
	    		}
	    		if(!predicate.equals("")){
	    			LinkedDataNode objectNode = new LinkedDataNode(object, label, type, repoConnection, image);
					LinkedDataTriple newTriple = new LinkedDataTriple(this, objectNode, predicate, CurrentNode.subject, repoConnection);
					samples.add(new Sample(newTriple));
	    		}
	    	}
		}
	
	}
	
	public void retrieveSubjectExtensions(List<Sample> samples, boolean remote) throws RepositoryException, MalformedQueryException, QueryEvaluationException{
		if(remote){
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
                    + "} GROUP BY ?subject LIMIT 500";
			System.out.println(queryString);
			TupleQuery query = repoConnection.prepareTupleQuery(QueryLanguage.SPARQL, queryString);
			TupleQueryResult result = query.evaluate();

			while(result.hasNext()){
				BindingSet bindingSet = result.next();
				Literal subjectLiteral = (Literal)bindingSet.getValue("label");
				String language = subjectLiteral.getLanguage();
				if(language != null){
					if(subjectLiteral.getLanguage().equals("en")){
						LinkedDataNode subjectNode = new LinkedDataNode(bindingSet.getValue("subject").stringValue(), subjectLiteral.getLabel(), bindingSet.getValue("type").stringValue(), repoConnection);
						LinkedDataTriple newConnection = new LinkedDataTriple(subjectNode, this, bindingSet.getValue("predicate").stringValue(), CurrentNode.object, repoConnection);
						samples.add(new Sample(newConnection));
					}
				}
			}
		}
		else{
			String queryStr = "SELECT ?image ?subject ?predicate ?label ?type WHERE {"
					+ "GRAPH ?g { "
	    			+"{  SELECT ?subject ?predicate WHERE { ?subject ?predicate <"+ uri +"> } }"
	    			+ "?subject <http://www.w3.org/2000/01/rdf-schema#label> ?label. "
                    + "?subject <http://dbpedia.org/ontology/thumbnail> ?image. "
	    			+ "OPTIONAL {?subject <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> ?type. "
	    			+ "FILTER(?type=  <http://dbpedia.org/ontology/Person> "
	    			+ "|| ?type=  <http://dbpedia.org/ontology/Place> "
	    			+ "|| ?type=  <http://dbpedia.org/ontology/Organisation> "
	    			+ "|| ?type = <http://dbpedia.org/ontology/Work>)"
	    			+ "}}} LIMIT 500";
			System.out.println("RUN SPARQL: " + queryStr);
	    	Query query = QueryFactory.create(queryStr);
	    	QueryExecution qExe = QueryExecutionFactory.sparqlService( "http://lodstories.isi.edu:3030/integrated_dbpedia/query", query );
	    	ResultSet results = qExe.execSelect();
	    	//ResultSetFormatter.out(System.out, results, query) ;
	    	while(results.hasNext()){
	    		Binding binding = results.nextBinding();
	    		Iterator<Var> vars = binding.vars();
	    		String predicate = "";
	    		String subject = "";
	    		String label = "";
	    		String type = "";
                String image = "";
	    		while(vars.hasNext()){
	    			Var var = vars.next();
	    			Node node = binding.get(var);
	    			String name = var.getVarName();
	    			String value;
	    			if(node.isURI())
	    				value = node.getURI();
	    			else
	    				value = node.getLiteralValue().toString();
	    			if(name.equals("predicate"))
	    				predicate = value;
	    			else if(name.equals("subject"))
	    				subject = value;
	    			else if(name.equals("label"))
	    				label = value;
	    			else if(name.equals("type"))
	    				type = value;
                    else if(name.equals("image"))
                        image = value;
	    		}
	    		if(!predicate.equals("")){
	    			LinkedDataNode subjectNode = new LinkedDataNode(subject, label, type, repoConnection, image);
					LinkedDataTriple newTriple = new LinkedDataTriple(subjectNode, this, predicate, CurrentNode.object, repoConnection);
					samples.add(new Sample(newTriple));
	    		}
	    	}	
		}
		
	}
	//Doesn't work...Java and Tomcat don't play nice together with their encodings?
	public String retrieveNameFromURI(String uri){
		String temp= uri;
		try {
			temp = URLDecoder.decode(temp, "UTF-8");
			int index=0;
			for(int i = 0; i < temp.length(); i++){
				if(temp.charAt(i) == '/')
					index = i;
			}
			temp = temp.substring(index + 1, temp.length());
			temp = temp.replace("_", " ");
			System.out.println(temp);
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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
    public String getImage(){
        return image;
    }
	public String getTypeURI(){
		return this.typeURI;
	}
	
	public RepositoryConnection getRepoConnection(){
		return this.repoConnection;
	}
}
