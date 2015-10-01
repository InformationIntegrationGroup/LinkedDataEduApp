package edu.isi.serveletdemo;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/DemoPageServlet")
public class DemoPageServlet extends HttpServlet {
	
	private static final long serialVersionUID = 1L;

	public DemoPageServlet(){
		super();
	}
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		/*PrintWriter out = response.getWriter();
		response.setContentType("application/json");
		 out.println("hello1");
		try {
			String triple = request.getParameter("triple");
			String number = request.getParameter("key");
			String[] SubjectObject = triple.split(",");
			String[] features = number.split(",");
			Map<Integer, Integer> mapping = new HashMap<Integer, Integer>();
			mapping.put(1, 0);
			mapping.put(2, 0);
			mapping.put(3, 0);
			mapping.put(4, 0);
			mapping.put(5, 0);
			for (String num : features) {
				int n = Integer.parseInt(num);
				mapping.put(n, 1);
			}
			 out.println("hello3");
			out.println(triple + ""+number);
			SubjectObject[0] = "\""+SubjectObject[0] +"\"";
			SubjectObject[1] = "\""+SubjectObject[1] +"\"";
			SubjectObject[2] = "\""+SubjectObject[2] +"\"";
			Class.forName("com.mysql.jdbc.Driver");
			Connection connection = DriverManager.getConnection("jdbc:mysql://localhost/lodstories?user=root");  
			Statement statement = connection.createStatement();  
		    statement.setQueryTimeout(30);  // set timeout to 30 sec.
		    String stmt = String.format("insert into user_feedbacks(subject, predicate, object,SI, RI, OI, WI, NI) values('%s','%s','%s','%d','%d','%d','%d','%d')", 
		    		SubjectObject[0], SubjectObject[1], SubjectObject[2], mapping.get(1), mapping.get(2), mapping.get(3), mapping.get(4), mapping.get(5));
		    statement.execute(stmt);
		    //statement.execute("insert into user_feedbacks(subject) values('"+SubjectObject[0]+"');");
		    //statement.execute("insert into user_feedbacks(subject) values('\"false\"');");
		    out.println("hello2");
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			out.flush();
			out.close();
		}		*/
					
	}
	
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		/*String[] feedback = request.getParameterValues("feed");
		PrintWriter out = response.getWriter();
		response.setContentType("text/html");
		out.print("<html><body>");
		out.print("<h1>Feedback</h1>");
		out.print("<u1>");
		for(String s : feedback){
			out.print("<li"+s+"</li");
		}
	out.print("</u1>");
	out.print("</body></html>");*/
	
	}

	
}
