package edu.isi.serverbackend.Servlet;

import java.io.IOException;
import java.io.PrintWriter;

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

import edu.isi.serverbackend.request.*;

/**
 * Servlet implementation class LinkRankingServlet
 */
@WebServlet("/LinkRankServlet")
public class LinkRankServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public LinkRankServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		System.out.println("Get Request!!!!");
		PrintWriter out = response.getWriter();
		HTTPRepository endpoint = new HTTPRepository("http://dbpedia.org/sparql", "");
		LinkedDataNode currentNode;
		
		try {
			endpoint.initialize();
			RepositoryConnection repoConnection = endpoint.getConnection();
			String currentURI = request.getParameter("uri");
			int num = 0;
			String numString = request.getParameter("num");
			
			if(currentURI != null && numString != null){
				ExtractRankRequest extractRankRequest = new ExtractRankRequest(currentURI);
				num = Integer.parseInt(numString);
				if(!extractRankRequest.checkAlreadyCached()){
					currentNode = new LinkedDataNode(currentURI, repoConnection);
					long startTime = System.currentTimeMillis();
					ConnectionRankRequest rankRequest = new ConnectionRankRequest(currentNode);
					long endTime = System.currentTimeMillis();
					long featureRatingStartTime = System.currentTimeMillis();
					rankRequest.rateInterestingness();
					long featureRatingEndTime = System.currentTimeMillis();
					long sortingStartTime = System.currentTimeMillis();
					rankRequest.sortConnections();
					long sortingEndTime = System.currentTimeMillis();
					response.setContentType("application/json");
					
					try {
						out.println(rankRequest.exportD3JSON(num).toString());
					} catch (JSONException e) {
						e.printStackTrace();
					}
					System.out.println("Total number of Connections: " + rankRequest.getNumbetConnections());
					System.out.println("Retrieving Data Elapsed milliseconds: "+(endTime - startTime));
					System.out.println("Sorting Data Elapsed milliseconds: "+(sortingEndTime - sortingStartTime));
					System.out.println("Start Caching");
					
					//LinkedDataCachingRequest cachingRequest = new LinkedDataCachingRequest(extractRankRequest.getSQLConnection(), rankRequest.getSubjectConnections(), rankRequest.getObjectConnections());
					//cachingRequest.startCaching();
				}
				else{
					long startTime = System.currentTimeMillis();
					out.println(extractRankRequest.extractD3JSON(num).toString());
					long endTime = System.currentTimeMillis();
					System.out.println("Retrieving Data From MySQL Elapsed milliseconds: "+(endTime - startTime));
				}
			}
		} catch (RepositoryException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (MalformedQueryException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (QueryEvaluationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
