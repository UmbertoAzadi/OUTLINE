package it.unimib.disco.essere.CodeSmellDetectorML;

import weka.core.converters.ConverterUtils.DataSource;
import weka.core.converters.CSVSaver;
import weka.core.converters.ArffSaver;
import weka.core.OptionHandler;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Properties;
import weka.classifiers.Classifier;
import weka.classifiers.SingleClassifierEnhancer;
import weka.classifiers.meta.AdaBoostM1;
import java.lang.ArrayIndexOutOfBoundsException;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import weka.core.Instances;

public class LoaderProperties {
	
	private String path_for_result;
	private Instances dataset;
	private static String[] PATH_CLASSIFIER= {"weka.classifiers.trees.", 
											  "weka.classifiers.bayes.", 
											  "weka.classifiers.functions.",
											  "weka.classifiers.rules.",
											  "weka.classifiers.lazy.",
											  "weka.classifiers.misc."};
	
	public LoaderProperties(){}
	
	public void loadDataset(String path){
		try{
			DataSource source = new DataSource(path);
			dataset = source.getDataSet();
			dataset.setClassIndex(dataset.numAttributes()-1);
		}catch(Exception e){
			System.out.println("Unable to load the dataset, please make sure that the path is correct");
			e.printStackTrace();
		}
	}

	public Instances getDataset(){
		return dataset;
	}
	
	public String getPath_for_reusult(){
		return this.path_for_result;
	}
	
	public String toString(){
		return dataset.toSummaryString();
	}
	
	public void toCSV(String path){
		CSVSaver saver = new CSVSaver();
		saver.setInstances(dataset);
		try {
			saver.setFile(new File(path));
			saver.writeBatch();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	public void toArff(String path){
		ArffSaver saver = new ArffSaver();
		saver.setInstances(dataset);
		try{
			saver.setFile(new File(path));
			saver.writeBatch();
		}catch(IOException e){
			e.printStackTrace();
		}
	}
	
	public ArrayList<Classifier> loadProperties(String path_properties){
		ArrayList<Classifier> classifiers = new ArrayList<Classifier>();
		Properties properties = new Properties();
		
		// LEGGO
		BufferedReader in;
		try {
			in = new BufferedReader(new FileReader(path_properties));
			 properties.load(in);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		// CARICO IL DATASET
		String path_dataset = properties.getProperty("dataset");
		this.loadDataset(path_dataset);
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
	
	public Classifier extractClassifier(String key, String elem){
		
		String[] parse_key = key.split("_");
		String name = parse_key[1];
		boolean boost = false;
		boolean other_ensemble_method = false;
		OptionHandler oh = null;
		String options_ensamble_method = "";
		
		if(!parse_key[0].equals("")){
			if(parse_key[0].toUpperCase().equals("BOOSTED")){
				boost = true;
			}else{
				other_ensemble_method = true;
			}
		}
	    
	    // CONTROLLO CHE IL CAMPO CLASSE SIA VALIDO
	    int i = 0;
	    while(oh == null && i < LoaderProperties.PATH_CLASSIFIER.length){
	    	oh = this.findClass(name, LoaderProperties.PATH_CLASSIFIER[i]);
	    	i++;
	    }
	    
	    if(!elem.equals("")){
	    	String temp = elem.replaceAll(" ", "");
    		elem = temp.substring(1, temp.length()-1);
	    	if(boost || other_ensemble_method){
	    		String[] separate_option = elem.split(";");
	    		
	    		try{
	    			elem =  separate_option[0];
	    		}catch(ArrayIndexOutOfBoundsException e){
	    			elem = "";
	    		}
	    		
	    		try{
	    			options_ensamble_method = separate_option[1];
	    		}catch(ArrayIndexOutOfBoundsException e){
	    			options_ensamble_method = "";
	    		}
	    	}
	    }
	    this.addOptions(oh, elem.split(","));
	    
	    // UPCASTING DA OptionHandler A Object E DOWNCASTING DA Object A Classifier
	    Object o = (Object) oh;
	    Classifier c = null;
	    if(boost){
	    	AdaBoostM1 temp = new AdaBoostM1();
	    	this.addOptions(temp, options_ensamble_method.split(","));
			temp.setClassifier((Classifier) o);
	    	c = temp;
		}else{
			if(other_ensemble_method){
				OptionHandler oh_ensemble = this.findClass(parse_key[0], "weka.classifiers.meta.");
				this.addOptions(oh_ensemble, elem.split(","));
				SingleClassifierEnhancer oh_ens = (SingleClassifierEnhancer) oh_ensemble;
				oh_ens.setClassifier((Classifier) o);
				c = oh_ens;
			}else{
				c = (Classifier) o; // <<<<<<< NON HO TROVATO ALTRO MODO!!!!!
			}
		}
	    
	    return c;
	}
	
	private OptionHandler findClass(String name, String path){
		OptionHandler o = null;
		try {
			o = (OptionHandler) Class.forName(path + name).newInstance();
		} catch (InstantiationException e1) {
			e1.printStackTrace();
		} catch (IllegalAccessException e1) {
			e1.printStackTrace();
		} catch (ClassNotFoundException e1) {}
		
		return o;
	}
	
	private void addOptions(OptionHandler o, String[] options){
		try {
			o.setOptions(options);
		} catch (Exception e) {
			System.out.println("ERROR DURING ADDING OPTIONS");
			e.printStackTrace();
		}
	} 

}
