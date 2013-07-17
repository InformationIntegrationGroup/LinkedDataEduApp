package linearRegression;

import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.core.Instances;
import weka.core.OptionHandler;
import weka.core.Utils;
import weka.filters.Filter;

import java.io.FileReader;
import java.io.BufferedReader;
import java.util.Vector;

public class WekaDemo {
  
	
  //public static void main(String[] args) throws Exception {
    public WekaDemo() throws Exception{
    	
    	ConvertCSV c = new ConvertCSV();
    	c.convert("test1");
        InterprateArff ia = new InterprateArff("d:/etc/train.arff","d:/etc/test.arff");
        
        
    }
    
    
   
   
}
