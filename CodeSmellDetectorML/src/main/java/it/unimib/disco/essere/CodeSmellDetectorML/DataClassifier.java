package it.unimib.disco.essere.CodeSmellDetectorML;

import weka.core.Instances;
import java.io.PrintWriter;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import weka.classifiers.Classifier;
import weka.classifiers.SingleClassifierEnhancer;


public class DataClassifier {
	
	private Instances dataset;
	private List<Classifier> classifiers;
	
	public DataClassifier(Instances data, List<Classifier> classifiers){
		this.dataset = data;
		this.classifiers = classifiers;
		
		
		for(Classifier c: classifiers){
			this.buildClassifier(c);
		}
	
	}

	public DataClassifier(LoaderProperties data, List<Classifier> classifiers){
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
			System.out.println("Unable to apply " + c.getClass().getName() + ": " + e.getMessage());
		}
	}
	
	public String getSummary(){
		String summary = "";
		for(Classifier c: classifiers){
			summary += c.toString() + "\n\n";
		}	
		return summary;
	}
	
	public void getSummary(String _path) throws Exception{
		Path path = Paths.get(_path); 
		int index_for_file = 1;
		for(Classifier c: classifiers){
			String name = this.generateNameForFile(c, index_for_file);
			PrintWriter writer;
			writer = new PrintWriter(path + "\\" + name +".txt", "UTF-8");
			writer.println(c.toString()+"\n\n");
			writer.close();
			index_for_file++;
		}
		
		System.out.println("The files were saved in: "+_path);
		
	}
	
	public String generateNameForFile(Classifier c, int index_for_file){
		String nameClassifier = c.getClass().getName();
		nameClassifier = nameClassifier.substring(nameClassifier.lastIndexOf(".")).replace(".", "");
		String name =  index_for_file + "_" + dataset.classAttribute().name() + "_" + nameClassifier;
		
		try{
			SingleClassifierEnhancer c_ens = (SingleClassifierEnhancer) c;
			String nameClassIfEns = c_ens.getClassifier().getClass().getName();
			String nameClassIfEns2  = nameClassIfEns.substring(nameClassIfEns.lastIndexOf(".")).replace(".", "");
			name += "_" + nameClassIfEns2;
		}catch(ClassCastException e){
			// QUI SI ENTRA SE IL CLASSIFICATORE NON PUO' ESSERE CASTATO A SingleClassifierEnhancer
			// IN TAL CASO IL CLASSIFICATORE NON E' UN ENSEMBLE E QUINDI SI CONTINUA IGNORANDO QUESTA
			// SEZIONE
		}
		
		return name;
	}
}
