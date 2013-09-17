package edu.isi.linearRegression;

public class WekaDemo {
	  //public static void main(String[] args) throws Exception {
	
	String rankResponse ="";
    public WekaDemo(String csvFileLocation,String trainFileLocation, String testFileLocation) throws Exception{
    	
    	ConvertCSV c = new ConvertCSV();
    	c.convert(csvFileLocation);
        InterprateArff ia = new InterprateArff(trainFileLocation,testFileLocation);//"d:/etc/train1.arff","d:/etc/test1.arff");
        ia.TrainModel();
		rankResponse = ia.getPredict();
        
    }
    
    /*public static void main(String[] args) throws Exception {
    	WekaDemo wk= new WekaDemo();
    }*/
   
   public String getRanking(){
	   return rankResponse;
   }
   
}
