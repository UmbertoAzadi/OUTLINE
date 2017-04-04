package it.unimib.disco.essere.CodeSmellDetectorML;

import weka.core.OptionHandler;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Properties;
import weka.classifiers.Classifier;
import weka.classifiers.SingleClassifierEnhancer;
import weka.classifiers.meta.AdaBoostM1;
import java.lang.ArrayIndexOutOfBoundsException;

import java.io.BufferedReader;
import java.io.FileReader;
import weka.core.Instances;

public class LoaderProperties {
	
	private String path_for_result;
	private DatasetHandler dataset;
	private static String[] PATH_CLASSIFIER= {"weka.classifiers.trees.", 
											  "weka.classifiers.bayes.", 
											  "weka.classifiers.functions.",
											  "weka.classifiers.rules.",
											  "weka.classifiers.lazy.",
											  "weka.classifiers.misc."};
	
	public LoaderProperties(){}
	
	public String getPath_for_result(){
		return this.path_for_result;
	}
	
	public Instances getDataset(){
		return dataset.getDataset();
	}
	
	public Properties readProperties(String path_properties) {
		Properties properties = new Properties();
		try{
			BufferedReader in;
			in = new BufferedReader(new FileReader(path_properties));
			properties.load(in);
		}catch(Exception e){
			System.out.println("ERROR : Invalid or not found property file, please check the path");
			System.exit(0);
		}
		return properties;
	}
	
	public ArrayList<Classifier> loadProperties(String path_properties){
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
	
	public Classifier extractClassifier(String key, String elem){
		
		
		String[] parse_key = key.split("_");
		String name = "";
		
		try{	
			name = parse_key[1];
		}catch(ArrayIndexOutOfBoundsException e){
			this.checkNotNull(null, "", "");
		}
		
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
	    
	    // CONTROLLO CHE IL CLASSIFICATORE SIA VALIDO
	    int i = 0;
	    while(oh == null && i < LoaderProperties.PATH_CLASSIFIER.length){
	    	oh = this.findClass(name, LoaderProperties.PATH_CLASSIFIER[i]);
	    	
	    	i++;
	    }
	    this.checkNotNull(oh, "Classefier", name);
	    
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
				this.checkNotNull(oh_ensemble, "Ensemble method", parse_key[0]); 
				this.addOptions(oh_ensemble, elem.split(","));
				SingleClassifierEnhancer oh_ens = (SingleClassifierEnhancer) oh_ensemble;
				oh_ens.setClassifier((Classifier) o);
				c = oh_ens;
			}else{
				c = (Classifier) o; 
			}
		}
	    
	    return c;
	}
	
	private OptionHandler findClass(String name, String path){
		OptionHandler o = null;
		
			try {
				o = (OptionHandler) Class.forName(path + name).newInstance();
			} catch (InstantiationException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (ClassNotFoundException e) {}
		
		return o;
	}
	
	private void addOptions(OptionHandler o, String[] options){
		try {
			o.setOptions(options);
		} catch (Exception e) {
			System.out.println("--------------------------------------------------");
			System.out.print("ERROR : these options are incorrect: [");
			for(String s : options)
				System.out.print(s + ", ");
			System.out.println("]");
			System.out.println( "	plese check them in the weka documentation");
			System.out.println("--------------------------------------------------");
			System.exit(0);
		}
	}
	
	public String[] loadPropertyForPrediction(String path){
		Properties properties = new Properties();
		properties = this.readProperties(path);
		
		String path_dataset = properties.getProperty("dataset");
		String path_serialized = properties.getProperty("serialized");
		
		String[] s = {path_dataset, path_serialized};
		return s;
	}
	
	
	private void checkNotNull(Object o, String type, String name){
		
		 /* QUESTO METODO E' NECESSARIO POICHE' LA CHIAMATA A findClass ALLA RIGA 115 E' DENTRO UN WHILE
		    QUINDI IL CONTROLLO VA FATTO DOPO CHE IL CICLO SI E' CONCLUSO */

		if(o == null){
			if(!type.equals("")){
				System.out.println("-------------------------------------------------------------------------------------");
				System.out.println("ERROR : There isn't a " + type + " with the name \"" + name +"\",");
				System.out.println("	check the configuration file and please insert the name of a valid ");
				System.out.println("	" + type + " and remember that the format is:"); 
				System.out.println("	<ensemble method>_<name of the classifier>_<everythigs you want> = <options>");
				System.out.println("	                 ^^^^^^^^^^^^^^^^^^^^^^^^^ ");
				System.out.println("	                 (   required section    )"); 
				System.out.println("-------------------------------------------------------------------------------------");
			}else{
				System.out.println("-------------------------------------------------------------------------------------");
				System.out.println("ERROR : There is an error in the configuration file, please make sure that all the");
				System.out.println("	classifier specified respect this format:");
				System.out.println("	<ensemble method>_<name of the classifier>_<everythigs you want> = <options>");
				System.out.println("	                 ^^^^^^^^^^^^^^^^^^^^^^^^^ ");
				System.out.println("	                 (   required section    )"); 
				System.out.println("-------------------------------------------------------------------------------------");
			}
			System.exit(0);
		}
	}
}
