package it.unimib.disco.essere.core;


import weka.classifiers.Classifier;

import java.io.File;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.DefaultListModel;

import it.unimib.disco.essere.load.*;

public class EntryPoint {

	private ArrayList<Classifier> classifiers;
	private Loader configuration;
	private Serializer serializer = new Serializer();
	private DataClassifier  classifier;
	private Predictor predictor;
	private DataEvaluator evaluator;
	private DataExperimenter experiment;

	private static long startTime = System.currentTimeMillis();

	public EntryPoint(){}

	public static void main(String[] args){ 
		EntryPoint workflow = new EntryPoint();
		
		String path = new java.io.File("").getAbsolutePath();
		System.out.println("The path is: "+path+"\n\n\n\n");
		
		try {
			workflow.start(args);
		} catch (Exception e) {
			// do nothing, the error message are already print out
		}
		long endTime = System.currentTimeMillis();
		System.out.println("It took " + (endTime - startTime) + " milliseconds");
	}

	public void start(String[] args) throws Exception{
		List<String> input = Arrays.asList(args);
		boolean something = false;

		if(input.contains("-pred")){
			something = true;
			pred(args);
		}else{
			// this.load(args[args.length - 1]); dentro gli if altrimenti non stampa i flag
			if(input.contains("-print") || input.contains("-save") || input.contains("-ser")){
				this.load(args[args.length - 1]);
				something = true;
				ser_print_save(args, input);
			}
			if(input.contains("-cross")){
				this.load(args[args.length - 1]);
				something = true;
				cross(args, input);
			}
			if(input.contains("-wekaExp")){
				this.load(args[args.length - 1]);
				something = true;
				wekaExp(input);
			}
		}

		if(!something){
			print_avaiable_flag();
		}
	}

	public void wekaExp(List<String> input) throws Exception{
		
		// create a list of dataset that contain just the one specify in the config file
		DefaultListModel<File> model = new DefaultListModel<File>();
		File file = new File(configuration.gatPathDataset());
		model.addElement(file);
		
		experiment = new DataExperimenter(classifiers, model, configuration.getPath_for_result());
		String exptype = "classification";
		String splittype = "crossvalidation";
		int folds = 10;
		boolean randomized = false;
		double percentage = 66.0;
		int runs = 10;
		
		if(input.contains("-exptype")){
			exptype = input.get(input.indexOf("-exptype") + 1);

			if(!exptype.equals("classification") && !exptype.equals("regression")){
				System.out.println("WARNING: the exptype value wasn't valid, the default value will be used (classification)");
				exptype = "classification";
			}
		}
		
		if(input.contains("-splittype")){
			splittype = input.get(input.indexOf("-splittype") + 1);

			if(!splittype.equals("crossvalidation") && !splittype.equals("randomsplit")){
				System.out.println("WARNING: the splittype value wasn't valid, the default value will be used (crossvalidation)");
				splittype = "crossvalidation";
			}
		}
		
		try{
			if(input.contains("-folds")){
				folds = Integer.parseInt(input.get(input.indexOf("-folds")+1));
			}	
		}catch(Exception e){
			System.out.println("WARNING: the fold value wasn't valid, the default value will be used (10)");
			folds = 10;
		}
		
		if(input.contains("-randomized")){
			randomized = true;
		}
		
		try{
			if(input.contains("-percentage")){
				percentage = Double.parseDouble(input.get(input.indexOf("-percentage") + 1));
			}
		}catch(Exception e){
			System.out.println("WARNING: the percentage value wasn't valid, the default value will be used (66.0)");
			percentage = 66.0;
		}
		
		try{
			if(input.contains("-runs")){
				runs = Integer.parseInt(input.get(input.indexOf("-runs") + 1));
			}
		}catch(Exception e){
			System.out.println("WARNING: the runs value wasn't valid, the default value will be used (10)");
			runs = 10;
		}
		
		String result = experiment.experiment(exptype, splittype, folds, randomized, percentage, runs);
		System.out.println(result);
	}

	private void cross(String[] args, List<String> input) throws Exception {
		System.out.println("Crossvalidating...");
		int fold = 10;
		int seed = 1;
		boolean printOnFile = false;

		if(input.contains("-fold")){
			try{
				int index_fold = input.indexOf("-fold") + 1;
				fold = Integer.parseInt(input.get(index_fold));
				fold = Math.abs(fold);
			}catch(Exception e){
				fold = 10;
				System.out.println("WARNING: the number of fold are not correct specified, the default number (10) will be used");
			}
		}
		if(input.contains("-seed")){
			try{
				int index_seed = input.indexOf("-seed") + 1;
				seed = Integer.parseInt(input.get(index_seed));
				seed = Math.abs(seed);
			}catch(Exception e){
				seed = 1;
				System.out.println("WARNING: the number of seed are not correct specified, the default number (1) will be used");
			}
		}
		if(configuration.getPath_for_result() != null){
			printOnFile = true;
		}

		this.crossValidation(fold, seed, printOnFile);
	}

	private void print_avaiable_flag() {
		System.out.println("No valid operation selected, please use:");
		System.out.println("-ser for serialize the classifier specified in the configuaration file");
		System.out.println("-print for print the human-readable result of classification");
		System.out.println("-save for save the human-readable result of classification");
		System.out.println("-pred for predict the class of a new dataset");
		System.out.println("-cross for using the cross validation for classify and evaluate");
		System.out.println("-wekaExp for perorm a experiment (as intended by weka)");
		System.out.println("\n For more information on how to use it please read the README.md");
	}

	private void ser_print_save(String[] args, List<String> input) throws Exception {
		this.classify(); 


		if(input.contains("-print"))
			this.printClassifier();

		if(input.contains("-save")){
			System.out.println("Salving the human-readable description of the classifiers...");
			this.saveClassifier();
		}

		if(input.contains("-ser")){
			System.out.println("Serializing the classifiers...");
			this.serialize();
		}
	}

	private void pred(String[] args) throws Exception {
		System.out.println("Predicting...");
		if(args.length == 2)
			this.predict(args[args.length - 1]);
		else
			this.predict(args[args.length - 1], args[args.length - 2]);
	}

	public void load(String path) throws Exception{
		try {
			configuration = new LoaderProperties();
			classifiers = configuration.load(path);
		} catch (Exception e1) {
			System.out.println("ERROR : Invalid or not found property file, please check the path");
			//System.exit(0);
			throw new Exception();
		}
	}

	public void classify(){
		System.out.println("Classifing...");
		classifier = new DataClassifier(configuration.getDataset(), classifiers);
	}

	public void saveClassifier() throws Exception{
		try{	
			classifier.getSummary(configuration.getPath_for_result());
		}catch(Exception e){
			try {
				System.out.println("WARNING: Path not specified or incorrect");
				String path = new java.io.File("").getAbsolutePath();
				
				if(path.contains("\\CodeSmellDetectorML\\CodeSmellDetectorML")){
					classifier.getSummary(path.substring(0, path.lastIndexOf("\\"))+"\\result");
				}else{
					classifier.getSummary(path + "\\result");
				}
				
			} catch (Exception e1) {
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
				serializer.serialize(configuration.getPath_for_result() + "\\" + name + ".model", c);
				pathToPrint = configuration.getPath_for_result();
			}catch(Exception e){
				try {
					String path = new java.io.File("").getAbsolutePath();

					if(path.contains("\\CodeSmellDetectorML\\CodeSmellDetectorML")){
						serializer.serialize(path.substring(0, path.lastIndexOf("\\"))+"\\result" + "\\" + name + ".model", c);
						pathToPrint = path.substring(0, path.lastIndexOf("\\")) + "\\result";
					}else{ 
						serializer.serialize(path+"\\result" + "\\" + name + ".model", c);
						pathToPrint = path + "\\result";
					}

				} catch (Exception e1) {
					System.out.println("ERROR : "+e1.getMessage());
					throw new Exception();
				}
			}
			i++;
		}
		System.out.println("The serialized files were saved in: "+ pathToPrint);
	}

	public void predict(String path) throws Exception{
		configuration = new LoaderProperties();
		ArrayList<String> paths =  configuration.loadForPred(path);
		String	path_dataset =  paths.get(0);
		paths.remove(0);

		for(String s: paths){
			predWithOneClassifier(path_dataset, s);
		}
	}

	private void predWithOneClassifier(String path_dataset, String path_serialized) throws Exception{
		Classifier c = null;
		try {
			c = serializer.read(path_serialized);
		}catch(Exception e){
			printErrorPred();
		}

		predictAndPrint(path_dataset, c);
	}

	public void predict(String path_1, String path_2) throws Exception{
		String path_dataset = path_1;
		String path_serialized = path_2;
		Classifier c = null;
		try{
			c = serializer.read(path_serialized);
		}catch(Exception e1){
			String temp = path_dataset;
			path_dataset = path_serialized;
			path_serialized = temp;
			try {
				c = serializer.read(path_serialized);
			} catch(Exception e){
				printErrorPred();
			} 
		}

		predictAndPrint(path_dataset, c);
	}

	private void predictAndPrint(String path_dataset, Classifier c) throws Exception {
		//predict
		DatasetHandler dataset = new DatasetHandler(path_dataset);
		predictor = new Predictor(dataset.getDataset());
		DatasetHandler datasetPredicted = predictor.makePredicitions(c, false);

		//print
		String directory = path_dataset.substring(0, path_dataset.lastIndexOf("/")+1);
		String name = path_dataset.substring(path_dataset.lastIndexOf("/") + 1);
		String nameClassifier = c.getClass().getName();
		nameClassifier = nameClassifier.substring(nameClassifier.lastIndexOf(".")).replace(".", "");

		String path = directory + "Predicted_" + nameClassifier + "_" + name;

		//save
		datasetPredicted.toCSV(path);
		System.out.println("The predict model is saved in: " + path);
	}

	private void printErrorPred() throws Exception {
		System.out.println("------------------------------------------------------------------");
		System.out.println("ERROR : the serialized file is incorrect, please check the path ");
		System.out.println("	and make sure that the file is a .model"); 
		System.out.println("------------------------------------------------------------------");
		//System.exit(0);
		throw new Exception();
	}

	public void crossValidation(int fold, int seed, boolean printOnFile) throws Exception{
		String message = "";
		evaluator = new DataEvaluator(configuration.getDataset());
		for(Classifier c: classifiers){
			message +=  "_____"+c.getClass().getName()+ "_____" + evaluator.crossValidation(c, fold, seed)+"\n";
		}
		if(printOnFile){
			System.out.println("Saving the result of the cross validation...");
			try{
				PrintWriter writer;
				String path = configuration.getPath_for_result().replaceAll(" ", "");
				writer = new PrintWriter(path + "\\" + "CrossValidation_Result(rename_this_file).txt", "UTF-8");
				writer.println(message);
				writer.close();
				System.out.println("The result of the cross validation were saved in "+path + "\\" + "CrossValidation_Result(rename_this_file).txt");
			}catch(Exception e){
				System.out.println("ERROR: Unable to save the result on the specified path");
				e.printStackTrace();
			}
		}else{
			System.out.println(message);
		}
	}


}
