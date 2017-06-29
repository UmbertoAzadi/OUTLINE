package it.unimib.disco.essere.experiment;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.swing.DefaultListModel;

import it.unimib.disco.essere.load.LoaderProperties;
import weka.classifiers.Classifier;
import weka.core.Instances;
import weka.core.Utils;
import weka.experiment.CrossValidationResultProducer;
import weka.experiment.OutputZipper;
import weka.experiment.InstancesResultListener;

public class CustomCrossValidationResultProducer extends CrossValidationResultProducer{

	/** for serialization */
	private static final long serialVersionUID = 1L;
	private final Object lock = new Object();

	/**
	 * Gets the results for a specified run number. Different run numbers
	 * correspond to different randomizations of the data. Results produced should
	 * be sent to the current ResultListener
	 * 
	 * @param run the run number to get results for.
	 * @throws Exception if a problem occurs while getting the results
	 */
	@Override
	public void doRun(int run) throws Exception {

		ArrayList<Instances> trainingData = new ArrayList<Instances>();
		ArrayList<Instances> testingData = new ArrayList<Instances>();
		ArrayList<Object[]> allKey = new ArrayList<Object[]>(); 
		CustomClassifierSplitEvaluator customSplitEvaluator = (CustomClassifierSplitEvaluator) this.m_SplitEvaluator;

		if (getRawOutput()) {
			if (m_ZipDest == null) {
				m_ZipDest = new OutputZipper(m_OutputFile);
			}
		}

		if (m_Instances == null) {
			throw new Exception("No Instances set");
		}
		// Randomize on a copy of the original dataset
		Instances runInstances = new Instances(m_Instances);
		Random random = new Random(run);
		runInstances.randomize(random);
		if (runInstances.classAttribute().isNominal()) {
			runInstances.stratify(m_NumFolds);
		}


		for (int fold = 0; fold < m_NumFolds; fold++) {
			// Add in some fields to the key like run and fold number, dataset name

			Object[] seKey = customSplitEvaluator.getKey();
			Object[] key = new Object[seKey.length + 3];
			key[0] = Utils.backQuoteChars(m_Instances.relationName());
			key[1] = "" + run;
			key[2] = "" + (fold + 1);
			System.arraycopy(seKey, 0, key, 3, seKey.length);

			allKey.add(key);

			if (m_ResultListener.isResultRequired(this, key)) {
				Instances train = runInstances.trainCV(m_NumFolds, fold, random);
				Instances test = runInstances.testCV(m_NumFolds, fold);
				trainingData.add(train);
				testingData.add(test);
			}
		}

		customSplitEvaluator.getResult(trainingData, testingData);
		int fold = 0;

		while(!customSplitEvaluator.getQueue().isEmpty() || !customSplitEvaluator.getThreads().isEmpty()){
			if(customSplitEvaluator.getQueue().isEmpty()){
				synchronized(lock){
					try {
						lock.wait(100);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
			else{
				getNextResult(run, allKey, customSplitEvaluator, runInstances, fold);
				fold++;
			}
		}

	}

	private void getNextResult(int run, ArrayList<Object[]> allKey, CustomClassifierSplitEvaluator customSplitEvaluator,
			Instances runInstances, int currentFold) {

		Object[] seResults = customSplitEvaluator.getQueue().removeFirst();

		try {
			Object[] key = allKey.get(currentFold);
			Object[] results = new Object[seResults.length + 1];
			results[0] = getTimestamp();
			System.arraycopy(seResults, 0, results, 1, seResults.length);

			if (m_debugOutput) {
				String resultName = ("" + run + "." + (currentFold + 1) + "."
						+ Utils.backQuoteChars(runInstances.relationName()) + "." + m_SplitEvaluator
						.toString()).replace(' ', '_');
				resultName = Utils.removeSubstring(resultName, "weka.classifiers.");
				resultName = Utils.removeSubstring(resultName, "weka.filters.");
				resultName = Utils.removeSubstring(resultName,
						"weka.attributeSelection.");
				m_ZipDest.zipit(m_SplitEvaluator.getRawResultOutput(), resultName);
			}

			m_ResultListener.acceptResult(this, key, results);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args){

		long startTime = System.currentTimeMillis();

		LoaderProperties lp = new LoaderProperties();
		ArrayList<Classifier> a = null;
		try {
			//a = lp.load("C:/Users/uazad/Desktop/università/Stage/Progetto/CodeSmellDetectorML/configuration/Configuration_for_testing"
			//			+ "/serialization/serialization_valid_with_path.properties");
			//a = lp.load("C:\\Users\\uazad\\Desktop\\università\\Stage\\Progetto\\CodeSmellDetectorML\\configuration\\serialization_valid.properties");
			a = lp.loadForClassification("C:/Users/uazad/Desktop/università/Stage/Progetto/CodeSmellDetectorML/configuration/is_dataset_classification.properties");

		} catch (Exception e) {
			e.printStackTrace();
		}

		DefaultListModel<File> model = new DefaultListModel<File>();
		File file = new File(lp.getDatasetHandler().getPath());
		model.addElement(file);

		DataExperimenter de = new DataExperimenter(a, model);
		try {
			String result = de.experiment("custom", "custom", 10, false, 0.0, 10);
			System.out.println(result);
		} catch (Exception e) {
			e.printStackTrace();
		}

		System.out.println("it took: " + (System.currentTimeMillis() - startTime));

	}

}
