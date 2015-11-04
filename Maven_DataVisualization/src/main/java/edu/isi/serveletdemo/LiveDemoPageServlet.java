package edu.isi.serveletdemo;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import edu.isi.serverbackend.linkedData.LinkedDataNode;

import org.json.JSONException;
import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.http.HTTPRepository;
import java.util.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

import edu.isi.serverbackend.request.*;

/**
 * Servlet implementation class LinkRankingServlet
 */
@WebServlet("/LiveDemoPageServlet")
public class LiveDemoPageServlet extends HttpServlet {
    
    //private static final long serialVersionUID = 1L;

    /**
     * @see HttpServlet#HttpServlet()
     */
    public LiveDemoPageServlet() {
        super();
        
        try{
            File file = new File("data.csv");
        
            // if file doesnt exists, then create it
            if (!file.exists()) {
                file.createNewFile();
            }
        
        } catch(Exception e){}

    }

    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // TODO Auto-generated method stub
    }
    /**
     * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    	//Always call the encoding before anything else
		response.setCharacterEncoding("UTF-8");
		
		
		try {
			String subject = request.getParameter("subject");
	        String predicate = request.getParameter("predicate");
	        String object = request.getParameter("object");    				
			String chosenString = request.getParameter("chosen");
			boolean chosen = Boolean.parseBoolean(chosenString);
                
                DateFormat dateFormat = new SimpleDateFormat("MM/dd/yy HH:mm:ss");
                Date date = new Date();
                
                String msg = dateFormat.format(date) + "," + subject + "," + predicate + "," + object + "," + chosen;
                
                System.out.println(msg);
            
                FileWriter out = new FileWriter("data.csv", true);
                out.write(msg + "\n");
		    
		} catch (Exception e) {
			e.printStackTrace();
		}
    	
        response.setStatus(HttpServletResponse.SC_OK);
    }

}
