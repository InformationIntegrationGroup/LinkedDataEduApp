package edu.isi.serverbackend.localDatabase.mongoCollection;

import edu.isi.serverbackend.localDatabase.bean.*;

import java.util.*;

import com.mongodb.*;

public class TripleCollection {
	public static final String COLLECTION = "Triple";

	public static List<TripleBean> retrieveTriples(String nodeURI, DB dbConn){
		List<TripleBean> tripleBeans = new ArrayList<TripleBean>();
		DBCollection tripleCollection = dbConn.getCollection(COLLECTION);
		if( nodeURI != null){
			BasicDBObject subjectQuery = new BasicDBObject("subject.uri", nodeURI);
			BasicDBObject objectQuery = new BasicDBObject("object.uri", nodeURI);
			DBCursor objQueryCursor = tripleCollection.find(objectQuery);
			DBCursor sbjQueryCursor = tripleCollection.find(subjectQuery);
			try{
				while(sbjQueryCursor.hasNext()){
					DBObject record = sbjQueryCursor.next();
					tripleBeans.add(retrieveEmbeddedData(record));
				}
				while(objQueryCursor.hasNext()){
					DBObject record = objQueryCursor.next();
					tripleBeans.add(retrieveEmbeddedData(record));
				}
			}
			catch(MongoException e){
				e.getStackTrace();
			}
			finally{
				sbjQueryCursor.close();
				objQueryCursor.close();
			}	
		}
		return tripleBeans;
	}
	
	public static void insertTriple(TripleBean triple, DB dbConn){
		BasicDBObject query = new BasicDBObject();
		BasicDBObject subject = new BasicDBObject();
		BasicDBObject object = new BasicDBObject();
		BasicDBObject predicate = new BasicDBObject();
		subject.append("name", triple.getSubject().getName());
		subject.append("uri", triple.getSubject().getUri());
		subject.append("typeURI", triple.getObject().getTypeURI());
		object.append("name", triple.getObject().getName());
		object.append("uri", triple.getObject().getUri());
		object.append("typeURI", triple.getObject().getTypeURI());
		predicate.append("name", triple.getPredicate().getName());
		predicate.append("uri", triple.getPredicate().getURI());
		
		query.append("subject", subject);
		query.append("object", object);
		query.append("predicate", predicate);
		query.append("subjectDegree", triple.getSubjectDegree());
		query.append("objectDegree", triple.getObjectDegree());
		query.append("sbjExtensionRarity", triple.getSbjExtensionRarity());
		query.append("objExtensionRarity", triple.getObjExtensionRarity());
		dbConn.getCollection(COLLECTION).insert(query);
	}
	
	private static TripleBean retrieveEmbeddedData(DBObject record){
		TripleBean triple = new TripleBean();
		NodeBean subjectBean =  new NodeBean();
		NodeBean objectBean = new NodeBean();
		PredicateBean predicateBean = new PredicateBean();
		subjectBean.setName(((DBObject)record.get("subject")).get("name").toString());
		subjectBean.setTypeURI(((DBObject)record.get("subject")).get("typeURI").toString());
		subjectBean.setUri(((DBObject)record.get("subject")).get("uri").toString());
		
		objectBean.setName(((DBObject)record.get("object")).get("name").toString());
		objectBean.setTypeURI(((DBObject)record.get("object")).get("typeURI").toString());
		objectBean.setUri(((DBObject)record.get("object")).get("uri").toString());
		
		predicateBean.setURI(((DBObject)record.get("predicate")).get("uri").toString());
		triple.setSubject(subjectBean);
		triple.setObject(objectBean);
		triple.setPredicate(predicateBean);
		triple.setTripleID(record.get("_id").toString());
		triple.setSubjectDegree(Double.parseDouble(record.get("subjectDegree").toString()));
		triple.setObjectDegree(Double.parseDouble(record.get("objectDegree").toString()));
		triple.setSbjExtensionRarity(Double.parseDouble(record.get("sbjExtensionRarity").toString()));
		triple.setObjExtensionRarity(Double.parseDouble(record.get("objExtensionRarity").toString()));
		
		return triple;
	}
}
