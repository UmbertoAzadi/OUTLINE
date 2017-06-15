package it.unimib.disco.essere.core;

import java.util.logging.Logger;

import it.unimib.disco.essere.load.LoaderProperties;
import weka.classifiers.Classifier;
import weka.core.Instance;
import weka.core.Instances;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.Add;

public class Predictor {
	
	private static final Logger LOGGER = Logger.getLogger(Predictor.class.getName());
	
	Instances dataset;
	
	
	public Predictor(Instances datasetToBePredict) {
		this.dataset = datasetToBePredict;
	}

	public Predictor(LoaderProperties datasetToBePredict) {
		this(datasetToBePredict.getDataset());
	}

	public DatasetHandler makePredicitions(Classifier c, boolean printComparison) throws Exception{
		
		addClassAttribute();
		
		for (int i = 0; i < dataset.numInstances(); i++) {
			Instance newInst = dataset.instance(i);
			newInst.setClassMissing();
			
			String actual = "";
			if(printComparison){
				double actualClass = dataset.instance(i).classValue();
				actual = dataset.classAttribute().value((int) actualClass);
			}

			double predicted = 0;
			try {
				predicted = c.classifyInstance(newInst);
			} catch (Exception e) {
				LOGGER.warning("Unable to classify the following instances [" + e + "]: \n" + newInst.toString());
			}

			newInst.setClassValue(predicted);

			if(printComparison){
				String predClass = dataset.classAttribute().value((int) predicted);
				System.out.println(dataset.instance(i).value(0)+"  :  "+actual + ", " + predClass);
			}
		}

		return new DatasetHandler(dataset);
	}

	private void addClassAttribute() throws Exception {
		Add _class = new Add();
		_class.setAttributeIndex("last");
		_class.setNominalLabels("false,true");
		//_class.setAttributeName("predicted_class");
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
