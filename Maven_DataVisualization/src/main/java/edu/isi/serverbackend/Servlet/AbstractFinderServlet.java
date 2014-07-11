package edu.isi.serverbackend.Servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.openrdf.model.Literal;
import org.openrdf.query.BindingSet;
import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.query.QueryLanguage;
import org.openrdf.query.TupleQuery;
import org.openrdf.query.TupleQueryResult;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.http.HTTPRepository;


/**
 * Servlet implementation class NodeDescriptionServlet
 */
@WebServlet("/descriptions")
public class AbstractFinderServlet extends HttpServlet{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
     * @see HttpServlet#HttpServlet()
     */
    public AbstractFinderServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		PrintWriter out = response.getWriter();
		HTTPRepository endpoint = new HTTPRepository("http://dbpedia.org/sparql", "");
		String allUris;
		String[] uris;
		JSONObject result = new JSONObject();
		RepositoryConnection repoConnection = null;
		try {
			endpoint.initialize();
			repoConnection = endpoint.getConnection();
			allUris = request.getParameter("uri");
			uris=allUris.split(",");
			
			for(int i=0; i<uris.length; i++){
				String queryString =
				"PREFIX p: <http://dbpedia.org/property/> "+
				"PREFIX dbpedia: <http://dbpedia.org/resource/> "+ 
				"PREFIX category: <http://dbpedia.org/resource/Category:> "+ 
				"PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> "+ 
				"PREFIX dbo: <http://dbpedia.org/ontology/> "+ 
				"SELECT ?label ?abstract ?comment ?type WHERE { " +
				"<" + uris[i] + "> rdfs:label ?label ."+
				"?x rdfs:label ?label ."+ 
				"?x dbo:abstract ?abstract ."+ 
				"?x rdfs:comment ?comment ."+ 
				"?x rdf:type ?type ."+ 
				"FILTER (lang(?abstract) = \"en\") ."+ 
				"FILTER (lang(?comment) = \"en\") ."+ 
				"FILTER (lang(?label) = \"en\") ."+ 
				"} LIMIT 2 ";
				
				TupleQuery query = repoConnection.prepareTupleQuery(QueryLanguage.SPARQL, queryString);
				TupleQueryResult queryResult = query.evaluate();
				
				BindingSet bindingSet = queryResult.next();
				
				JSONObject newNode = new JSONObject();
				String abstractString = bindingSet.getValue("abstract").stringValue();
				if (abstractString.contains("(") && abstractString.contains(")"))
					abstractString = abstractString.substring(0, abstractString.indexOf('(')) + abstractString.substring(abstractString.indexOf(')')+1);
				newNode.put("abstract", abstractString);
				
				abstractString = bindingSet.getValue("comment").stringValue();
				if (abstractString.contains("(") && abstractString.contains(")"))
						abstractString = abstractString.substring(0, abstractString.indexOf('(')) + abstractString.substring(abstractString.indexOf(')')+1);
				newNode.put("comment", abstractString);
				
				newNode.put("label", bindingSet.getValue("label").stringValue());
				newNode.put("type", bindingSet.getValue("type").stringValue());
				
				result.put(uris[i], newNode);
				
			
			}
			response.setContentType("application/json");
			response.setCharacterEncoding("UTF-8");
			
			out.println(result.toString());
			
		} catch (RepositoryException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (MalformedQueryException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (QueryEvaluationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		finally{
			if (repoConnection!=null){
				try {
					repoConnection.close();
				} catch (RepositoryException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
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
