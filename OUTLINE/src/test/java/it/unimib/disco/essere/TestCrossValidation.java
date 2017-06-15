package it.unimib.disco.essere;

import org.junit.Before;
import org.junit.Test;

import it.unimib.disco.essere.core.EntryPoint;
import junit.framework.TestCase;

public class TestCrossValidation extends TestCase{
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
    	String[] args = {"-cross", 
    					 "C:/Users/uazad/Desktop/università/Stage/Progetto/CodeSmellDetectorML/configuration/serialization_valid.properties"};
    	try{
    		workflow.start(args);
    	}catch(Exception e){
    		correct = false;
    	} 
    	assertTrue(correct);
    }
    
    @Test 
    public void testValidWithPath(){
    	String[] args = {"-cross",  
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
}
