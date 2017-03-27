package com.unimib.CodeSmellDetectorML;
import weka.classifiers.Classifier;

public class Serializer {
	
	public Serializer(){}
	
	public void serialize(String path, Classifier c){
		try {
			weka.core.SerializationHelper.write(path, c);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public Classifier read(String path){
		Classifier c = null;
		return this.read(path, c);
	}
	
	public Classifier read(String path, Classifier c){
		Classifier classifier = null;
		try {
			classifier = (Classifier)weka.core.SerializationHelper.read(path);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return classifier;
	}
}
