package it.unimib.disco.essere.experiment;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import weka.core.Instances;
import weka.experiment.ClassifierSplitEvaluator;

public class CustomClassifierSplitEvaluator extends ClassifierSplitEvaluator {

	ArrayList<ResultCreator> threads = new ArrayList<ResultCreator>(); 
	private LinkedList<Object[]> queue = new  LinkedList<Object[]>();
	private final Object lock = new Object();


	/** for serialization */
	private static final long serialVersionUID = 1L;

	public void getResult(ArrayList<Instances> trainDatasets, ArrayList<Instances> testDatasets){

		ArrayList<Object[]> result = new ArrayList<Object[]>();

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


	public ArrayList<ResultCreator> getThreads() {
		return threads;
	}

	public LinkedList<Object[]> getQueue() {
		return queue;
	}

	private void waitForThreads() {
		synchronized(lock){
			try {
				lock.wait(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

	}

	private ResultCreator getNextReady(){
		for(ResultCreator thread : threads){
			if(thread.isFinish()){
				return thread;
			}
		}

		return null;
	}
}
