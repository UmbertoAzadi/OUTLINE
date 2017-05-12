package it.unimib.disco.essere.core;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;

import javax.swing.DefaultListModel;

import weka.classifiers.Classifier;
import weka.core.Instances;
import weka.core.Range;
import weka.experiment.ClassifierSplitEvaluator;
import weka.experiment.CrossValidationResultProducer;
import weka.experiment.Experiment;
import weka.experiment.InstancesResultListener;
import weka.experiment.PairedCorrectedTTester;
import weka.experiment.PairedTTester;
import weka.experiment.PropertyNode;
import weka.experiment.RandomSplitResultProducer;
import weka.experiment.RegressionSplitEvaluator;
import weka.experiment.ResultMatrix;
import weka.experiment.ResultMatrixPlainText;
import weka.experiment.SplitEvaluator;

public class DataExperimenter {
	
	private ArrayList<Classifier> classifiers;
	private String pathForResult = null;
	private DefaultListModel<File> model;
	
	public DataExperimenter(ArrayList<Classifier> classifiers, DefaultListModel<File> datasets){
		this.classifiers = classifiers;
		this.model = datasets;
	}
	
	public DataExperimenter(ArrayList<Classifier> classifiers, DefaultListModel<File> datasets, String pathForResult){
		this.classifiers = classifiers;
		this.model = datasets;
		this.pathForResult = pathForResult;
	}
	
	public String experimetWithDefaultValues() throws Exception{
		String result = this.experiment("classification", "crossvalidation", 10, false, 66.0, 10);
		return result;
	}
	
	public String experiment(String exptype, String splittype, int folds, boolean randomized, double percentage, int runs) throws Exception{

		// 1. setup the experiment
		System.out.println("Setting up...");
		Experiment exp = new Experiment();
		Classifier[] a = new Classifier[classifiers.size()];
		exp.setPropertyArray(classifiers.toArray(a));
		exp.setUsePropertyIterator(true);

		// classification or regression
		SplitEvaluator se = null;
		Classifier sec    = null;
		boolean classification = false;
		if (exptype.equals("classification")) {
			classification = true;
			se  = new ClassifierSplitEvaluator();
			sec = ((ClassifierSplitEvaluator) se).getClassifier();
		}else if(exptype.equals("regression")){
			se  = new RegressionSplitEvaluator();
			sec = ((RegressionSplitEvaluator) se).getClassifier();
		}

		// crossvalidation or randomsplit
		if (splittype.equals("crossvalidation")) {
			CrossValidationResultProducer cvrp = new CrossValidationResultProducer();
			cvrp.setNumFolds(folds);
			setUpCrossValidaton(exp, se, sec, cvrp);
		}
		else if (splittype.equals("randomsplit")) {
			RandomSplitResultProducer rsrp = new RandomSplitResultProducer();

			if(randomized)
				rsrp.setRandomizeData(true);
			else
				rsrp.setRandomizeData(false);

			rsrp.setTrainPercent(percentage);
			setUpRandomsplit(exp, se, sec, rsrp);
		}

		// runs
		exp.setRunLower(1);
		exp.setRunUpper(runs);

		// datasets
		exp.setDatasets(model);

		// result
		InstancesResultListener irl = createFileForResult();
		exp.setResultListener(irl);

		// 2. run experiment
		try {
			System.out.println("Initializing...");
			exp.initialize();
			System.out.println("Running...");
			exp.runExperiment();
			System.out.println("Finishing...");
			exp.postProcess();
		} catch (Exception e) {
			e.printStackTrace();
		}

		// 3. calculate statistics and output them
		System.out.println("Evaluating...");
		ResultMatrix matrix = ttest(splittype, classification, irl);

		//System.out.println(matrix.toString()); //<<<< da salvare come per crossvalidation
		return matrix.toString();
	}

	private void setUpRandomsplit(Experiment exp, SplitEvaluator se, Classifier sec, RandomSplitResultProducer rsrp) {
		rsrp.setSplitEvaluator(se);
		
		PropertyNode[] propertyPath = new PropertyNode[2];
		try {
			propertyPath[0] = new PropertyNode(
					se, 
					new PropertyDescriptor("splitEvaluator",
							RandomSplitResultProducer.class),
					RandomSplitResultProducer.class);
			propertyPath[1] = new PropertyNode(
					sec, 
					new PropertyDescriptor("classifier",
							se.getClass()),
					se.getClass());
		}
		catch (IntrospectionException e) {
			e.printStackTrace();
		}

		exp.setResultProducer(rsrp);
		exp.setPropertyPath(propertyPath);
	}

	private void setUpCrossValidaton(Experiment exp, SplitEvaluator se, Classifier sec,
			CrossValidationResultProducer cvrp) {
		cvrp.setSplitEvaluator(se);

		PropertyNode[] propertyPath = new PropertyNode[2];
		try {
			propertyPath[0] = new PropertyNode(
					se, 
					new PropertyDescriptor("splitEvaluator",
							CrossValidationResultProducer.class),
					CrossValidationResultProducer.class);
			propertyPath[1] = new PropertyNode(
					sec, 
					new PropertyDescriptor("classifier",
							se.getClass()),
					se.getClass());
		}
		catch (IntrospectionException e) {
			e.printStackTrace();
		}
		
		exp.setResultProducer(cvrp);
	    exp.setPropertyPath(propertyPath);
	}

	private InstancesResultListener createFileForResult() {
		InstancesResultListener irl = new InstancesResultListener();
		
		if(this.pathForResult != null){
			irl.setOutputFile(new File(this.pathForResult+ "\\" + "ResultExperiment.arff"));
		}else{
			String path = new java.io.File("").getAbsolutePath();

			if(path.contains("\\CodeSmellDetectorML\\CodeSmellDetectorML")){
				irl.setOutputFile(new File(path.substring(0, path.lastIndexOf("\\"))+"\\result\\" + "ResultExperiment.arff"));

			}else{
				irl.setOutputFile(new File(path+"\\CodeSmellDetectorML\\result\\" + "ResultExperiment.arff"));
			}
		}
		return irl;
	}

	private ResultMatrix ttest(String splittype, boolean classification, InstancesResultListener irl) throws Exception {
		PairedTTester tester = new PairedCorrectedTTester();
		Instances result;

		try {
			result = new Instances(new BufferedReader(new FileReader(irl.getOutputFile())));
		} catch (Exception e) {
			System.out.println("ERROR: Unable to use the ResultExperiment.arff file");
			throw new Exception("Unable to use the ResultExperiment.arff file");
		}

		tester.setInstances(result);
		tester.setSortColumn(-1);
		tester.setRunColumn(result.attribute("Key_Run").index());
		
		//if (classification)
		if(splittype.equals("crossvalidation")){	
			tester.setFoldColumn(result.attribute("Key_Fold").index());
		}
		tester.setResultsetKeyColumns(
				new Range(
						"" 
								+ (result.attribute("Key_Dataset").index() + 1)));
		tester.setDatasetKeyColumns(
				new Range(
						"" 
								+ (result.attribute("Key_Scheme").index() + 1)
								+ ","
								+ (result.attribute("Key_Scheme_options").index() + 1)
								+ ","
								+ (result.attribute("Key_Scheme_version_ID").index() + 1)));
		tester.setResultMatrix(new ResultMatrixPlainText());
		tester.setDisplayedResultsets(null);       
		tester.setSignificanceLevel(0.05);
		tester.setShowStdDevs(true);
		
		// fill result matrix (but discarding the output)
		if (classification)
			tester.multiResultsetFull(0, result.attribute("Percent_correct").index());
		else
			tester.multiResultsetFull(0, result.attribute("Correlation_coefficient").index());
		
		// output results for reach dataset
		System.out.println("\nResult:");
		ResultMatrix matrix = tester.getResultMatrix();
		return matrix;
	}
}
