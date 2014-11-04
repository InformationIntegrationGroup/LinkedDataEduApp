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

@WebServlet("/saveHash")
public class HashStoreServlet extends HttpServlet{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
     * @see HttpServlet#HttpServlet()
     */
    public HashStoreServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		PrintWriter out = response.getWriter();
		String hashObject = request.getParameter("hash");
		String id = request.getParameter("hashID");
		if (id!=null)
			id = id.trim();
		
		Connection conn=null;
		Statement st=null;
		try{  
			//Read the SQL password from a file
			BufferedReader reader = null;
			InputStream inputStream = getClass().getClassLoader().getResourceAsStream("SQLpw.txt");
			reader = new BufferedReader(new InputStreamReader(inputStream));
			String password = reader.readLine();
		
		
			// create a mysql database connection
			String myDriver = "com.mysql.jdbc.Driver";
			String myUrl = "jdbc:mysql://localhost/test";
			Class.forName(myDriver);			
			conn = DriverManager.getConnection(myUrl, "root", password);
			st = conn.createStatement();
			
			int updated=0;
			
			//Update process rather than insert
			if (id!=null && !id.isEmpty()){
				updated = st.executeUpdate("UPDATE hashtest SET hash='"+hashObject+"',lastModified=NOW() WHERE id='"+id+"'");
				
				//If the update process didn't change any entries, then try to create a new entry with that id, since it's open...
				if (updated==0){
					//Loop to create new ids, in case the original insert fails (it shouldn't, since the update failed b/c the id didn't exist...just being cautious)
					while (!insertEntry(st,"INSERT INTO hashtest(id, hash) VALUES ('"+id+"', '"+hashObject+"')")){
						id = generateId();
					}
				}
			}
			//if there's no given id, then we start with the randomizer
			else{
				id =generateId();
			 
				while (!insertEntry(st,"INSERT INTO hashtest(id, hash) VALUES ('"+id+"', '"+hashObject+"')")){
					id = generateId();
				}
			} 
			
			//Return the final id of the hash object.
			response.setContentType("text/plain");
			out.println(id);
		  
		}
		catch (ClassNotFoundException e){
			 System.err.println("Could not connect to driver!");
			 System.err.println(e.getMessage());
			
		}
		catch (SQLException ex)
		{
			System.err.println("SQLException: " + ex.getMessage()+", SQLState: " + ex.getSQLState() + "VendorError: " + ex.getErrorCode());
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
		}
		
	}
	
	private static String generateId(){
		Random random = new Random();	
		//Takes in a random value 0-1, where 0 will represent digits and 2 lower-case letters
		int type;
		char[] tempID = new char[20];
		
		for (int i=0; i<20; i++){
			type = random.nextInt(2);
			switch(type){
				case 0:		//Select a random digit 
					tempID[i] = (char) (48+random.nextInt(10));
					break;
				case 1:		//Select a random lower-case letter
					tempID[i] = (char) (97+random.nextInt(26));
					break;
				default:	//Shouldn't really ever reach this, but just in case
					tempID[i]='-';
			}
		}
		
		return new String(tempID);
		
	}
	
	private static boolean insertEntry(Statement st, String query) throws SQLException{
		try{
			st.executeUpdate(query);
		  }
		  catch (SQLException ex){
			  if (ex.getErrorCode()==1062){
				  return false;
			  }
			  else{
				throw ex;
			  }
		  }
		
		return true;
	
	}
	
	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doPost(request, response);
	}

}


//Delete function example
//delete from hashtest where lastModified<=DATE_SUB(NOW(), INTERVAL 1 MONTH);