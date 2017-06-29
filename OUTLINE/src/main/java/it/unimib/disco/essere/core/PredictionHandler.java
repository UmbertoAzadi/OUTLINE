package it.unimib.disco.essere.core;

import java.io.File;
import java.io.PrintWriter;
import java.util.ArrayList;

import it.unimib.disco.essere.functionalities.Predictor;
import it.unimib.disco.essere.load.Loader;
import it.unimib.disco.essere.load.LoaderProperties;
import weka.classifiers.Classifier;

public class PredictionHandler extends Handler {
	
	private ArrayList<String> paths;
	
	/**
	 * Default Constructor for the prediction throws command line
	 * */
	public PredictionHandler(){
		this.configuration = null;
	}
	
	/**
	 * Constructor for the prediction throws configuration file
	 * @param path : path of the configuaraton file 
	 * @throws Exception 
	 * */
	public PredictionHandler(Loader config, String path) throws Exception{
		this.configuration = config;
		paths =  configuration.loadForPrediction(path);
	}
	
	public void predict() throws Exception{
		
		if(configuration == null){
			throw new NullPointerException("Wrong contructor used for create the istances");
		}
		
		String	pathDataset =  paths.get(0);
		paths.remove(0);

		for(String s: paths){
			predWithOneClassifier(pathDataset, s);
		}
	}
	
	public void predict(String path1, String path2) throws Exception{
		String pathDataset = path1;
		String pathSerialized = path2;
		Classifier c = null;
		try{
			c = serializer.read(pathSerialized);
		}catch(Exception e1){
			String temp = pathDataset;
			pathDataset = pathSerialized;
			pathSerialized = temp;
			try {
				c = serializer.read(pathSerialized);
			} catch(Exception e){
				printErrorPred();
			} 
		}

		predictAndPrint(pathDataset, c);
	}

	private void predWithOneClassifier(String pathDataset, String pathSerialized) throws Exception{
		
		Classifier c = null;
		try {
			c = serializer.read(pathSerialized);
		}catch(Exception e){
			printErrorPred();
		}

		predictAndPrint(pathDataset, c);
	}

	private void predictAndPrint(String pathDataset, Classifier c) throws Exception {
		//predict
		DatasetHandler dataset = new DatasetHandler(pathDataset);
		Predictor predictor = new Predictor(dataset.getDataset());
		DatasetHandler datasetPredicted = predictor.makePredicitions(c, false);

		//print
		String directory = pathDataset.substring(0, pathDataset.lastIndexOf('/')+1);
		String name = pathDataset.substring(pathDataset.lastIndexOf('/') + 1);
		String nameClassifier = c.getClass().getName();
		nameClassifier = nameClassifier.substring(nameClassifier.lastIndexOf('.')).replace(".", "");

		String path = directory + "Predicted_" + nameClassifier + "_" + name;

		//save
		datasetPredicted.toCSV(path);
		System.out.println("The predict model is saved in: " + path);
	}

	private void printErrorPred() throws Exception {
		LOGGER.severe("------------------------------------------------------------------\n"
		+"ERROR : the serialized file is incorrect, please check the path \n"
		+"	and make sure that the file is a .model\n" 
		+"------------------------------------------------------------------");
		throw new Exception();
	}

}
