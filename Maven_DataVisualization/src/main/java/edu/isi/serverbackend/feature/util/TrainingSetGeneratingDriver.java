package edu.isi.serverbackend.feature.util;
import java.awt.Dimension;
import javax.swing.JFrame;

public class TrainingSetGeneratingDriver extends JFrame{
	InputFilePanel inputFilePanel;
	public TrainingSetGeneratingDriver(){
		inputFilePanel = new InputFilePanel(this);
		this.add(inputFilePanel);
		this.setTitle("DBpedia Dataset Test");
		this.setSize(new Dimension(400, 150));
		this.setVisible(true);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		new TrainingSetGeneratingDriver();
	}

}
