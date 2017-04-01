package it.unimib.disco.essere.CodeSmellDetectorML;
import weka.classifiers.Classifier;

public class Serializer {
	
	public Serializer(){}
	
	public void serialize(String path, Classifier c) throws Exception{
		weka.core.SerializationHelper.write(path, c);
	}
	
	public Classifier read(String path){
		Classifier classifier = null;
		try {
			classifier = (Classifier)weka.core.SerializationHelper.read(path);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return classifier;
	}
	
	
}
