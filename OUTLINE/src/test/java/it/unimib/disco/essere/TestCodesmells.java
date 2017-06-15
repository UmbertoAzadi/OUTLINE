package it.unimib.disco.essere;

import org.junit.Before;

import it.unimib.disco.essere.core.EntryPoint;
import junit.framework.TestCase;

public class TestCodesmells extends TestCase {
	
	EntryPoint workflow;
	boolean correct; 
	long start;
 
	@Before
	public void setUp(){
		workflow = new EntryPoint();
		correct = true;
		start = System.currentTimeMillis();
		System.out.println("");
		System.out.println("<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<");
		System.out.println("\nTESTING: "+this.getName());
		System.out.println("");
	}

	public void testDataClass(){
		String[] args = {"-wekaExp",
				"-exptype", "classification", 
				"-splittype", "crossvalidation",
				"-runs", "10", 
				"-folds", "10", 
				"C:\\Users\\uazad\\Desktop\\università\\Stage\\Progetto\\CodeSmellDetectorML\\codesmells"
						+ "\\is_data_class\\is_data_class.properties"};

		try{
			workflow.start(args);
		}catch(Exception e){
			e.printStackTrace();
			correct = false;
		}
		
		System.out.println("time request: " + (System.currentTimeMillis() - start));
		
		assertTrue(correct);
	}
	
	public void testFeatureEnvy(){
		String[] args = {"-wekaExp",
				"-exptype", "classification", 
				"-splittype", "crossvalidation",
				"-runs", "10", 
				"-folds", "10", 
				"C:\\Users\\uazad\\Desktop\\università\\Stage\\Progetto\\CodeSmellDetectorML\\codesmells"
						+ "\\is_feature_envy\\is_feature_envy.properties"};

		try{
			workflow.start(args);
		}catch(Exception e){
			e.printStackTrace();
			correct = false;
		}
		assertTrue(correct);
	}
	
	public void testGodClass(){
		String[] args = {"-wekaExp",
				"-exptype", "classification", 
				"-splittype", "crossvalidation",
				"-runs", "10", 
				"-folds", "10", 
				"C:\\Users\\uazad\\Desktop\\università\\Stage\\Progetto\\CodeSmellDetectorML\\codesmells"
						+ "\\is_god_class\\is_god_class.properties"};

		try{
			workflow.start(args);
		}catch(Exception e){
			e.printStackTrace();
			correct = false;
		}
		assertTrue(correct);
	}
	
	public void testLongMethod(){
		String[] args = {"-wekaExp",
				"-exptype", "classification", 
				"-splittype", "crossvalidation",
				"-runs", "10", 
				"-folds", "10", 
				"C:\\Users\\uazad\\Desktop\\università\\Stage\\Progetto\\CodeSmellDetectorML\\codesmells"
						+ "\\is_long_method\\is_long_method.properties"};

		try{
			workflow.start(args);
		}catch(Exception e){
			e.printStackTrace();
			correct = false;
		}
		assertTrue(correct);
	}
	
}
