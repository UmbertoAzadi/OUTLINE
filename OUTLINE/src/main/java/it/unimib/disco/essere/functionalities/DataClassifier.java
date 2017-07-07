package it.unimib.disco.essere.functionalities;

import weka.core.Instances;

import java.io.PrintWriter;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.logging.Logger;

import weka.classifiers.Classifier;
import weka.classifiers.SingleClassifierEnhancer;

/**
 * This class fulfills all the responsibilities that concern the building of classifiers and 
 * it produce the human-readable results.
 * */

public class DataClassifier {

	private static final Logger LOGGER = Logger.getLogger(DataClassifier.class.getName());

	/** The dataset that has to used for building the classifier */
	private Instances dataset;
	
	/** The list of the classifiers that have to be build */
	private List<Classifier> classifiers;
	
	/**
	 * Create an instance that can be used for build one classifier 
	 * (using the buildClassifier(Classifier) method)
	 * 
	 * @param data        the dataset that has to used for building the classifier
	 * */
	public DataClassifier(Instances data) throws Exception{
		this.dataset = data;
		this.classifiers = null;
	}
	
	/**
	 * Create an instance and cycle on the classifier's list and build them.
	 * 
	 * @param data        the dataset that has to used for building the classifier
	 * @param classifiers the list of the classifiers that have to be build
	 * */
	public DataClassifier(Instances data, List<Classifier> classifiers) throws Exception{
		this.dataset = data;
		this.classifiers = classifiers;

		for(Classifier c: classifiers){
			this.buildClassifier(c);
		}
	}

	/** @return the dataset that has been used for building the classifier */
	public Instances getDataset() {
		return dataset;
	}

	/** @return the list of the classifiers that has been builded */
	public List<Classifier> getClassifiers() {
		return classifiers;
	}

	/** Build the classifier with the dataset specified using the constructor
	 *  @param c the classifier that has to be build
	 *  */
	public void buildClassifier(Classifier c){
		try {
			c.buildClassifier(dataset);
		} catch (Exception e) {
			LOGGER.severe("Unable to apply " + c.getClass().getName() + ": " + e);
			e.printStackTrace();
		}
	}

	/**
	 * @return the summary of all the classifier in the list specified using the constructor
	 * */
	public String getSummary(){
		StringBuilder summary = new StringBuilder();
		for(Classifier c: classifiers){
			summary.append(c.toString() + "\n\n");
		}	
		return summary.toString();
	}

	/**
	 * Create a file for each classifier in the list specified using the constructor, and print on it the releted summary
	 * 
	 * @param _path the path where the file will be saved 
	 * */
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

	
	/**
	 * @return the name of the file generated using the name of the classifier and the index in the list specified 
	 * using the constructor
	 * */
	public String generateNameForFile(Classifier classifier, int indexForFile){
		String nameClassifier = classifier.getClass().getName();
		nameClassifier = nameClassifier.substring(nameClassifier.lastIndexOf('.')).replace(".", "");
		String name =  indexForFile + "_" + dataset.classAttribute().name() + "_" + nameClassifier;

		try{
			SingleClassifierEnhancer ens = (SingleClassifierEnhancer) classifier;
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
