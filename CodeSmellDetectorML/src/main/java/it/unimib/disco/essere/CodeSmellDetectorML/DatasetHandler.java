package it.unimib.disco.essere.CodeSmellDetectorML;

import java.io.File;
import java.io.IOException;

import weka.core.Instances;
import weka.core.converters.ArffSaver;
import weka.core.converters.CSVSaver;
import weka.core.converters.ConverterUtils.DataSource;

public class DatasetHandler {
	
	private Instances dataset;
	
	public DatasetHandler(String path){
		try{
			DataSource source = new DataSource(path);
			dataset = source.getDataSet();
			dataset.setClassIndex(dataset.numAttributes()-1);
		}
		catch(Exception e){
			System.out.println("------------------------------------------------------------------------------------------------");
			System.out.println("ERROR : Incorrect dataset!");
			System.out.println("	please check the path related to the dataset (remember to use <\\> not </> for the path),");
			System.out.println("	and make sure that the dataset selected is well-formed ");
			System.out.println("	(Exemple: make sure that the \"not know\" value is represent with \"?\")");
			System.out.println("----------------------------------------------------------------------------------------------");
			System.exit(0);
		}
	}
	
	public DatasetHandler(Instances dataset){
		this.dataset = dataset;
	}
	
	public Instances getDataset(){
		return dataset;
	}
	
	public String toString(){
		return dataset.toSummaryString();
	}
	
	public void toCSV(String path){
		CSVSaver saver = new CSVSaver();
		saver.setInstances(dataset);
		String directory = path.substring(0, path.lastIndexOf("/")+1);
		String name = path.substring(path.lastIndexOf("/") + 1);
		try {
			System.out.println("The predict model is saved in: " + directory + "Predicted_" + name);
			saver.setFile(new File(directory + "Predicted_" + name));
			saver.writeBatch();
		} catch (IOException e) {
			System.out.println("ERROR : Unable to save the model in CSV extesion");
			System.out.println("MESSAGE: " + e.getMessage()); 
		}
		
	}
	
	public void toArff(String path){
		ArffSaver saver = new ArffSaver();
		saver.setInstances(dataset);
		try{
			saver.setFile(new File(path));
			saver.writeBatch();
		}catch(IOException e){
			System.out.println("ERROR : Unable to save the model in ARFF extesion");
			System.out.println("MESSAGE: " + e.getMessage()); 
		}
	}
	
}
