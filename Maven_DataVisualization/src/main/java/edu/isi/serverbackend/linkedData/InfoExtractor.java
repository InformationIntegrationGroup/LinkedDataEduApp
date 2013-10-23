package edu.isi.serverbackend.linkedData;

import java.util.*;


import org.openrdf.query.*;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;

import edu.isi.serverbackend.feature.util.*;

public class InfoExtractor {
	public static void extractNames(List<Sample> samples){
		if(!samples.isEmpty()){
			RepositoryConnection repoConn = samples.get(0).getLink().getRepoConnection();
			HashMap<String, String> nameMap = new HashMap<String, String>();
			String stringQuery = "SELECT ?s ?label{ "
					+ "?s rdfs:label ?label. " 
					+ "FILTER(langMatches(lang(?label), \"EN\")) ";
			String filterQuery = "FILTER( ";
			for(Sample sample:samples){
				if(!nameMap.containsKey(sample.getLink().getSubject().getURI())){
					nameMap.put(sample.getLink().getSubject().getURI(), "");
				}
				if(!nameMap.containsKey(sample.getLink().getObject().getURI())){
					nameMap.put(sample.getLink().getObject().getURI(), "");
				}
			}
			int count = 0;
			for(String node:nameMap.keySet()){
				filterQuery += "?s = <" + node + ">";
				count++;
				if(count < nameMap.keySet().size()){
					filterQuery += " OR ";
				}
			}

			filterQuery += ") }";
			stringQuery += filterQuery;
			System.out.println(filterQuery);
			System.out.println(stringQuery);
			
			try{
				TupleQuery query = repoConn.prepareTupleQuery(QueryLanguage.SPARQL, stringQuery);
				TupleQueryResult result = query.evaluate();
				while(result.hasNext()){
					BindingSet bindingSet = result.next();
					nameMap.put(bindingSet.getValue("s").stringValue(), bindingSet.getValue("label").stringValue());
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
			for(int i = 0; i < samples.size(); i++){
				samples.get(i).getLink().getSubject().setName(nameMap.get(samples.get(i).getLink().getSubject().getURI()));
				samples.get(i).getLink().getObject().setName(nameMap.get(samples.get(i).getLink().getObject().getURI()));
			}
		}
	}
}
