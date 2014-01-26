package edu.isi.serverbackend.feature.util;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;

import javax.swing.*;

import org.openrdf.query.*;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.http.HTTPRepository;

import com.mongodb.*;

import edu.isi.serverbackend.feature.*;
import edu.isi.serverbackend.linkedData.*;
import edu.isi.serverbackend.localDatabase.bean.*;
import edu.isi.serverbackend.localDatabase.mongoCollection.TripleCollection;


public class InputFilePanel extends JPanel implements ActionListener{
	final static String PERSON = "Person";
	final static String PLACE = "Place";
	final static String WORK = "Work";
	final static String ORG = "Organisation";
	final static String EVENT = "Event";
	final static String TYPEPREFIX = "http://dbpedia.org/ontology/";
	
	JFrame parent;
	JLabel fileNameLabel, seedLabel;
	JTextField fileNameField, seedField;
	JButton generateBtn, extractBtn, testMongoBtn;
	JPanel inputPanel;
	JPanel extractPanel;
	JPanel mongoTestPanel;
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
		
		testMongoBtn = new JButton("Test MongoDB");
		testMongoBtn.setPreferredSize(new Dimension(150, 30));
		testMongoBtn.setMaximumSize(new Dimension(150, 30));
		testMongoBtn.setMinimumSize(new Dimension(150, 30));
		testMongoBtn.addActionListener(this);
		
		mongoTestPanel =  new JPanel();
		mongoTestPanel.setLayout(new BoxLayout(mongoTestPanel, BoxLayout.X_AXIS));
		mongoTestPanel.add(testMongoBtn);
		
		
		this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		this.setAlignmentY(CENTER_ALIGNMENT);
		this.add(extractPanel);
		this.add(inputPanel);
		this.add(mongoTestPanel);
	}
	
	
	public void actionPerformed(ActionEvent ae) {
		// TODO Auto-generated method stub
		if(ae.getSource() == generateBtn){
			readRawDataFromFile(fileNameField.getText());
		}
		else if(ae.getSource() == extractBtn){
			extractTriples(seedField.getText());
		}
		else if (ae.getSource() == testMongoBtn){
			testMongoDB(seedField.getText());
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
			RepositoryConnection repoConn = endpoint.getConnection();
			FileInputStream fstream = new FileInputStream(fileName);
			DataInputStream dstream = new DataInputStream(fstream);
			reader = new BufferedReader(new InputStreamReader(dstream));
			String line;
			line = reader.readLine();
			while(line != null){
				ArrayList<Sample> temp  =  new ArrayList<Sample>();
				LinkedDataNode node = new LinkedDataNode(line, repoConn);
				node.retrieveNameAndType();
				node.retrieveSubjectConnections(temp);
				node.retrieveObjectConnections(temp);
				RarityDegree.calculateExtensionRarity(temp);
				RarityDegree.calcuateNodeDegree(temp);
				samples.addAll(temp);
				line = reader.readLine();
			}
			
			System.out.println("Sample processing finished");
			dstream.close();
			fstream.close();
			
			exportTypeTrainingSetCSV(samples, PERSON, PERSON);
			exportTypeTrainingSetCSV(samples, PERSON, WORK);
			exportTypeTrainingSetCSV(samples, PERSON, PLACE);
			exportTypeTrainingSetCSV(samples, WORK, PERSON);
		}
		catch(Exception e){
			System.err.println("Error: " + e.getMessage());
		}
	}
	
	public void exportTypeTrainingSetCSV(ArrayList<Sample> samples, String subjectType, String objectType){
		try {
			FileWriter fwriter = new FileWriter(subjectType + "_" + objectType + ".csv", false);
			PrintWriter pw = new PrintWriter(fwriter);
			pw.println("Subject,Predicate,Object,sbjExtensionRarity,objExtensionRarity,subjectDegree,objectDegree");
			for(Sample sample:samples){
				if(sample.getLink().getSubject().getTypeURI().equals(TYPEPREFIX+subjectType) && sample.getLink().getObject().getTypeURI().equals(TYPEPREFIX+objectType)){
					String record = "\"" + sample.getLink().getSubject().getName() + "\","
							+ "\"" + PredicateBean.obtainPredicateName(sample.getLink().getPredicate()) +"\","
							+ "\"" + sample.getLink().getObject().getName() + "\","
							+ sample.getSubjectExtensionRarity() + ","
							+ sample.getObjectExtensionRarity() + ","
							+ sample.getSubjectRarity() + ","
							+ sample.getObjectRarity();
					pw.println(record);
				}
			}
			pw.flush();
			pw.close();
			fwriter.close();
		}
		catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
		}
	}
	
	public void exportTraingSetCSV(ArrayList<Sample> samples){
		try {
			FileWriter fwriter = new FileWriter("train1.csv");
			PrintWriter pw = new PrintWriter(fwriter);
			pw.print("Subject,");
			pw.print("predicate,");
			pw.print("Object,");
			pw.print("Rarity,");
			pw.print("EitherNotPlace,");
			pw.print("DifferentOccupation,");
			pw.print("smallPlace,");
			pw.print("Importance,");
			pw.println("interestingness");
			for(int i = 0; i < samples.size(); i++){
				String features = samples.get(i).getLink().getSubject().getName()+","
						+ samples.get(i).getLink().getPredicate()+","
						+ samples.get(i).getLink().getObject().getName()+","
						+ samples.get(i).getRarity()+","
						+ samples.get(i).getEitherNotPlace()+","
						+ samples.get(i).getDifferentOccupation()+","
						+ samples.get(i).getSmallPlace()+","
						+ samples.get(i).getExtensionImportance()+","
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
	
	public void testMongoDB(String seed){
		try{
			HTTPRepository endpoint = new HTTPRepository("http://dbpedia.org/sparql", "");
			MongoClient mongoClient = new MongoClient("localhost");
			DB dbConn = mongoClient.getDB("LinkedData");
			endpoint.initialize();
			RepositoryConnection repoConn = endpoint.getConnection();
			LinkedDataNode currentNode = new LinkedDataNode(seed, repoConn);
			currentNode.retrieveNameAndType();
			ArrayList<Sample> samples = new ArrayList<Sample>();
			currentNode.retrieveSubjectConnections(samples);
			currentNode.retrieveObjectConnections(samples);
			RarityDegree.calcuateNodeDegree(samples);
			RarityDegree.calculateExtensionRarity(samples);
			NodeBean currentNodeBean = new NodeBean();
			currentNodeBean.setName(currentNode.getName());
			currentNodeBean.setExplored(true);
			currentNodeBean.setUri(currentNode.getURI());
			currentNodeBean.setTypeURI(currentNode.getTypeURI());
			for(Sample sample:samples){
				TripleBean tripleBean = new TripleBean();
				PredicateBean predicateBean = new PredicateBean();
				predicateBean.setURI(sample.getLink().getPredicate());
				tripleBean.setPredicate(predicateBean);
				if(sample.getLink().isSubjectConnection()){
						NodeBean node = new NodeBean();
						node.setExplored(false);
						node.setName(sample.getLink().getObject().getName());
						node.setTypeURI(sample.getLink().getObject().getTypeURI());
						node.setUri(sample.getLink().getObject().getURI());
					
					tripleBean.setSubject(currentNodeBean);
					tripleBean.setObject(node);
				}
				else{
						NodeBean node = new NodeBean();
						node.setExplored(false);
						node.setName(sample.getLink().getSubject().getName());
						node.setTypeURI(sample.getLink().getSubject().getTypeURI());
						node.setUri(sample.getLink().getSubject().getURI());
					
					tripleBean.setObject(currentNodeBean);
					tripleBean.setSubject(node);
				}
				tripleBean.setSbjExtensionRarity(sample.getSubjectExtensionRarity());
				tripleBean.setObjExtensionRarity(sample.getObjectExtensionRarity());
				tripleBean.setSubjectDegree(sample.getSubjectRarity());
				tripleBean.setObjectDegree(sample.getObjectRarity());
				TripleCollection.insertTriple(tripleBean, dbConn);
			}
			TripleCollection.retrieveTriples(seed, dbConn);
		}catch(Exception e){
			System.err.println("Error: " + e.getMessage());
		}
		
	}
}
