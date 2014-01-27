package de.dfki.revlibReader;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class RevlibFileReader {
	private ReversibleCircuit currentCircuit;
	
	public static ReversibleCircuit readRealFileContents(String fileContents) {
		RevlibFileReader rfr = new RevlibFileReader();
		return rfr.readRealContents(fileContents);
	}
	
	public ReversibleCircuit readRealContents(String fileContents) {
		currentCircuit = new ReversibleCircuit();
		String[] lines = fileContents.split("\n");
		for (int i = 0; i < lines.length; i++) {
			if (readLine(lines[i]))
				return currentCircuit;
		}
		return currentCircuit;
	}
	
	public static ReversibleCircuit readRealFile(String filename) {
		RevlibFileReader rfr = new RevlibFileReader();
		return rfr.readReal(filename);
	}
	
	public ReversibleCircuit readReal(String filename) {
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
					if(readLine(line));
						return currentCircuit;
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return currentCircuit;
	}
	
	/**
	 * Reads a line from the real file to be added to the current circuit
	 * @param line the line to be read
	 * @return true when the circuit has been read, false otherwise
	 */
	private boolean readLine(String line) {
		if (line.startsWith("t")) {
			currentCircuit.addGate(readT3GateFromLine(line));
		} else if (line.startsWith(".variables")) {
			readVariablesLine(line);
		} else if (line.startsWith(".garbage")) {
			readGarbageLine(line);
		} else if (line.startsWith(".constants")) {
			readConstLine(line);
		} else if (line.startsWith(".inputbus") || line.startsWith(".outputbus")) {
			readBus(line);
		} else if (line.startsWith(".module")) {
			
		} else if (line.startsWith(".end")) {
			return true;
		}
		return false;
	}
	
	private static ToffoliGate readT3GateFromLine(String line){
		String[] elements = line.split(" ");
		ToffoliGate result = new ToffoliGate();
		int lastElement = 0;
		for (int i = 1; i < elements.length - 1; i++) {
			if (!elements[i+1].trim().startsWith("#")) {
				result.addInput(elements[i]);
				lastElement++;
			}
			else {
				//lastElement++;
				break;
			}
		}
		result.output = elements[lastElement + 1].trim();
		return result;
	}
	
	private void readVariablesLine(String line) {
		String[] elements = line.split(" ");
		for (int i = 1; i < elements.length; i++) {
			if (!elements[i].startsWith("#")) {
				if (elements[i].length() > 0) {
					currentCircuit.addLine(elements[i]);
				}
			}
			else {
				break;
			}
		}
		if (currentCircuit.getAmountOfVars() >= 0)
			assert(currentCircuit.getVars().size() == currentCircuit.getVarAmount());
	}
	
	private void readGarbageLine(String line) {
		String[] elements = line.split(" ");
		String garbageInfo = elements[1];
		for (int j = 0; j < garbageInfo.length(); j++) {
			if (garbageInfo.charAt(j) != '-' && j < currentCircuit.getVars().size())
				currentCircuit.addGarbageLine(currentCircuit.getVars().elementAt(j));
		}
	}
	
	private void readConstLine(String line) {
		String[] elements = line.split(" ");
		String constInfo = elements[1];
		for (int j = 0; j < constInfo.length(); j++) {
			if (constInfo.charAt(j) != '-') {
				String toParse = new String(new char[]{constInfo.charAt(j)});
				int value;
				try {
					value = Integer.parseInt(toParse);
					currentCircuit.addConstLine(currentCircuit.getVars().elementAt(j), value);
				} catch(Exception e) {
					System.out.println("Could not parse const value " + toParse);
				}
			
				
			}
		}
	}	
	
	private void readFunctionLine(String line) {
		String[] elements = line.split(" ");
		for (int i = 1; i < elements.length - 1; i++) {
			if (!elements[i].startsWith("#"))
				currentCircuit.addFunctionLine(elements[i]);
			else
				break;
		}
	}
	
	private void readBus(String line) {
		String[] elements = line.split(" ");
		for (int i = 2; i < elements.length; i++) {
			currentCircuit.addToBus(elements[i].trim(), elements[1].trim());
		}
	}

}
