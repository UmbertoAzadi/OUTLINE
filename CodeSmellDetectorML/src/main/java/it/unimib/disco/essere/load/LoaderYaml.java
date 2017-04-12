package it.unimib.disco.essere.load;

import java.io.File;
import java.util.ArrayList;

import org.ho.yaml.Yaml;

import it.unimib.disco.essere.core.DatasetHandler;
import it.unimib.disco.essere.yaml.*;
import weka.classifiers.Classifier;
import weka.classifiers.SingleClassifierEnhancer;
import weka.core.OptionHandler;

public class LoaderYaml extends Loader{

	public LoaderYaml(){
		super();
	}
	
	public ArrayList<Classifier> load(String path_yaml) throws Exception{
		
		YamlConfig config = (YamlConfig) this.parseYaml(path_yaml)[0];
		ArrayList<Classifier> classifiers = new ArrayList<Classifier>();
		
		// CARICO IL DATASET
		this.dataset = new DatasetHandler(config.dataset);
		this.path_for_result = config.path;
		
		for(Classifiers classifier : config.classifiers){
			Classifier c = this.createClassifier(classifier);
		    classifiers.add(c);
		}
		
		return classifiers;
	}
	
	public Classifier createClassifier(Classifiers classifier) throws Exception{
		
		Classifier c = null;
		OptionHandler oh = null;
		OptionHandler oh_ensemble = null;
		
		// CONTROLLO CHE IL CLASSIFICATORE SIA VALIDO
		oh = this.findClass(classifier.name);
	    this.checkNotNull(oh, "Classifier", classifier.name);
	    
	    
	    // AGGIUNGO LE OPZIONI
	    this.addOptions(oh, classifier.options);
	    
	    // GESTISCO L'ENSEMBLE METHOD
	    if(classifier.ensemble != null){
	    	oh_ensemble = this.findClass(classifier.ensemble);
	    	this.checkNotNull(oh_ensemble, "Ensemble method", classifier.ensemble);
	    	this.addOptions(oh_ensemble, classifier.ens_options);
	    	SingleClassifierEnhancer oh_ens = (SingleClassifierEnhancer) oh_ensemble;
			oh_ens.setClassifier((Classifier) oh);
			c = oh_ens;
	    }else{
	    	c = (Classifier) oh;
	    }
	    
	    return c;
	}
	
	public String[] loadForPred(String path) throws Exception{
		YamlPred config = (YamlPred) this.parseYaml(path)[1];
		
		String[] result = {config.dataset, config.serialized};
		
		return result;
	}
	
	private Object[] parseYaml(String path) throws Exception{
		YamlConfig config1 = null;
		YamlPred config2 = null;
		
		try {
			config1 = Yaml.loadType(new File(path), YamlConfig.class);
		} catch (Exception e) {
			try {
				config2 = Yaml.loadType(new File(path), YamlPred.class);
			} catch (Exception e1) { 
				Exception my_exception = new Exception("Not a yaml file");
				throw my_exception;
			}
		}
		
		Object[] result = {config1, config2};
		return result;
	}
	
}
