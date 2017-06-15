package it.unimib.disco.essere.core;

import weka.core.Instances;
import weka.core.SetupGenerator;
import weka.core.setupgenerator.AbstractParameter;

import java.io.PrintWriter;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.logging.Logger;

import it.unimib.disco.essere.load.LoaderProperties;
import weka.classifiers.Classifier;
import weka.classifiers.SingleClassifierEnhancer;
import weka.classifiers.meta.GridSearch;
import weka.classifiers.meta.MultiSearch;


public class DataClassifier {

	private static final Logger LOGGER = Logger.getLogger(DataClassifier.class.getName());

	private Instances dataset;
	private List<Classifier> classifiers;

	public DataClassifier(Instances data){
		this.dataset = data;
		this.classifiers = null;
	}
	
	public DataClassifier(Instances data, List<Classifier> classifiers) throws Exception{
		this.dataset = data;
		this.classifiers = classifiers;

		for(Classifier c: classifiers){
			this.buildClassifier(c);
		}
	}

	public DataClassifier(LoaderProperties data, List<Classifier> classifiers) throws Exception{
		this(data.getDataset(), classifiers);
	}

	public Instances getDataset() {
		return dataset;
	}

	public List<Classifier> getClassifiers() {
		return classifiers;
	}

	public void buildClassifier(Classifier c){
		try {
			c.buildClassifier(dataset);
		} catch (Exception e) {
			LOGGER.severe("Unable to apply " + c.getClass().getName() + ": " + e);
			e.printStackTrace();
		}
	}

	public String getSummary(){
		StringBuilder summary = new StringBuilder();
		for(Classifier c: classifiers){
			summary.append(c.toString() + "\n\n");
		}	
		return summary.toString();
	}

	public void getSummary(String _path) throws Exception{
		Path path = Paths.get(_path); 
		int indexForFile = 1;
		for(Classifier c: classifiers){
			String name = this.generateNameForFile(c, indexForFile);
			PrintWriter writer;
			writer = new PrintWriter(path + "\\" + name +".txt", "UTF-8");
			writer.println(c.toString()+"\n\n");
			writer.close();
			indexForFile++;
		}

		System.out.println("The files were saved in: "+_path);

	}

	public String generateNameForFile(Classifier c, int indexForFile){
		String nameClassifier = c.getClass().getName();
		nameClassifier = nameClassifier.substring(nameClassifier.lastIndexOf('.')).replace(".", "");
		String name =  indexForFile + "_" + dataset.classAttribute().name() + "_" + nameClassifier;

		try{
			SingleClassifierEnhancer ens = (SingleClassifierEnhancer) c;
			String nameClassIfEns = ens.getClassifier().getClass().getName();
			String nameClassIfEns2  = nameClassIfEns.substring(nameClassIfEns.lastIndexOf('.')).replace(".", "");
			name += "_" + nameClassIfEns2;
		}catch(ClassCastException e){
			// QUI SI ENTRA SE IL CLASSIFICATORE NON PUO' ESSERE CASTATO A SingleClassifierEnhancer
			// IN TAL CASO IL CLASSIFICATORE NON E' UN ENSEMBLE E QUINDI SI CONTINUA IGNORANDO QUESTA
			// SEZIONE
		}

		return name;
	}
}
