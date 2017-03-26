package com.unimib.CodeSmellDetectorML;

import weka.classifiers.Classifier;
import weka.core.Instance;
import weka.core.Instances;

public class Predictor {
	Instances dataset;

	public Predictor(Instances dataset_to_be_predict) {
		super();
		this.dataset = dataset_to_be_predict;
	}
	
	public Predictor(Loader dataset_to_be_predict) {
		this(dataset_to_be_predict.getDataset());
	}
	
	public Loader makePredicition(Classifier c){
		Instances datasetPredicted = dataset;
		
		for (int i = 0; i < datasetPredicted.numInstances(); i++) {
			Instance newInst = datasetPredicted.instance(i);
			
			/* PER STAMPARE IL CONFRONTO TRA PREDIZIONI E VALORE EFFETTIVO
			double actualClass = datasetPredicted.instance(i).classValue();
			String actual = datasetPredicted.classAttribute().value((int) actualClass);
			*/
			
			double predicted;
			
			try {
				predicted = c.classifyInstance(newInst);
				newInst.setClassValue(predicted);
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			/* PER STAMPARE IL CONFRONTO TRA PREDIZIONI E VALORE EFFETTIVO
			System.out.println(datasetPredicted.instance(i).value(0)+"  :  "+actual + ", " + predString);
			*/
		}
		
		Loader result = new Loader(datasetPredicted);
		return result;
	}
}
