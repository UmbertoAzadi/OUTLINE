package it.unimib.disco.essere.experiment;

import java.util.ArrayList;
import java.util.LinkedList;

import weka.core.Instances;
import weka.experiment.ClassifierSplitEvaluator;

/**
 * This is one of the classes that allows to perform a custom weka experiment
 * */

public class CustomClassifierSplitEvaluator extends ClassifierSplitEvaluator {

	/** The list of the threads which will build and evaluate the classifiers with a specific pair [train,test] Instances */
	private ArrayList<ResultCreator> threads = new ArrayList<ResultCreator>();
	
	/** The queue of threads that have terminated  */
	private LinkedList<Object[]> queue = new  LinkedList<Object[]>();
	
	/** for synchronization */
	private final Object lock = new Object();

	/** for serialization */
	private static final long serialVersionUID = 1L;


	/**
	 * Gets the results for all the train and test datasets. 
	 *
	 * @param trainDatasets the list of the training Instances.
	 * @param testDatasets  the list of the testing Instances.
	 * @throws Exception if a problem occurs while getting the results
	 */	
	public void getResult(ArrayList<Instances> trainDatasets, ArrayList<Instances> testDatasets){

		for(int fold = 0; fold < trainDatasets.size(); fold++){
			ResultCreator rc = new ResultCreator(m_Template, m_AdditionalMeasures, m_doesProduce, m_numberAdditionalMeasures, 
					m_ClassifierOptions, m_ClassifierVersion, this.getClassForIRStatistics(), this.getPredTargetColumn(), 
					this.getAttributeID(), m_pluginMetrics, m_numPluginStatistics, trainDatasets.get(fold), testDatasets.get(fold));
			threads.add(rc);
			rc.start();
		}

		while(!threads.isEmpty()){
			ResultCreator ready = this.getNextReady();
			if(ready != null){
				queue.add(ready.getResultArray());
				threads.remove(ready);
			}
			else{
				waitForThreads();

			}
		}
	}

	/** @return the list of the threads which will build and evaluate the classifiers with a specific pair [train,test] Instances  */
	public ArrayList<ResultCreator> getThreads() {
		return threads;
	}

	/** @return the queue of threads that have terminated */
	public LinkedList<Object[]> getQueue() {
		return queue;
	}

	/** Put this class in the wait state for 100ms */
	private void waitForThreads() {
		synchronized(lock){
			try {
				lock.wait(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

	}

	/** Check if some thread have terminated
	 * @return a thread that have terminated the execution, if no one have terminated return null
	 *  */
	private ResultCreator getNextReady(){
		for(ResultCreator thread : threads){
			if(thread.isFinish()){
				return thread;
			}
		}

		return null;
	}
}
