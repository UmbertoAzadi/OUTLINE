package it.unimib.disco.essere.CodeSmellDetectorML;


import weka.classifiers.Classifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;



public class EntryPoint {
	
	private ArrayList<Classifier> classifiers;
	private LoaderProperties configuration;
	private Serializer serializer;
	DataClassifier  classifier;
	
	public static void main(String[] args) { 
		
		List input = Arrays.asList(args);
		EntryPoint workflow = new EntryPoint();
		
		workflow.load(args[args.length - 1]);
		workflow.classify();
		
		String s;
		
		if(input.contains("-print")){
			workflow.printClassifier();
		}
		
		if(input.contains("-save")){
			workflow.saveClassifier();
		}
			
		if(input.contains("-ser"))
			workflow.serialize();
		
		//if(input.contains("-read")){}
		
		/*
		
		
		Predictor DP = new Predictor(dl1);
		SMO smo = new SMO();
		DC.buildClassifier(smo);
		DP.makePredicition(smo).toCSV("C:/Users/uazad/Documents/Progetto/RisultatiPredizioneNew.txt");
		
		Serializer s = new Serializer();
		s.serialize("C:/Users/uazad/Documents/Progetto/ModelloSerializzatoNew", smo);
		
		DP.makePredicition(s.read("C:/Users/uazad/Documents/Progetto/ModelloSerializzatoNew")).toCSV("C:/Users/uazad/Documents/Progetto/RisultatiPredizioneNewSer.txt");
		*/
		//ArrayList<Classifier> list = data.readProperties("C:/Users/uazad/Documents/Progetto/ProvaConfigurazione.properties");
	
	}
	
	public void load(String path){
		configuration = new LoaderProperties();
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
				serializer.serialize(configuration.getPath_for_result() + "\\" + name + ".bsi", c);
				pathToPrint = configuration.getPath_for_result();
			}catch(Exception e){
				try {
					String path = new java.io.File("").getAbsolutePath();
					serializer.serialize(path.substring(0, path.lastIndexOf("\\"))+"\\result" + "\\" + name + ".bsi", c);
					pathToPrint = path.substring(0, path.lastIndexOf("\\"))+"\\result";
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			}
			i++;
		}
		System.out.println("The serialized files were saved in: "+ pathToPrint);
	}
}
