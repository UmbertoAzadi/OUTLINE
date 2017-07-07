package it.unimib.disco.essere.core;


import weka.classifiers.Classifier;
import java.io.File;
import java.util.ArrayList;

import javax.swing.DefaultListModel;

import it.unimib.disco.essere.experiment.DataExperimenter;
import it.unimib.disco.essere.functionalities.DataClassifier;
import it.unimib.disco.essere.functionalities.DataEvaluator;
import it.unimib.disco.essere.functionalities.Serializer;
import it.unimib.disco.essere.load.*;

/**
* Class for execute all the operations concerning: the building and serialization of the classifiers, the printing of the 
* human-readable result, the cross validation and the weka experiment.  
* <br/>
* */
public class ClassificationHandler extends Handler {

	/** The list containg all the classifiers */
	private ArrayList<Classifier> classifiers;
	
	/** An instance of the class which will build the classifiers */
	private DataClassifier  classifier;

	
	/**
	 * Create an instance of a ClassificationHandler
	 * 
	 * @param loader an instance of the class that is suppose to load the configuration file
	 * @param path   the path of the configuration file
	 * */
	public ClassificationHandler(Loader loader,String path) throws Exception{
		configuration = loader;
		load(path);
	}
	
	/**
	 * The getter of the classifiers
	 * @return a list containing all the classifiers
	 * */
	public ArrayList<Classifier> getClassifiers() {
		return classifiers;
	}

	/**
	 * Load the configuration file
	 * @param path the path of the configuration file
	 * */
	public void load(String path) throws Exception{
		try {
			classifiers = configuration.loadForClassification(path);
		} catch (Exception e) {
			LOGGER.severe("Invalid or not found property file, please check the path! [" + e + "]");
			throw e;
		}
	}
	
	/**
	 * Perform a custom experiment, which is basically a standard weka experiment where folds are 
	 * created before the execution and then executed concurrently
	 * 
	 * @param runs  how many times the the cross validation has to be repeted
	 * @param folds number of fold used by the cross validation
	 * */
	public void customExp(int runs, int folds) throws Exception{
		wekaExperiment("custom", "custom", runs, folds, 0.0, false);
	}
	
	/**
	 * Perform a standard weka experiment
	 * 
	 * @param exptype    the type of experiment: classification or regression
	 * @param splittype  the type of split: crossvalidation or randomsplit
	 * @param runs       how many times the the split has to be repeted 
	 * @param folds      number of fold used by the cross validation
	 * @param percentage  how many instances has to used for the creation of the training set in the randomsplit
	 * @param randomized if the instances used for the creating the training and testing set in the randomsplit 
	 * 					 has to be randomized first
	 * */
	public void wekaExperiment(String exptype, 
						String splittype, 
						int runs, 
						int folds, 
						double percentage, 
						boolean randomized) throws Exception{

		// create a list of dataset that contain just the one specify in the config file
		DefaultListModel<File> model = new DefaultListModel<File>();
		File file = new File(configuration.getDatasetHandler().getPath());
		model.addElement(file);

		DataExperimenter experiment = new DataExperimenter(classifiers, model, configuration.getPathForResult());
		boolean printOnFile = false;

		if(configuration.getPathForResult() != null){
			printOnFile = true;
		}

		String result = experiment.experiment(exptype, splittype, folds, randomized, percentage, runs);

		if(printOnFile){
			printInConfigPath(result, "Saving the result of the experiment...", "Experiment_Result", ".txt");
		}else{
			System.out.println(result);
		}
	}

	/**
	 * Perform a cross validation
	 * 
	 * @param folds number of fold used by the cross validation
	 * @param seed  the number used by the Random class for splitting the instances (usually is used 1)
	 * */
	public void crossValidation(int folds, int seed) throws Exception{
		LOGGER.info("Crossvalidating...");
		DataEvaluator evaluator = new DataEvaluator();
		StringBuilder message = new StringBuilder();
		
		for(Classifier c: classifiers){
			evaluator.crossValidation(configuration.getDataset(), c, folds, seed);
			message.append("_____"+c.getClass().getName()+ "_____" + evaluator.getSummary()+"\n");
		}
		if(configuration.getPathForResult() != null){
			printInConfigPath(message.toString(), "Saving the result of the cross validation...", "CrossValidation_Result", ".txt");
		}else{
			System.out.println(message.toString());
		}
	}
	
	
	/**
	 * Build all the classifiers contained by the list classifiers (the first attribute)
	 * */
	public void classify() throws Exception{
		LOGGER.info("Classifing...");
		classifier = new DataClassifier(configuration.getDataset(), classifiers);
	}

	/**
	 * Save the summary of all the classifiers contained by the list classifiers (the first attribute)
	 * It save them in the path specified by the configuration file or in the fold .\result of the repository 
	 * */
	public void saveClassifier() throws Exception{
		try{	
			classifier.getSummary(configuration.getPathForResult());
		}catch(Exception e){
			try {
				LOGGER.warning("Path not specified or incorrect [" + e + "]");
				String path = new java.io.File("").getAbsolutePath();

				if(path.contains("\\OUTLINE\\OUTLINE")){
					classifier.getSummary(path.substring(0, path.lastIndexOf('\\'))+"\\result");
				}else{
					classifier.getSummary(path + "\\result");
				}

			} catch (Exception e1) {
				LOGGER.severe("Unsable to save the human-readable description of the classifiers:" + e1);
				throw new Exception();
			}
		}
	}

	/**
	 * Print on screen the summary of all the classifiers contained by the list classifiers (the first attribute) 
	 * */
	public void printClassifier(){
		System.out.println(classifier.getSummary());
	}

	/**
	 * Serialize all the classifiers contained by the list classifiers (the first attribute)
	 * It save the serialized file in the path specified by the configuration file or in the fold .\result of the repository 
	 * */
	public void serialize() throws Exception{
		serializer = new Serializer();
		int i = 1;
		String pathToPrint = "";
		for(Classifier c: classifiers){
			String name = classifier.generateNameForFile(c, i);
			try{	
				serializer.serialize(configuration.getPathForResult() + "\\" + name + ".model", c);
				pathToPrint = configuration.getPathForResult();
			}catch(Exception e){
				try {
					String path = new java.io.File("").getAbsolutePath();

					if(path.contains("\\OUTLINE\\OUTLINE")){
						serializer.serialize(path.substring(0, path.lastIndexOf('\\'))+"\\result" + "\\" + name + ".model", c);
						pathToPrint = path.substring(0, path.lastIndexOf('\\')) + "\\result";
					}else{ 
						serializer.serialize(path+"\\result" + "\\" + name + ".model", c);
						pathToPrint = path + "\\result";
					}

				} catch (Exception e1) {

					LOGGER.severe("Unable to serialize the classifiers:" + e1);
					throw e1;
				}
			}
			i++;
		}
		System.out.println("The serialized files were saved in: "+ pathToPrint);
	}
}
