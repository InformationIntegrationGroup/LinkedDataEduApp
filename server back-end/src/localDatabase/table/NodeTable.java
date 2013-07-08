package localDatabase.table;

import java.sql.*;

import localDatabase.bean.NodeBean;

public class NodeTable {
	
	public static int findNode(String uri, Connection conn){
		int index = -1;
		String SQL = "SELECT nodeId FROM node WHERE uri=? ";
		ResultSet result = null;
		try {
			PreparedStatement statement = conn.prepareStatement(SQL, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
			statement.setString(1, uri);
			result = statement.executeQuery();
			result.last();
			
			if(result.getRow() == 0){
				return index;
			}
			else{
				result.beforeFirst();
				result.next();
				index = result.getInt("nodeId");
				return index;
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return index;
		}
	}
	
	public static NodeBean retrieveNode(String uri, Connection conn){
		NodeBean node = null;
		String SQL = "SELECT * FROM node WHERE uri=? ";
		ResultSet result = null;
		
		try {
			PreparedStatement statement =  conn.prepareStatement(SQL, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
			statement.setString(1, uri);
			result = statement.executeQuery();
			result.last();
			if(result.getRow() != 0){
				result.beforeFirst();
				result.next();
				node = new NodeBean();
				node.setNodeId(result.getInt("nodeId"));
				node.setNodeName(result.getString("nodeName"));
				node.setExplored(result.getBoolean("explored"));
				node.setUri(result.getString("uri"));
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return node;
	}
	
	public static NodeBean retrieveNode(int index, Connection conn){
		NodeBean node = null;
		String SQL = "SELECT * FROM node WHERE nodeId=? ";
		ResultSet result = null;
		
		try {
			PreparedStatement statement =  conn.prepareStatement(SQL, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
			statement.setInt(1, index);
			result = statement.executeQuery();
			result.last();
			if(result.getRow() != 0){
				result.beforeFirst();
				result.next();
				node = new NodeBean();
				node.setNodeId(index);
				node.setNodeName(result.getString("nodeName"));
				node.setExplored(result.getBoolean("explored"));
				node.setUri(result.getString("uri"));
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return node;
	}
	
	public static boolean insertNode(NodeBean newNode, Connection conn){
		boolean success = false;

		try {
			String SQL = "INSERT INTO node (nodeName, uri, explored) VALUES(?, ?, ?)";
			PreparedStatement statement = conn.prepareStatement(SQL, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
			statement.setString(1, newNode.getNodeName());
			statement.setString(2, newNode.getUri());
			statement.setBoolean(3, newNode.isExplored());
			if(statement.executeUpdate() == 1){
				success = true;
			}
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return success;
	}
	
	public static boolean updateNode(int index, NodeBean newNode, Connection conn){
		boolean success = false;
		String SQL = "UPDATE node SET explored=? WHERE nodeId=?";
		PreparedStatement statement;
		try {
			statement = conn.prepareStatement(SQL, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
			statement.setBoolean(1, newNode.isExplored());
			statement.setInt(2, index);
			if(statement.executeUpdate() == 1){
				success = true;
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return success;
	}
	
	public static boolean checkExplored(String uri, Connection conn){
		boolean explored = false;
		String SQL = "SELECT explored FROM node WHERE uri=? ";
		try {
			PreparedStatement statement = conn.prepareStatement(SQL, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
			statement.setString(1, uri);
			ResultSet result = statement.executeQuery();
			result.last();
			if(result.getRow() != 0){
				result.beforeFirst();
				result.next();
				if(result.getInt("explored")== 1){
					explored = true;
				}
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return explored;
	}
}
