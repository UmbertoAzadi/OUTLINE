package it.unimib.disco.essere.core;

import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.core.Instances;
import java.util.ArrayList;
import java.util.Random;

import it.unimib.disco.essere.load.LoaderProperties;

public class DataEvaluator {
	private DataClassifier DC;
	private Instances testing_dataset;
	private ArrayList<Evaluation> evaluators = new ArrayList<Evaluation>();
	
	
	public DataEvaluator(DataClassifier dc, Instances t){
		System.out.println(dc.getSummary());
		this.DC = dc;
		this.testing_dataset = t;
		for(Classifier c: DC.getClassifiers()){
			evaluators.add(this.evaluate(c));
		}
	}
	
	public DataEvaluator(DataClassifier dc, LoaderProperties DL ){
		this(dc, DL.getDataset());
	}
	
	/*
	public String crossValidation(Classifier c){
		return crossValidation(c, 10, 1);
	}
	
	public String crossValidation(Classifier c, int folds, int seed){
		try {
			Evaluation eval = new Evaluation(DC.getDataset());
			Random rand = new Random(seed);
			eval.crossValidateModel(c, DC.getDataset(), folds, rand);		    
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}*/
	
	public Evaluation evaluate(Classifier classifier){
		Evaluation evaluator = null;
		try {
			evaluator  = new Evaluation(testing_dataset);
			evaluator.evaluateModel(classifier, testing_dataset);
		} catch (Exception e) {
			System.out.println("ERROR DURING EVALUATION");
			e.printStackTrace();
		}
		return evaluator;
	}
	
	public String getSummary(){
		String summary = "";
		int i=0;
		for(Evaluation e: evaluators){	
			try {
				summary += "\n\n======== " + DC.getClassifiers().get(i).getClass().getName() + " ========\n\n";
				summary += e.toClassDetailsString("Summary:");
				summary += "\nAccuracy: " + (1 - e.errorRate());
				summary += "\n\n"+e.toMatrixString("Overall Confusion Matrix:\n");
			} catch (Exception e2) {
				e2.printStackTrace();
			}
			i++;
		}
		return summary;
	}

}
