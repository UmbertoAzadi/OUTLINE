package com.unimib.CodeSmellDetectorML;

import weka.core.Instances;
import weka.core.OptionHandler;
import weka.classifiers.Classifier;
import weka.classifiers.bayes.NaiveBayes;
import weka.classifiers.trees.J48;
import weka.classifiers.functions.SMO;
import weka.classifiers.trees.RandomForest;
import weka.classifiers.functions.LibSVM; // < chiedere se hanno usato questa!!!
import weka.classifiers.rules.JRip;
import weka.classifiers.meta.AdaBoostM1;

public class DataClassifier {
	
	private Instances dataset;
	
	// classificatori senza boost
	private J48 j48_pruned = new J48();
	private J48 j48_unpruned = new J48();
	private J48 j48_reduce_error = new J48();
	private JRip jRip = new JRip();
	private RandomForest randomForest = new RandomForest();
	private NaiveBayes naiveBayes = new NaiveBayes();
	private SMO smo_RBF = new SMO();
	private SMO smo_Poly = new SMO();
	private LibSVM libSVM_CSVC_Linear = new LibSVM();
	private LibSVM libSVM_CSVC_Polynomial = new LibSVM();
	private LibSVM libSVM_CSVC_Radial = new LibSVM();
	private LibSVM libSVM_CSVC_Sigmoid = new LibSVM();
	private LibSVM libSVM_nuSVC_Linear = new LibSVM();
	private LibSVM libSVM_nuSVC_Polynomial = new LibSVM();
	private LibSVM libSVM_nuSVC_Radial = new LibSVM();
	private LibSVM libSVM_nuSVC_Sigmoid = new LibSVM();
	
	// classificatori con boost
	private int numIterationAda = 10;
	
	private AdaBoostM1 Bj48_pruned = new AdaBoostM1();
	private AdaBoostM1 Bj48_unpruned = new AdaBoostM1();
	private AdaBoostM1 Bj48_reduce_error = new AdaBoostM1();
	private AdaBoostM1 BjRip = new AdaBoostM1();
	private AdaBoostM1 BrandomForest = new AdaBoostM1();
	private AdaBoostM1 BnaiveBayes = new AdaBoostM1();
	private AdaBoostM1 Bsmo_RBF = new AdaBoostM1();
	private AdaBoostM1 Bsmo_Poly = new AdaBoostM1();
	private AdaBoostM1 BlibSVM_CSVC_Linear = new AdaBoostM1();
	private AdaBoostM1 BlibSVM_CSVC_Polynomial = new AdaBoostM1();
	private AdaBoostM1 BlibSVM_CSVC_Radial = new AdaBoostM1();
	private AdaBoostM1 BlibSVM_CSVC_Sigmoid = new AdaBoostM1();
	private AdaBoostM1 BlibSVM_nuSVC_Linear = new AdaBoostM1();
	private AdaBoostM1 BlibSVM_nuSVC_Polynomial = new AdaBoostM1();
	private AdaBoostM1 BlibSVM_nuSVC_Radial = new AdaBoostM1();
	private AdaBoostM1 BlibSVM_nuSVC_Sigmoid = new AdaBoostM1();
	
	
	public DataClassifier(Instances dataset){
		this.dataset = dataset;
		// li devo applicare fin da subito??
	}
	
	public DataClassifier(Loader dataset){
		this.dataset = dataset.getDataset();
	}
	
	/* Sara' quindi possibile invocarli tutti tramite applyAll
	 * ma anche prenderne solo un sottoinsieme. 
	 * Non ho fatto un metodo applyGenerics che sfutta l'ereditariet� con 
	 * Classifier per dare la lista dei possibili classificatori e anche perch�
	 * quel metodo avrebbe dovuto prendere in input il classificato quindi il suo utilizzo
	 * sarebbe stato: 
	 * 		DataClassifier DC = new DataClassifier(...)
	 * 		DC.applyGenerics(DC.getJ48_pruned()); -> NON MI PIACE
	 * */
	public String applyJ48_pruned(boolean is_boosted){
		if(is_boosted){
			J48 temp = new J48();
			this.buildClassifierBoosted(Bj48_pruned, temp);
			return  Bj48_pruned.toString();
		}else{
			this.buildClassifier(j48_pruned);
			return  j48_pruned.toString();
		}
	}
	
	public String applyJ48_unpruned(boolean is_boosted){
		String[] options = {"-U"};
		if(is_boosted){
			J48 temp = new J48();
			this.buildClassifierBoosted(Bj48_unpruned, temp, temp, options);
			return Bj48_unpruned.toString();
		}else{
			this.buildClassifier(j48_unpruned, j48_unpruned, options);
			return j48_unpruned.toString();
		}
	}
	
	public String applyJ48_reduce_error(boolean is_boosted){
		String[] options = {"-R"};
		if(is_boosted){
			J48 temp = new J48();
			this.buildClassifierBoosted(Bj48_reduce_error, temp, temp, options);
			return Bj48_reduce_error.toString();
		}else{
			this.buildClassifier(j48_reduce_error, j48_reduce_error, options);
			return j48_reduce_error.toString();
		}
	}
	
	public String applyJRip(boolean is_boosted){
		if(is_boosted){
			JRip temp = new JRip();
			this.buildClassifierBoosted(BjRip, temp);	
			return BjRip.toString();
		}else{
			this.buildClassifier(jRip);	
			return jRip.toString();
		}
	}
	
	public String applyRandomForest(boolean is_boosted){
		if(is_boosted){
			RandomForest temp = new RandomForest();
			this.buildClassifierBoosted(BrandomForest, temp);
			return BrandomForest.toString();
		}else{
			this.buildClassifier(randomForest);
			return randomForest.toString();
		}
	}
	
	public String applyNaiveBayes(boolean is_boosted){
		if(is_boosted){
			NaiveBayes temp = new NaiveBayes();
			this.buildClassifierBoosted(BnaiveBayes, temp);
			return BnaiveBayes.toString();
		}else{
			this.buildClassifier(naiveBayes);
			return naiveBayes.toString();
		}
	}
	
	public String applySMO_Poly(boolean is_boosted){
		// di default PolyKernel
		if(is_boosted){
			SMO temp = new SMO();
			this.buildClassifierBoosted(Bsmo_Poly, temp);
			return Bsmo_Poly.toString();
		}else{
			this.buildClassifier(smo_Poly);
			return smo_Poly.toString();
		}
	}
	
	public String applySMO_RBF(boolean is_boosted){
		String[] options = {"-K", "weka.classifiers.functions.supportVector.RBFKernel"};
		if(is_boosted){
			SMO temp = new SMO();
			this.buildClassifierBoosted(Bsmo_RBF, temp, temp, options);
			return Bsmo_RBF.toString();
		}else{
			this.buildClassifier(smo_RBF, smo_RBF, options);
			return smo_RBF.toString();
		}
		
	}
	
	public String applyLibSVM_CSVC_Linear(boolean is_boosted){
		String[] options = {"-S", "0", "-K", "0"};
		if(is_boosted){
			LibSVM temp = new LibSVM();
			this.buildClassifierBoosted(BlibSVM_CSVC_Linear, temp, temp, options);
			return BlibSVM_CSVC_Linear.toString();
		}else{
			this.buildClassifier(libSVM_CSVC_Linear, libSVM_CSVC_Linear, options);
			return this.getSummaryLibSVM(libSVM_CSVC_Linear, "LibSVM CSVC Linear");
		}	
	}
	
	public String applyLibSVM_CSVC_Polynomial(boolean is_boosted){
		String[] options = {"-S", "0", "-K", "1"};
		if(is_boosted){
			LibSVM temp = new LibSVM();
			this.buildClassifierBoosted(BlibSVM_CSVC_Polynomial, temp, temp, options);
			return BlibSVM_CSVC_Polynomial.toString();
		}else{
			this.buildClassifier(libSVM_CSVC_Polynomial, libSVM_CSVC_Polynomial, options);
			return this.getSummaryLibSVM(libSVM_CSVC_Polynomial, "LibSVM CSVC Polynomial");
		}
	}
	
	public String applyLibSVM_CSVC_Radial(boolean is_boosted){
		String[] options = {"-S", "0", "-K", "2"};
		if(is_boosted){
			LibSVM temp = new LibSVM();
			this.buildClassifierBoosted(BlibSVM_CSVC_Radial, temp, temp, options);
			return BlibSVM_CSVC_Radial.toString();
		}else{
			this.buildClassifier(libSVM_CSVC_Radial, libSVM_CSVC_Radial, options);
			return this.getSummaryLibSVM(libSVM_CSVC_Radial, "LibSVM CSVC Radial");
		}
		
		
	}
	
	public String applyLibSVM_CSVC_Sigmoid(boolean is_boosted){
		String[] options = {"-S", "0", "-K", "3"};
		if(is_boosted){
			LibSVM temp = new LibSVM();
			this.buildClassifierBoosted(BlibSVM_CSVC_Sigmoid, temp, temp, options);
			return BlibSVM_CSVC_Sigmoid.toString();
		}else{
			this.buildClassifier(libSVM_CSVC_Sigmoid, libSVM_CSVC_Sigmoid, options);
			return this.getSummaryLibSVM(libSVM_CSVC_Sigmoid, "LibSVM CSVC Sigmoid");
		}
	}
	
	public String applyLibSVM_nuSVC_Linear(boolean is_boosted){
		String[] options = {"-S", "1", "-K", "0"};
		if(is_boosted){
			LibSVM temp = new LibSVM();
			this.buildClassifierBoosted(BlibSVM_nuSVC_Linear, temp, temp, options);
			return BlibSVM_nuSVC_Linear.toString();
		}else{
			this.buildClassifier(libSVM_nuSVC_Linear, libSVM_nuSVC_Linear, options);
			return this.getSummaryLibSVM(libSVM_nuSVC_Linear, "LibSVM nuSVC Linear");
		}
		
	}
	
	public String applyLibSVM_nuSVC_Polynomial(boolean is_boosted){
		String[] options = {"-S", "1", "-K", "1"};
		if(is_boosted){
			LibSVM temp = new LibSVM();
			this.buildClassifierBoosted(BlibSVM_nuSVC_Polynomial, temp, temp, options);
			return BlibSVM_nuSVC_Polynomial.toString();
		}else{
			this.buildClassifier(libSVM_nuSVC_Polynomial, libSVM_nuSVC_Polynomial, options);
			return this.getSummaryLibSVM(libSVM_nuSVC_Polynomial, "LibSVM nuSVC Polynomial");
		}
	}
	
	public String applyLibSVM_nuSVC_Radial(boolean is_boosted){
		//String[] options = {"-S", "1", "-K", "2", "-N", "1"};
		String opt = "-P 100 -S 1 -K 2 -D 3 -G 0.0 -R 0.0 -N 0.2 -M 40.0 -C 1.0 -E 0.001 -seed 1";
		String[] options = opt.split(" ");
		if(is_boosted){
			LibSVM temp = new LibSVM();
			this.buildClassifierBoosted(BlibSVM_nuSVC_Radial, temp, temp, options);
			return BlibSVM_nuSVC_Radial.toString();
		}else{
			this.buildClassifier(libSVM_nuSVC_Radial, libSVM_nuSVC_Radial, options);
			return this.getSummaryLibSVM(libSVM_nuSVC_Radial, "LibSVM nuSVC Radial");
		}
	}
	
	public String applyLibSVM_nuSVC_Sigmoid(boolean is_boosted){
		String[] options = {"-S", "1", "-K", "3"};
		if(is_boosted){
			LibSVM temp = new LibSVM();
			this.buildClassifierBoosted(BlibSVM_nuSVC_Sigmoid, temp, temp, options);
			return BlibSVM_nuSVC_Sigmoid.toString();
		}else{
			this.buildClassifier(libSVM_nuSVC_Sigmoid, libSVM_nuSVC_Sigmoid, options);
			return this.getSummaryLibSVM(libSVM_nuSVC_Sigmoid, "LibSVM nuSVC Sigmoid");
		}
	}
	
	private String getSummaryLibSVM(LibSVM classifier, String name){
		String summary = name+"\n";
		
		double[][] a = null;
		try {
			a = classifier.distributionsForInstances(dataset);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		for(int i=0;i<a.length;i++){
			summary += i+") ";;
			for(int j=0;j<a[0].length;j++){
				summary += a[i][j]+" ";
			}
			summary += "\n";
		}
		
		return summary;
	}
	
	public void buildClassifierBoosted(AdaBoostM1 boost, Classifier c){
		boost.setNumIterations(numIterationAda);
		boost.setClassifier(c);
		try {
			boost.buildClassifier(dataset);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void buildClassifierBoosted(AdaBoostM1 boost, Classifier c, OptionHandler o, String[] options){
		try {  
			o.setOptions(options);
			boost.setNumIterations(numIterationAda);
			boost.setClassifier(c);
			boost.buildClassifier(dataset);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void buildClassifier(Classifier c){
		try {
			c.buildClassifier(dataset);
		} catch (Exception e) {
			e.printStackTrace();
		} 
	}
	
	public void buildClassifier(Classifier c, OptionHandler o, String[] options){
		try {
			o.setOptions(options);
			c.buildClassifier(dataset);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public Instances getDataset() {
		return dataset;
	}

	public J48 getJ48_pruned() {
		return j48_pruned;
	}

	public J48 getJ48_unpruned() {
		return j48_unpruned;
	}

	public J48 getJ48_reduce_error() {
		return j48_reduce_error;
	}

	public JRip getjRip() {
		return jRip;
	}

	public RandomForest getRandomForest() {
		return randomForest;
	}

	public NaiveBayes getNaiveBayes() {
		return naiveBayes;
	}

	public SMO getSmo_RBF() {
		return smo_RBF;
	}

	public SMO getSmo_Poly() {
		return smo_Poly;
	}

	public LibSVM getLibSVM_CSVC_Linear() {
		return libSVM_CSVC_Linear;
	}

	public LibSVM getLibSVM_CSVC_Polynomial() {
		return libSVM_CSVC_Polynomial;
	}

	public LibSVM getLibSVM_CSVC_Radial() {
		return libSVM_CSVC_Radial;
	}

	public LibSVM getLibSVM_CSVC_Sigmoid() {
		return libSVM_CSVC_Sigmoid;
	}

	public LibSVM getLibSVM_nuSVC_Linear() {
		return libSVM_nuSVC_Linear;
	}

	public LibSVM getLibSVM_nuSVC_Polynomial() {
		return libSVM_nuSVC_Polynomial;
	}

	public LibSVM getLibSVM_nuSVC_Radial() {
		return libSVM_nuSVC_Radial;
	}

	public LibSVM getLibSVM_nuSVC_Sigmoid() {
		return libSVM_nuSVC_Sigmoid;
	}

	public AdaBoostM1 getBj48_pruned() {
		return Bj48_pruned;
	}

	public AdaBoostM1 getBj48_unpruned() {
		return Bj48_unpruned;
	}

	public AdaBoostM1 getBj48_reduce_error() {
		return Bj48_reduce_error;
	}

	public AdaBoostM1 getBjRip() {
		return BjRip;
	}

	public AdaBoostM1 getBrandomForest() {
		return BrandomForest;
	}

	public AdaBoostM1 getBnaiveBayes() {
		return BnaiveBayes;
	}

	public AdaBoostM1 getBsmo_RBF() {
		return Bsmo_RBF;
	}

	public AdaBoostM1 getBsmo_Poly() {
		return Bsmo_Poly;
	}

	public AdaBoostM1 getBlibSVM_CSVC_Linear() {
		return BlibSVM_CSVC_Linear;
	}

	public AdaBoostM1 getBlibSVM_CSVC_Polynomial() {
		return BlibSVM_CSVC_Polynomial;
	}

	public AdaBoostM1 getBlibSVM_CSVC_Radial() {
		return BlibSVM_CSVC_Radial;
	}

	public AdaBoostM1 getBlibSVM_CSVC_Sigmoid() {
		return BlibSVM_CSVC_Sigmoid;
	}

	public AdaBoostM1 getBlibSVM_nuSVC_Linear() {
		return BlibSVM_nuSVC_Linear;
	}

	public AdaBoostM1 getBlibSVM_nuSVC_Polynomial() {
		return BlibSVM_nuSVC_Polynomial;
	}

	public AdaBoostM1 getBlibSVM_nuSVC_Radial() {
		return BlibSVM_nuSVC_Radial;
	}

	public AdaBoostM1 getBlibSVM_nuSVC_Sigmoid() {
		return BlibSVM_nuSVC_Sigmoid;
	}

	public int getNumIterationAda() {
		return numIterationAda;
	}
	
	public void setNumIterationAda(int num) {
		this.numIterationAda = num;
	}
	
	public String applyAll(){
		String summary = "";
		
		System.out.println("Learning...");
		
		System.out.println("J48 pruned");
		summary += this.applyJ48_pruned(true) + "\n\n";
		summary += this.applyJ48_pruned(false) + "\n\n";
		
		System.out.println("J48 unpruned");
		summary += this.applyJ48_unpruned(true) + "\n\n";
		summary += this.applyJ48_unpruned(false) + "\n\n";
		
		System.out.println("J48 reduce error");
		summary += this.applyJ48_reduce_error(true) + "\n\n";
		summary += this.applyJ48_reduce_error(false) + "\n\n";
		
		System.out.println("JRip");
		summary += this.applyJRip(true) + "\n\n";
		summary += this.applyJRip(false) + "\n\n";
		
		System.out.println("Random forest");
		summary += this.applyRandomForest(true) + "\n\n";
		summary += this.applyRandomForest(false) + "\n\n";
		
		System.out.println("Naive Bayes");
		summary += this.applyNaiveBayes(true) + "\n\n";
		summary += this.applyNaiveBayes(false) + "\n\n";
		
		System.out.println("SMO Poly Kernel");
		summary += this.applySMO_Poly(true) + "\n\n";
		summary += this.applySMO_Poly(false) + "\n\n";
		
		System.out.println("SMO RBF Kernel");
		summary += this.applySMO_RBF(true) + "\n\n";
		summary += this.applySMO_RBF(false) + "\n\n";
		
		System.out.println("LibSVM CSVC Linear");
		summary += this.applyLibSVM_CSVC_Linear(true) + "\n\n";
		summary += this.applyLibSVM_CSVC_Linear(false) + "\n\n";
		
		System.out.println("LibSVM CSVC Polynomial");
		summary += this.applyLibSVM_CSVC_Polynomial(true) + "\n\n";
		summary += this.applyLibSVM_CSVC_Polynomial(false) + "\n\n";
		
		System.out.println("LibSVM CSVC Radial");
		summary += this.applyLibSVM_CSVC_Radial(true) + "\n\n";
		summary += this.applyLibSVM_CSVC_Radial(false) + "\n\n";
		
		System.out.println("LibSVM CSVC Sigmoid");
		summary += this.applyLibSVM_CSVC_Sigmoid(true) + "\n\n";
		summary += this.applyLibSVM_CSVC_Sigmoid(false) + "\n\n";
		
		System.out.println("LibSVM nuSVC Linear");
		summary += this.applyLibSVM_nuSVC_Linear(true) + "\n\n";
		summary += this.applyLibSVM_nuSVC_Linear(false) + "\n\n";
		
		System.out.println("LibSVM nuSVC Polynomial");
		summary += this.applyLibSVM_nuSVC_Polynomial(true) + "\n\n";
		summary += this.applyLibSVM_nuSVC_Polynomial(false) + "\n\n";
		
		System.out.println("LibSVM nuSVC Radial");
		summary += this.applyLibSVM_nuSVC_Radial(true) + "\n\n";
		summary += this.applyLibSVM_nuSVC_Radial(false) + "\n\n";
		
		System.out.println("LibSVM nuSVC Sigmoid");
		summary += this.applyLibSVM_nuSVC_Sigmoid(true) + "\n\n";
		summary += this.applyLibSVM_nuSVC_Sigmoid(false) + "\n\n";
		
		return summary;
	}
	
}
