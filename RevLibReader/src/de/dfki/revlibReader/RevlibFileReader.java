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
		if (line.startsWith("t3")) {
			currentCircuit.addGate(readT3GateFromLine(line));
		}
	}
	
	private static ToffoliGate readT3GateFromLine(String line){
		String[] elements = line.split(" ");
		ToffoliGate result = new ToffoliGate();
		result.inputA = elements[1];
		result.inputB = elements[2];
		result.output = elements[3];
		return result;
	}
	
	

}
