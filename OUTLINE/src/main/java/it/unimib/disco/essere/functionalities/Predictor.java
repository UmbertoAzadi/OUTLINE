package it.unimib.disco.essere.functionalities;

import java.util.logging.Logger;

import it.unimib.disco.essere.core.DatasetHandler;
import weka.classifiers.Classifier;
import weka.core.Instance;
import weka.core.Instances;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.Add;

/**
 * This class fulfills all the responsibilities that concern the prediction of new instance.
 * */

public class Predictor {
	
	private static final Logger LOGGER = Logger.getLogger(Predictor.class.getName());
	
	/** The dataset that have to be predicted */
	Instances dataset;
	
	public Predictor(Instances datasetToBePredict) {
		this.dataset = datasetToBePredict;
	}

	/**
	 * Perform the actual prediction, the class that has to predicted must be the last attribute
	 * 
	 * @param c      the classifier that have to be used to predict the instances
	 * @param labels the value which the class that has to predicted can assume
	 * 
	 * @return the dataset with the values predicted
	 * */
	public DatasetHandler makePredicitions(Classifier c, String labels) throws Exception{
		
		addClassAttribute(labels);
		
		for (int i = 0; i < dataset.numInstances(); i++) {
			Instance newInst = dataset.instance(i);
			newInst.setClassMissing();

			double predicted = 0;
			try {
				predicted = c.classifyInstance(newInst);
			} catch (Exception e) {
				LOGGER.warning("Unable to classify the following instances [" + e + "]: \n" + newInst.toString());
			}

			newInst.setClassValue(predicted);
		}

		return new DatasetHandler(dataset);
	}

	/**
	 * Generate the new attribute that will be predict
	 * 
	 *  @param labels the value which the class that has to predicted can assume
	 * */
	private void addClassAttribute(String labels) throws Exception {
		Add _class = new Add();
		_class.setAttributeIndex("last");
		if(labels != null){
			_class.setNominalLabels(labels);
		}
		else{
			_class.setNominalLabels("false,true");
		}
		_class.setAttributeName(dataset.relationName());
		try {
			_class.setInputFormat(dataset);
			dataset = Filter.useFilter(dataset, _class);
		} catch (Exception e) {
			LOGGER.severe("Unable to add the classvalue: "+e.getMessage());
			throw e;
		}
		
		dataset.setClassIndex(dataset.numAttributes()-1);
	}
	
}
