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

import java.util.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
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
			System.out.println(currentURI);
			int num = 0;
			String numString = request.getParameter("num");
			System.out.println(numString);
			String jsonCallback = request.getParameter("jsoncallback");
			
			if(currentURI != null && numString != null){
				//ExtractRankRequest extractRankRequest = new ExtractRankRequest(currentURI);
				num = Integer.parseInt(numString);
				//if(!extractRankRequest.checkAlreadyCached()){
					currentNode = new LinkedDataNode(currentURI, repoConnection);
					long startTime = System.currentTimeMillis();
					TripleRankRequest rankRequest = new TripleRankRequest(currentNode, getServletContext());
					long endTime = System.currentTimeMillis();
					long featureRatingStartTime = System.currentTimeMillis();
					rankRequest.rateInterestingness();
					long featureRatingEndTime = System.currentTimeMillis();
					long sortingStartTime = System.currentTimeMillis();
					rankRequest.sortConnections();
					long sortingEndTime = System.currentTimeMillis();
					response.setContentType("application/json");
					response.setCharacterEncoding("UTF-8");
					long exportJSONstartTime = System.currentTimeMillis();
					try {
						
						out.println(jsonCallback + "(" + rankRequest.exportD3JSON(num).toString() + ")");
						System.out.println("JSON RESULT: " + rankRequest.exportD3JSON(num).toString());
					} catch (JSONException e) {
						e.printStackTrace();
					}
					long exportJSONendTime = System.currentTimeMillis();
					System.out.println("Total number of Connections: " + rankRequest.getNumbetConnections());
					System.out.println("Retrieving Data Elapsed milliseconds: "+(endTime - startTime));
					System.out.println("Feature Rating Elapsed milliseconds: "+(featureRatingEndTime-featureRatingStartTime));
					System.out.println("Sorting Data Elapsed milliseconds: "+(sortingEndTime - sortingStartTime));
					System.out.println("Exporting JSON Elapsed milliseconds: "+(exportJSONendTime - exportJSONstartTime));
					System.out.println("Total time Elapsed milliseconds: "+(System.currentTimeMillis()-startTime));
					System.out.println("Start Caching");
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


/* demo dbpedia query
SELECT ?predicate ?object ?label ?type WHERE{ 
<http://dbpedia.org/resource/Pablo_Picasso> ?predicate ?object .
?predicate rdf:type owl:ObjectProperty .
?object a owl:Thing .
?object rdfs:label ?label .
?object a ?type.
FILTER(?type = <http://dbpedia.org/ontology/Person> OR 
?type = <http://dbpedia.org/ontology/Place> OR 
?type = <http://dbpedia.org/ontology/Organisation> OR 
?type = <http://dbpedia.org/ontology/Work> OR 
?type =<http://dbpedia.org/ontology/Event>).
FILTER(langMatches(lang(?label), "EN"))
} GROUP BY ?object
*/
