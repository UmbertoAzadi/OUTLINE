package it.unimib.disco.essere.test;

import org.junit.Before;
import org.junit.Test;


import it.unimib.disco.essere.core.InputParser;
import junit.framework.TestCase;

public class TestPrediction extends TestCase{
	
	InputParser workflow;
	boolean correct; 
	
    @Before
    public void setUp(){
    	workflow = new InputParser();
    	correct = true;
    	System.out.println("");
    	System.out.println("<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<");
    	System.out.println("\nTESTING: "+this.getName());
    	System.out.println("");
    }
    
    @Test 
    public void testValidPredByConfigFile(){
    	String[] args = {"-pred", 
    			"C:/Users/uazad/Desktop/università/Stage/Progetto/OUTLINE/configuration/Configuration_for_testing/try_prediction.properties"
    	};
    	
    	try{
    		workflow.start(args);
    	}catch(Exception e){
    		correct = false;
    	}
    	assertTrue(correct);
    }

    @Test
    public void testValidPredByArgumentsDatasetSerializedFile(){
    	String[] argsDS = {"-pred",
    			"C:/Users/uazad/Desktop/università/Stage/Progetto/OUTLINE/dataset/feature-envy_for_pred.csv",
    			"C:/Users/uazad/Desktop/università/Stage/Progetto/OUTLINE/result/2_is_feature_envy_LibSVM.model"
    	};
    	
    	try{
    		workflow.start(argsDS);
    	}catch(Exception e){
    		correct = false;
    	}
    	assertTrue(correct);
    }
    
    @Test
    public void testValidPredByArgumentsSerializedFileDataset(){
    	String[] argsSD = {"-pred",
    			"C:/Users/uazad/Desktop/università/Stage/Progetto/OUTLINE/result/2_is_feature_envy_LibSVM.model",
    			"C:/Users/uazad/Desktop/università/Stage/Progetto/OUTLINE/dataset/feature-envy_for_pred.csv",	
    	};
    	
    	try{
    		workflow.start(argsSD);
    	}catch(Exception e){
    		correct = false;
    	}
    	assertTrue(correct);
    }
    
    @Test
    public void testBadPathDataset(){
    	String[] argsSD = {"-pred",
    			"C:/Users/uazad/Desktop/università/Stage/Progetto/OUTLINE/result/2_is_feature_envy_LibSVM.model",
    			"C:/Users/uazad/Desktop/università/Stage/Progetto/OUTLINE/dataset/feature-envy_for_pred",	
    	};
    	
    	try{
    		workflow.start(argsSD);
    	}catch(Exception e){
    		correct = false;
    	}
    	assertFalse(correct);
    }
    
    @Test
    public void testBadPathModel(){
    	String[] argsSD = {"-pred",
    			"C:/Users/uazad/Desktop/università/Stage/Progetto/OUTLINE/result/2_is_feature_envy_LibSVM",
    			"C:/Users/uazad/Desktop/università/Stage/Progetto/OUTLINE/dataset/feature-envy_for_pred.csv",	
    	};
    	
    	try{
    		workflow.start(argsSD);
    	}catch(Exception e){
    		correct = false;
    	}
    	assertFalse(correct);
    }
}
