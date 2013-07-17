package edu.isi.serverbackend.request;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.*;

import edu.isi.serverbackend.linkedData.LinkedDataConnection;
import edu.isi.serverbackend.localDatabase.bean.*;
import edu.isi.serverbackend.localDatabase.table.ConnectionTable;
import edu.isi.serverbackend.localDatabase.table.NodeTable;

public class LinkedDataCachingRequest{
	private final String USER = "linkedDataAdmin";
	private final String PASSWORD = "linkedData";
	private final String CONN_STRING = "jdbc:mysql://localhost/linked_data_project";
	
	Connection SQLConnection;
	
	List<LinkedDataConnection> subjectConnections;
	List<LinkedDataConnection> objectConnections;
	private CachingThread cachingThread;

	
	public LinkedDataCachingRequest(Connection sql, List<LinkedDataConnection> subjectConnections, List<LinkedDataConnection> objectConnections){
		this.subjectConnections = subjectConnections;
		this.objectConnections = objectConnections;
		cachingThread = new CachingThread();
		SQLConnection = sql;
	}
	
	public void cachNodes(){
		NodeBean nodeBean = new NodeBean();
		nodeBean.setNodeName(subjectConnections.get(0).getSubject().getName());
		nodeBean.setUri(subjectConnections.get(0).getSubject().getURI());
		nodeBean.setExplored(true);
		int index = NodeTable.findNode(subjectConnections.get(0).getSubject().getURI(), SQLConnection);
		if( index != -1){
			NodeTable.updateNode(index, nodeBean, SQLConnection);
		}
		else{
			NodeTable.insertNode(nodeBean, SQLConnection);
		}
		
		
		for(int i = 0; i<subjectConnections.size();i++){
			nodeBean = new NodeBean();
			nodeBean.setNodeName(subjectConnections.get(i).getObject().getName());
			nodeBean.setUri(subjectConnections.get(i).getObject().getURI());
			nodeBean.setExplored(false);
			index = NodeTable.findNode(subjectConnections.get(i).getObject().getURI(), SQLConnection);
			if(index == -1){
				NodeTable.insertNode(nodeBean, SQLConnection);
			}
		}
		
		for(int i = 0; i<objectConnections.size();i++){
			nodeBean = new NodeBean();
			nodeBean.setNodeName(objectConnections.get(i).getSubject().getName());
			nodeBean.setUri(objectConnections.get(i).getSubject().getURI());
			nodeBean.setExplored(false);
			index = NodeTable.findNode(objectConnections.get(i).getSubject().getURI(), SQLConnection);
			if(index == -1){
				NodeTable.insertNode(nodeBean, SQLConnection);
			}
		}
	}
	
	public void cachSubjectConnections(){
		int subjectIndex = NodeTable.findNode(subjectConnections.get(0).getSubject().getURI(), SQLConnection);
		for(int i=0; i<subjectConnections.size(); i++){
			ConnectionBean cBean = new ConnectionBean();
			cBean.setRelation(obtainRelation(subjectConnections.get(i).getPredicate()));
			cBean.setNamespace(obtainNamespace(subjectConnections.get(i).getPredicate()));
			cBean.setSubjectId(subjectIndex);
			cBean.setObjectId(NodeTable.findNode(subjectConnections.get(i).getObject().getURI(), SQLConnection));
			cBean.setObjectRank((int)subjectConnections.get(i).getConnectionParam());
			ConnectionTable.insertConnection(cBean, SQLConnection);
		}
	}
	
	public void cachObjectConnections(){
		int objectIndex = NodeTable.findNode(objectConnections.get(0).getObject().getURI(), SQLConnection);
		for(int i=0; i<objectConnections.size(); i++){
			ConnectionBean cBean = new ConnectionBean();
			cBean.setRelation(obtainRelation(objectConnections.get(i).getPredicate()));
			cBean.setNamespace(obtainNamespace(objectConnections.get(i).getPredicate()));
			cBean.setSubjectId(NodeTable.findNode(objectConnections.get(i).getSubject().getURI(), SQLConnection));
			cBean.setObjectId(objectIndex);
			cBean.setSubjectRank((int)objectConnections.get(i).getConnectionParam());
			ConnectionTable.insertConnection(cBean, SQLConnection);
		}
	}
	
	public void startCaching(){
		cachingThread.start();
	}
	
	private String obtainNamespace(String predicate){
		String[] temp = predicate.split("//");
		String[] temp2 = temp[1].split("/");
		String result = "http://";
		for(int i =0; i<temp2.length-1; i++){
			result = result +temp2[i]+"/";
		}
		
		return result;
	}
	
	private String obtainRelation(String predicate){
		String[] temp = predicate.split("//");
		String[] temp2 = temp[1].split("/");
		return temp2[temp2.length-1];
	}
	
	private class CachingThread extends Thread{
		
		public CachingThread(){
			super();
		}
		
		public void run(){
			System.out.println("Caching thread starts.");
			cachNodes();
			cachSubjectConnections();
			cachObjectConnections();
			System.out.println("Caching thread ends.");
		}
	}
	
	
}
