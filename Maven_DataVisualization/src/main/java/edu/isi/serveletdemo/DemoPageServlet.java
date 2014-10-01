package edu.isi.serveletdemo;
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

@WebServlet("/DemoPageServlet")
public class DemoPageServlet extends HttpServlet {
	
	private static final long serialVersionUID = 1L;

	public DemoPageServlet(){
		super();
	}
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		PrintWriter out = response.getWriter();
		response.setContentType("application/json");
		 out.println("hello1");
		try {
			String triple = request.getParameter("triple");
			String number = request.getParameter("num");
			String[] SubjectObject = triple.split(",");
			String[] features = number.split(",");
			 out.println("hello3");
			out.println(triple + ""+number);
			SubjectObject[0] = "\""+SubjectObject[0] +"\"";
			SubjectObject[1] = "\""+SubjectObject[1] +"\"";
			Class.forName("com.mysql.jdbc.Driver");
			Connection connection = DriverManager.getConnection("jdbc:mysql://localhost/lodstories?user=root");  
			Statement statement = connection.createStatement();  
		    statement.setQueryTimeout(30);  // set timeout to 30 sec.
		    //statement.execute("insert into user_feedbacks(subject, predicate, object,SI, RI, OI, WI) values('"+SubjectObject[0]+"','" +"\"sf\""+"','"+ SubjectObject[1]+"','0','0','0','0' )");
		    //statement.execute("insert into user_feedbacks(subject) values('"+SubjectObject[0]+"');");
		    statement.execute("insert into user_feedbacks(subject) values('\"false\"');");
		    out.println("hello2");
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			out.flush();
			out.close();
		}		
					
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
