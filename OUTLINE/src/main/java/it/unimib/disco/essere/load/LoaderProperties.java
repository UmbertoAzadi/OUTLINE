package it.unimib.disco.essere.load;

import weka.core.OptionHandler;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Properties;

import it.unimib.disco.essere.core.DatasetHandler;
import weka.classifiers.Classifier;

import java.io.BufferedReader;
import java.io.FileReader;

public class LoaderProperties extends Loader{
	
	public LoaderProperties(){
		super();
	}
	 
	private Properties readProperties(String pathProperties) throws Exception{
		Properties properties = new Properties();

		BufferedReader in;
		in = new BufferedReader(new FileReader(pathProperties));
		properties.load(in);
		
		return properties;
	}
	
	@Override
	public ArrayList<Classifier> loadForClassification(String path_properties) throws Exception{
		ArrayList<Classifier> classifiers = new ArrayList<Classifier>();
		Properties properties = this.readProperties(path_properties);

		// CARICO IL DATASET
		String pathDataset = properties.getProperty("dataset");
		dataset = new DatasetHandler(pathDataset);
		properties.remove("dataset");
		
		pathForResult = properties.getProperty("path");
		
		if(pathForResult != null){
			char lastElem = pathForResult.charAt(pathForResult.length() - 1);
			properties.remove("path");
	
			while(lastElem == ' '){
				pathForResult = pathForResult.substring(0, pathForResult.length() - 1);
				lastElem = pathForResult.charAt(pathForResult.length() - 1);
			}
		}
		
		// ESTRAGGO E ITERO SULLE PROPERIETA'
		Enumeration<?> e =  properties.propertyNames();
		while (e.hasMoreElements()) {
			String key = (String) e.nextElement();
			String elem = properties.getProperty(key);
			Classifier c = this.extractClassifier(key, elem);
		    classifiers.add(c);
		}
		
		return classifiers;
	}
	
	public Classifier extractClassifier(String key, String elem) throws Exception{
		
		String element = elem;
		String classifier = key;
		
		// CONTROLLO CHE IL CLASSIFICATORE SIA VALIDO
		OptionHandler oh = this.findClass(classifier);
		
		if(oh == null){
			element = element + " ";
			classifier = element.split(" ")[0];
			element = element.substring(element.indexOf(' '));
			
			// CONTROLLO CHE IL CLASSIFICATORE SIA VALIDO
			oh = this.findClass(classifier);
			
		}

		this.checkNotNull(oh, "Classifier", classifier);
		
	    if(!"".equals(element)){
	    	//String[] option = element.split(" ");
	    	this.addOptions(oh, element);
	    }
	    
	    return (Classifier) oh;
	} 

	@Override
	public ArrayList<String> loadForPrediction(String path) throws Exception {
		Properties properties = this.readProperties(path);
		ArrayList<String> result = new ArrayList<String>();
		
		String pathDataset = properties.getProperty("dataset");
		properties.remove("dataset");
		
		String labels = properties.getProperty("labels");
		properties.remove("labels");
		
		// ESTRAGGO E ITERO SULLE PROPERIETA'
		Enumeration<?> e =  properties.propertyNames();
		while (e.hasMoreElements()) {
			String key = (String) e.nextElement();
			String classifier = properties.getProperty(key);
			result.add(classifier);
		}
		
		result.add(0, pathDataset);
		result.add(1, labels);
		
		return result; 
	}
	
	
}
