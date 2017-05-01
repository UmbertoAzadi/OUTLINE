package it.unimib.disco.essere.CodeSmellDetectorML;

import org.junit.*;
import it.unimib.disco.essere.core.EntryPoint;
import junit.framework.TestCase;


// THIS TEST TAKES APPROXIMATELY 3 MINUTE TO RUN BECAUSE IT TAKE 2 CONFIGURTION FILE, 
// EACH WITH 32 CLASSIFIER DEFINE 

public class TestBigConfiguration extends TestCase{
    
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
    
}
