package it.unimib.disco.essere.load;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.logging.Logger;

import it.unimib.disco.essere.core.DatasetHandler;
import weka.classifiers.Classifier;
import weka.core.Instances;
import weka.core.OptionHandler;

public abstract class Loader {

	private static final Logger LOGGER = Logger.getLogger(Loader.class.getName());

	protected String pathForResult;
	protected DatasetHandler dataset;
	protected String pathDataset;

	public Loader(){}

	public abstract ArrayList<Classifier> load(String path) throws Exception;
	public abstract ArrayList<String> loadForPred(String path) throws Exception;

	public String getPathForResult(){
		return this.pathForResult;
	}

	public Instances getDataset(){
		return dataset.getDataset();
	}

	public String gatPathDataset(){
		return pathDataset;
	}

	protected void addOptions(OptionHandler o, String[] options) throws Exception{
		String[] tmp = new String[options.length]; 
		System.arraycopy(options, 0, tmp, 0, options.length);
		if(options.length != 0){
			try {
				o.setOptions(options);
			} catch (Exception e) {
				LOGGER.severe("------------------------------------------------------------------------------------------------- +\n"
						+"ERROR : these options are incorrect: \n"
						+"\t"+Arrays.toString(tmp)+"\n"
						+"	plese check them in the weka documentation\n\n"
						+"--------------------------------------------------------------------------------------------------");
				throw e;
			}
		}
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
