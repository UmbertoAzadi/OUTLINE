package it.unimib.disco.essere.core;

import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;

import weka.core.Instances;
import weka.core.converters.ArffSaver;
import weka.core.converters.CSVSaver;
import weka.core.converters.ConverterUtils.DataSource;


/**
* Class  that wrap the dataset and provide some method can be useful to handle it. 
* <br/>
* */
public class DatasetHandler {

	/** The instance used for print out the information, warning and error messages */
	private static final Logger LOGGER = Logger.getLogger(DatasetHandler.class.getName());

	/** The dataset  wrapped */
	private Instances dataset;
	
	/** The path of the dataset wrapped */
	private String path;
	
	/**
	 * Create an instance of a DatasetHandler and load the dataset
	 * 
	 * @param path the path of the dataset 
	 * */
	public DatasetHandler(String path) throws Exception{
		try{ 
			DataSource source = new DataSource(path);
			dataset = source.getDataSet();
			dataset.setClassIndex(dataset.numAttributes()-1);
			this.path = path;
		}
		catch(Exception e){
			LOGGER.severe("------------------------------------------------------------------------------------------------\n"
					+"ERROR : Incorrect dataset!\n"
					+"	please check the path related to the dataset (remember to use </> not <\\> for the path, and \n"
					+ " make sure that the path doesn't contain any letters with accents),\n"
					+"	finally make sure that the dataset selected is well-formed \n"
					+"	(Exemple: make sure that the \"not know\" value is represent with \"?\")\n"
					+"  cause: " + e.getMessage() + "\n"
					+"----------------------------------------------------------------------------------------------");
			throw e;
		}
	}
	
	/**
	 * Create an instance of a DatasetHandler
	 * 
	 * @param dataset the weka.core.Instances that contain the dataset
	 * */
	public DatasetHandler(Instances dataset){
		this.dataset = dataset;
		this.path = "no path specified";
	}
	
	/**
	 * @return the path of the dataset 
	 * */
	public String getPath(){
		return path;
	}

	/**
	 * @return the dataset as a  weka.core.Instances
	 * */
	public Instances getDataset(){
		return dataset;
	}

	/**
	 * Convert a dataset in a .csv file
	 * 
	 * @param path the path where the .csv dataset has to be saved (complete with the name of the new file)
	 * */
	public void toCSV(String path){
		CSVSaver saver = new CSVSaver();
		saver.setInstances(dataset);
		try {
			LOGGER.info("Saving the .csv...");
			saver.setFile(new File(path));
			saver.writeBatch();
		} catch (IOException e) {
			LOGGER.severe("Unable to save the model in CSV extesion: "+ e);
		}

	}

	/**
	 * Convert a dataset in a .arff file
	 * 
	 * @param path the path where the .arff dataset has to be saved (complete with the name of the new file)
	 * */
	public void toArff(String path){
		ArffSaver saver = new ArffSaver();
		saver.setInstances(dataset);
		try{
			LOGGER.info("Saving the .arff...");
			saver.setFile(new File(path));
			saver.writeBatch();
		}catch(IOException e){
			LOGGER.severe("Unable to save the model in ARFF extesion: "+ e);
			 
		}
	}

	/**
	 * @return the summary of the dataset wrapped
	 * */
	public String toString(){
		return dataset.toSummaryString();
	}
}

