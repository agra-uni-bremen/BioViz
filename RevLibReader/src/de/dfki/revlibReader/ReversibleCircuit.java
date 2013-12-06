package de.dfki.revlibReader;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Vector;

public class ReversibleCircuit {
	private Vector<ToffoliGate> gates = new Vector<ToffoliGate>();

	private Vector<String> vars = new Vector<String>();
	private HashMap<ToffoliGate, Integer> indicesOfGates = new HashMap<ToffoliGate, Integer>();
	private HashSet<String> garbageLines = new HashSet<String>();
	private HashSet<String> functionLines = new HashSet<String>();
	private HashMap<String, ToffoliGate> firstGates = new HashMap<String, ToffoliGate>();
	private HashMap<String, ToffoliGate> lastGates = new HashMap<String, ToffoliGate>();
	HashSet<String> inputOnly = new HashSet<String>();
	HashSet<String> outputOnly = new HashSet<String>();
	HashSet<String> mixedInputOutput = new HashSet<String>();
	HashMap<String, Integer> constOnly = new HashMap<String, Integer>();

	public void addGate(ToffoliGate g) {
		this.gates.add(g);

		for (int i = 0; i < g.getInputs().size(); i++) {
			if (!(vars.contains(g.getInputs().get(i)))) {
				vars.add(g.getInputs().get(i));
			}
			if (!(firstGates.containsKey(g.getInputs().get(i)))) {
				firstGates.put(g.getInputs().get(i), g);
			} if (!mixedInputOutput.contains(g.getInputs().get(i))) {
				inputOnly.add(g.getInputs().get(i));
			} if (outputOnly.contains(g.getInputs().get(i))) {
				mixedInputOutput.add(g.getInputs().get(i));
				outputOnly.remove(g.getInputs().get(i));
				inputOnly.remove(g.getInputs().get(i));
			}
			lastGates.put(g.getInputs().get(i), g);
		}
		if (!(vars.contains(g.output))) {
			vars.add(g.output);
		}
		if (!(firstGates.containsKey(g.output))) {
			firstGates.put(g.output, g);
		}
		if (!mixedInputOutput.contains(g.output)) {
			outputOnly.add(g.output);
		} if (inputOnly.contains(g.output)) {
			mixedInputOutput.add(g.output);
			inputOnly.remove(g.output);
			outputOnly.remove(g.output);
		}
		lastGates.put(g.output, g);
	}

	public ToffoliGate getGate(int gateNumber) {
		return gates.get(gateNumber);
	}

	public int getAmountOfVars() {
		return vars.size();
	}

	public Collection<ToffoliGate> getGates() {
		return gates;
	}

	public Vector<String> getVars() {
		return vars;
	}

	public void setVars(Vector<String> allVars) {
		this.vars = allVars;
	}

	public long totalDistance() {
		long result = 0;
		for (int i = 0; i < gates.size(); i++) {
			result += gates.get(i).calculateDistance(this.vars);
		}
		return result;
	}

	public boolean isTargetOnly(String var) {
		return outputOnly.contains(var);
	}

	public boolean isInputOnly(String var) {
		return inputOnly.contains(var);
	}

	public void addGarbageLine(String line) {
		this.garbageLines.add(line);
	}

	public void addFunctionLine(String line) {
		this.functionLines.add(line);
	}

	public boolean isGarbageLine(String line) {
		return this.garbageLines.contains(line);
	}

	public boolean isFunctionLine(String line) {
		return this.functionLines.contains(line);
	}

	public ToffoliGate getFirstGateOnLine(String line) {
		return this.firstGates.get(line);
	}

	public ToffoliGate getLastGateOnLine(String line) {
		return this.lastGates.get(line);
	}

	public int getCoordOfGate(ToffoliGate gate) {
		if (!indicesOfGates.containsKey(gate))
			indicesOfGates.put(gate, gates.indexOf(gate));
		return indicesOfGates.get(gate);
	}

	public void addLine(String varName) {
		if (!this.vars.contains(varName))
			this.vars.add(varName);
	}
	
	public int getAmountOfGarbageLines() {
		return this.garbageLines.size();
	}
	
	/**
	 * Checks whether or not a line is a designated constant input line
	 * @param line the name of the line to check
	 * @return the const value if the line is a const, -1 else
	 */
	public int constValue(String line) {
		if (constOnly.containsKey(line)) {
			return constOnly.get(line);
		} else {
			return -1;
		}
	}
	
	public void addConstLine(String line, int value) {
		this.constOnly.put(line, value);
	}
	
	public int calculateGateMobilityLeft(int gate) {
		return calculateGateMobility(gate, false);
	}
	
	public int calculateGateMobilityRight(int gate) {
		return calculateGateMobility(gate, true);
	}
	
	private int calculateGateMobility(int gate, boolean goRight) {
		int direction = 0;
		if (goRight)
			direction = 1;
		else
			direction = -1;
		
		HashSet<String> controlVars = new HashSet<String>();
		String target = this.gates.get(gate).output;
		for (int i = 0; i < this.gates.get(gate).getInputs().size(); i++) {
			controlVars.add(this.gates.get(gate).getInputs().get(i));
		}
		
		int movement = 0;
		//boolean blocked = false;
		while(true) {
			movement += direction;
			
			if (gate + movement >= 0 && gate + movement < this.gates.size()) {  
				ToffoliGate g = this.gates.get(gate + movement);
				
				//First rule: The given gate's CONTROL gates may not cross any other
				// TARGET gate.
				if (this.gates.get(gate).getInputs().contains(g.output)) {
					break;
				}
				
				//Second rule: The given gate's TARGET gate may not cross any other
				// CONTROL gate.
				if (g.getInputs().contains(this.gates.get(gate).output)) {
					break;
				}
				
			} else {
				break;
			}
		}
		
		return Math.abs(movement - direction);
	}
}
