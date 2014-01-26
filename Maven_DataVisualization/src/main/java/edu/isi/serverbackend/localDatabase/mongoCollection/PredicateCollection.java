package edu.isi.serverbackend.localDatabase.mongoCollection;

import com.mongodb.*;
import edu.isi.serverbackend.localDatabase.bean.*;

public class PredicateCollection {
	
	public static final String COLLECTION = "Predicate";
	
	public static String insertPredicate(PredicateBean predicate, DB dbConn){
		BasicDBObject doc = new BasicDBObject();
		doc.append("name", predicate.getName());
		doc.append("uri", predicate.getURI());
		dbConn.getCollection(COLLECTION).insert(doc);
		String id = findPredicate(predicate.getURI(), dbConn);
		return id;
	}
	
	public static String findPredicate(String uri, DB dbConn){
		String nodeID = null;
		BasicDBObject query =  new BasicDBObject("uri", uri);
		BasicDBObject projection = new BasicDBObject("_id", "1");
		DBCollection predicateCollection = dbConn.getCollection(COLLECTION);
		DBCursor cursor = predicateCollection.find(query, projection);
		try{
			if(cursor.hasNext()){
				DBObject record =  cursor.next();
				nodeID = record.get("_id").toString();
				System.out.println(nodeID);
			}
		}
		catch(MongoException e){
			e.getStackTrace();
		}
		finally{
			cursor.close();
		}
		return nodeID;
	}
	
	public static PredicateBean retrievePredicate(String predicateID, DB dbConn){
		PredicateBean predicate = null;
		BasicDBObject query =  new BasicDBObject("_id", predicateID);
		DBCollection predicateCollection = dbConn.getCollection(COLLECTION);
		DBCursor cursor = predicateCollection.find(query);
		try{
			if(cursor.hasNext()){
				predicate = new PredicateBean();
				DBObject record =  cursor.next();
				predicate.setPredicateID(record.get("_id").toString());
				predicate.setURI(record.get("uri").toString());
				System.out.println(record.get("uri").toString());
			}
		}
		catch(MongoException e){
			e.getStackTrace();
		}
		finally{
			cursor.close();
		}
		
		return predicate;
	}
}
