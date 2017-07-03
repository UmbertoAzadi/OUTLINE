package it.unimib.disco.essere.core;

import java.io.File;
import java.io.PrintWriter;
import java.util.logging.Logger;

import it.unimib.disco.essere.functionalities.Serializer;
import it.unimib.disco.essere.load.Loader;

public abstract class Handler {

	protected static final Logger LOGGER = Logger.getLogger(Handler.class.getName());
	
	protected Loader configuration;
	protected Serializer serializer = new Serializer();
	
	public void printInConfiPath(String textToPrint, String messageToShow, String nameOfTheFile, String extensionWithPoint) throws Exception {
		System.out.println(messageToShow);
		try{
			//PrintWriter writer = null;
			String path = configuration.getPathForResult();

			String name = "\\" + nameOfTheFile + extensionWithPoint;
			File f = new File(path + name);
			int i = 0;

			while(f.exists()) { 
				name = "\\" + nameOfTheFile + "_" + i + extensionWithPoint;
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
