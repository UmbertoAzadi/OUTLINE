package it.unimib.disco.essere.core;


import weka.classifiers.Classifier;
import weka.classifiers.meta.AdaBoostM1;
import weka.classifiers.meta.CVParameterSelection;
import weka.classifiers.meta.MultiSearch;
import weka.classifiers.meta.multisearch.DefaultEvaluationMetrics;
import weka.classifiers.trees.J48;
import weka.core.SelectedTag;
import weka.core.SetupGenerator;
import weka.core.Utils;
import weka.core.setupgenerator.AbstractParameter;
import weka.core.setupgenerator.MathParameter;

import java.io.File;
import java.io.PrintWriter;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;
import java.util.logging.Logger;

import javax.swing.DefaultListModel;

import it.unimib.disco.essere.experiment.DataExperimenter;
import it.unimib.disco.essere.functionalities.DataClassifier;
import it.unimib.disco.essere.functionalities.DataEvaluator;
import it.unimib.disco.essere.functionalities.Serializer;
import it.unimib.disco.essere.load.*;

public class ClassificationHandler extends Handler {

	private ArrayList<Classifier> classifiers;
	private DataClassifier  classifier;

	public ClassificationHandler(Loader l,String path) throws Exception{
		configuration = l;
		load(path);
	}
	
	public void load(String path) throws Exception{
		try {
			classifiers = configuration.loadForClassification(path);
		} catch (Exception e) {
			LOGGER.severe("Invalid or not found property file, please check the path! [" + e + "]");
			throw e;
		}
	}
	
	public void customExp(int runs, int folds) throws Exception{
		wekaExperiment("custom", "custom", runs, folds, 0.0, false);
	}

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
			printInConfiPath(result, "Saving the result of the experiment...", "Experiment_Result", ".txt");
		}else{
			System.out.println(result);
		}
	}

	public void crossValidation(int fold, int seed) throws Exception{
		LOGGER.info("Crossvalidating...");
		boolean printOnFile = false;
		DataEvaluator evaluator = new DataEvaluator();
		StringBuilder message = new StringBuilder();
		
		for(Classifier c: classifiers){
			message.append("_____"+c.getClass().getName()+ "_____" + evaluator.crossValidation(configuration.getDataset(), c, fold, seed)+"\n");
		}
		if(configuration.getPathForResult() != null){
			printInConfiPath(message.toString(), "Saving the result of the cross validation...", "CrossValidation_Result", ".txt");
		}else{
			System.out.println(message.toString());
		}
	}

	public void classify() throws Exception{
		LOGGER.info("Classifing...");
		classifier = new DataClassifier(configuration.getDataset(), classifiers);
	}

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

	public void printClassifier(){
		System.out.println(classifier.getSummary());
	}

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
