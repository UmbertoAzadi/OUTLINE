package com.unimib.CodeSmellDetectorML;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Scanner;

public class EntryPoint {

	public static void main(String[] args) {
		//Scanner input = new Scanner(System.in);
		//System.out.print("Inserisci il path del dataset: ");
		//String datapath = input.nextLine();
		
		Loader data = new Loader(args[0]);
		//data.toCSV("C:/Users/uazad/Documents/Progetto/dataset/long-method.csv");
		DataClassifier DC = new DataClassifier(data.getDataset());
		
		boolean is_boosted = true;
		
		try{
			PrintWriter writer = new PrintWriter("C:/Users/uazad/Documents/Progetto/RisultatiClassificazione.txt", "UTF-8");
			writer.println(DC.applyJ48_pruned(is_boosted)+"\n\n");
		    /*writer.println(DC.applyJRip(is_boosted)+"\n\n");
		    writer.println(DC.applyJ48_unpruned(is_boosted)+"\n\n");
		    writer.println(DC.applySMO_Poly(is_boosted)+"\n\n");
		    writer.println(DC.applySMO_RBF(is_boosted)+"\n\n");
		    writer.println(DC.applyLibSVM_nuSVC_Radial(is_boosted)+"\n\n");
		    writer.println(DC.applyLibSVM_nuSVC_Linear(is_boosted)+"\n\n");*/
			//writer.print(DC.applyAll());
		    writer.close(); 
		} catch (IOException e) {
		   e.printStackTrace();
		}
		
		//DC.applyJ48_pruned(true);
		DataEvaluator DE = new DataEvaluator(DC, data);
		//System.out.println(DE.evaluate(DC.getBj48_pruned()));
		
		//System.out.println(DE.evaluate(DC.getBj48_pruned()));
		//DE.crossValidation(DC.getBj48_pruned());
		
		try{
			PrintWriter writer = new PrintWriter("C:/Users/uazad/Documents/Progetto/RisultatiValutazione.txt", "UTF-8");
			//writer.print(DE.evaluate(DC.getJRip()));
			//writer.print(DE.evaluateAll());
		    writer.close(); 
		} catch (IOException e) {
		   e.printStackTrace();
		}
		
		Loader dl1 = new Loader("C:/Users/uazad/Documents/Progetto/dataset/data-class_for_prediction.arff");
		
		Predictor DP = new Predictor(dl1);
		//DP.makePredicition(DC.getSmo_Poly()).toCSV("C:/Users/uazad/Documents/Progetto/RisultatiPredizione.txt");
		
		Serializer s = new Serializer();
		s.serialize("C:/Users/uazad/Documents/Progetto/ModelloSerializzato", DC.getBj48_pruned());
		System.out.println("SAMPA MODELLO SERIALIZZATO !!!!!!!!!!!!!!!!!");
		System.out.println(s.read("C:/Users/uazad/Documents/Progetto/ModelloSerializzato").toString());
		
		
	}

}
