package it.unimib.disco.essere.core;

import java.io.File;
import java.io.PrintWriter;
import java.util.logging.Logger;

import it.unimib.disco.essere.functionalities.Serializer;
import it.unimib.disco.essere.load.Loader;

/**
 * The abstract class which contain all the method and the parameter that should be in common to all Handlers.
 * */

public abstract class Handler {

	/** The instance used for print out the information, warning and error messages */
	protected static final Logger LOGGER = Logger.getLogger(Handler.class.getName());
	
	/** The class that is suppose to load the configuration file */
	protected Loader configuration;
	
	/** An instance of the class that will serialize or deserialize the classifiers */
	protected Serializer serializer = new Serializer();
	
	/**
	 * Print some text in a file created as follow: 
	 * [path specified in the configuration file]\[nameOfTheFile](_i).extensionWithPoint
	 * where i is a incrementing number which make sure that no file will be overwritten
	 * 
	 * @param textToPrint        string that have to be printed in the file
	 * @param messageToShow      string that have to be print out as information to the user
	 * @param nameOfTheFile      the name of the file
	 * @param extensionWithPoint the extension complete with point (for example .txt)
	 * */
	public void printInConfigPath(String textToPrint, String messageToShow, String nameOfTheFile, String extensionWithPoint) throws Exception {
		System.out.println(messageToShow);
		try{
			String path = configuration.getPathForResult();

			String name = "/" + nameOfTheFile + extensionWithPoint;
			File f = new File(path + name);
			int i = 0;

			while(f.exists()) { 
				name = "/" + nameOfTheFile + "_" + i + extensionWithPoint;
				f = new File(path + name);
				i++;
			}

			try{
				PrintWriter writer = new PrintWriter(path + name, "UTF-8");
				writer.println(textToPrint);
				writer.close();
			}catch(Exception e){
				LOGGER.severe("Unable to save the result:" + e.getMessage());
				throw e;
			}

			System.out.println("The result of the cross validation were saved in "+path + name);
		}catch(Exception e){
			LOGGER.severe("Unable to save the result on the specified path");
			throw e;
		}
	}
}
