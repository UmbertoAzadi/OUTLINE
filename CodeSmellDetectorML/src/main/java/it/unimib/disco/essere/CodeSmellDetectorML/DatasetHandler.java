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
		}catch(Exception e){
			System.out.println("Unable to load the dataset, please make sure that the path is correct");
			//e.printStackTrace();
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
			e.printStackTrace();
		}
		
	}
	
	public void toArff(String path){
		ArffSaver saver = new ArffSaver();
		saver.setInstances(dataset);
		try{
			saver.setFile(new File(path));
			saver.writeBatch();
		}catch(IOException e){
			e.printStackTrace();
		}
	}
	
}
