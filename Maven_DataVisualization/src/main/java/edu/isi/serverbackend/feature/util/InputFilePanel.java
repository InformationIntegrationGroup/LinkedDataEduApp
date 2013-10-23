package edu.isi.serverbackend.feature.util;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;

import javax.swing.*;

import org.openrdf.query.BindingSet;
import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.query.QueryLanguage;
import org.openrdf.query.TupleQuery;
import org.openrdf.query.TupleQueryResult;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.http.HTTPRepository;

import edu.isi.serverbackend.feature.DifferentOccupationFeature;
import edu.isi.serverbackend.feature.EitherNotPlaceFeature;
import edu.isi.serverbackend.feature.RarityFeature;
import edu.isi.serverbackend.linkedData.*;
import edu.isi.serverbackend.linkedData.LinkedDataConnection.CurrentNode;


public class InputFilePanel extends JPanel implements ActionListener{
	final static long LINKTOTAL = 10000; //256470235;
	JFrame parent;
	JLabel fileNameLabel, seedLabel;
	JTextField fileNameField, seedField;
	JButton generateBtn, extractBtn;
	JPanel inputPanel;
	JPanel extractPanel;
	private BufferedReader reader;
	public InputFilePanel(JFrame parent){
		this.parent = parent;
		
		fileNameLabel = new JLabel("File Name: ");
		seedLabel = new JLabel("Seed: ");
		
		fileNameField = new JTextField();
		fileNameField.setPreferredSize(new Dimension(200, 30));
		fileNameField.setMaximumSize(new Dimension(200, 30));
		fileNameField.setMinimumSize(new Dimension(200, 30));
		
		seedField = new JTextField();
		seedField.setPreferredSize(new Dimension(200, 30));
		seedField.setMaximumSize(new Dimension(200, 30));
		seedField.setMinimumSize(new Dimension(200, 30));
		
		generateBtn = new JButton("Generate");
		generateBtn.setPreferredSize(new Dimension(80, 30));
		generateBtn.setMaximumSize(new Dimension(80, 30));
		generateBtn.setMinimumSize(new Dimension(80, 30));
		generateBtn.addActionListener(this);
		
		inputPanel = new JPanel();
		inputPanel.setLayout(new BoxLayout(inputPanel, BoxLayout.X_AXIS));
		inputPanel.setAlignmentX(CENTER_ALIGNMENT);
		inputPanel.add(fileNameLabel);
		inputPanel.add(Box.createRigidArea(new Dimension(10, 30)));
		inputPanel.add(fileNameField);
		inputPanel.add(Box.createRigidArea(new Dimension(10, 30)));
		inputPanel.add(generateBtn);
		
		extractBtn = new JButton("Extract Triples");
		extractBtn.setPreferredSize(new Dimension(150, 30));
		extractBtn.setMaximumSize(new Dimension(150, 30));
		extractBtn.setMinimumSize(new Dimension(150, 30));
		extractBtn.addActionListener(this);
		
		extractPanel = new JPanel();	
		extractPanel.setLayout(new BoxLayout(extractPanel, BoxLayout.X_AXIS));
		extractPanel.setAlignmentY(CENTER_ALIGNMENT);
		extractPanel.add(seedLabel);
		inputPanel.add(Box.createRigidArea(new Dimension(10, 30)));
		extractPanel.add(seedField);
		inputPanel.add(Box.createRigidArea(new Dimension(10, 30)));
		extractPanel.add(extractBtn);
		
		
		this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		this.setAlignmentY(CENTER_ALIGNMENT);
		this.add(extractPanel);
		this.add(inputPanel);
	}
	
	
	public void actionPerformed(ActionEvent ae) {
		// TODO Auto-generated method stub
		if(ae.getSource() == generateBtn){
			readRawDataFromFile(fileNameField.getText());
		}
		else if(ae.getSource() == extractBtn){
			extractTriples(seedField.getText());
		}
	}
	
	public void extractTriples(String seedURI) {
		HTTPRepository endpoint = new HTTPRepository("http://dbpedia.org/sparql", "");
		try {
			FileWriter fwriter = new FileWriter("samples.txt");
			PrintWriter pw = new PrintWriter(fwriter);
			endpoint.initialize();
			RepositoryConnection repoConnection = endpoint.getConnection();
			LinkedDataNode seed = new LinkedDataNode(seedURI, repoConnection);
			DBpediaCrawler crawler = new DBpediaCrawler(seed);
			crawler.startExplore(100);
			ArrayList<LinkedDataConnection> links = crawler.exportLinks();
			for(int i = 0; i < links.size(); i++){
				pw.print(links.get(i).getSubject().getURI() + " ");
				pw.print(links.get(i).getPredicate() + " ");
				pw.println(links.get(i).getObject().getURI() + " ");
				pw.flush();
			}
			/*for(int i = 0; i < 1000; i++){
				long offset = (long) (LINKTOTAL * Math.random());
				String queryString = "SELECT ?s ?p ?o { "
					+ "?s ?p ?o. "	
					//+ "?o a owl:Thing. "
					//+ "?s a owl:Thing. "
					+ "?s rdfs:label ?label1. "
					+ "?o rdfs:label ?label2. "
					+ "FILTER(langMatches(lang(?label1), \"EN\")) "
					+ "FILTER(langMatches(lang(?label2), \"EN\")) "
					+ "} OFFSET " + offset + " LIMIT 1";
				System.out.println(queryString);
				TupleQuery query = repoConnection.prepareTupleQuery(QueryLanguage.SPARQL, queryString);
				query.setMaxQueryTime(10000000);
				TupleQueryResult result = query.evaluate();
				while(result.hasNext()){
					BindingSet bindingSet = result.next();
					pw.print(bindingSet.getValue("s").stringValue()+" ");
					pw.print(bindingSet.getValue("p").stringValue()+" ");
					pw.println(bindingSet.getValue("o").stringValue()+" ");
					pw.flush();
				}
			}*/
			pw.close();
			fwriter.close();
		} catch (RepositoryException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (MalformedQueryException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (QueryEvaluationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	public void readRawDataFromFile(String fileName){
		try{
			HTTPRepository endpoint = new HTTPRepository("http://dbpedia.org/sparql", "");
			endpoint.initialize();
			ArrayList<Sample> samples = new ArrayList<Sample>();
			RepositoryConnection repoConnection = endpoint.getConnection();
			FileInputStream fstream = new FileInputStream(fileName);
			DataInputStream dstream = new DataInputStream(fstream);
			reader = new BufferedReader(new InputStreamReader(dstream));
			String line;
			line = reader.readLine();
			while(line != null){
				String[] strs = line.split(" ");
				if(strs.length == 3){
					System.out.println("sample detected");
					LinkedDataNode subject = new LinkedDataNode(strs[0], repoConnection);
					LinkedDataNode object = new LinkedDataNode(strs[2], repoConnection);
					String predicate = strs[1];
					LinkedDataConnection link = new LinkedDataConnection(subject, object, predicate, CurrentNode.subject, repoConnection);
					Sample newSample = new Sample(link);
					//Sample newSample = new Sample(link, Double.parseDouble(strs[3]));
					//newSample.evalutateFeature();
					samples.add(newSample);
				}
				line = reader.readLine();
			}
			InfoExtractor.extractNames(samples);
			RarityFeature.calculateRarity(samples);
			EitherNotPlaceFeature.isEitherNotPlace(samples);
			DifferentOccupationFeature.isDifferentOccupation(samples);
			
			System.out.println("Sample processing finished");
			dstream.close();
			fstream.close();
			exportTraingSetCSV(samples);
		}
		catch(Exception e){
			System.err.println("Error: " + e.getMessage());
		}
	}
	
	public void exportTraingSetCSV(ArrayList<Sample> samples){
		try {
			FileWriter fwriter = new FileWriter("train1.csv");
			PrintWriter pw = new PrintWriter(fwriter);
			
			pw.print("rarity,");
			pw.print("EitherNotPlace,");
			pw.print("differentOccupation,");
			//pw.print("smallPlace,");
			pw.println("interestingness");
			for(int i = 0; i < samples.size(); i++){
				String features = samples.get(i).getRarity()+","
						+ samples.get(i).getEitherNotPlace()+","
						+ samples.get(i).getDifferentOccupation()+","
						+ samples.get(i).getInterestingness()+"\n";
				pw.print(features);
				/*pw.print(samples.get(i).getRarity() + ",");
				pw.print(samples.get(i).getEitherNotPlace() + ",");
				pw.print(samples.get(i).getDifferentOccupation() + ",");
				//pw.println(samples.get(i).getSmallPlace());
				pw.println(samples.get(i).getInterestingness());*/
			}
			pw.flush();
			pw.close();
			fwriter.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
