package it.unimib.disco.essere.load;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

import it.unimib.disco.essere.core.DatasetHandler;
import weka.classifiers.Classifier;
import weka.classifiers.functions.Logistic;
import weka.classifiers.meta.MultiSearch;
import weka.core.Instances;
import weka.core.ListOptions;
import weka.core.OptionHandler;
import weka.core.Utils;
import weka.core.setupgenerator.ListParameter;

public abstract class Loader {

	private static final Logger LOGGER = Logger.getLogger(Loader.class.getName());

	protected String pathForResult;
	protected DatasetHandler dataset;

	public Loader(){}

	public abstract ArrayList<Classifier> loadForClassification(String path) throws Exception;
	public abstract ArrayList<String> loadForPrediction(String path) throws Exception;

	public String getPathForResult(){
		return this.pathForResult;
	}

	public DatasetHandler getDatasetHandler(){
		return dataset;
	}

	public Instances getDataset(){
		return dataset.getDataset();
	}

	protected void addOptions(OptionHandler oh, String options) throws Exception{
		//String tmp = options.substring(0);
		if(options.length() != 0){
			try {
				String[] opt = parseOptions(options, oh);
				oh.setOptions(opt);
			} catch (Exception e) {
				LOGGER.severe("\n-------------------------------------------------------------------------------------------------\n"
						+"ERROR : these options are incorrect: \n"
						//+"\t"+Arrays.toString(tmp)+"\n"
						+"\t" + oh.getClass().getName() + " " + options +"\n"
						+"\t" + e + "\n"
						+"	plese check them in the weka documentation\n\n"
						+"--------------------------------------------------------------------------------------------------");
				throw e;	
			}
		}
	}

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

	protected void checkNotNull(Object o, String type, String name) throws Exception{

		/* QUESTO METODO E' NECESSARIO POICHE' CI SONO DUE CHIAMATE A findClass ALLA RIGA 71 E 80 (LoaderProperties),
		 * E L'ECCEZIONE DEVE ESSERE GENERATA SOLO DOPO CHE ENTRAMBE LE CHIAMATE SONO STATE FATTE  */

		if(o == null){
			if(!"".equals(type)){
				System.out.println("-------------------------------------------------------------------------------------");
				System.out.println("ERROR : There isn't a " + type + " with the name \"" + name +"\",");
				System.out.println("	check the configuration file and please insert the name of a valid ");
				System.out.println("	" + type + " and remember to respect the formats specified in the README.md");
				System.out.println("-------------------------------------------------------------------------------------");
			}else{
				System.out.println("-------------------------------------------------------------------------------------");
				System.out.println("ERROR : There is an error in the configuration file, please make sure that all the");
				System.out.println("	classifier specified respect the formats specified in the README.md");
				System.out.println("-------------------------------------------------------------------------------------");
			}
			//System.exit(0);
			throw new Exception();
		}
	}
}
