package org.example.dbpedia;

import java.io.File;
import java.io.PrintWriter;

import weka.classifiers.functions.SMOreg;
import weka.classifiers.functions.supportVector.NormalizedPolyKernel;
import weka.classifiers.functions.supportVector.RegSMOImproved;
import weka.core.Instances;
import weka.core.converters.CSVLoader;

public class WekaClassification {

	public static void main(String[] args) throws Exception {
		PrintWriter pw = new PrintWriter("ranking.txt");
		CSVLoader trainingLoader =new CSVLoader();
		trainingLoader.setSource(new File(args[0]));
		Instances inputDataset = trainingLoader.getDataSet();
		CSVLoader dataLoader =new CSVLoader();
		dataLoader.setSource(new File(args[1]));
		Instances dataset = dataLoader.getDataSet();
		SMOreg smoreg = new SMOreg();
		smoreg.setC(1.0);
		NormalizedPolyKernel kernel = new NormalizedPolyKernel();
		kernel.setCacheSize(250007);
		kernel.setChecksTurnedOff(false);
		kernel.setExponent(2.0);
		kernel.setUseLowerOrder(false);
		kernel.setDebug(false);
		smoreg.setKernel(kernel);
		RegSMOImproved optimizer = new RegSMOImproved();
		optimizer.setEpsilon(1E-12);
		optimizer.setEpsilonParameter(0.001);
		optimizer.setTolerance(0.001);
		optimizer.setUseVariant1(true);
		smoreg.setRegOptimizer(optimizer);
		inputDataset.setClassIndex(inputDataset.numAttributes() - 1); 
		dataset.setClassIndex(inputDataset.numAttributes() - 1); 
		smoreg.buildClassifier(inputDataset);
		for (int i = 0; i < dataset.numInstances(); i++) {
			pw.println(smoreg.classifyInstance(dataset.instance(i)));
		}
		pw.close();
	}

}
