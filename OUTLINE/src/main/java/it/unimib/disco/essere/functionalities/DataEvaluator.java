package it.unimib.disco.essere.functionalities;

import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.core.Instances;
import java.util.Random;
import java.util.logging.Logger;

public class DataEvaluator {
	//private DataClassifier DC;
	//private Instances testing_dataset;

	private static final Logger LOGGER = Logger.getLogger(DataEvaluator.class.getName());

	//private Instances dataset;
	//private ArrayList<Evaluation> evaluators = new ArrayList<Evaluation>();

	/*
	public DataEvaluator(Instances dataset){
		this.dataset = dataset;
	}
	
	public DataEvaluator(DataClassifier dc, Instances t){
		//System.out.println(dc.getSummary());
		this.DC = dc;
		this.testing_dataset = t;
		for(Classifier c: DC.getClassifiers()){
			evaluators.add(this.evaluate(c));
		}
	}*/

	/*
	public DataEvaluator(DataClassifier dc, LoaderProperties DL ){
		this(dc, DL.getDataset());
	}
	 */

	public String crossValidation(Instances dataset, Classifier c) throws Exception{
		return crossValidation(dataset, c, 10, 1);
	}

	public String crossValidation(Instances dataset, Classifier c, int folds, int seed) throws Exception{
		String result = "";
		try {
			Evaluation eval = new Evaluation(dataset);
			Random rand = new Random(seed);
			eval.crossValidateModel(c, dataset, folds, rand);	
			result = eval.toSummaryString()  
					+"Area Under ROC                         " + eval.areaUnderROC(0) 
					+"\nFmeasure                               "+ eval.fMeasure(0) + "\n";
		} catch (Exception e) {
			LOGGER.severe("-------------------------------------------------------------------------\n"
					+"ERROR: UNABLE TO PERFORM THE CROSSVALIDATION\n"
					+ e + "\n"
					+"-------------------------------------------------------------------------");
			throw e;
		}
		return result; 
	}

	public Evaluation evaluate(Instances trainingDataset, Instances testingDataset, Classifier classifier) throws Exception{
		Evaluation evaluator = null;
		try {
			evaluator  = new Evaluation(trainingDataset);
			evaluator.evaluateModel(classifier, testingDataset);
		} catch (Exception e) {
			LOGGER.severe("ERROR DURING EVALUATION : " + e.getMessage());
			throw e;
		}
		return evaluator;
	}
	
	
/*
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
	}*/
}
