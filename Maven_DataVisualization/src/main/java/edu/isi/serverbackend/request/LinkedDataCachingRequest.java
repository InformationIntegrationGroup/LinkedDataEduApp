package edu.isi.serverbackend.request;

import java.net.UnknownHostException;

import com.mongodb.*;

import java.util.*;

import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.repository.RepositoryException;

import edu.isi.serverbackend.linkedData.*;
import edu.isi.serverbackend.localDatabase.bean.*;
import edu.isi.serverbackend.localDatabase.mongoCollection.*;
import edu.isi.serverbackend.feature.RarityDegree;
import edu.isi.serverbackend.feature.util.*;

public class LinkedDataCachingRequest{
	private static final String USER = "linkedDataAdmin";
	private static final String PASSWORD = "";
	private static final String HOST = "localhost";
	private static final String DATABASE = "LinkedData";
	private static final String EXPLORED = "FullyExploredNode";
	private static final String ADDED = "QueueAddedNode";

	
	private DB dbConn;
	public enum TripleType {Person, Place, Organisation, Work, Event, All};
	
	private CachingThread cachingThread;

	
	public LinkedDataCachingRequest(){
		try {
			MongoClient mongo = new MongoClient(HOST);
			this.dbConn = mongo.getDB(DATABASE);
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public void cachingTripleByBFS(LinkedDataNode seed, TripleType cachingType, long max){
		String type =  null;
		List<Sample> samples = new ArrayList<Sample>();
		Queue<LinkedDataNode> nodeQueue = new LinkedList<LinkedDataNode>();
		long total = 0;
		if(cachingType == TripleType.Person){
			type = "http://dbpedia.org/ontology/Person";
		}
		else if(cachingType == TripleType.Place){
			type = "http://dbpedia.org/ontology/Place";
		}
		else if(cachingType == TripleType.Organisation){
			type = "http://dbpedia.org/ontology/Organisation";
		}
		else if(cachingType == TripleType.Work){
			type = "http://dbpedia.org/ontology/Work";
		}
		else if(cachingType == TripleType.Event){
			type = "http://dbpedia.org/ontology/Event";
		}
		
		try {
			insertNodeIntoTemp(seed, ADDED);
			seed.retrieveNameAndType();
			seed.retrieveSubjectConnections(samples);
			seed.retrieveObjectConnections(samples);
			RarityDegree.calcuateNodeDegree(samples);
			RarityDegree.calculateExtensionRarity(samples);
			total += pushNodeIntoQueueAndStoreTriple(samples, nodeQueue, type);
			while (!nodeQueue.isEmpty() && total < max) {
				samples = new ArrayList<Sample>();
				LinkedDataNode currentNode = nodeQueue.remove();
				currentNode.retrieveSubjectConnections(samples);
				currentNode.retrieveObjectConnections(samples);
				RarityDegree.calcuateNodeDegree(samples);
				RarityDegree.calculateExtensionRarity(samples);
				total += pushNodeIntoQueueAndStoreTriple(samples, nodeQueue, type);
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
	
	private long pushNodeIntoQueueAndStoreTriple(List<Sample> samples, Queue<LinkedDataNode> nodeQueue, String type){
		DBCollection exploredNodeCollection = dbConn.getCollection(EXPLORED);
		DBCollection addedNodeCollection = dbConn.getCollection(ADDED);
		NodeBean currentNodeBean = new NodeBean();
		long tripleNum = 0;
		if(!samples.isEmpty()){
			if(samples.get(0).getLink().isSubjectConnection()){
				insertNodeIntoTemp(samples.get(0).getLink().getSubject(), EXPLORED);
				currentNodeBean.setName(samples.get(0).getLink().getSubject().getName());
				currentNodeBean.setExplored(true);
				currentNodeBean.setUri(samples.get(0).getLink().getSubject().getURI());
				currentNodeBean.setTypeURI(samples.get(0).getLink().getSubject().getTypeURI());
			}
			else{
				insertNodeIntoTemp(samples.get(0).getLink().getObject(), EXPLORED);
				currentNodeBean.setName(samples.get(0).getLink().getObject().getName());
				currentNodeBean.setExplored(true);
				currentNodeBean.setUri(samples.get(0).getLink().getObject().getURI());
				currentNodeBean.setTypeURI(samples.get(0).getLink().getObject().getTypeURI());
			}
		}
		for(Sample sample:samples){
			if(sample.getLink().isSubjectConnection()){
				BasicDBObject query = new BasicDBObject("uri", sample.getLink().getObject().getURI());
				DBCursor cursor = exploredNodeCollection.find(query);
				if(!cursor.hasNext()){
					cursor = addedNodeCollection.find(query);
					if(!cursor.hasNext() && (sample.getLink().getObject().getTypeURI().equals(type) || type == null)){
						nodeQueue.add(sample.getLink().getObject());
						addedNodeCollection.insert(query);
					}
					
					TripleBean tripleBean =  new TripleBean();
					PredicateBean predicateBean = new PredicateBean();
					predicateBean.setURI(sample.getLink().getPredicate());
					tripleBean.setPredicate(predicateBean);
					NodeBean node = new NodeBean();
					node.setExplored(false);
					node.setName(sample.getLink().getObject().getName());
					node.setTypeURI(sample.getLink().getObject().getTypeURI());
					node.setUri(sample.getLink().getObject().getURI());
					tripleBean.setSubject(currentNodeBean);
					tripleBean.setObject(node);
					
					tripleBean.setSbjExtensionRarity(sample.getSubjectExtensionRarity());
					tripleBean.setObjExtensionRarity(sample.getObjectExtensionRarity());
					tripleBean.setSubjectDegree(sample.getSubjectRarity());
					tripleBean.setObjectDegree(sample.getObjectRarity());
					TripleCollection.insertTriple(tripleBean, dbConn);
					tripleNum++;
				}
			}
			else{
				BasicDBObject query = new BasicDBObject("uri", sample.getLink().getSubject().getURI());
				DBCursor cursor = exploredNodeCollection.find(query);
				if(!cursor.hasNext()){
					cursor = addedNodeCollection.find(query);
					if(!cursor.hasNext() && (sample.getLink().getSubject().getTypeURI().equals(type) || type == null)){
						nodeQueue.add(sample.getLink().getSubject());
						addedNodeCollection.insert(query);
					}
					TripleBean tripleBean =  new TripleBean();
					PredicateBean predicateBean = new PredicateBean();
					predicateBean.setURI(sample.getLink().getPredicate());
					tripleBean.setPredicate(predicateBean);
					NodeBean node = new NodeBean();
					node.setExplored(false);
					node.setName(sample.getLink().getSubject().getName());
					node.setTypeURI(sample.getLink().getSubject().getTypeURI());
					node.setUri(sample.getLink().getSubject().getURI());
					tripleBean.setObject(currentNodeBean);
					tripleBean.setSubject(node);
					
					tripleBean.setSbjExtensionRarity(sample.getSubjectExtensionRarity());
					tripleBean.setObjExtensionRarity(sample.getObjectExtensionRarity());
					tripleBean.setSubjectDegree(sample.getSubjectRarity());
					tripleBean.setObjectDegree(sample.getObjectRarity());
					TripleCollection.insertTriple(tripleBean, dbConn);
					tripleNum++;
				}
			}
		}
		return tripleNum;
	}
	
	private void insertNodeIntoTemp(LinkedDataNode node, String collectionName){
		BasicDBObject query = new BasicDBObject("uri", node.getURI());
		DBCollection tempCollection = dbConn.getCollection(collectionName);
		tempCollection.insert(query);
	}
	
	public void startCaching(){
		cachingThread.start();
	}
	
	private class CachingThread extends Thread{
		
		public CachingThread(){
			super();
		}
		
		public void run(){
			
		}
	}
	
}
