package edu.isi.serverbackend.localDatabase.table;

import edu.isi.serverbackend.localDatabase.bean.ConnectionBean;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class ConnectionTable {

	public static ConnectionBean retrieveConnection(int index, Connection conn){
		ConnectionBean connection = null;
		String SQL = "SELECT * FROM connection WHERE connectionId=? ";
		ResultSet result = null;
		try {
			PreparedStatement statement = conn.prepareStatement(SQL, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
			result = statement.executeQuery();
			
			result.last();
			if(result.getRow() == 1){
				result.beforeFirst();
				result.next();
				connection =  new ConnectionBean();
				connection.setConnectionId(index);
				connection.setNamespace(result.getString("namespace"));
				connection.setRelation(result.getString("relation"));
				connection.setObjectId(result.getInt("objectId"));
				connection.setSubjectId(result.getInt("subjectId"));
				connection.setObjectRank(result.getInt("objectRank"));
				connection.setSubjectRank(result.getInt("subjectRank"));
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return connection;
	}
	
	public static List<ConnectionBean> retrieveConnections(int nodeId, int num, Connection conn){
		List<ConnectionBean> subjectConnBeans = new ArrayList<ConnectionBean>();
		List<ConnectionBean> objectConnBeans = new ArrayList<ConnectionBean>();
		List<ConnectionBean> beans =  new ArrayList<ConnectionBean>();
		
		String subjectSQL = "SELECT * FROM connection WHERE subjectId=? ORDER BY objectRank ASC ";
		String objectSQL = "SELECT * FROM connection WHERE objectId=? ORDER BY subjectRank ASC ";
		System.out.println("current node id: "+nodeId);
		ResultSet result = null;
		
		try {
			//extract the links where current node is subject
			PreparedStatement statement = conn.prepareStatement(subjectSQL, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
			statement.setInt(1, nodeId);
			//statement.setInt(2, num);
			result = statement.executeQuery();
			result.last();
			System.out.println("RUN subject query and get "+ result.getRow());
			result.beforeFirst();
			int count=0;
			while(result.next()){
				ConnectionBean cbean = new ConnectionBean();
				cbean.setConnectionId(result.getInt("connectionId"));
				cbean.setNamespace(result.getString("namespace"));
				cbean.setRelation(result.getString("relation"));
				cbean.setObjectId(result.getInt("objectId"));
				cbean.setSubjectId(result.getInt("subjectId"));
				cbean.setObjectRank(result.getInt("objectRank"));
				cbean.setSubjectRank(result.getInt("subjectRank"));
				subjectConnBeans.add(cbean);
				count++;
			}
			System.out.println("loop "+count);
			//extract the links where current node is object
			statement = conn.prepareStatement(objectSQL, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
			statement.setInt(1, nodeId);
			//statement.setInt(2, num);
			result = statement.executeQuery();
			result.last();
			System.out.println("RUN object query and get "+ result.getRow());
			result.beforeFirst();
			while(result.next()){
				ConnectionBean cbean = new ConnectionBean();
				cbean.setConnectionId(result.getInt("connectionId"));
				cbean.setNamespace(result.getString("namespace"));
				cbean.setRelation(result.getString("relation"));
				cbean.setObjectId(result.getInt("objectId"));
				cbean.setSubjectId(result.getInt("subjectId"));
				cbean.setObjectRank(result.getInt("objectRank"));
				cbean.setSubjectRank(result.getInt("subjectRank"));
				objectConnBeans.add(cbean);
			}
			
			//combine the two ordered list
			int i = 0;
			int j = 0;
			int k = 0;
			while(k < num && i < subjectConnBeans.size() && j < objectConnBeans.size()){
				if(objectConnBeans.get(j).getSubjectRank() > subjectConnBeans.get(i).getObjectRank()){
					beans.add(subjectConnBeans.get(i));
					i++;
					k++;
				}
				else{
					beans.add(objectConnBeans.get(j));
					j++;
					k++;
				}
			}
			if(i >= subjectConnBeans.size() && k < num){
				while(j < objectConnBeans.size() && k < num){
					beans.add(objectConnBeans.get(j));
					j++;
					k++;
				}
			}
			else if(j >= objectConnBeans.size() && k < num){
				while(i < subjectConnBeans.size() && k < num){
					beans.add(subjectConnBeans.get(i));
					i++;
					k++;
				}
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("num :"+num);
		System.out.println("size of subject array: "+subjectConnBeans.size());
		System.out.println("size of object array: "+objectConnBeans.size());
		System.out.println("size of beans: "+beans.size());
		return beans;
	}
	
	public static boolean insertConnection(ConnectionBean connBean, Connection conn){
		boolean success = false;
		ResultSet result = null;
		
		String SQL = "SELECT connectionId, subjectRank, objectRank FROM connection WHERE subjectId=? AND ObjectId=? AND relation=? AND namespace=? ";
		try {
			PreparedStatement findStatement = conn.prepareStatement(SQL, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
			findStatement.setInt(1, connBean.getSubjectId());
			findStatement.setInt(2, connBean.getObjectId());
			findStatement.setString(3, connBean.getRelation());
			findStatement.setString(4, connBean.getNamespace());
			result = findStatement.executeQuery();
			result.last();
			if(result.getRow() != 0){
				result.beforeFirst();
				result.next();
				if(result.getInt("subjectRank") == -1 && connBean.getSubjectRank() != -1){
					SQL = "UPDATE connection SET subjectRank=? WHERE connectionId=? ";
					PreparedStatement updateStatement  = conn.prepareStatement(SQL, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
					updateStatement.setInt(1, connBean.getSubjectRank());
					updateStatement.setInt(2, result.getInt("connectionId"));
					if(updateStatement.executeUpdate() == 1){
						success = true;
					}
				}
				else if(result.getInt("objectRank") == -1 && connBean.getObjectRank() != -1){
					SQL = "UPDATE connection SET objectRank=? WHERE connectionId=? ";
					PreparedStatement updateStatement  = conn.prepareStatement(SQL, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
					updateStatement.setInt(1, connBean.getObjectRank());
					updateStatement.setInt(2, result.getInt("connectionId"));
					if(updateStatement.executeUpdate() == 1){
						success = true;
					}
				}
			}
			else{
				SQL = "INSERT INTO connection (relation, namespace, subjectId, objectId, subjectRank, objectRank) VALUES (?, ?, ?, ?, ?, ?) ";
				PreparedStatement insertStatement = conn.prepareStatement(SQL, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
				insertStatement.setString(1, connBean.getRelation());
				insertStatement.setString(2, connBean.getNamespace());
				insertStatement.setInt(3, connBean.getSubjectId());
				insertStatement.setInt(4, connBean.getObjectId());
				insertStatement.setInt(5, connBean.getSubjectRank());
				insertStatement.setInt(6, connBean.getObjectRank());
				if(insertStatement.executeUpdate() == 1){
					success = true;
				}
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return success;
	}
}
