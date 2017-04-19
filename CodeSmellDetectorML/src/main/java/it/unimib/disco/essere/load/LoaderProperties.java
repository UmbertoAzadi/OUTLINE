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
	 
	private Properties readProperties(String path_properties) throws Exception{
		Properties properties = new Properties();

		BufferedReader in;
		in = new BufferedReader(new FileReader(path_properties));
		properties.load(in);
		
		return properties;
	}
	
	public ArrayList<Classifier> load(String path_properties) throws Exception{
		ArrayList<Classifier> classifiers = new ArrayList<Classifier>();
		Properties properties = null;

		properties = this.readProperties(path_properties);

		// CARICO IL DATASET
		String path_dataset = properties.getProperty("dataset");
		dataset = new DatasetHandler(path_dataset);
		properties.remove("dataset");
		
		path_for_result = properties.getProperty("path");
		properties.remove("path");
	
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
	
	public Classifier extractClassifier(String key, String element) throws Exception{
		
		OptionHandler oh = null;
		
		String classifier = key;
			
		// CONTROLLO CHE IL CLASSIFICATORE SIA VALIDO
		oh = this.findClass(classifier);
		
		if(oh == null){
			element = element + " ";
			classifier = element.split(" ")[0];
			element = element.substring(element.indexOf(" "));
			
			// CONTROLLO CHE IL CLASSIFICATORE SIA VALIDO
			oh = this.findClass(classifier);
		}

		this.checkNotNull(oh, "Classifier", classifier);
	    
	    if(!element.equals("")){
	    	String[] option = element.split(" ");
	    	this.addOptions(oh, option);
	    }
	    
	    return (Classifier) oh;
	} 

	@Override
	public ArrayList<String> loadForPred(String path) throws Exception {
		Properties properties = new Properties();
		properties = this.readProperties(path);
		ArrayList<String> result = new ArrayList<String>();
		
		String path_dataset = properties.getProperty("dataset");
		properties.remove("dataset");
		
		// ESTRAGGO E ITERO SULLE PROPERIETA'
		Enumeration<?> e =  properties.propertyNames();
		while (e.hasMoreElements()) {
			String key = (String) e.nextElement();
			String classifier = properties.getProperty(key);
			result.add(classifier);
		}
		
		result.add(0, path_dataset);
		
		return result;
	}
	
	
}
