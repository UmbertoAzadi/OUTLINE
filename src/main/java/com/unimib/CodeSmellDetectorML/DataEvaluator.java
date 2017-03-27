package com.unimib.CodeSmellDetectorML;

import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.core.Instances;
import java.util.Random;

public class DataEvaluator {
	private DataClassifier DC;
	private Instances testing_dataset;
	//private Hashtable<Classifier, Evaluation> evaluated = new Hashtable<Classifier, Evaluation>();
	
	
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
	
	public String crossValidation(Classifier c){
		return crossValidation(c, 10, 1);
	}
	
	public String crossValidation(Classifier c, int folds, int seed){
		try {
			Evaluation eval = new Evaluation(DC.getDataset());
			Random rand = new Random(seed);
			eval.crossValidateModel(c, DC.getDataset(), folds, rand);
			
			System.out.println(eval.toSummaryString("Evaluation results:\n", false));
			
			System.out.println("Correct % = "+eval.pctCorrect());
			System.out.println("Incorrect % = "+eval.pctIncorrect());
			System.out.println("AUC = "+eval.areaUnderROC(1));
			System.out.println("kappa = "+eval.kappa());
			System.out.println("MAE = "+eval.meanAbsoluteError());
			System.out.println("RMSE = "+eval.rootMeanSquaredError());
			System.out.println("RAE = "+eval.relativeAbsoluteError());
			System.out.println("RRSE = "+eval.rootRelativeSquaredError());
			System.out.println("Precision = "+eval.precision(1));
			System.out.println("Recall = "+eval.recall(1));
			System.out.println("fMeasure = "+eval.fMeasure(1));
			System.out.println("Error Rate = "+eval.errorRate());
		    //the confusion matrix
			System.out.println(eval.toMatrixString("=== Overall Confusion Matrix ===\n"));
		    
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
		
		
		return "";
	}
	
	public String evaluate(Classifier classifier){
		try {
			Evaluation evaluator;
			evaluator = new Evaluation(testing_dataset);
			evaluator.evaluateModel(classifier, testing_dataset);
			
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
