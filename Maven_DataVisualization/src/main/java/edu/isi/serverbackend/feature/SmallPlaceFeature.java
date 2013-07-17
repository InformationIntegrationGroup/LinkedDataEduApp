package edu.isi.serverbackend.feature;

import edu.isi.serverbackend.linkedData.LinkedDataConnection;

import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.query.QueryLanguage;
import org.openrdf.query.TupleQuery;
import org.openrdf.query.TupleQueryResult;
import org.openrdf.repository.RepositoryException;

public class SmallPlaceFeature {
	public static float calculateSmallPlace(LinkedDataConnection link){
		float smallParam = 0;
		String evaluateURI;
		
		if(link.isSubjectConnection()){
			evaluateURI = link.getObject().getURI();
		}
		else{
			evaluateURI = link.getSubject().getURI();
		}
		
		String stringQuery = "SELECT ?num WHERE{ "
				+"<"+evaluateURI+"> dbpedia-owl:areaTotal ?num. "
				+"}";
		
		try {
			TupleQuery query = link.getRepoConnection().prepareTupleQuery(QueryLanguage.SPARQL, stringQuery);
			TupleQueryResult result = query.evaluate();
			if(result == null){
				smallParam = 0;
			}
			else{
				smallParam = (float)1/Float.parseFloat(result.next().getValue("num").stringValue());
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
		return smallParam;
	}
}
