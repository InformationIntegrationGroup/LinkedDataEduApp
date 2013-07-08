package feature.util;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;
import javax.swing.*;

import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.http.HTTPRepository;

import linkedData.*;
import linkedData.LinkedDataConnection.CurrentNode;

public class InputFilePanel extends JPanel implements ActionListener{
	JFrame parent;
	JLabel fileNameLabel;
	JTextField fileNameField;
	JButton generateBtn;
	JPanel inputPanel;
	private BufferedReader reader;
	public InputFilePanel(JFrame parent){
		this.parent = parent;
		
		fileNameLabel = new JLabel("File Name: ");
		
		fileNameField = new JTextField();
		fileNameField.setPreferredSize(new Dimension(200, 30));
		fileNameField.setMaximumSize(new Dimension(200, 30));
		fileNameField.setMinimumSize(new Dimension(200, 30));
		
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
		
		this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		this.setAlignmentY(CENTER_ALIGNMENT);
		this.add(inputPanel);
	}
	
	@Override
	public void actionPerformed(ActionEvent ae) {
		// TODO Auto-generated method stub
		if(ae.getSource() == generateBtn){
			readRawDataFromFile(fileNameField.getText());
		}
	}
	
	public void readRawDataFromFile(String fileName){
		try{
			HTTPRepository endpoint = new HTTPRepository("http://dbpedia.org/sparql", "");
			endpoint.initialize();
			ArrayList<TrainingSample> samples = new ArrayList<TrainingSample>();
			RepositoryConnection repoConnection = endpoint.getConnection();
			FileInputStream fstream = new FileInputStream(fileName);
			DataInputStream dstream = new DataInputStream(fstream);
			reader = new BufferedReader(new InputStreamReader(dstream));
			String line;
			line = reader.readLine();
			while(line != null){
				String[] strs = line.split(" ");
				if(strs.length == 4){
					System.out.println("sample detected");
					LinkedDataNode subject = new LinkedDataNode(strs[0], repoConnection);
					LinkedDataNode object = new LinkedDataNode(strs[2], repoConnection);
					String predicate = strs[1];
					LinkedDataConnection link = new LinkedDataConnection(subject, object, predicate, CurrentNode.subject, repoConnection);
					TrainingSample newSample = new TrainingSample(link, Float.parseFloat(strs[3]));
					newSample.evalutateFeature();
					samples.add(newSample);
				}
				line = reader.readLine();
			}
			dstream.close();
			fstream.close();
			exportTraingSetCSV(samples);
		}
		catch(Exception e){
			System.err.println("Error: " + e.getMessage());
		}
	}
	
	public void exportTraingSetCSV(ArrayList<TrainingSample> samples){
		try {
			FileWriter fwriter = new FileWriter("trainingSamples.csv");
			PrintWriter pw = new PrintWriter(fwriter);
			
			pw.print("rarity,");
			pw.print("EitherNotPlace,");
			pw.print("differentOccupation,");
			//pw.print("smallPlace,");
			pw.println("interestingness");
			for(int i = 0; i < samples.size(); i++){
				pw.print(samples.get(i).getRarity() + ",");
				pw.print(samples.get(i).getEitherNotPlace() + ",");
				pw.print(samples.get(i).getDifferentOccupation() + ",");
				//pw.println(samples.get(i).getSmallPlace());
				pw.println(samples.get(i).getInterestingness());
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
