package it.unimib.disco.essere.core;


import it.unimib.disco.essere.load.LoaderProperties;
import weka.classifiers.Classifier;
import weka.core.Instance;
import weka.core.Instances;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.Add;

public class Predictor {
	Instances dataset;
	
	public Predictor(Instances dataset_to_be_predict) {
		this.dataset = dataset_to_be_predict;
	}

	public Predictor(LoaderProperties dataset_to_be_predict) {
		this(dataset_to_be_predict.getDataset());
	}

	public DatasetHandler makePredicitions(Classifier c, boolean print_comparison){
		
		addClassAttribute();
		
		for (int i = 0; i < dataset.numInstances(); i++) {
			Instance newInst = dataset.instance(i);
			newInst.setClassMissing();
			
			String actual = "";
			if(print_comparison){
				double actualClass = dataset.instance(i).classValue();
				actual = dataset.classAttribute().value((int) actualClass);
			}

			double predicted = 0;
			try {
				predicted = c.classifyInstance(newInst);
			} catch (Exception e) {
				System.out.println("WARNING : Unable to classify the following instances: ");
				System.out.println(newInst.toString());
			}

			newInst.setClassValue(predicted);

			if(print_comparison){
				String predClass = dataset.classAttribute().value((int) predicted);
				System.out.println(dataset.instance(i).value(0)+"  :  "+actual + ", " + predClass);
			}
		}

		DatasetHandler result = new DatasetHandler(dataset);
		return result;
	}

	private void addClassAttribute() {
		Add _class = new Add();
		_class.setAttributeIndex("last");
		_class.setNominalLabels("false,true");
		_class.setAttributeName("predicted_class");
		try {
			_class.setInputFormat(dataset);
			dataset = Filter.useFilter(dataset, _class);
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		
		dataset.setClassIndex(dataset.numAttributes()-1);
	}
	
}
