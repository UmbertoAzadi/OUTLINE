package it.unimib.disco.essere.core;


import weka.classifiers.Classifier;

import java.io.File;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

import javax.swing.DefaultListModel;

import it.unimib.disco.essere.experiment.DataExperimenter;
import it.unimib.disco.essere.load.*;

public class EntryPoint {

	private static final Logger LOGGER = Logger.getLogger(EntryPoint.class.getName());

	private ArrayList<Classifier> classifiers;
	private Loader configuration;
	private Serializer serializer = new Serializer();
	private DataClassifier  classifier;
	// usati solo in un metodo per ora:
	// private Predictor predictor; 
	// private DataEvaluator evaluator;
	// private DataExperimenter experiment;

	private static long startTime = System.currentTimeMillis();

	public EntryPoint(){
		/* Default constructor for the API*/
	}

	public static void main(String[] args) throws Exception{ 
		EntryPoint workflow = new EntryPoint();
		try {
			workflow.start(args);
		} catch (Exception e) {
			LOGGER.severe("Program fail because: " + e);
			// do nothing, the error message are already print out
		}
		long endTime = System.currentTimeMillis();
		String time = String.format("Total time in sec: %1$s", (endTime - startTime)/1000);
		LOGGER.info(time);
		//LOGGER.info(String.format("Total time in sec: %1$s", (endTime - startTime)/1000));
		
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
				serPrintSave(input);
			}
			if(input.contains("-cross")){
				this.load(args[args.length - 1]);
				something = true;
				cross(input);
			}
			if(input.contains("-wekaExp")){
				this.load(args[args.length - 1]);
				something = true;
				wekaExp(input);
			}
			if(input.contains("-customExp")){
				this.load(args[args.length - 1]);
				something = true;
				customExp(args);
			}
		}

		if(!something){
			printAvaiableFlag();
		}
	}
	
	public void customExp(String[] args) throws Exception{

		String[] custom = {"-exptype", "custom", "-splittype", "custom", args[args.length - 1]};
		String[] newArgs = new String[args.length + custom.length - 1];
		System.arraycopy(args, 0, newArgs, 0, args.length);
		System.arraycopy(custom, 0, newArgs, args.length - 1, custom.length);

		wekaExp(Arrays.asList(newArgs));
	}

	public void wekaExp(List<String> input) throws Exception{

		// create a list of dataset that contain just the one specify in the config file
		DefaultListModel<File> model = new DefaultListModel<File>();
		File file = new File(configuration.getDatasetHandler().getPath());
		model.addElement(file);

		DataExperimenter experiment = new DataExperimenter(classifiers, model, configuration.getPathForResult());
		String exptype = DataExperimenter.getDefaultExptype();
		String splittype = DataExperimenter.getDefaultSplittype();
		int folds = DataExperimenter.getDefaultFolds();
		boolean randomized = DataExperimenter.isDefaultRandomized();
		double percentage = DataExperimenter.getDefaultPercentage();
		int runs = DataExperimenter.getDefaultRuns();
		boolean printOnFile = false;

		if(input.contains("-exptype")){
			exptype = input.get(input.indexOf("-exptype") + 1);

			if(!experiment.exptypeCheckValue(exptype)){
				String warning = "the exptype value wasn't valid, the default value will be used (" + DataExperimenter.getExptypeValues()[0] + ")"; 	 
				LOGGER.warning(warning);
				exptype = DataExperimenter.getDefaultExptype();
			}
		}

		if(input.contains("-splittype")){
			splittype = input.get(input.indexOf("-splittype") + 1);

			if(!experiment.splittypeCheckValue(splittype)){  
				String warning = "the splittype value wasn't valid, the default value will be used (" + DataExperimenter.getDefaultSplittype() + ") ";
				LOGGER.warning(warning);
				splittype = DataExperimenter.getDefaultSplittype();
			}
		}

		try{
			if(input.contains("-folds")){
				folds = Integer.parseInt(input.get(input.indexOf("-folds")+1));
			}	
		}catch(Exception e){
			LOGGER.warning("the fold value wasn't valid [" + e + "], the default value will be used (" 
					+ DataExperimenter.getDefaultFolds()+ ") ");
			folds = DataExperimenter.getDefaultFolds();
		}

		if(input.contains("-randomized")){
			randomized = true;
		}

		try{
			if(input.contains("-percentage")){
				percentage = Double.parseDouble(input.get(input.indexOf("-percentage") + 1));
			}
		}catch(Exception e){
			LOGGER.warning("the percentage value wasn't valid [" + e + "], the default value will be used (" 
					+ DataExperimenter.getDefaultPercentage()+ ") ");
			percentage = DataExperimenter.getDefaultPercentage();
		}

		try{
			if(input.contains("-runs")){
				runs = Integer.parseInt(input.get(input.indexOf("-runs") + 1));
			}
		}catch(Exception e){
			LOGGER.warning("the runs value wasn't valid [" + e + "], the default value will be used (" 
					+ DataExperimenter.getDefaultRuns() + ")");
			runs = DataExperimenter.getDefaultRuns();
		}

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

	private void cross(List<String> input) throws Exception {
		LOGGER.info("Crossvalidating...");
		int fold = 10;
		int seed = 1;
		boolean printOnFile = false;

		if(input.contains("-fold")){
			try{
				int indexFold = input.indexOf("-fold") + 1;
				fold = Integer.parseInt(input.get(indexFold));
				fold = Math.abs(fold);
			}catch(Exception e){
				fold = 10;
				LOGGER.warning(" the number of fold are not correct specified [" + e + "], the default number (10) will be used");
			}
		}
		if(input.contains("-seed")){
			try{
				int indexSeed = input.indexOf("-seed") + 1;
				seed = Integer.parseInt(input.get(indexSeed));
				seed = Math.abs(seed);
			}catch(Exception e){
				seed = 1;
				LOGGER.warning("the number of seed are not correct specified [" + e + "], the default number (1) will be used");
			}
		}
		if(configuration.getPathForResult() != null){
			printOnFile = true;
		}

		this.crossValidation(fold, seed, printOnFile);
	}

	private void printAvaiableFlag() {
		LOGGER.severe("No valid operation selected, please use:\n"
				+"-ser for serialize the classifier specified in the configuaration file\n"
				+"-print for print the human-readable result of classification\n"
				+"-save for save the human-readable result of classification\n"
				+"-pred for predict the class of a new dataset\n"
				+"-cross for using the cross validation for classify and evaluate\n"
				+"-wekaExp for perform an experiment (as intended by weka)\n"
				+"-customExp for perform an experiment (as intended by weka), but customized for make it faster\n"
				+"\n For more information on how to use it please read the README.md\n");
	}

	private void serPrintSave(List<String> input) throws Exception {
		this.classify(); 


		if(input.contains("-print"))
			this.printClassifier();

		if(input.contains("-save")){
			LOGGER.info("Salving the human-readable description of the classifiers...");
			this.saveClassifier();
		}

		if(input.contains("-ser")){
			LOGGER.info("Serializing the classifiers...");
			this.serialize();
		}
	}

	private void pred(String[] args) throws Exception {
		LOGGER.info("Predicting...");
		if(args.length == 2)
			this.predict(args[args.length - 1]);
		else
			this.predict(args[args.length - 1], args[args.length - 2]);
	}

	public void load(String path) throws Exception{
		try {
			configuration = new LoaderProperties();
			classifiers = configuration.load(path);
		} catch (Exception e) {
			LOGGER.severe("Invalid or not found property file, please check the path! [" + e + "]");
			throw new Exception();
		}
	}

	public void classify(){
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

				if(path.contains("\\CodeSmellDetectorML\\CodeSmellDetectorML")){
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

					if(path.contains("\\CodeSmellDetectorML\\CodeSmellDetectorML")){
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

	public void predict(String path) throws Exception{
		configuration = new LoaderProperties();
		ArrayList<String> paths =  configuration.loadForPred(path);
		String	pathDataset =  paths.get(0);
		paths.remove(0);

		for(String s: paths){
			predWithOneClassifier(pathDataset, s);
		}
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

	public void crossValidation(int fold, int seed, boolean printOnFile) throws Exception{
		DataEvaluator evaluator = new DataEvaluator();
		StringBuilder message = new StringBuilder();
		for(Classifier c: classifiers){
			message.append("_____"+c.getClass().getName()+ "_____" + evaluator.crossValidation(configuration.getDataset(), c, fold, seed)+"\n");
		}
		if(printOnFile){
			printInConfiPath(message.toString(), "Saving the result of the cross validation...", "CrossValidation_Result", ".txt");
		}else{
			System.out.println(message.toString());
		}
	}

	private void printInConfiPath(String textToPrint, String messageToShow, String nameOfTheFile, String extensionWithPoint) throws Exception {
		System.out.println(messageToShow);
		try{
			//PrintWriter writer = null;
			String path = configuration.getPathForResult();

			String name = "\\" + nameOfTheFile + extensionWithPoint;
			File f = new File(path + name);
			int i = 0;

			while(f.exists()) { 
				name = "\\" + nameOfTheFile + "_" + i + extensionWithPoint;
				f = new File(path + name);
				i++;
			}

			try(PrintWriter writer = new PrintWriter(path + name, "UTF-8")){
				writer.println(textToPrint);
			}catch(Exception e){
				LOGGER.severe("Unable to save the result:" + e.getMessage());
				throw e;
			}

			System.out.println("The result of the cross validation were saved in "+path + name);
		}catch(Exception e){
			LOGGER.severe("Unable to save the result on the specified path");
			throw e;
		}
	}


}
