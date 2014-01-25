package edu.isi.serverbackend.localDatabase.mongoCollection;

import com.mongodb.*;

import edu.isi.serverbackend.localDatabase.bean.*;

public class NodeCollection {
	public static final String COLLECTION = "Node";

	public static String findNode(String uri, DB dbConn){
		String nodeID = null;
		DBCollection nodeCollection = dbConn.getCollection(COLLECTION);
		BasicDBObject query =  new BasicDBObject("uri", uri);
		BasicDBObject projection = new BasicDBObject("_id", "1");
		DBCursor cursor = nodeCollection.find(query, projection);
		try{
			if(cursor.hasNext()){
				nodeID = cursor.next().get("_id").toString();
			}
		}catch(MongoException e){
			e.getStackTrace();
		}
		finally{
			cursor.close();
		}
		return nodeID;
	}
	
	public static NodeBean retreiveNode(String uri, DB dbConn){
		NodeBean node = null;
		DBCollection nodeCollection = dbConn.getCollection(COLLECTION);
		BasicDBObject query =  new BasicDBObject("uri", uri);
		BasicDBObject projection = new BasicDBObject("_id", "1");
		DBCursor cursor = nodeCollection.find(query, projection);
		try{
			if(cursor.hasNext()){
				DBObject record = cursor.next();
				node = new NodeBean();
				System.out.println(record.get("name").toString());
				System.out.println(record.get("evaluated").toString());
				node.setName(record.get("name").toString());
			}
		}catch(MongoException e){
			e.getStackTrace();
		}
		finally{
			cursor.close();
		}
		return node;
	}
	
	public static boolean checkNodeIfEvaluated(String uri, DB dbConn){
		boolean result = false;
		DBCollection nodeCollection = dbConn.getCollection(COLLECTION);
		BasicDBObject query = new BasicDBObject("uri", uri);
		BasicDBObject projection = new BasicDBObject("evaluated", "1");
		DBCursor cursor = nodeCollection.find(query, projection);
		try{
			if(cursor.hasNext()){
				DBObject record = cursor.next();
				result = Boolean.parseBoolean(record.get("evaluated").toString());
			}
		}catch(MongoException e){
			e.getStackTrace();
		}
		finally{
			cursor.close();
		}
		return result;
	}
	
	public static String insertNode(NodeBean newNode, DB dbConn){
		BasicDBObject doc = new BasicDBObject("name", newNode.getName());
		doc.append("uri", newNode.getUri());
		doc.append("type", newNode.getTypeURI());
		dbConn.getCollection("Node").insert(doc);
		String id = findNode(newNode.getUri(), dbConn);
		return id;
	}
	
}
