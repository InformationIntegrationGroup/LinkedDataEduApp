package edu.isi.serverbackend.Servlet;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class LinkRankingServlet
 */
@WebServlet("/AnswerStoreServlet")
public class AnswerStoreServlet extends HttpServlet {       

	
	private static final long serialVersionUID = 1L;
	/**
     * @see HttpServlet#HttpServlet()
     */
    public AnswerStoreServlet() {
        super();
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
        System.out.println("Recieved POST request");
        String a = request.getParameter("A");
        String b = request.getParameter("B");
        String interesting = request.getParameter("Interesting");

        File data = new File("answers.txt");
        if (!data.exists()){
           data.createNewFile();
        }
        PrintWriter writeData = new PrintWriter(new FileWriter(data, true));
        writeData.println(a + "," + b + "," + interesting);
        writeData.flush();

        response.setStatus(HttpServletResponse.SC_OK);
    }

}
