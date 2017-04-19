package it.unimib.disco.essere.core;


import it.unimib.disco.essere.load.LoaderProperties;
import weka.classifiers.Classifier;
import weka.core.Instance;
import weka.core.Instances;

public class Predictor {
	Instances dataset;
	
	public Predictor(Instances dataset_to_be_predict) {

		this.dataset = dataset_to_be_predict;
	}

	public Predictor(LoaderProperties dataset_to_be_predict) {
		this(dataset_to_be_predict.getDataset());
	}

	public DatasetHandler makePredicitions(Classifier c, boolean print_comparison){

		for (int i = 0; i < dataset.numInstances(); i++) {
			Instance newInst = dataset.instance(i);
			
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
	
	
/*
	private void predictOneInstance(Instance instance, Classifier c, boolean print_comparison, int i){ 
		// viene modificata l'instance passata come parametro, quindi non torna niente

		String actual = "";
		if(print_comparison){
			double actualClass = dataset.instance(i).classValue();
			actual = dataset.classAttribute().value((int) actualClass);
		}

		double predicted = 0;
		try {
			predicted = c.classifyInstance(instance);
		} catch (Exception e) {
			System.out.println("WARNING : Unable to classify the following instances: ");
			System.out.println(instance.toString());
		}

		instance.setClassValue(predicted);

		if(print_comparison){
			String predClass = dataset.classAttribute().value((int) predicted);
			System.out.println(dataset.instance(i).value(0)+"  :  "+actual + ", " + predClass);
		}
	}

	
	public String predictOneInstance(String instance, Classifier c, boolean print_comparison){
		//String[] attr_instance = instance.split(",");
		//ArrayList<String> attr_instance = (ArrayList)Arrays.asList(attr_instance_s);
		//ArrayList<Attribute> atts = new ArrayList<Attribute>();
		
		String nameAttr = "";
		
		for(int i = 0; i < dataset.numAttributes(); i++){
			String[] tmp = dataset.attribute(i).toString().split(" ");
			nameAttr += tmp[1] + ",";
		}
		
		nameAttr = nameAttr.substring(0, nameAttr.length()-2);
		
		createAndWriteFile(nameAttr+"\n"+instance);
		DatasetHandler temp = new DatasetHandler("./temp.csv");
		deleteFile();
		
		Instances _temp = temp.getDataset(); 
		
		predictOneInstance(_temp.get(0), c, false, 0);
	
		int p = (int) _temp.instance(0).classValue();
		
		return dataset.classAttribute().value(p);
		//return _temp.classAttribute().value((int) _temp.instance(0).classValue());
		
	}
	*/
	
}
