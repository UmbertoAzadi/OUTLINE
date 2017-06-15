package it.unimib.disco.essere;

import org.junit.Before;
import org.junit.Test;

import it.unimib.disco.essere.core.EntryPoint;
import junit.framework.TestCase;

public class TestExperiment extends TestCase{
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
	public void testClassificationCrossvalidation(){
		String[] args = {"-wekaExp",
				"-exptype", "classification", 
				"-splittype", "crossvalidation",
				"-runs", "2", 
				"-folds", "8", 
		"C:/Users/uazad/Desktop/università/Stage/Progetto/OUTLINE/configuration/serialization_valid.properties"};

		try{
			workflow.start(args);
		}catch(Exception e){
			e.printStackTrace();
			correct = false;
		}
		assertTrue(correct);
	}

	@Test
	public void testRegressionCrossvalidation(){
		String[] args = {"-wekaExp",
				"-exptype", "regression", 
				"-splittype", "crossvalidation",
				"-runs", "2", 
				"-fold", "8",  
		"C:/Users/uazad/Desktop/università/Stage/Progetto/OUTLINE/configuration/try_regression.properties"};

		try{
			workflow.start(args);
		}catch(Exception e){
			correct = false;
		}
		assertTrue(correct);
	}

	@Test
	public void testClassificationRandomsplit(){
		String[] args = {"-wekaExp",
				"-exptype", "classification", 
				"-splittype", "randomsplit",
				"-runs", "2", 
				"-percentage", "80",
				"-randomized",
		"C:/Users/uazad/Desktop/università/Stage/Progetto/OUTLINE/configuration/serialization_valid.properties"};

		try{
			workflow.start(args);
		}catch(Exception e){
			correct = false;
		}
		assertTrue(correct);
	}

	@Test
	public void testRegressionRandomsplit(){
		String[] args = {"-wekaExp",
				"-exptype", "regression", 
				"-splittype", "randomsplit",
				"-runs", "8", 
				"-percentage", "70",  
		"C:/Users/uazad/Desktop/università/Stage/Progetto/OUTLINE/configuration/try_regression.properties"};

		try{
			workflow.start(args);
		}catch(Exception e){
			correct = false;
		}
		assertTrue(correct);
	}

	@Test
	public void testTryWarning(){
		String[] args = {"-wekaExp",
				"-exptype", "NULL", 
				"-splittype", "NULL",
				"-runs", "NULL", 
				"-folds", "NULL",
		"C:/Users/uazad/Desktop/università/Stage/Progetto/OUTLINE/configuration/serialization_valid.properties"};

		try{
			workflow.start(args);
		}catch(Exception e){
			correct = false;
		}
		assertTrue(correct);
	}

	@Test
	public void testCCWithPath(){
		String[] args = {"-wekaExp",
				"-exptype", "classification", 
				"-splittype", "crossvalidation",
				"-runs", "2", 
				"-folds", "8", 
				"C:/Users/uazad/Desktop/università/Stage/Progetto/OUTLINE/configuration/Configuration_for_testing"
						+ "/serialization/serialization_valid_with_path.properties"};

		try{
			workflow.start(args);
		}catch(Exception e){
			e.printStackTrace();
			correct = false;
		}
		assertTrue(correct);
	}
	
	@Test
	public void testCustomExperiment(){
		String[] args = {"-customExp",
				"-runs", "2", 
				"-folds", "8", 
		"C:/Users/uazad/Desktop/università/Stage/Progetto/OUTLINE/configuration/serialization_valid.properties"};

		try{
			workflow.start(args);
		}catch(Exception e){
			e.printStackTrace();
			correct = false;
		}
		assertTrue(correct);
	}
	
	/*
	 @Test
	    public void testTryStressTest(){
	    	String[] args = {"-customExp",
	    					"-runs", "10", 
	    					"-folds", "10",
	    			"C:/Users/uazad/Desktop/università/Stage/Progetto/OUTLINE/configuration/is_dataset_classification.properties"};

	    	try{
	    		workflow.start(args);
	    	}catch(Exception e){
	    		correct = false;
	    	}
	    	assertTrue(correct);
	    }
	*/

	/*
    //!!!!!!! THIS TEST NEED 1h 35m TO BE RUN  (5745,768 s) !!!!!!!!!!!!
    @Test
    public void testTryStressTest(){
    	String[] args = {"-wekaExp",
    					"-exptype", "classification", 
    					"-splittype", "crossvalidation",
    					"-runs", "10", 
    					"-folds", "10",
    			"C:/Users/uazad/Desktop/università/Stage/Progetto/OUTLINE/configuration/is_dataset_classification.properties"};

    	try{
    		workflow.start(args);
    	}catch(Exception e){
    		correct = false;
    	}
    	assertTrue(correct);
    }
	 */

}
