package edu.isi.serveletdemo;
import java.util.List;

import com.mongodb.*;

import edu.isi.serverbackend.linkedData.LinkedDataNode;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONException;
import org.json.JSONObject;
import org.openrdf.repository.http.HTTPRepository;

import java.io.*;
import java.util.Random;

@WebServlet("/DemoPageServlet")
public class DemoPageServlet extends HttpServlet {

	public DemoPageServlet(){
		super();
	}
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		PrintWriter out = response.getWriter();
		response.setContentType("application/json");
		
		try {
			String number = request.getParameter("num");
			int num = Integer.parseInt(number);
			JSONObject hash = null;
			hash.put("id", "h-3690378823082678040");
			hash.put("subject","http://dbpedia.org/resource/Orsay_Tennyson_Dickens");
			hash.put("relation","http://dbpedia.org/ontology/child");
			hash.put("object","http://dbpedia.org/resource/Charles_Dickens");
			out.println(hash.toString());
		} catch (JSONException e) {
			e.printStackTrace();
		} finally{
			out.flush();
			out.close();
		}
			//response = Response
					
					
	}
	
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String[] feedback = request.getParameterValues("feed");
		PrintWriter out = response.getWriter();
		response.setContentType("text/html");
		out.print("<html><body>");
		out.print("<h1>Feedback</h1>");
		out.print("<u1>");
		for(String s : feedback){
			out.print("<li"+s+"</li");
		}
	out.print("</u1>");
	out.print("</body></html>");
	
	}


}
	/*
		@Produces({"application/x-javascript", "application/json", "application/xml"})
		public Response returnSimplePath(@QueryParam("jsoncallback") String callback){
			BufferedReader reader = null;
			try{
				reader = new BufferedReader(new FileReader("/home/lindaxu/Desktop/hash.txt"));
			}
			catch(FileNotFoundException e){}
			
			String line = null;
			int counter = 0;
			int num;
			Random random = new Random();
			num = random.nextInt(4);
			System.out.println(num);
			String s = null;
			
			try{
				while (counter<num){
					line = reader.readLine();
					counter ++;
				}
				s = reader.readLine();
			}
			catch(Exception e){	}
			
			Response response;
			response = Response
		    			.ok()
		    			.entity(new JSONWithPadding(
		    				"{'hash':'h-3690378823082678040','excecution_time': 1300,'source':	{label: 'Orsay Dickens', uri: 'http://dbpedia.org/resource/Orsay_Tennyson_Dickens'}, 'destination':	{label: 'Charles Dickens', uri: 'http://dbpedia.org/resource/Charles_Dickens'},	'path':[" +
		    				"{'type':'node','uri':'http://dbpedia.org/resource/Orsay_Tennyson_Dickens', audio_text: 'More blah blah filler text for Orsay Dickens'},"+
		    				"{'type':'link', 'inverse':true, 'uri':'http://dbpedia.org/ontology/child'},"+
		    				"{'type':'node','uri':'http://dbpedia.org/resource/Charles_Dickens', }"+
		    				"]}"
		    					
		    					,callback))
		    			.build();
		        
		        return response;
		}
	*/
	

