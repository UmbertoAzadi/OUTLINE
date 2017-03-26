package com.unimib.CodeSmellDetectorML;


import java.util.Hashtable;

import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.core.Instances;

public class DataEvaluator {
	private DataClassifier DC;
	private Instances testing_dataset;
	private Hashtable<Classifier, Evaluation> evaluated = new Hashtable<Classifier, Evaluation>();
	
	
	public DataEvaluator(DataClassifier dc, Instances t){
		this.DC = dc;
		this.testing_dataset = t;
	}
	
	public DataEvaluator(DataClassifier dc, Loader DL ){
		this(dc, DL.getDataset());
	}
	
	public String evaluateJ48_pruned(boolean is_boosted){
		return this.evaluate(DC.getJ48_pruned());
	}
	
	public String evaluate(Classifier classifier){
		try {
			Evaluation evaluator;
			if(evaluated.containsKey(classifier)){
				evaluator = evaluated.get(classifier);
			}else{
				evaluator = new Evaluation(testing_dataset);
				evaluator.evaluateModel(classifier, testing_dataset);
				evaluated.put(classifier, evaluator);
			}
			String summary = classifier.toString();
			summary += "Area under ROC: " + evaluator.areaUnderROC(1);
			summary += "\n fMeasure: " + evaluator.fMeasure(1);
			summary += "\n Accuracy: " + (1 - evaluator.errorRate());
			summary += "\n"+evaluator.toMatrixString("=== Overall Confusion Matrix ===\n");
			
			return summary;
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		}
	}
	
	public String evaluateAll(){
		String summary = "";
		
		System.out.println("Evaluating...");
		
		System.out.println("J48 pruned");
		summary += this.evaluate(DC.getBj48_pruned()) + "\n\n";
		summary += this.evaluate(DC.getJ48_pruned()) + "\n\n";
		
		System.out.println("J48 unpruned");
		summary += this.evaluate(DC.getBj48_unpruned()) + "\n\n";
		summary += this.evaluate(DC.getJ48_unpruned()) + "\n\n";
		
		System.out.println("J48 reduce error");
		summary += this.evaluate(DC.getBj48_reduce_error()) + "\n\n";
		summary += this.evaluate(DC.getJ48_reduce_error()) + "\n\n";
		
		System.out.println("JRip");
		summary += this.evaluate(DC.getBjRip()) + "\n\n";
		summary += this.evaluate(DC.getjRip()) + "\n\n";
		
		System.out.println("Random forest");
		summary += this.evaluate(DC.getBrandomForest()) + "\n\n";
		summary += this.evaluate(DC.getRandomForest()) + "\n\n";
		
		System.out.println("Naive Bayes");
		summary += this.evaluate(DC.getBnaiveBayes()) + "\n\n";
		summary += this.evaluate(DC.getNaiveBayes()) + "\n\n";
		
		System.out.println("SMO Poly Kernel");
		summary += this.evaluate(DC.getBsmo_Poly()) + "\n\n";
		summary += this.evaluate(DC.getSmo_Poly()) + "\n\n";
		
		System.out.println("SMO RBF Kernel");
		summary += this.evaluate(DC.getBsmo_RBF()) + "\n\n";
		summary += this.evaluate(DC.getSmo_RBF()) + "\n\n";
		
		System.out.println("LibSVM CSVC Linear");
		summary += this.evaluate(DC.getBlibSVM_CSVC_Linear()) + "\n\n";
		summary += this.evaluate(DC.getLibSVM_CSVC_Linear()) + "\n\n";
		
		System.out.println("LibSVM CSVC Polynomial");
		summary += this.evaluate(DC.getBlibSVM_CSVC_Polynomial()) + "\n\n";
		summary += this.evaluate(DC.getLibSVM_CSVC_Polynomial()) + "\n\n";
		
		System.out.println("LibSVM CSVC Radial");
		summary += this.evaluate(DC.getBlibSVM_CSVC_Radial()) + "\n\n";
		summary += this.evaluate(DC.getLibSVM_CSVC_Radial()) + "\n\n";
		
		System.out.println("LibSVM CSVC Sigmoid");
		summary += this.evaluate(DC.getBlibSVM_CSVC_Sigmoid()) + "\n\n";
		summary += this.evaluate(DC.getLibSVM_CSVC_Sigmoid()) + "\n\n";
		
		System.out.println("LibSVM nuSVC Linear");
		summary += this.evaluate(DC.getBlibSVM_nuSVC_Linear()) + "\n\n";
		summary += this.evaluate(DC.getLibSVM_nuSVC_Linear()) + "\n\n";
		
		System.out.println("LibSVM nuSVC Polynomial");
		summary += this.evaluate(DC.getBlibSVM_nuSVC_Polynomial()) + "\n\n";
		summary += this.evaluate(DC.getLibSVM_nuSVC_Polynomial()) + "\n\n";
		
		System.out.println("LibSVM nuSVC Radial");
		summary += this.evaluate(DC.getBlibSVM_nuSVC_Radial()) + "\n\n";
		summary += this.evaluate(DC.getLibSVM_nuSVC_Radial()) + "\n\n";
		
		System.out.println("LibSVM nuSVC Sigmoid");
		summary += this.evaluate(DC.getBlibSVM_nuSVC_Sigmoid()) + "\n\n";
		summary += this.evaluate(DC.getLibSVM_nuSVC_Sigmoid()) + "\n\n";
		
		return summary;
	}
}
