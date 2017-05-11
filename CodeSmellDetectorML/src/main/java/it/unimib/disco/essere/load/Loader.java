package it.unimib.disco.essere.load;

import java.util.ArrayList;
import java.util.Arrays;

import it.unimib.disco.essere.core.DatasetHandler;
import weka.classifiers.Classifier;
import weka.core.Instances;
import weka.core.OptionHandler;

public abstract class Loader {
	
	protected String path_for_result;
	protected DatasetHandler dataset;
	protected String PathDataset;
	
	public Loader(){}
	
	public abstract ArrayList<Classifier> load(String path) throws Exception;
	public abstract ArrayList<String> loadForPred(String path) throws Exception;
	
	public String getPath_for_result(){
		return this.path_for_result;
	}
	
	public Instances getDataset(){
		return dataset.getDataset();
	}
	
	public String gatPathDataset(){
		return PathDataset;
	}
	
	protected void addOptions(OptionHandler o, String[] options) throws Exception{
		String[] tmp = new String[options.length]; 
		System.arraycopy(options, 0, tmp, 0, options.length);
		if(options != null){
			try {
				o.setOptions(options);
			} catch (Exception e) {
				System.out.println("-------------------------------------------------------------------------------------------------");
				System.out.println("ERROR : these options are incorrect: ");
				System.out.println("\t"+Arrays.toString(tmp));
				System.out.println("	plese check them in the weka documentation");
				System.out.println(""); 
				System.out.println("--------------------------------------------------------------------------------------------------");
				//System.exit(0);
				throw new Exception();
			}
		}
	}
	
	protected OptionHandler findClass(String name){
		OptionHandler o = null;
		
			try {
				o = (OptionHandler) Class.forName(name).newInstance();
			} catch (InstantiationException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (ClassNotFoundException e) {}
		
		return o;
	}
	
	protected void checkNotNull(Object o, String type, String name) throws Exception{
		
		 /* QUESTO METODO E' NECESSARIO POICHE' LA CHIAMATA A findClass ALLA RIGA 115 (LoaderProperties) E' DENTRO UN WHILE
		    QUINDI IL CONTROLLO VA FATTO DOPO CHE IL CICLO SI E' CONCLUSO */

		if(o == null){
			if(!type.equals("")){
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
