package it.unimib.disco.essere.core;

import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;

import weka.core.Instances;
import weka.core.converters.ArffSaver;
import weka.core.converters.CSVSaver;
import weka.core.converters.ConverterUtils.DataSource;

public class DatasetHandler {

	private static final Logger LOGGER = Logger.getLogger(DatasetHandler.class.getName());

	private Instances dataset;
	private String path;

	public DatasetHandler(String path) throws Exception{
		try{ 
			DataSource source = new DataSource(path);
			dataset = source.getDataSet();
			dataset.setClassIndex(dataset.numAttributes()-1);
		}
		catch(Exception e){
			LOGGER.severe("------------------------------------------------------------------------------------------------\n"
					+"ERROR : Incorrect dataset!\n"
					+"	please check the path related to the dataset (remember to use <\\> not </> for the path),\n"
					+"	and make sure that the dataset selected is well-formed \n"
					+"	(Exemple: make sure that the \"not know\" value is represent with \"?\")\n"
					+"----------------------------------------------------------------------------------------------");
			throw e;
		}
		
		this.path = path;
	}
	
	public String getPath(){
		return path;
	}

	public DatasetHandler(Instances dataset){
		this.dataset = dataset;
	}

	public Instances getDataset(){
		return dataset;
	}

	@Override
	public String toString(){
		return dataset.toSummaryString();
	}

	public void toCSV(String path){
		CSVSaver saver = new CSVSaver();
		saver.setInstances(dataset);
		try {
			System.out.println("Saving the .csv...");
			saver.setFile(new File(path));
			saver.writeBatch();
		} catch (IOException e) {
			LOGGER.severe("Unable to save the model in CSV extesion: "+ e);
		}

	}

	public void toArff(String path){
		ArffSaver saver = new ArffSaver();
		saver.setInstances(dataset);
		try{
			System.out.println("Saving the .arff...");
			saver.setFile(new File(path));
			saver.writeBatch();
		}catch(IOException e){
			LOGGER.severe("Unable to save the model in ARFF extesion: "+ e);
			 
		}
	}

}
