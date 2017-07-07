package it.unimib.disco.essere.functionalities;

import weka.classifiers.Classifier;
/**
 * This class fulfills all the responsibilities that concern the serialization and the deserialization
 * of the classifiers.
 * */
public class Serializer {
	
	/**
	 * Serialize the classifier and save it
	 * 
	 * @param path the file where the classifier will be saved
	 * @param c    classifier that has to be serialized 
	 * */
	public void serialize(String path, Classifier c) throws Exception{
		weka.core.SerializationHelper.write(path, c);
	}
	
	
	/**
	 * Deserialize a classifier
	 * 
	 * @param path the file that contain the serialized classifier
	 * 
	 * @return the classifier deserialized
	 * */
	public Classifier read(String path) throws Exception{
		return (Classifier)weka.core.SerializationHelper.read(path);
	}
	
}
