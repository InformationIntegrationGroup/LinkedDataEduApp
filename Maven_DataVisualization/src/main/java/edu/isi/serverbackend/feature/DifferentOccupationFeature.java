package edu.isi.serverbackend.feature;

import edu.isi.serverbackend.linkedData.LinkedDataConnection;

import org.openrdf.query.BooleanQuery;
import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.query.QueryLanguage;
import org.openrdf.repository.RepositoryException;

public class DifferentOccupationFeature {
	public static float isDifferentOccupation(LinkedDataConnection link){
		float differentOccupation = 0;
		String currentURI;
		String extensionURI;
		if(link.isSubjectConnection()){
			currentURI = link.getSubject().getURI();
			extensionURI = link.getObject().getURI();
		}
		else{
			currentURI = link.getObject().getURI();
			extensionURI = link.getSubject().getURI();
		}
		String stringQuery = "ASK { "
				+"<"+currentURI+"> a ?type. "
				+"?type rdfs:subClassOf dbpedia-owl:Person. "
				+"<"+extensionURI+"> a ?type. "
				+"}";
		try {
			BooleanQuery query = link.getRepoConnection().prepareBooleanQuery(QueryLanguage.SPARQL, stringQuery);
			boolean result = query.evaluate();
			if(result == true){
				differentOccupation = 0;
			}
			else{
				differentOccupation = 1;
			}
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
		
		return differentOccupation;
	}
}
