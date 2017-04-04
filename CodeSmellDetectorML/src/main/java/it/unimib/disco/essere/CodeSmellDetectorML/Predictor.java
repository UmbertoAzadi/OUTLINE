package it.unimib.disco.essere.CodeSmellDetectorML;

import weka.classifiers.Classifier;
import weka.core.Instance;
import weka.core.Instances;

public class Predictor {
	Instances dataset;

	public Predictor(Instances dataset_to_be_predict) {
		super();
		this.dataset = dataset_to_be_predict;
	}
	
	public Predictor(LoaderProperties dataset_to_be_predict) {
		this(dataset_to_be_predict.getDataset());
	}
	
	public DatasetHandler makePredicitions(Classifier c){
		
		for (int i = 0; i < dataset.numInstances(); i++) {
			Instance newInst = dataset.instance(i);
			
			/* PER STAMPARE IL CONFRONTO TRA PREDIZIONI E VALORE EFFETTIVO
			double actualClass = dataset.instance(i).classValue();
			String actual = dataset.classAttribute().value((int) actualClass);
			*/
			
			double predicted = 0;
			try {
				predicted = c.classifyInstance(newInst);
			} catch (Exception e) {
				System.out.println("WARNING : Unable to classify the following instances: ");
				System.out.println(newInst.toString());
			}
			
			newInst.setClassValue(predicted);
			
			
			/* PER STAMPARE IL CONFRONTO TRA PREDIZIONI E VALORE EFFETTIVO
			String predClass = dataset.classAttribute().value((int) predicted);
			System.out.println(dataset.instance(i).value(0)+"  :  "+actual + ", " + predClass);
			*/
		}
		
		DatasetHandler result = new DatasetHandler(dataset);
		return result;
	}
	
	
}
