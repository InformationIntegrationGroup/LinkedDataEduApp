package edu.isi.linearRegression;



import weka.core.Instances;
import weka.core.Utils;
import weka.classifiers.functions.LinearRegression;


import weka.classifiers.meta.CVParameterSelection;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.Random;

public class InterprateArff {

	BufferedReader reader;
	Instances data,test,labeled;
	/*public InterprateArff(String fileName) throws Exception{
		BufferedReader reader = new BufferedReader(
                             new FileReader(fileName));
		Instances data = new Instances(reader);
	}*/
	
	public InterprateArff(String file1, String file2) throws Exception{
		reader = new BufferedReader(
                new FileReader(file1));
		data = new Instances(reader);
		data.setClassIndex(data.numAttributes()-1);
		
		reader = new BufferedReader(
                new FileReader(file2));
		test = new Instances(reader);
		// setting class attribute
		test.setClassIndex(data.numAttributes()-1);
		reader.close();
		
		String[] options = new String[3];
		options[0] = "-D";
		options[1] ="-S";
		options[2]="1";
		LinearRegression lr = new LinearRegression();
		lr.setOptions(options);
		lr.buildClassifier(data);
		
		labeled = new Instances(test);
		
		//NaiveBayes nB = new NaiveBayes();
		//nB.buildClassifier(data);
		
		for(int i = 0; i < test.numInstances(); i++){
			double clsLabel = lr.classifyInstance(test.instance(i));
			labeled.instance(i).setClassValue(clsLabel);
		}
		
		BufferedWriter writer = new BufferedWriter(
							new FileWriter("d:/etc/label.arff"));
		writer.write(labeled.toString());
		
		//Evaluation evl = new Evaluation(data);
		//evl.crossValidateModel(nB,data,10,new Random(1));
		//System.out.println(evl.toSummaryString("\nResults	\n=========\n",true));
		//System.out.println(evl.fMeasure(classIndex)
		System.out.println(lr);
		System.out.println("\nTesting datasets\n");
		System.out.println(labeled);
		
		
		/* Evaluation eval = new Evaluation(data);
		 Random rand = new Random(1);  // using seed = 1
		 int folds = 10;
		 eval.crossValidateModel(lr, data, folds, rand);
		 System.out.println(eval.toSummaryString());
	*/
	} 
	
	public Instances getPredict(){
		//Instances labeled;
		return labeled;
	}

}

