package edu.isi.serverbackend.feature;

import org.openrdf.query.BooleanQuery;
import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.query.QueryLanguage;
import org.openrdf.repository.RepositoryException;

import edu.isi.serverbackend.linkedData.LinkedDataConnection;

public class EitherNotPlaceFeature {
	public static int isEitherNotPlace(LinkedDataConnection link){
		int eitherIsNotPlace = 1;
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
				+"<"+currentURI+"> a dbpedia-owl:Place. "
				+"<"+extensionURI+"> a dbpedia-owl:Place. "
				+"}";
		
		try {
			BooleanQuery query = link.getRepoConnection().prepareBooleanQuery(QueryLanguage.SPARQL, stringQuery);
			boolean result = query.evaluate();
			if(result == true){
				eitherIsNotPlace = 0;
			}
			else{
				eitherIsNotPlace = 1;
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
		return eitherIsNotPlace;
	}
}
