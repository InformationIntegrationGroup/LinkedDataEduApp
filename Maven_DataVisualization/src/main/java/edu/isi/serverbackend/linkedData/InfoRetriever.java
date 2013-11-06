package edu.isi.serverbackend.linkedData;


import java.util.*;
import org.openrdf.query.*;
import org.openrdf.repository.*;

import edu.isi.serverbackend.feature.util.Sample;

public class InfoRetriever {
	
	public static void retrieveNames(List<Sample> samples){
		if(!samples.isEmpty()){
			RepositoryConnection repoConn = samples.get(0).getLink().getRepoConnection();
			Map<String, String> nameMap = new LinkedHashMap<String, String>();
			List<String> nameList =  new ArrayList<String>();
			for(Sample sample:samples){
				if(!nameMap.containsKey(sample.getLink().getSubject().getURI())){
					nameMap.put(sample.getLink().getSubject().getURI(), "");
				}
				if(!nameMap.containsKey(sample.getLink().getObject().getURI())){
					nameMap.put(sample.getLink().getObject().getURI(), "");
				}
			}
			StringBuilder queryBuilder = new StringBuilder("SELECT ?label WHERE{");
			StringBuilder unionQueryBuilder = new StringBuilder();
			int count = 0;
			for(String node:nameMap.keySet()){
				unionQueryBuilder.append("{ <"+node+"> rdfs:label ?label ");
				unionQueryBuilder.append("FILTER(langMatches(lang(?label), \"EN\")) }");
				count++;
				if(count < nameMap.keySet().size()){
					unionQueryBuilder.append(" UNION ");
				}
			}
			queryBuilder.append(unionQueryBuilder);
			queryBuilder.append("}");
			System.out.println(unionQueryBuilder.toString());
			System.out.println(queryBuilder.toString());
			
			try{
				TupleQuery query = repoConn.prepareTupleQuery(QueryLanguage.SPARQL, queryBuilder.toString());
				TupleQueryResult result = query.evaluate();
				while(result.hasNext()){
					BindingSet bindingSet = result.next();
					nameList.add(bindingSet.getValue("label").stringValue());
				}
				count = 0;
				for(String key:nameMap.keySet()){
					if(count < nameList.size()){
						nameMap.put(key, nameList.get(count));
						count++;
					}
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
