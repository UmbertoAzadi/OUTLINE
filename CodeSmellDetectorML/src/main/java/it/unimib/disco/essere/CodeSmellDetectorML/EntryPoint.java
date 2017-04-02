package it.unimib.disco.essere.CodeSmellDetectorML;


import weka.classifiers.Classifier;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;



public class EntryPoint {
	
	private ArrayList<Classifier> classifiers;
	private LoaderProperties configuration = new LoaderProperties();;
	private Serializer serializer = new Serializer();
	private DataClassifier  classifier;
	private Predictor predictor;
	
	public static void main(String[] args) { 
		
		List<String> input = Arrays.asList(args);
		EntryPoint workflow = new EntryPoint();
		
		if(input.contains("-pred")){
			if(args.length == 2)
				workflow.predict(args[args.length - 1]);
			else
				workflow.predict(args[args.length - 1], args[args.length - 2]);
			
		}else{
		
			workflow.load(args[args.length - 1]);
			workflow.classify();
		
			if(input.contains("-print"))
				workflow.printClassifier();
		
			if(input.contains("-save"))
				workflow.saveClassifier();
			
			if(input.contains("-ser"))
				workflow.serialize();
		}
		
	}
	
	public void load(String path){
		classifiers = configuration.loadProperties(path);
	}
	
	public void classify(){
		classifier = new DataClassifier(configuration.getDataset(), classifiers);
	}
	
	public void saveClassifier(){
		try{	
			classifier.getSummary(configuration.getPath_for_result());
		}catch(Exception e){
			try {
				System.out.println("Path not specified or incorrect");
				String path = new java.io.File("").getAbsolutePath();
				classifier.getSummary(path.substring(0, path.lastIndexOf("\\"))+"\\result");
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		}
	}
	
	public void printClassifier(){
		System.out.println(classifier.getSummary());
	}
	
	public void serialize(){
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
					serializer.serialize(path.substring(0, path.lastIndexOf("\\"))+"\\result" + "\\" + name + ".model", c);
					pathToPrint = path.substring(0, path.lastIndexOf("\\"))+"\\result";
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			}
			i++;
		}
		System.out.println("The serialized files were saved in: "+ pathToPrint);
	}
	
	public void predict(String path){

		String[] data = configuration.loadPropertyForPrediction(path);
		String path_dataset = data[0];
		String path_serialized = data[1];
		Classifier c = null;
		try {
			c = serializer.read(path_serialized);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		DatasetHandler dataset = new DatasetHandler(path_dataset);
		predictor = new Predictor(dataset.getDataset());
		DatasetHandler datasetPredicted = predictor.makePredicitions(c);
		
		datasetPredicted.toCSV(path_dataset);
	}
	
	public void predict(String path_1, String path_2){
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
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		DatasetHandler dataset = new DatasetHandler(path_dataset);
		predictor = new Predictor(dataset.getDataset());
		DatasetHandler datasetPredicted = predictor.makePredicitions(c);
		
		datasetPredicted.toCSV(path_dataset);
	}
}
