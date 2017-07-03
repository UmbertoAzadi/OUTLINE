package it.unimib.disco.essere.functionalities;

import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.core.Instances;
import java.util.Random;
import java.util.logging.Logger;

public class DataEvaluator{
	private static final Logger LOGGER = Logger.getLogger(DataEvaluator.class.getName());
	private Evaluation evaluator;
	
	public Evaluation getEvaluator(){
		return evaluator;
	}

	public Evaluation crossValidation(Instances dataset, Classifier c) throws Exception{
		return crossValidation(dataset, c, 10, 1);
	}

	public Evaluation crossValidation(Instances dataset, Classifier c, int folds, int seed) throws Exception{
		try {
			evaluator = new Evaluation(dataset);
			Random rand = new Random(seed);
			evaluator.crossValidateModel(c, dataset, folds, rand);	
		} catch (Exception e) {
			LOGGER.severe("-------------------------------------------------------------------------\n"
					+"ERROR: UNABLE TO PERFORM THE CROSSVALIDATION\n"
					+ e + "\n"
					+"-------------------------------------------------------------------------");
			throw e;
		}
		return evaluator; 
	}

	public Evaluation evaluate(Instances trainingDataset, Instances testingDataset, Classifier classifier) throws Exception{
		evaluator = null;
		try {
			evaluator  = new Evaluation(trainingDataset);
			evaluator.evaluateModel(classifier, testingDataset);
		} catch (Exception e) {
			LOGGER.severe("ERROR DURING EVALUATION : " + e.getMessage());
			throw e;
		}
		return evaluator;
	}
	
	public String getSummary(){
		String summary = "";
		
		summary = evaluator.toSummaryString()  
				+"Area Under ROC                         " + evaluator.areaUnderROC(0) 
				+"\nFmeasure                               "+ evaluator.fMeasure(0) + "\n";
		
		return summary;
	}
}
