package edu.isi.serverbackend.feature;

import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.query.QueryLanguage;
import org.openrdf.query.TupleQuery;
import org.openrdf.query.TupleQueryResult;
import org.openrdf.repository.RepositoryException;

import edu.isi.serverbackend.linkedData.LinkedDataConnection;

public class RarityFeature {
	
	public static double calculateRarity(LinkedDataConnection link){
		double rarity = 0;
		String stringQuery = "SELECT (COUNT(*) AS ?count) WHERE{ "
				+ "?s <"+link.getPredicate()+"> ?o. "
				+"}";
		try {
			TupleQuery query = link.getRepoConnection().prepareTupleQuery(QueryLanguage.SPARQL, stringQuery);
			TupleQueryResult result = query.evaluate();
			rarity = 1/Math.log(Double.parseDouble(result.next().getValue("count").stringValue()));
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
		return rarity;
	}
}
