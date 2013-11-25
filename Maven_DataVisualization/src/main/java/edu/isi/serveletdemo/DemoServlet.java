package edu.isi.serveletdemo;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.InputStream;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.supercsv.cellprocessor.ParseDouble;
import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.io.CsvListWriter;
import org.supercsv.prefs.CsvPreference;

import edu.isi.linearRegression.WekaDemo;

/**
 * Servlet implementation class DemoServlet
 */
@WebServlet("/DemoServlet")
public class DemoServlet extends HttpServlet {
	CsvListWriter listWriter = null;
	private static final long serialVersionUID = 1L;
    private static final String generateCsvLocation= "/Users/Alison/Documents/";
    private static final String trainFileLocation = "/Users/Alison/Documents/";
    private static final String testFileLocation = "/Users/Alison/Documents/";
    /**
     * @see HttpServlet#HttpServlet()
     */
    public DemoServlet() {
        super();
    }
	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {	
		doPost(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		//String ranking1 = ""+1.1+"\n"+""+1.2+"\n"+""+1.3+"\n"+""+2.1+"\n"+""+2.2+"\n"+""+2.3+"\n";
		//out.print(ranking1);
		
		String features = request.getParameter("features");	//process request	
		try {	
			/*
			generateCSV(generateCsvLocation,features);
			WekaDemo demo= new WekaDemo();
			demo.ConvertCSV(generateCsvLocation);
			demo.TrainModel(trainFileLocation);
			String ranking = demo.getRanking(); 
			*/
			
			String[] arrayFeatures = features.split("\n");
			List<Double> cell ;
			String ranking ="";
			for(int i = 0; i <arrayFeatures.length; i++){
	    		String[] entries = arrayFeatures[i].split(",");
	    		double value = (-6.9595 * Double.parseDouble(entries[0])) + (0.8631 * Double.parseDouble(entries[1])) +  (-0.1643 *Double.parseDouble(entries[2]))+ (-0.143 *Double.parseDouble(entries[3]))+ (-0.1623 *Double.parseDouble(entries[4])) +1.9394;
	    		ranking += value+"\n";	
			}
			
			response.setContentType("text");
			PrintWriter out = response.getWriter();
			out.print(ranking);		//Response with the ranking array
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void generateCSV(String fileName,String features) throws Exception{
		listWriter = new CsvListWriter(new FileWriter(fileName+"test.csv"),
				CsvPreference.STANDARD_PREFERENCE);
		String[] arrayFeatures = features.split("\n");
		List<Double> cell ;

		    try{
		    	final String[] header = new String[] { "rarity", "EitherNotPlace", "differentOccupation","importance","SmallPlace"};
		    		listWriter.writeHeader(header);
		    			
		    	final CellProcessor[] processors = getProcessors();
		    		
		    	for(int i = 0; i <arrayFeatures.length; i++){
		    		cell = new ArrayList<Double>();
		    		String[] entries = arrayFeatures[i].split(",");
			
		    		for(int j=0;j<entries.length;j++){
		    			cell.add(Double.parseDouble(entries[j]));			
		    		}//end of j for   		
		    		listWriter.write(cell, processors);
		    	}//end of i for		
		    }//end of try	    	
		    finally {
				if( listWriter != null ) {
					listWriter.close();
			}
		    	}
	}
	
	private static CellProcessor[] getProcessors() {
        
        final CellProcessor[] processors = new CellProcessor[] { 
                new ParseDouble(), 
                new ParseDouble(),
                new ParseDouble(),
                new ParseDouble()
        };
        return processors;
}
		
}
		

