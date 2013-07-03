package linearRegression;

import weka.core.Instances;
import weka.classifiers.Evaluation;
import weka.classifiers.functions.LinearRegression;
import weka.classifiers.bayes.NaiveBayes;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;

public class InterprateArff {

	/*public InterprateArff(String fileName) throws Exception{
		BufferedReader reader = new BufferedReader(
                             new FileReader(fileName));
		Instances data = new Instances(reader);
	}*/
	
	public InterprateArff(String file1, String file2) throws Exception{
		BufferedReader reader = new BufferedReader(
                new FileReader(file1));
		Instances data = new Instances(reader);
		data.setClassIndex(data.numAttributes()-1);
		
		reader = new BufferedReader(
                new FileReader(file2));
		Instances test = new Instances(reader);
		// setting class attribute
		test.setClassIndex(data.numAttributes()-1);
		reader.close();
		
		LinearRegression lr = new LinearRegression();
		lr.buildClassifier(data);
		Instances labeled = new Instances(test);
		
		
		
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
	
	/**
		   * sets the classifier to use
		   * @param name        the classname of the classifier
		   * @param options     the options for the classifier
		   */
	/*public void setClassifier(String name, String[] options) throws Exception {
		    m_Classifier = Classifier.forName(LinearRegression, options);
		  }
		  */
}
