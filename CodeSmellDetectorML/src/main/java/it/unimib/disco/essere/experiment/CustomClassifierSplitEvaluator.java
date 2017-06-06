package it.unimib.disco.essere.experiment;

import java.util.ArrayList;
import java.util.List;

import weka.core.Instances;
import weka.experiment.ClassifierSplitEvaluator;

public class CustomClassifierSplitEvaluator extends ClassifierSplitEvaluator {

	ArrayList<ResultCreator> threads = new ArrayList<ResultCreator>(); 
	private final Object lock = new Object();

	/** for serialization */
	private static final long serialVersionUID = 1L;

	public List<Object[]> getResult(ArrayList<Instances> trainDatasets, ArrayList<Instances> testDatasets){
		
		ArrayList<Object[]> result = new ArrayList<Object[]>();

		for(int fold = 0; fold < trainDatasets.size(); fold++){
			ResultCreator rc = new ResultCreator(m_Template, m_AdditionalMeasures, m_doesProduce, m_numberAdditionalMeasures, 
					m_ClassifierOptions, m_ClassifierVersion, this.getClassForIRStatistics(), this.getPredTargetColumn(), 
					this.getAttributeID(), m_pluginMetrics, m_numPluginStatistics, trainDatasets.get(fold), testDatasets.get(fold));
			threads.add(rc);
			rc.start();
		}
		
		waitForThreads();
				
		for(ResultCreator thread : threads){
			//System.out.println("the result's length of the " + thread.getId() + " thread is " + thread.getResultArray().length);
			result.add(thread.getResultArray());
		}
		
		threads.clear();
		
		return result;
	}
	
	private void waitForThreads() {
		while(!allDone()){
			//System.out.println("waiting");
			synchronized(lock){
				try {
					lock.wait(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}

	private boolean allDone() {
		
		for(ResultCreator thread : threads){
			if(!thread.isFinish())
				return false;
		}
		
		return true;
	}

}
