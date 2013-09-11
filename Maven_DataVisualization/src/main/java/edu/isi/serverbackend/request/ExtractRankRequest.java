package edu.isi.serverbackend.request;

import java.sql.*;
import java.util.*;
import org.json.*;
import edu.isi.serverbackend.localDatabase.table.*;
import edu.isi.serverbackend.localDatabase.bean.*;

public class ExtractRankRequest {
	private final String USER = "linkedDataAdmin";
	private final String PASSWORD = "linkedData";
	private final String CONN_STRING = "jdbc:mysql://localhost/linked_data_project";
	
	Connection SQLConnection;
	String uri;
	
	public ExtractRankRequest(String uri){
		try {
			this.uri = uri;
			Class.forName("com.mysql.jdbc.Driver");
			SQLConnection = DriverManager.getConnection(CONN_STRING, USER, PASSWORD);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public boolean checkAlreadyCached(){
		return NodeTable.checkExplored(uri, SQLConnection);
	}
	
	public JSONObject extractD3JSON(int num){
		try {
			JSONObject result = new JSONObject();
			JSONArray nodeArray = new JSONArray();
			JSONArray linkArray = new JSONArray();
			NodeBean currentNode = NodeTable.retrieveNode(uri, SQLConnection);
			List<ConnectionBean> cBeans = ConnectionTable.retrieveConnections(currentNode.getNodeId(), num, SQLConnection);
			JSONObject current = new JSONObject();
			current.put("name", currentNode.getNodeName());
			current.put("uri", currentNode.getUri());
			nodeArray.put(current);
			for(int i=0; i<cBeans.size(); i++){
				JSONObject node = new JSONObject();
				JSONObject link = new JSONObject();
				boolean exist = false;
				if(cBeans.get(i).getSubjectId() == currentNode.getNodeId()){
					NodeBean tempBean = NodeTable.retrieveNode(cBeans.get(i).getObjectId(), SQLConnection);
					node.put("name", tempBean.getNodeName());
					node.put("uri", tempBean.getUri());
					node.put("group", 1);
					int j;
					for(j=0; j<nodeArray.length(); j++){
						if(nodeArray.get(j).toString().equals(node.toString())){
							exist = true;
							break;
						}
					}
					if(!exist){
						nodeArray.put(node);
						link.put("source", 0);
						link.put("target", nodeArray.length()-1);
						link.put("value", cBeans.get(i).getNamespace()+cBeans.get(i).getRelation());
						linkArray.put(link);
					}
					else{
						link.put("source", 0);
						link.put("target", j);
						link.put("value", cBeans.get(i).getNamespace()+cBeans.get(i).getRelation());
						linkArray.put(link);
					}
					
					
				}
				else if(cBeans.get(i).getObjectId() == currentNode.getNodeId()){
					NodeBean tempBean = NodeTable.retrieveNode(cBeans.get(i).getSubjectId(), SQLConnection);
					node.put("name", tempBean.getNodeName());
					node.put("uri", tempBean.getUri());
					node.put("group", 1);
					int j;
					for(j=0; j<nodeArray.length(); j++){
						if(nodeArray.get(j).toString().equals(node.toString())){
							exist = true;
							break;
						}
					}
					if(!exist){
						nodeArray.put(node);
						link.put("source", nodeArray.length()-1);
						link.put("target", 0);
						link.put("value", cBeans.get(i).getNamespace()+cBeans.get(i).getRelation());
						linkArray.put(link);
					}
					else{
						link.put("source", j);
						link.put("target", 0);
						link.put("value", cBeans.get(i).getNamespace()+cBeans.get(i).getRelation());
						linkArray.put(link);
					}
					
				}
			}
			
			result.put("nodes", nodeArray);
			result.put("links", linkArray);
			return result;
		} catch(Exception t){
			t.printStackTrace();
		}
		return null;
	}
	
	public Connection getSQLConnection(){
		return this.SQLConnection;
	}
}
