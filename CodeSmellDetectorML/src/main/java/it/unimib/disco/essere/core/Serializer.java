package it.unimib.disco.essere.core;

import weka.classifiers.Classifier;

public class Serializer {
	
	public Serializer(){}
	
	public void serialize(String path, Classifier c) throws Exception{
		weka.core.SerializationHelper.write(path, c);
	}
	
	public Classifier read(String path) throws Exception{
		Classifier classifier = (Classifier)weka.core.SerializationHelper.read(path);
		return classifier;
	}
	
}
