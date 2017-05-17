package it.unimib.disco.essere.CodeSmellDetectorML;

import org.junit.*;
import it.unimib.disco.essere.core.EntryPoint;
import junit.framework.TestCase;


public class TestSerialization extends TestCase {
	
	EntryPoint workflow;
	boolean correct; 
	
    @Before
    public void setUp(){
    	workflow = new EntryPoint();
    	correct = true;
    	System.out.println("");
    	System.out.println("<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<");
    	System.out.println("\nTESTING: "+this.getName());
    	System.out.println("");
    }
    
    @Test
    public void testValid(){
    	String[] args = {"-ser", 
    					 "-save", 
    					 "C:/Users/uazad/Desktop/università/Stage/Progetto/CodeSmellDetectorML/configuration/serialization_valid.properties"};
    	try{
    		workflow.start(args);
    	}catch(Exception e){
    		correct = false;
    	}
    	assertTrue(correct);
    }
    
    @Test 
    public void testBadClassifier(){
    	String[] args = {"-ser", 
    					 "-save", 
    	"C:/Users/uazad/Desktop/università/Stage/Progetto/CodeSmellDetectorML/configuration/Configuration_for_testing"
    	+ "/serialization/serialization_bad_classifier.properties"
    	}; // end declaration
    	
    	try{
    		workflow.start(args);
    	}catch(Exception e){
    		correct = false;
    	}
    	assertFalse(correct);
    }
    
    @Test 
    public void testBadClassifierOption(){
    	String[] args = {"-ser", 
    					 "-save", 
    	"C:/Users/uazad/Desktop/università/Stage/Progetto/CodeSmellDetectorML/configuration/Configuration_for_testing"
    	+ "/serialization/serialization_bad_classifier_options.properties"
    	}; // end declaration
    	
    	try{
    		workflow.start(args);
    	}catch(Exception e){
    		correct = false;
    	}
    	assertFalse(correct);
    }
    
    @Test 
    public void testBadDataset(){
    	String[] args = {"-ser", 
    					 "-save", 
    	"C:/Users/uazad/Desktop/università/Stage/Progetto/CodeSmellDetectorML/configuration/Configuration_for_testing"
    	+ "/serialization/serialization_bad_dataset_no_path.properties"
    	}; // end declaration
    	
    	try{
    		workflow.start(args);
    	}catch(Exception e){
    		correct = false;
    	}
    	assertFalse(correct);
    }
    
    @Test 
    public void testBadEnsemble(){
    	String[] args = {"-ser", 
    					 "-save",  
    	"C:/Users/uazad/Desktop/università/Stage/Progetto/CodeSmellDetectorML/configuration/Configuration_for_testing"
    	+ "/serialization/serialization_bad_ensemble.properties"
    	}; // end declaration
    	
    	try{
    		workflow.start(args);
    	}catch(Exception e){
    		correct = false;
    	}
    	assertFalse(correct);
    }
    
    @Test 
    public void testBadEnsembleOptions(){
    	String[] args = {"-ser", 
    					 "-save", 
    	"C:/Users/uazad/Desktop/università/Stage/Progetto/CodeSmellDetectorML/configuration/Configuration_for_testing"
    	+ "/serialization/serialization_bad_ensemble_options.properties"
    	}; // end declaration
    	
    	try{
    		workflow.start(args);
    	}catch(Exception e){
    		correct = false;
    	}
    	assertFalse(correct);
    }
    
    @Test 
    public void testValidWithPath(){
    	String[] args = {"-ser", 
    					 "-save",  
    	"C:/Users/uazad/Desktop/università/Stage/Progetto/CodeSmellDetectorML/configuration/Configuration_for_testing"
    	+ "/serialization/serialization_valid_with_path.properties"
    	}; // end declaration
    	
    	try{
    		workflow.start(args);
    	}catch(Exception e){
    		correct = false;
    	}
    	assertTrue(correct);
    }      
    
    /*
	@Test 
    public void testBigProperties(){
    	String[] args = {"-ser",  
    					 "-save",  
    	"C:/Users/uazad/Desktop/università/Stage/Progetto/CodeSmellDetectorML/configuration/is_dataset_classification.properties"
    	}; // end declaration
    	
    	try{
    		workflow.start(args);
    	}catch(Exception e){
    		correct = false;
    	}
    	assertTrue(correct);
    }
    */
}
