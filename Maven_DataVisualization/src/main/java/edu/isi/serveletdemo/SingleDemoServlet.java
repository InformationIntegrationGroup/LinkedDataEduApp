package edu.isi.serveletdemo;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Random;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

import com.sun.jersey.api.json.JSONWithPadding;
@Path("/path")
public class SingleDemoServlet {
	
	@Path("/single/")
    @GET
    @Produces({"application/x-javascript", "application/json", "application/xml"})	
	public Response returnSimplePath(@QueryParam("jsoncallback") String callback){
		Response response = null;
		
		BufferedReader reader = null;
		InputStream inputStream = getClass().getClassLoader().getResourceAsStream("abridgedHash.txt");
		if (inputStream == null){	
			
			response = Response
                    .ok()
					.status(201)
                    .type("text/plain")
                    .entity("Failed to load path")
                    .build();
					
			return response;
		}
		
		reader = new BufferedReader(new InputStreamReader(inputStream));
		
		String line = null;
		int counter = 0;
		int num;
		Random random = new Random();
		num = random.nextInt(50);
		String s = null;
		try{
		while (counter<num){
			line = reader.readLine();
			counter ++;

		}
			s = reader.readLine();
		}
		catch(Exception e){
			//
		}
		
		response = Response
	    			.ok()
					.status(200)
	    			.entity(new JSONWithPadding(s,callback))
	    			.build();
	        
	        return response;
	}
	
	@GET
	@Path("/test")
	public String test() {
		return "haha";
	}
    //reader.close();

}

