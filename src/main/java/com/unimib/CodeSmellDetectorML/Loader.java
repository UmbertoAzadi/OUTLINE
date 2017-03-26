package com.unimib.CodeSmellDetectorML;

import weka.core.converters.ConverterUtils.DataSource;
//import weka.core.converters.CSVLoader;
import weka.core.converters.CSVSaver;
import weka.core.converters.ArffSaver;

import java.io.File;
import java.io.IOException;

import weka.core.Instances;

public class Loader {
	
	private Instances dataset;
	
	public Loader(String path){
		// per ora legge qualsiasi formato, vogliamo SOLO csv?
		//CSVLoader loader = new CSVLoader();
		//loader.setSource(new File(datapath));
		//Instances data = loader.getDataSet(); 
		try{
			DataSource source = new DataSource(path);
			dataset = source.getDataSet();
			dataset.setClassIndex(dataset.numAttributes()-1);
		}catch(Exception e){ 
			e.printStackTrace();
			System.out.println("Unable to load the dataset, please make sure that the path is correct");
			/* Genero un eccezzione custom?*/}
	}
	
	public Loader(Instances dataset) {
		this.dataset = dataset;
		dataset.setClassIndex(dataset.numAttributes()-1);
	}

	public Instances getDataset(){
		return dataset;
	}
	
	public String toString(){
		return dataset.toSummaryString();
	}
	
	public void toCSV(String path){
		CSVSaver saver = new CSVSaver();
		saver.setInstances(dataset);
		try {
			saver.setFile(new File(path));
			saver.writeBatch();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	public void toArff(String path){
		ArffSaver saver = new ArffSaver();
		saver.setInstances(dataset);
		try{
			saver.setFile(new File(path));
			saver.writeBatch();
		}catch(IOException e){
			e.printStackTrace();
		}
	}
	
	// filtro, controlli, ...
}
