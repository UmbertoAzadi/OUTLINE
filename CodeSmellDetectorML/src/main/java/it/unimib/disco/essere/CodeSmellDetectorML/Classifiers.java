package it.unimib.disco.essere.CodeSmellDetectorML;

public class Classifiers {
	private String name;
    private String[] options;
    
    public Classifiers(String name, String[] options){
    	this.name = name;
    	this.options = options;
    }
    
    public String toString(){
    	return name;
    }
}
