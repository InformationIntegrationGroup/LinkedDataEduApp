package feature;

import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.query.QueryLanguage;
import org.openrdf.query.TupleQuery;
import org.openrdf.query.TupleQueryResult;
import org.openrdf.repository.RepositoryException;

import linkedData.*;

public class RarityFeature {
	
	public static float calculateRarity(LinkedDataConnection link){
		float rarity = 0;
		String stringQuery = "SELECT (COUNT(*) AS ?count) WHERE{ "
				+ "?s <"+link.getPredicate()+"> ?o. "
				+"}";
		try {
			TupleQuery query = link.getRepoConnection().prepareTupleQuery(QueryLanguage.SPARQL, stringQuery);
			TupleQueryResult result = query.evaluate();
			rarity = (float)1/Float.parseFloat(result.next().getValue("count").stringValue());
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
