package edu.isi.serverbackend.feature;

import java.util.*;

import org.openrdf.query.*;
import org.openrdf.repository.*;

import edu.isi.serverbackend.feature.util.Sample;

public class ImportanceFeature {
	public static void calculateImportance(List<Sample> samples){
		if(!samples.isEmpty()){
			RepositoryConnection repoConn = samples.get(0).getLink().getRepoConnection();
			HashMap<String, Double> importanceMap = new HashMap<String, Double>();
			if(samples.get(0).getLink().isSubjectConnection()){
				importanceMap.put(samples.get(0).getLink().getSubject().getURI(), 0.0);
			}
			else{
				importanceMap.put(samples.get(0).getLink().getObject().getURI(), 0.0);
			}
			for(Sample sample:samples){
				if(sample.getLink().isSubjectConnection()){
					importanceMap.put(sample.getLink().getObject().getURI(), 0.0);
				}
				else{
					importanceMap.put(sample.getLink().getSubject().getURI(), 0.0);
				}
			}
			StringBuffer queryBuffer = new StringBuffer();
			queryBuffer.append("SELECT ?s ?area WHERE{ ");
			queryBuffer.append("?s dbpedia-owl:areaTotal ?area. ");
			try {
				TupleQuery query = repoConn.prepareTupleQuery(QueryLanguage.SPARQL, queryBuffer.toString());
				TupleQueryResult result = query.evaluate();
				while(result.hasNext()){
					
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
			
		}
	}
}
