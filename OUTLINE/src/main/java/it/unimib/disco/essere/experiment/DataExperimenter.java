package it.unimib.disco.essere.experiment;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.List;
import java.util.logging.Logger;

import javax.swing.DefaultListModel;

import it.unimib.disco.essere.core.DatasetHandler;
import weka.classifiers.Classifier;
import weka.core.Instances;
import weka.core.Range;
import weka.experiment.ClassifierSplitEvaluator;
import weka.experiment.CrossValidationResultProducer;
import weka.experiment.Experiment;
import weka.experiment.InstancesResultListener;
import weka.experiment.PropertyNode;
import weka.experiment.RandomSplitResultProducer;
import weka.experiment.RegressionSplitEvaluator;
import weka.experiment.ResultMatrix;
import weka.experiment.ResultMatrixPlainText;
import weka.experiment.SplitEvaluator;

/**
 * This class fulfills all the responsibilities that concern the configuration and the performing of a weka experiment, 
 * either the standard and the customized one;
 * */

public class DataExperimenter {
	
	/** The instance used for print out the information, warning and error messages */
	private static final Logger LOGGER = Logger.getLogger(DataExperimenter.class.getName());

	/** Setup value used to require a custom experiment */
	protected static final String CUSTOM_EXPERIMENT = "custom";

	/** The list of all the experiment types handled */
	private static final String[] EXPTYPE_VALUES = {"classification", "regression", CUSTOM_EXPERIMENT};
	
	/** The list of all the split types handled */
	private static final String[] SPLITTYPE_VALUES = {"crossvalidation", "randomsplit", CUSTOM_EXPERIMENT};
	
	/** The default value used for the experiment type */
	protected static final String DEFAULT_EXPTYPE = "classification";
	
	/** The default value used for the split type */
	protected static final String DEFAULT_SPLITTYPE = "crossvalidation";
	
	/** The default value used for the cross validation's fold */
	protected static final int DEFAULT_FOLDS = 10;
	
	/** The default value used for the random split's randomization */
	protected static final boolean DEFAULT_RANDOMIZED = false;
	
	/** The default value used for the random split's percentage */
	protected static final double DEFAULT_PERCENTAGE = 66.0;
	
	/** The default value used for split type's runs */
	protected static final int DEFAULT_RUNS = 10;

	/** The list of the classifiers that have to be analyze */
	private List<Classifier> classifiers;
	
	/** The path where the experiment statistics and result will be saved */
	private String pathForResult = null;
	
	/** The list that contain all the datasets that have to be used */
	private DefaultListModel<File> model;

	/**
	 * The constructor that have to be used to specify more then one datasets to analyze.
	 * 
	 * @param classifiers the list of the classifiers that have to be analyze
	 * @param datasets    The list that contain all the paths of the datasets that have to be used
	 * */
	public DataExperimenter(List<Classifier> classifiers, DefaultListModel<File> datasets){
		this.classifiers = classifiers;
		this.model = datasets;
	}

	/**
	 * The constructor that have to be used to specify more then one datasets to analyze, and 
	 * that allow to specify the path where the experiment statistics and result will be saved.
	 * 
	 * @param classifiers   the list of the classifiers that have to be analyze
	 * @param datasets      the list that contain all the paths of the datasets that have to be used
	 * @param pathForResult the path where the experiment statistics and result will be saved
	 * */
	public DataExperimenter(List<Classifier> classifiers, DefaultListModel<File> datasets, String pathForResult){
		this(classifiers, datasets);
		this.pathForResult = pathForResult;
	}
	
	/**
	 * The constructor that could be used if you want to specify just one dataset
	 * 
	 * @param classifiers   the list of the classifiers that have to be analyze
	 * @param dataset       the dataset that have to be used
	 * */
	public DataExperimenter(List<Classifier> classifiers, DatasetHandler dataset){
		this.classifiers = classifiers;
		
		DefaultListModel<File> model = new DefaultListModel<File>();
		File file = new File(dataset.getPath());
		model.addElement(file);
		
		this.model = model;
	}

	/**
	 * The constructor that could be used if you want to specify just one dataset and 
	 * that allow to specify the path where the experiment statistics and result will be saved.
	 * 
	 * @param classifiers   the list of the classifiers that have to be analyze
	 * @param dataset       the dataset that have to be used
	 * @param pathForResult the path where the experiment statistics and result will be saved
	 * */
	public DataExperimenter(List<Classifier> classifiers, DatasetHandler dataset, String pathForResult){
		this(classifiers, dataset);
		this.pathForResult = pathForResult;
	}
	
	/** @return the setup value used to require a custom experiment */
	public static String getCustomExperiment() {
		return CUSTOM_EXPERIMENT;
	}

	/** @return the list of all the experiment types handled */
	public static String[] getExptypeValues() {
		return EXPTYPE_VALUES;
	}

	/** @return the list of all the split types handled */
	public static String[] getSplittypeValues() {
		return SPLITTYPE_VALUES;
	}

	/** @return the default value used for the experiment type */
	public static String getDefaultExptype() {
		return DEFAULT_EXPTYPE;
	}

	/** @return the default value used for the split type */
	public static String getDefaultSplittype() {
		return DEFAULT_SPLITTYPE;
	}

	/** @return the default value used for the cross validation's fold */
	public static int getDefaultFolds() {
		return DEFAULT_FOLDS;
	}

	/** @return the default value used for the random split's randomization */
	public static boolean isDefaultRandomized() {
		return DEFAULT_RANDOMIZED;
	}

	/** @return the default value used for the random split's percentage */
	public static double getDefaultPercentage() {
		return DEFAULT_PERCENTAGE;
	}
	
	/** @return the default value used for split type's runs */
	public static int getDefaultRuns() {
		return DEFAULT_RUNS;
	}

	/**
	 * Check if a string is a valid experiment type
	 * 
	 * @param exptype value to be checked
	 * @return true if the string is a valid experiment type
	 * */
	public static boolean exptypeCheckValue(String exptype){
		for(String value: EXPTYPE_VALUES){
			if(value.equals(exptype))
				return true;
		}
		return false;
	}

	/**
	 * Check if a string is a valid split type
	 * 
	 * @param splittype value to be checked
	 * @return true if the string is a valid split type
	 * */
	public static boolean splittypeCheckValue(String splittype){
		for(String value: SPLITTYPE_VALUES){
			if(value.equals(splittype))
				return true;
		}
		return false;
	}

	/**
	 * Perform the standard weka experiment with all the default values
	 * 
	 * @return the result of the experiment
	 * */
	public String experimetWithDefaultValues() throws Exception{
		return this.experiment(DataExperimenter.DEFAULT_EXPTYPE, 
				DataExperimenter.DEFAULT_SPLITTYPE, 
				DataExperimenter.DEFAULT_FOLDS, 
				DataExperimenter.DEFAULT_RANDOMIZED, 
				DataExperimenter.DEFAULT_PERCENTAGE, 
				DataExperimenter.DEFAULT_RUNS);
	}

	/**
	 * Perform a standard weka experiment
	 * 
	 * @param exptype    the type of experiment: classification or regression
	 * @param splittype  the type of split: crossvalidation or randomsplit
	 * @param folds      number of fold used by the cross validation
	 * @param percentage  how many instances has to used for the creation of the training set in the randomsplit
	 * @param randomized if the instances used for the creating the training and testing set in the randomsplit has to be randomized first
	 * @param runs       how many times the the split has to be repeted 
	 * 
	 * @return the result of the experiment
	 * */
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


		if(CUSTOM_EXPERIMENT.equals(exptype)){
			classification = true;
			se  = new CustomClassifierSplitEvaluator();
			sec = ((CustomClassifierSplitEvaluator) se).getClassifier();
		}else{
			if (EXPTYPE_VALUES[0].equals(exptype)) {
				classification = true;
				se  = new ClassifierSplitEvaluator();
				sec = ((ClassifierSplitEvaluator) se).getClassifier();
			}else if(EXPTYPE_VALUES[1].equals(exptype)){
				se  = new RegressionSplitEvaluator();
				sec = ((RegressionSplitEvaluator) se).getClassifier();
			}
		}

		// crossvalidation or randomsplit
		if(CUSTOM_EXPERIMENT.equals(splittype)){
			CustomCrossValidationResultProducer ccvrp = new CustomCrossValidationResultProducer();
			ccvrp.setNumFolds(folds);
			setUpCrossValidaton(exp, se, sec, ccvrp);
		}
		else{
			if (SPLITTYPE_VALUES[0].equals(splittype)) {
				CrossValidationResultProducer cvrp = new CrossValidationResultProducer();
				cvrp.setNumFolds(folds);
				setUpCrossValidaton(exp, se, sec, cvrp);
			}
			else if (SPLITTYPE_VALUES[1].equals(splittype)) {
				RandomSplitResultProducer rsrp = new RandomSplitResultProducer();

				if(randomized)
					rsrp.setRandomizeData(true);
				else
					rsrp.setRandomizeData(false);

				rsrp.setTrainPercent(percentage);
				setUpRandomsplit(exp, se, sec, rsrp);
			}
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
			LOGGER.severe("Unable to run the experimet: " + e.getMessage());
			throw e;
		}

		// 3. calculate statistics and output them
		System.out.println("Evaluating...");
		ResultMatrix matrix = ttest(splittype, classification, irl);

		//System.out.println(matrix.toString()); //<<<< da salvare come per crossvalidation
		return "\nResult:\n"+matrix.toString();
	}

	/**
	 * Setup the random split 
	 * */
	private void setUpRandomsplit(Experiment exp, SplitEvaluator se, Classifier sec, RandomSplitResultProducer rsrp) throws IntrospectionException {
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
			LOGGER.severe("Unable to set up the randomsplit: " + e.getMessage());
			throw e;
		}

		exp.setResultProducer(rsrp);
		exp.setPropertyPath(propertyPath);
	}

	/**
	 * Setup the cross validation
	 * */
	private void setUpCrossValidaton(Experiment exp, SplitEvaluator se, Classifier sec,
			CrossValidationResultProducer cvrp) throws IntrospectionException {
		cvrp.setSplitEvaluator(se);

		// DA LEVARE VVVVVVVVVV
		//cvrp.setRawOutput(true);
		//cvrp.setOutputFile(new File("C:\\Users\\uazad\\Desktop\\try1.zip"));

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
			LOGGER.severe("Unable to set up the crossvalidation: " + e.getMessage());
			throw e;
		}

		exp.setResultProducer(cvrp);
		exp.setPropertyPath(propertyPath);
	}

	/**
	 * Create the file where the experiment statistics will be saved
	 * */
	private InstancesResultListener createFileForResult() {
		InstancesResultListener irl = new InstancesResultListener();
		String statisticFileName = "ExperimentStatistics";

		if(this.pathForResult != null){
			String name = "\\" + statisticFileName + ".arff";
			File file = new File(this.pathForResult+ name);
			int i = 0;

			while(file.exists()) { 
				name = "\\"+ statisticFileName+"_"+i+".arff";
				file = new File(this.pathForResult + name);
				i++;
			}

			irl.setOutputFile(new File(this.pathForResult+ name));
		}else{
			String path = new java.io.File("").getAbsolutePath();

			if(path.contains("\\OUTLINE\\OUTLINE")){
				irl.setOutputFile(new File(path.substring(0, path.lastIndexOf('\\'))+"\\result\\" + statisticFileName + ".arff"));
			}else{
				irl.setOutputFile(new File(path+"\\result\\" + statisticFileName + ".arff"));
			}
		}
		return irl;
	}

	/**
	 * Setup and perform the PairedCorrectedTTester 
	 * */
	private ResultMatrix ttest(String splittype, boolean classification, InstancesResultListener irl) throws Exception {
		CustomPrintPairedTTester tester = new CustomPrintPairedTTester();
		Instances result;

		try{
			FileReader file = new FileReader(irl.getOutputFile());
			result = new Instances(new BufferedReader(file));
		} catch (Exception e) {
			LOGGER.severe("Unable to use the ResultExperiment.arff file");
			throw e;
		}

		tester.setInstances(result);
		tester.setSortColumn(-1);
		tester.setRunColumn(result.attribute("Key_Run").index());

		//if (classification)
		if("crossvalidation".equals(splittype)){	
			tester.setFoldColumn(result.attribute("Key_Fold").index());
		}
		tester.setResultsetKeyColumns(
				new Range(Integer.toString(result.attribute("Key_Dataset").index() + 1)));
		tester.setDatasetKeyColumns(
				new Range(
						result.attribute("Key_Scheme").index() + 1
						+ ", "
						+ (result.attribute("Key_Scheme_options").index() + 1)
						+ ", "
						+ (result.attribute("Key_Scheme_version_ID").index() + 1)));
		
		tester.setResultMatrix(new ResultMatrixPlainText());
		tester.setDisplayedResultsets(null);       
		tester.setSignificanceLevel(0.05);
		//tester.getResultMatrix().setRemoveFilterName(true);
		tester.getResultMatrix().setPrintColNames(true);
		tester.getResultMatrix().setPrintRowNames(true);
		tester.setShowStdDevs(true);
	
		

		// fill result matrix (but discarding the output)

		if (classification){
			tester.multiResultsetFull(0, result.attribute("Percent_correct").index());
		}
		else
			tester.multiResultsetFull(0, result.attribute("Correlation_coefficient").index());
		
		// output results for reach dataset
		return tester.getResultMatrix();
	}

}
