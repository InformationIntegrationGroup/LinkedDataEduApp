package edu.isi.linearRegression;

public class WekaDemo {
	  //public static void main(String[] args) throws Exception {
    public WekaDemo() throws Exception{
    	
    	ConvertCSV c = new ConvertCSV();
    	c.convert("test1");
        InterprateArff ia = new InterprateArff("d:/etc/train1.arff","d:/etc/test1.arff");
        ia.TrainModel();
		String ranResponse = ia.getPredict();
        
    }
    
    public static void main(String[] args) throws Exception {
    	WekaDemo wk= new WekaDemo();
    }
   
   
   
}
