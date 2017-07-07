package it.unimib.disco.essere.experiment;

import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;
import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;
import java.util.ArrayList;
import java.util.List;

import weka.classifiers.AbstractClassifier;
import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.classifiers.evaluation.AbstractEvaluationMetric;
import weka.core.AdditionalMeasureProducer;
import weka.core.Attribute;
import weka.core.Instances;
import weka.core.OptionHandler;
import weka.core.Summarizable;
import weka.core.Utils;

/**
 * This is one of the classes that allows to perform a custom weka experiment
 * */

public class ResultCreator extends Thread {

	/** The template classifier */
	public Classifier m_Template;
	/** The classifier used for evaluation */
	public Classifier m_Classifier;
	/** Holds the most recently used Evaluation object */
	public Evaluation m_Evaluation;
	/** The names of any additional measures to look for in SplitEvaluators */
	public String[] m_AdditionalMeasures;
	/**
	 * Array of booleans corresponding to the measures in m_AdditionalMeasures
	 * indicating which of the AdditionalMeasures the current classifier can
	 * produce
	 */
	public boolean[] m_doesProduce;
	/**
	 * The number of additional measures that need to be filled in after taking
	 * into account column constraints imposed by the final destination for
	 * results
	 */
	public int m_numberAdditionalMeasures;
	/** Holds the statistics for the most recent application of the classifier */
	public String m_result;
	/** The classifier options (if any) */
	public String m_ClassifierOptions;
	/** The classifier version */
	public String m_ClassifierVersion;
	/** Class index for information retrieval statistics (default 0) */
	public int m_IRclass;
	/** Flag for prediction and target columns output. */
	public boolean m_predTargetColumn;
	/** Attribute index of instance identifier (default -1) */
	public int m_attID;
	/** whether to skip determination of sizes (train/test/classifier). */
	public boolean m_NoSizeDetermination;
	public List<AbstractEvaluationMetric> m_pluginMetrics;
	public int m_numPluginStatistics;

	/** The length of a result */
	private static final int RESULT_SIZE = 32;

	/** The number of IR statistics */
	private static final int NUM_IR_STATISTICS = 16;

	/** The number of averaged IR statistics */
	private static final int NUM_WEIGHTED_IR_STATISTICS = 10;

	/** The number of unweighted averaged IR statistics */
	private static final int NUM_UNWEIGHTED_IR_STATISTICS = 2;
	
	/** It will become true when this thread is terminated */
	private boolean finish = false;
	
	/** The training Instances */
	private Instances train;
	
	/** The testing Instances */
	private Instances test;
	
	/** The vector that will contain the result of the execution */
	private Object[] result;
	
	/**
	 * The constructor customized to for the customized weka experiment
	 * */
	public ResultCreator(Classifier m_Template, String[] m_AdditionalMeasures, boolean[] m_doesProduce,
			int m_numberAdditionalMeasures, String m_ClassifierOptions, String m_ClassifierVersion,
			int m_IRclass, boolean m_predTargetColumn, int m_attID, List<AbstractEvaluationMetric> m_pluginMetrics,
			int m_numPluginStatistics, Instances train, Instances test) {
		try {
			this.m_Template = generateNewClassifier(m_Template);
		} catch (Exception e) {
			e.printStackTrace();
		} 
		this.m_AdditionalMeasures = m_AdditionalMeasures.clone();
		this.m_doesProduce = m_doesProduce.clone();
		this.m_numberAdditionalMeasures = m_numberAdditionalMeasures; 
		this.m_ClassifierOptions = new String(m_ClassifierOptions);
		this.m_ClassifierVersion = new String(m_ClassifierVersion);
		this.m_IRclass = m_IRclass;
		this.m_predTargetColumn = m_predTargetColumn;
		this.m_attID = m_attID;
		this.m_pluginMetrics = new ArrayList<AbstractEvaluationMetric> (m_pluginMetrics);
		this.m_numPluginStatistics = m_numPluginStatistics;
		this.train = train;
		this.test = test;
	}

	/** @return the vector that will contain the result of the execution */
	public Object[] getResultArray(){
		if(result == null){
			System.err.println("Classifier not build yet, start the thread for build it");
			throw new NullPointerException();
		}
		return result;
	}
	
	/** @return true if this thread is terminated, false otherwise */
	public boolean isFinish(){
		return finish;
	}
	
	/** Performs a deep copy of the classifier before it is built and evaluated 
	 * (just in case the classifier is not initialized properly in buildClassifier() and for handle concurrently problem)
	 * 
	 * @param classifier the classifier to be copied
	 * @return the new classifier create as copy of that one passed as a parameter
	 * */
	private Classifier generateNewClassifier(Classifier classifier) throws Exception {
		Classifier c = null;
		String nameClassifier = classifier.getClass().getName();
		String[] optionsClassifier = ((OptionHandler) classifier).getOptions();

		try {
			OptionHandler tmpClassifier = (OptionHandler) Class.forName(nameClassifier).newInstance();
			tmpClassifier.setOptions(optionsClassifier);
			c = (Classifier) tmpClassifier;
		} catch (Exception e) {
			System.err.println(" Unable to classify " + nameClassifier + " [ " + e.getMessage() + " ] ");
			throw e;
		}

		return c;
	}

	/**
	 * Generate the results for the supplied train and test datasets and save them in the "result" vector 
	 */	
	public void getResult() throws Exception {

		if (train.classAttribute().type() != Attribute.NOMINAL) {
			throw new Exception("Class attribute is not nominal!");
		}
		if (m_Template == null) {
			throw new Exception("No classifier has been specified");
		}

		int addm = ( m_AdditionalMeasures != null) ?  m_AdditionalMeasures.length : 0;
		int overall_length = RESULT_SIZE + addm;
		overall_length += NUM_IR_STATISTICS;
		overall_length += NUM_WEIGHTED_IR_STATISTICS;
		overall_length += NUM_UNWEIGHTED_IR_STATISTICS;
		if (m_attID >= 0) {
			overall_length += 1;
		}
		if (m_predTargetColumn) {
			overall_length += 2;
		}

		overall_length +=  m_numPluginStatistics;

		ThreadMXBean thMonitor = ManagementFactory.getThreadMXBean();
		boolean canMeasureCPUTime = thMonitor.isThreadCpuTimeSupported();
		if (canMeasureCPUTime && !thMonitor.isThreadCpuTimeEnabled()) {
			thMonitor.setThreadCpuTimeEnabled(true);
		}

		result = new Object[overall_length];
		
		Evaluation eval = new Evaluation(train);
		m_Classifier = AbstractClassifier.makeCopy( m_Template);
		double[] predictions;
		long thID = Thread.currentThread().getId();
		long CPUStartTime = -1, trainCPUTimeElapsed = -1, testCPUTimeElapsed = -1, trainTimeStart, trainTimeElapsed, testTimeStart, testTimeElapsed;

		// training classifier
		trainTimeStart = System.currentTimeMillis();
		if (canMeasureCPUTime) {
			CPUStartTime = thMonitor.getThreadUserTime(thID);
		}
		
		m_Classifier.buildClassifier(train);
		
		if (canMeasureCPUTime) {
			trainCPUTimeElapsed = thMonitor.getThreadUserTime(thID) - CPUStartTime;
		}
		trainTimeElapsed = System.currentTimeMillis() - trainTimeStart;

		// testing classifier
		testTimeStart = System.currentTimeMillis();
		if (canMeasureCPUTime) {
			CPUStartTime = thMonitor.getThreadUserTime(thID);
		}
		predictions = eval.evaluateModel( m_Classifier, test);
		if (canMeasureCPUTime) {
			testCPUTimeElapsed = thMonitor.getThreadUserTime(thID) - CPUStartTime;
		}
		testTimeElapsed = System.currentTimeMillis() - testTimeStart;
		thMonitor = null;

		m_result = eval.toSummaryString();
		
		// The results stored are all per instance -- can be multiplied by the
		// number of instances to get absolute numbers
		int current = 0;
		result[current++] = new Double(train.numInstances());
		result[current++] = new Double(eval.numInstances());
		result[current++] = new Double(eval.correct());
		result[current++] = new Double(eval.incorrect());
		result[current++] = new Double(eval.unclassified());
		result[current++] = new Double(eval.pctCorrect());
		result[current++] = new Double(eval.pctIncorrect());
		result[current++] = new Double(eval.pctUnclassified());
		result[current++] = new Double(eval.kappa());

		result[current++] = new Double(eval.meanAbsoluteError());
		result[current++] = new Double(eval.rootMeanSquaredError());
		result[current++] = new Double(eval.relativeAbsoluteError());
		result[current++] = new Double(eval.rootRelativeSquaredError());

		result[current++] = new Double(eval.SFPriorEntropy());
		result[current++] = new Double(eval.SFSchemeEntropy());
		result[current++] = new Double(eval.SFEntropyGain());
		result[current++] = new Double(eval.SFMeanPriorEntropy());
		result[current++] = new Double(eval.SFMeanSchemeEntropy());
		result[current++] = new Double(eval.SFMeanEntropyGain());

		// K&B stats
		result[current++] = new Double(eval.KBInformation());
		result[current++] = new Double(eval.KBMeanInformation());
		result[current++] = new Double(eval.KBRelativeInformation());

		// IR stats
		result[current++] = new Double(eval.truePositiveRate( m_IRclass));
		result[current++] = new Double(eval.numTruePositives( m_IRclass));
		result[current++] = new Double(eval.falsePositiveRate( m_IRclass));
		result[current++] = new Double(eval.numFalsePositives( m_IRclass));
		result[current++] = new Double(eval.trueNegativeRate( m_IRclass));
		result[current++] = new Double(eval.numTrueNegatives( m_IRclass));
		result[current++] = new Double(eval.falseNegativeRate( m_IRclass));
		result[current++] = new Double(eval.numFalseNegatives( m_IRclass));
		result[current++] = new Double(eval.precision( m_IRclass));
		result[current++] = new Double(eval.recall( m_IRclass));
		result[current++] = new Double(eval.fMeasure( m_IRclass));
		result[current++] = new Double(
				eval.matthewsCorrelationCoefficient( m_IRclass));
		result[current++] = new Double(eval.areaUnderROC( m_IRclass));
		result[current++] = new Double(eval.areaUnderPRC( m_IRclass));

		// Weighted IR stats
		result[current++] = new Double(eval.weightedTruePositiveRate());
		result[current++] = new Double(eval.weightedFalsePositiveRate());
		result[current++] = new Double(eval.weightedTrueNegativeRate());
		result[current++] = new Double(eval.weightedFalseNegativeRate());
		result[current++] = new Double(eval.weightedPrecision());
		result[current++] = new Double(eval.weightedRecall());
		result[current++] = new Double(eval.weightedFMeasure());
		result[current++] = new Double(eval.weightedMatthewsCorrelation());
		result[current++] = new Double(eval.weightedAreaUnderROC());
		result[current++] = new Double(eval.weightedAreaUnderPRC());

		// Unweighted IR stats
		result[current++] = new Double(eval.unweightedMacroFmeasure());
		result[current++] = new Double(eval.unweightedMicroFmeasure());

		// Timing stats
		result[current++] = new Double(trainTimeElapsed / 1000.0);
		result[current++] = new Double(testTimeElapsed / 1000.0);
		if (canMeasureCPUTime) {
			result[current++] =
					new Double((trainCPUTimeElapsed / 1000000.0) / 1000.0);
			result[current++] = new Double((testCPUTimeElapsed / 1000000.0) / 1000.0);

			result[current++] =
					new Double(trainCPUTimeElapsed / 1000000.0);
			result[current++] = new Double(testCPUTimeElapsed / 1000000.0);
		} else {
			result[current++] = new Double(Utils.missingValue());
			result[current++] = new Double(Utils.missingValue());
			result[current++] = new Double(Utils.missingValue());
			result[current++] = new Double(Utils.missingValue());
		}

		// sizes
		if ( m_NoSizeDetermination) {
			result[current++] = -1.0;
			result[current++] = -1.0;
			result[current++] = -1.0;
		} else {
			ByteArrayOutputStream bastream = new ByteArrayOutputStream();
			ObjectOutputStream oostream = new ObjectOutputStream(bastream);
			oostream.writeObject( m_Classifier);
			result[current++] = new Double(bastream.size());
			bastream = new ByteArrayOutputStream();
			oostream = new ObjectOutputStream(bastream);
			oostream.writeObject(train);
			result[current++] = new Double(bastream.size());
			bastream = new ByteArrayOutputStream();
			oostream = new ObjectOutputStream(bastream);
			oostream.writeObject(test);
			result[current++] = new Double(bastream.size());
		}

		// Prediction interval statistics
		result[current++] =
				new Double(eval.coverageOfTestCasesByPredictedRegions());
		result[current++] = new Double(eval.sizeOfPredictedRegions());

		// IDs
		if (m_attID >= 0) {
			String idsString = "";
			if (test.attribute( m_attID).isNumeric()) {
				if (test.numInstances() > 0) {
					idsString += test.instance(0).value( m_attID);
				}
				for (int i = 1; i < test.numInstances(); i++) {
					idsString += "|" + test.instance(i).value( m_attID);
				}
			} else {
				if (test.numInstances() > 0) {
					idsString += test.instance(0).stringValue( m_attID);
				}
				for (int i = 1; i < test.numInstances(); i++) {
					idsString += "|" + test.instance(i).stringValue( m_attID);
				}
			}
			result[current++] = idsString;
		}

		if (m_predTargetColumn) {
			if (test.classAttribute().isNumeric()) {
				// Targets
				if (test.numInstances() > 0) {
					String targetsString = "";
					targetsString += test.instance(0).value(test.classIndex());
					for (int i = 1; i < test.numInstances(); i++) {
						targetsString += "|" + test.instance(i).value(test.classIndex());
					}
					result[current++] = targetsString;
				}

				// Predictions
				if (predictions.length > 0) {
					String predictionsString = "";
					predictionsString += predictions[0];
					for (int i = 1; i < predictions.length; i++) {
						predictionsString += "|" + predictions[i];
					}
					result[current++] = predictionsString;
				}
			} else {
				// Targets
				if (test.numInstances() > 0) {
					String targetsString = "";
					targetsString += test.instance(0).stringValue(test.classIndex());
					for (int i = 1; i < test.numInstances(); i++) {
						targetsString += "|"
								+ test.instance(i).stringValue(test.classIndex());
					}
					result[current++] = targetsString;
				}

				// Predictions
				if (predictions.length > 0) {
					String predictionsString = "";
					predictionsString += test.classAttribute()
							.value((int) predictions[0]);
					for (int i = 1; i < predictions.length; i++) {
						predictionsString += "|"
								+ test.classAttribute().value((int) predictions[i]);
					}
					result[current++] = predictionsString;
				}
			}
		}

		if ( m_Classifier instanceof Summarizable) {
			result[current++] = ((Summarizable)  m_Classifier).toSummaryString();
		} else {
			result[current++] = null;
		}

		for (int i = 0; i < addm; i++) {
			if ( m_doesProduce[i]) {
				try {
					double dv = ((AdditionalMeasureProducer)  m_Classifier)
							.getMeasure( m_AdditionalMeasures[i]);
					if (!Utils.isMissingValue(dv)) {
						Double value = new Double(dv);
						result[current++] = value;
					} else {
						result[current++] = null;
					}
				} catch (Exception ex) {
					System.err.println(ex);
				}
			} else {
				result[current++] = null;
			}
		}

		// get the actual metrics from the evaluation object
		List<AbstractEvaluationMetric> metrics = eval.getPluginMetrics();
		if (metrics != null) {
			for (AbstractEvaluationMetric m : metrics) {
				if (m.appliesToNominalClass()) {
					List<String> statNames = m.getStatisticNames();
					for (String s : statNames) {
						result[current++] = new Double(m.getStatistic(s));
					}
				}
			}
		}

		if (current != overall_length) {
			throw new Error("Results didn't fit RESULT_SIZE");
		}

		m_Evaluation = eval;
		finish = true;
	}
	
	/** for starting the execution (just call the getResult()) */
	public void run(){
		try {
			this.getResult();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}