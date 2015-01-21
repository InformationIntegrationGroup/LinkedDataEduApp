package edu.isi.serverbackend.Servlet;

import java.io.*;
import java.util.ArrayList;
import java.util.NoSuchElementException;
import java.util.Random;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.sql.*;
import org.json.*;
/**
 *	This servlet merely searches the database and returns meta data about hash objects.
 *	It does NOT update the lastAccessed field, and does NOT return actual hash objects.
 *	To actually retrieve a hash object, one must use the retrieved hash ID to call the HashRetrievalServlet
 */
@WebServlet("/filterHash")
public class HashFilterServlet extends HttpServlet{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
     * @see HttpServlet#HttpServlet()
     */
    public HashFilterServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		PrintWriter out = response.getWriter();
		String sourceFilter = request.getParameter("startNode");
		JSONObject result = new JSONObject();
		JSONArray hashObjects = new JSONArray();
		
		Connection conn=null;
		Statement st=null;
		ResultSet rs=null;
		
		try{  
			//Read the SQL password from a file
			BufferedReader reader = null;
			InputStream inputStream = getClass().getClassLoader().getResourceAsStream("SQLpw.txt");
			reader = new BufferedReader(new InputStreamReader(inputStream));
			String password = reader.readLine();
		
		
			// create a mysql database connection
			String myDriver = "com.mysql.jdbc.Driver";
			String myUrl = "jdbc:mysql://localhost/lodstories";
			Class.forName(myDriver);			
			conn = DriverManager.getConnection(myUrl, "root", password);
			st = conn.createStatement();

			if (sourceFilter!=null && !sourceFilter.trim().isEmpty())
				rs = st.executeQuery("SELECT id,table,author,path,rating,thumbnail FROM hash_objects WHERE path LIKE '"+sourceFilter+";%'");
			else
				rs = st.executeQuery("SELECT id,table,author,path,rating,thumbnail FROM hash_objects");
		  
			System.out.println("SELECT id,table,author,path,rating,thumbnail FROM hash_objects WHERE path LIKE '"+sourceFilter+";%'");
			if (!rs.next()){
				response.setContentType("text/plain");
				response.setStatus(400);
				out.println("No videos available");
				return;
			} 
			
			//Handle the first match before entering the while loop...
			JSONObject newNode = new JSONObject();
			newNode.put("hashID", rs.getString("id"));
			newNode.put("thumbnail", rs.getString("thumbnail"));
			newNode.put("title", rs.getString("title"));
			newNode.put("author", rs.getString("author"));
			newNode.put("path", rs.getString("path"));
			newNode.put("rating", rs.getInt("rating"));
			hashObjects.put(newNode);
			
			while (rs.next()){
				newNode = new JSONObject();
				newNode.put("hashID", rs.getString("id"));
				newNode.put("thumbnail", rs.getString("thumbnail"));
				newNode.put("title", rs.getString("title"));
				newNode.put("author", rs.getString("author"));
				newNode.put("path", rs.getString("path"));
				newNode.put("rating", rs.getInt("rating"));
				hashObjects.put(newNode);
			}
			
			result.put("startingNode", sourceFilter);
			result.put("hashObjects",hashObjects);
			
			response.setContentType("application/json");		  
			response.setCharacterEncoding("UTF-8");
			
			out.println(result);
		}
		catch (ClassNotFoundException e){
			 System.err.println("Could not connect to driver!");
			 System.err.println(e.getMessage());
			
		}
		catch (SQLException ex)
		{
			System.err.println("SQLException: " + ex.getMessage()+", SQLState: " + ex.getSQLState() + "VendorError: " + ex.getErrorCode());
		}
		catch (JSONException ex){
			ex.printStackTrace();
		}
		finally{
			if (conn!=null){
				try{
					conn.close();
				}
				catch (SQLException ex){
					ex.printStackTrace();
				}
			}
			if (st!=null){
				try{
					st.close();
				}
				catch (SQLException ex){
					ex.printStackTrace();
				}
			}
			if (rs!=null){
				try{
					rs.close();
				}
				catch (SQLException ex){
					ex.printStackTrace();
				}
			}
		}
		
	}
	
	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doPost(request, response);
	}

}


//Delete function example
//delete from hash_objects where lastModified<=DATE_SUB(NOW(), INTERVAL 1 MONTH);