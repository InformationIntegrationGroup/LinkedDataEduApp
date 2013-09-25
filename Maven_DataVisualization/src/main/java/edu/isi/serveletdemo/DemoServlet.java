package edu.isi.serveletdemo;

import weka.core.converters.*;//ArffSaver;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
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

import weka.core.Instances;
import au.com.bytecode.opencsv.CSVWriter;
import edu.isi.linearRegression.ConvertCSV;
import edu.isi.linearRegression.InterprateArff;
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
        // TODO Auto-generated constructor stub
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
		
		//InputStream in = request.getInputStream();
		//if(in==null)throw new IOException("Input is null");
		response.setContentType("text");
		PrintWriter out = response.getWriter();
		String rankingTest ="";
		//String ranking1 = ""+1.1+"\n"+""+1.2+"\n"+""+1.3+"\n"+""+2.1+"\n"+""+2.2+"\n"+""+2.3+"\n";
		//out.print(ranking1);
		
		String features = request.getParameter("features");	//process request	
		
		String[] array = features.split("\n");
		for(int i = 0; i <array.length; i++){
			String[] entrie = array[i].split(",");
			for(int j=0;j<entrie.length;j++){
				rankingTest += entrie[j]+"\n";
			}
				//String ranking1 = ""+1.1+"\n"+""+1.2+"\n"+""+1.3+"\n"+""+2.1+"\n"+""+2.2+"\n"+""+2.3+"\n"+features;		
		}
		out.print(rankingTest);
		
		/*try {	
			//String features = request.getParameter("features");	//process request	
			
			generateCSV(generateCsvLocation,features);
			
			WekaDemo demo= new WekaDemo(generateCsvLocation,trainFileLocation,testFileLocation);
			String ranking = demo.getRanking(); 
			
			//response.setContentType("text");
			//PrintWriter out = response.getWriter();

			out.print(ranking);		//Response with the ranking array
				
		} catch (Exception e) {
			e.printStackTrace();
		}
		finally {
			in.close();
		}*/
		
	}
	

	public void generateCSV(String fileName,String features) throws Exception{
			
		ArraySplitting(features);
    	/*CSVLoader loader = new CSVLoader();
        loader.setSource(new File("/Users/Alison/Documents/test.csv"));
        Instances data = loader.getDataSet();
     
        // save ARFF
        ArffSaver saver = new ArffSaver();
        saver.setInstances(data);
        saver.setFile(new File("/Users/Alison/Documents/test.arff"));
       // saver.setDestination(new File(args[1]));
        saver.writeBatch();*/
	}
	
	private static CellProcessor[] getProcessors() {
        
        final CellProcessor[] processors = new CellProcessor[] { 
                new ParseDouble(), 
                new ParseDouble(),
                new ParseDouble()// customerNo (must be unique)
               // new NotNull(), // firstName
               // new NotNull() // lastName
        };
        return processors;
}
	
	public void  ArraySplitting(String features) throws Exception{
    	listWriter = new CsvListWriter(new FileWriter("/Users/Alison/Documents/test.csv"),
		CsvPreference.STANDARD_PREFERENCE);
    	String[] arrayFeatures = features.split("\n");
    	List<Double> cell = new ArrayList<Double>();

    	try{
    		final String[] header = new String[] { "rarity", "EitherNotPlace", "differentOccupation"};
    			listWriter.writeHeader(header);
    			
    		final CellProcessor[] processors = getProcessors();
    		
    		for(int i = 0; i <arrayFeatures.length; i++){
    			cell = new ArrayList<Double>();
    			String[] entries = arrayFeatures[i].split(",");
	
    			for(int j=0;j<entries.length;j++){
    				cell.add(Double.parseDouble(entries[j]));			
    				System.out.println(cell.get(j));		
			
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
			
		
}
		

