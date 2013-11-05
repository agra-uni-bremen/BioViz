package de.dfki.revlibReader;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class RevlibFileReader {
	private static ReversibleCircuit currentCircuit;
	
	public static ReversibleCircuit readRealFile(String filename) {
		BufferedReader br = null;
		currentCircuit = new ReversibleCircuit();
		try {
			br = new BufferedReader(new FileReader(filename));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (br != null) {
			
			String line;
			try {
				while((line = br.readLine()) != null) {
					readLine(line);
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return currentCircuit;
	}
	
	private static void readLine(String line) {
		if (line.startsWith("t")) {
			currentCircuit.addGate(readT3GateFromLine(line));
		}
	}
	
	private static ToffoliGate readT3GateFromLine(String line){
		String[] elements = line.split(" ");
		ToffoliGate result = new ToffoliGate();
		for (int i = 1; i < elements.length - 1; i++) {
			result.addInput(elements[i]);
		}
		result.output = elements[elements.length - 1];
		return result;
	}
	
	

}
