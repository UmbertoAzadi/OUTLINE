package it.unimib.disco.essere;

import org.junit.Before;
import org.junit.Test;

import it.unimib.disco.essere.core.EntryPoint;
import junit.framework.TestCase;

public class TestPrediction extends TestCase{
	
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
    public void testValidPredByConfigFile(){
    	String[] args = {"-pred", 
    			"C:/Users/uazad/Desktop/università/Stage/Progetto/CodeSmellDetectorML/configuration/try_classification.properties"
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
    			"C:/Users/uazad/Desktop/università/Stage/Progetto/CodeSmellDetectorML/dataset/feature-envy_for_pred.csv",
    			"C:/Users/uazad/Desktop/università/Stage/Progetto/CodeSmellDetectorML/result/2_is_feature_envy_LibSVM.model"
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
    			"C:/Users/uazad/Desktop/università/Stage/Progetto/CodeSmellDetectorML/result/2_is_feature_envy_LibSVM.model",
    			"C:/Users/uazad/Desktop/università/Stage/Progetto/CodeSmellDetectorML/dataset/feature-envy_for_pred.csv",	
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
    			"C:/Users/uazad/Desktop/università/Stage/Progetto/CodeSmellDetectorML/result/2_is_feature_envy_LibSVM.model",
    			"C:/Users/uazad/Desktop/università/Stage/Progetto/CodeSmellDetectorML/dataset/feature-envy_for_pred",	
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
    			"C:/Users/uazad/Desktop/università/Stage/Progetto/CodeSmellDetectorML/result/2_is_feature_envy_LibSVM",
    			"C:/Users/uazad/Desktop/università/Stage/Progetto/CodeSmellDetectorML/dataset/feature-envy_for_pred.csv",	
    	};
    	
    	try{
    		workflow.start(argsSD);
    	}catch(Exception e){
    		correct = false;
    	}
    	assertFalse(correct);
    }
}
