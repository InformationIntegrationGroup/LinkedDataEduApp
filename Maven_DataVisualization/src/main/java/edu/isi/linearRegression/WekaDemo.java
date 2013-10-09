package edu.isi.linearRegression;

public class WekaDemo {
	  //public static void main(String[] args) throws Exception {
	
	String rankResponse ="";
	
    public WekaDemo(){}
 
    public void ConvertCSV(String fileLocation) throws Exception{
    	ConvertCSV c = new ConvertCSV();
    	c.convert(fileLocation);
    }
    
    public void TrainModel(String fileLocation) throws Exception{
    	InterprateArff ia = new InterprateArff(fileLocation+"train.arff",fileLocation+"test1.arff");
        ia.TrainModel();
		rankResponse = ia.getPredict();
    }
   
   public String getRanking(){
	   return rankResponse;
   }
   
}
