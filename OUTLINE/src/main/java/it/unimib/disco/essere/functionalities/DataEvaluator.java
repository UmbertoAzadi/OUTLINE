package it.unimib.disco.essere.functionalities;

import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.core.Instances;
import java.util.Random;
import java.util.logging.Logger;

/**
 * This class fulfills all the responsibilities that concern to the evaluation of the classifiers, 
 * including the execution of the cross validation.
 * */

public class DataEvaluator{
	private static final Logger LOGGER = Logger.getLogger(DataEvaluator.class.getName());
	
	/** The instance use to evaluate the classifier */
	private Evaluation evaluator;
	
	/** @return the instance use to evaluate the classifier */
	public Evaluation getEvaluator(){
		return evaluator;
	}

	/**
	 * Perform the crossvalidation using the default value for fold (10) and seed (1)
	 * 
	 * @param dataset the dataset used for training and testing
	 * @param c       the classifier that have to be evaluated
	 * 
	 * @return the instance use to evaluate the classifier
	 * */
	public Evaluation crossValidation(Instances dataset, Classifier c) throws Exception{
		return crossValidation(dataset, c, 10, 1);
	}

	/**
	 * Perform the cross validation
	 * 
	 * @param dataset the dataset used for training and testing
	 * @param c       the classifier that have to be evaluated
	 * @param folds   number of fold used by the cross validation
	 * @param seed    the number used by the Random class for splitting the instances (usually is used 1)
	 * 
	 * @return the instance use to evaluate the classifier
	 * */
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

	/**
	 * Evaluate the classifier
	 * 
	 * @param trainingDataset the training dataset, as Instances  
	 * @param testingDataset  the testing dataset, as Instances
	 * @param classifier      the classifier that have to be evaluated 
	 * 
	 * @return the instance use to evaluate the classifier
	 * */
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
	
	/**
	 * @return the summary of the evaluation
	 * */
	public String getSummary(){
		String summary = "";
		
		summary = evaluator.toSummaryString()  
				+"Area Under ROC                         " + evaluator.areaUnderROC(0) 
				+"\nFmeasure                               "+ evaluator.fMeasure(0) + "\n";
		
		return summary;
	}
}
