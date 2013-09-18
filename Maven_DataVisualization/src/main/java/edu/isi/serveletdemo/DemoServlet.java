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
	private static final long serialVersionUID = 1L;
    private static final String generateCsvLocation= "/Users/Alison/Documents/workspace/";
    private static final String trainFileLocation = "/Users/Alison/Documents/workspace/";
    private static final String testFileLocation = "/Users/Alison/Documents/workspace/";
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
		// TODO Auto-generated method stub
		PrintWriter out = response.getWriter();
		out.println("hello world");
		
		Object requestObject = request.getParameter("filename");
		if(requestObject != null){
			String filename = (String)requestObject;
			String contentType=			response.getContentType();
		}
	}
		private String getContentType(String fileType){
			return null;
		
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		InputStream in = request.getInputStream();
		BufferedReader br = new BufferedReader(new InputStreamReader(in));
		if(in==null)throw new IOException("Input is null");
		
		try {
			//process request			
			String features = request.getParameter("features");	
			generateCSV(generateCsvLocation,features);//"d:/etc/test1.csv"
			//System.out.println(features);
			
			//WekaDemo demo= new WekaDemo(generateCsvLocation,trainFileLocation,testFileLocation);
			
			//System.out.println("Complete Model Analysis!");
			//String ranking = demo.getRanking(); 
			
			//Instances pred = ia.getPredict();
			response.setContentType("text");
			PrintWriter out = response.getWriter();
			//Response with the ranking array
			String ranking = ""+1.1+"\n"+""+1.2+"\n"+""+1.3+"\n"+""+2.1+"\n"+""+2.2+"\n"+""+2.3+"\n";
			
			out.print(ranking);		
			System.out.println(ranking);
			
			//response.setContentType("text/plain");	
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		finally {
			in.close();
		}
		
	}
	

	public void generateCSV(String fileName,String features) throws Exception{
			
		ArraySplitting();
    	CSVLoader loader = new CSVLoader();
        loader.setSource(new File("/Users/Alison/Documents/test.csv"));
        Instances data = loader.getDataSet();
     
        // save ARFF
        ArffSaver saver = new ArffSaver();
        saver.setInstances(data);
        saver.setFile(new File("/Users/Alison/Documents/test.arff"));
       // saver.setDestination(new File(args[1]));
        saver.writeBatch();
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
	
	public void  ArraySplitting() throws Exception{
    	listWriter = new CsvListWriter(new FileWriter("/Users/Alison/Documents/test.csv"),
		CsvPreference.STANDARD_PREFERENCE);
    	String[] arrayFeatures = testing.split("\n");
    	List<Double> cell = new ArrayList<Double>();

    	try{
    		final String[] header = new String[] { "rarity", "eitherPlace", "feature3"};
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
		

