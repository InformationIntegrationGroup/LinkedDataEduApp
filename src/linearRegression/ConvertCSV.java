package linearRegression;
import weka.core.Instances;
import weka.core.converters.ArffSaver;
import weka.core.converters.CSVLoader;

import java.io.File;
import java.io.IOException;
 
public class ConvertCSV {
  /**
   * takes 2 arguments:
   * - CSV input file
   * - ARFF output file
 * @throws Exception 
 * @throws Exception 
   */
	public ConvertCSV(){ 	 }
	
	public void convert(String fileName) throws Exception{
		// load CSV
    CSVLoader loader = new CSVLoader();
    loader.setSource(new File("d:/etc/"+fileName+".csv"));
    Instances data = loader.getDataSet();
 
    // save ARFF
    ArffSaver saver = new ArffSaver();
    saver.setInstances(data);
    saver.setFile(new File("d:/etc/"+fileName+".arff"));
   // saver.setDestination(new File(args[1]));
    saver.writeBatch();
    
	}
  

}


