package it.unimib.disco.essere.load;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

import it.unimib.disco.essere.core.DatasetHandler;
import weka.classifiers.Classifier;
import weka.classifiers.meta.MultiSearch;
import weka.core.Instances;
import weka.core.OptionHandler;
import weka.core.Utils;

/**
 * This is an abstract class that all the concrete loader must extends.
 * */

public abstract class Loader {

	private static final Logger LOGGER = Logger.getLogger(Loader.class.getName());

	/** The path where the result of the request will be saved */
	protected String pathForResult;

	/** The dataset that will be used */
	protected DatasetHandler dataset;

	/**
	 * Load and parse the configuration file used for classification
	 * 
	 * @param path the path of the configuration file
	 * @return the list of the classifier
	 * */
	public abstract ArrayList<Classifier> loadForClassification(String path) throws Exception;

	/**
	 * Load and parse the configuration file used for prediction
	 * 
	 * @param path the path of the configuration file
	 * @return the list of the that will contain: path of the dataset, labels, path of the serialized classifiers
	 * */
	public abstract ArrayList<String> loadForPrediction(String path) throws Exception;

	/** @return the path where the result of the request will be saved */
	public String getPathForResult(){
		return this.pathForResult;
	}

	/** @return the dataset as DatasetHandler*/
	public DatasetHandler getDatasetHandler(){
		return dataset;
	}

	/** @return the dataset as Instances*/
	public Instances getDataset(){
		return dataset.getDataset();
	}

	/**
	 * Apply the options to the Classifier
	 * 
	 * @param oh      the classifier The classifier to which the options have to be applied 
	 * 				  (the cast from any weka Classifier to OptionHandler will be always possible)
	 * @param options the options 
	 * */
	protected void addOptions(OptionHandler oh, String options) throws Exception{
		if(options.length() != 0){
			try {
				String[] opt = parseOptions(options, oh);
				oh.setOptions(opt);
			} catch (Exception e) {
				LOGGER.severe("\n-------------------------------------------------------------------------------------------------\n"
						+"ERROR : these options are incorrect: \n"
						+"\t" + oh.getClass().getName() + " " + options +"\n"
						+"\t" + e + "\n"
						+"	plese check them in the weka documentation\n\n"
						+"--------------------------------------------------------------------------------------------------");
				throw e;	
			}
		}
	}


	/**
	 * Parse the nested option
	 * 
	 * @param options the options
	 * @param oh      the classifier The classifier to which the options have to be applied 
	 * 				  (the cast from any weka Classifier to OptionHandler will be always possible)
	 * */
	public String[] parseOptions(String options, OptionHandler oh) throws Exception {
		String[] opt = options.split(" ");
		if(options.contains("\"")){

			opt = options.split("\"");

			if(oh instanceof MultiSearch){
				for(int i=0; i < opt.length; i++){
					if(opt[i].contains("-list")){
						opt[i] = opt[i] + "\"" + opt[i+1] + "\"";
						opt[i + 1] = "";
					}
				}
			}
			ArrayList<String> selectedOpt = new ArrayList<String>();
			for(int i=0; i < opt.length; i++){
				if(i % 2 == 0){
					for(String s : Utils.splitOptions(opt[i])){
						selectedOpt.add(Utils.backQuoteChars(s));
					}
				}
				else{
					//selectedOpt.add(Utils.backQuoteChars(opt[i]));
					selectedOpt.add(opt[i]);
				}
			}
			opt = selectedOpt.toArray(opt);
		}

		if(options.contains("-algorithm") && oh instanceof MultiSearch){
			List<String> temp = Arrays.asList(opt);
			int index = temp.indexOf("-algorithm");
			opt[index] = "";
			opt[index + 1] = "";
		}

		return opt;
	}

	/**
	 * Find the class specified by the name and return an instance of that class
	 * @param name the class name
	 * @return an instance of the class 
	 * */
	protected OptionHandler findClass(String name) throws InstantiationException, IllegalAccessException{
		OptionHandler o = null;

		try {
			o = (OptionHandler) Class.forName(name).newInstance();
		} catch (InstantiationException e) {
			LOGGER.severe(e.getMessage());
			throw e;
		} catch (IllegalAccessException e) {
			LOGGER.severe(e.getMessage());
			throw e;
		} catch (ClassNotFoundException e) {
			/* THE ERROR MESSAGE WILL BE PRINT OUT LATER,
			 * BECAUSE I DON'T WANT TO STOP THE EXECUTIONS
			 * */
		}

		return o;
	}

	/**
	 * Print out the error message if the class is not found (if the object is equal to null)
	 * @param obj  the object that you want to make sure that is not null
	 * @param type if the object not found is a classifier or an ensemble method
	 * @param name the name of the class not found
	 * */
	protected void checkNotNull(Object obj, String type, String name) throws Exception{

		/* QUESTO METODO E' NECESSARIO POICHE' CI SONO DUE CHIAMATE A findClass ALLA RIGA 71 E 80 (LoaderProperties),
		 * E L'ECCEZIONE DEVE ESSERE GENERATA SOLO DOPO CHE ENTRAMBE LE CHIAMATE SONO STATE FATTE  */

		if(obj == null){
			if(!"".equals(type)){
				LOGGER.severe("-------------------------------------------------------------------------------------\n"
						+"ERROR : There isn't a " + type + " with the name \"" + name +"\",\n"
						+"	check the configuration file and please insert the name of a valid \n"
						+"	" + type + " and remember to respect the formats specified in the README.md\n"
						+"-------------------------------------------------------------------------------------");
			}else{
				LOGGER.severe("-------------------------------------------------------------------------------------\n"
						+"ERROR : There is an error in the configuration file, please make sure that all the \n"
						+"	classifier specified respect the formats specified in the README.md \n"
						+"-------------------------------------------------------------------------------------");
			}
			//System.exit(0);
			throw new Exception();
		}
	}
}
