package edu.isi.serverbackend.Servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.NoSuchElementException;
import java.util.Random;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.sql.*;

@WebServlet("/retrieveHash")
public class HashRetrievalServlet extends HttpServlet{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
     * @see HttpServlet#HttpServlet()
     */
    public HashRetrievalServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		PrintWriter out = response.getWriter();
		String id = request.getParameter("hashID");
		String result;
		
		if (id.isEmpty()){
			response.setContentType("text/plain");
			response.setStatus(400);
			out.println("Empty hash ID");
			return;
		}
		
		Connection conn=null;
		Statement st=null;
		ResultSet rs=null;
		
		try{  
			// create a mysql database connection
			String myDriver = "com.mysql.jdbc.Driver";
			String myUrl = "jdbc:mysql://localhost/test";
			Class.forName(myDriver);
			conn = DriverManager.getConnection(myUrl, "root", "Lx176967");

			st = conn.createStatement();
		 
			rs = st.executeQuery("SELECT hash FROM hashtest where id='"+id+"'");
		  
			if (!rs.next()){
				response.setContentType("text/plain");
				response.setStatus(400);
				out.println("Error retrieving hash object");
				return;
			}
			
			result = rs.getString(1);
			
			//Update the lastAccessed field
			st.executeUpdate("UPDATE hashtest SET lastModified=NOW() WHERE id='"+id+"'");
			
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
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doPost(request, response);
	}

}


//Delete function example
//delete from hashtest where lastModified<=DATE_SUB(NOW(), INTERVAL 1 MONTH);