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
	private HashMap<String, Integer> lineUsage;
	HashSet<String> inputOnly = new HashSet<String>();
	HashSet<String> outputOnly = new HashSet<String>();
	HashSet<String> mixedInputOutput = new HashSet<String>();
	HashMap<String, Integer> constOnly = new HashMap<String, Integer>();
	private int[] movingRuleAccumulations;
	private int maximumMovingRuleAccumulation = 0;
	private int maximumLineUsage = 0;
	private int amountOfVars = -1;
	private HashMap<String, HashSet<String>> buses = new HashMap<String, HashSet<String>>();
	private Vector<String> busNames = new Vector<String>();
	boolean recalculateMobility = true;
	int cachedMaxMobility = 0;
	private HashMap<String, ReversibleCircuit> subCircuits = new HashMap<String, ReversibleCircuit>();
	private Vector<subCircuitDimensions> insertedCircuits = new Vector<subCircuitDimensions>();

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
		
		recalculateMobility = true;
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
	
	public void setVarAmount(int varCount) {
		this.amountOfVars = varCount;
	}
	
	public int getVarAmount() {
		if (this.amountOfVars >= 0) {
			return amountOfVars;
		} else {
			if (this.vars != null) {
				return this.vars.size();
			} else {
				throw new RuntimeException("The amount of variables needs to be either set explicitly or implicitly by having added variables before it can be retrieved.");
			}
		}
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
		varName = varName.trim();
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

		// Only calculate mobility once for each direction!
		if (!(
				(this.gates.get(gate).getMobilityLeft() >= 0 && !goRight) ||
				(this.gates.get(gate).getMobilityRight() >= 0 && goRight)
			)) {
			
			//Special case: Target/Control-ONLY lines
			boolean fullMovement = true;
			if (this.isTargetOnly(this.gates.get(gate).output)) {
				for (int i = 0; i < this.gates.get(gate).getInputs().size(); i++) {
					if (!(this.isInputOnly(this.gates.get(gate).getInputs().get(i)))) {
						fullMovement = false;
					}
				}
			} else {
				fullMovement = false;
			}

			if (fullMovement) {
				this.gates.get(gate).setMobilityRight(this.gates.size() - gate - 1);
				this.gates.get(gate).setMobilityLeft(gate);
			} else {

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

				int value = Math.abs(movement - direction);
				if (goRight)
					this.gates.get(gate).setMobilityRight(value);
				else
					this.gates.get(gate).setMobilityLeft(value);
			}
		}
		
		if (goRight)
			return this.gates.get(gate).getMobilityRight();
		else
			return this.gates.get(gate).getMobilityLeft();
	}
	
	public int[] getMovingRuleAccumulations() {
		if (this.movingRuleAccumulations == null)
			this.recalculateMovingRuleAccumulations();
		return this.movingRuleAccumulations;
	}
	
	public int getMaximumMovingRuleTargetValue() {
		return maximumMovingRuleAccumulation;
	}
	
	private void calculateLineUsage() {

		for (int i = 0; i < this.vars.size(); i++) {
			String line = this.vars.get(i);
			int result = 0;
			for (int j = 0; j < this.gates.size(); j++) {
				if (this.gates.get(j).getInputs().contains(line))
					result++;
				if (this.gates.get(j).output.equals(line))
					result++;
			}
			
			if (result > maximumLineUsage)
				maximumLineUsage = result;
			this.lineUsage.put(line, result);
		}
	}
	
	public int getLineUsage(String line) {
		if (this.lineUsage == null) {
			lineUsage = new HashMap<String, Integer>();
			calculateLineUsage();
		}
		
		// TODO: Check if usage contains string?
		return this.lineUsage.get(line);
	}
	
	public int getMaximumLineUsage() {
		return maximumLineUsage;
	}
	
	/**
	 * Recalculates the accumulated moving rule target values.
	 * The movingRuleAccumulations array afterwards contains values
	 * describing how many gates can move to a certain position.
	 */
	private void recalculateMovingRuleAccumulations() {
		int[] result = new int[this.gates.size() + 1];
		
		for (int i = 0; i < this.gates.size(); i++) {
			int valueRight = this.calculateGateMobility(i, true);
			int valueLeft = this.calculateGateMobility(i, false);
			
			result[i + valueRight + 1] += 1;
			if (result[i + valueRight] > maximumMovingRuleAccumulation)
				maximumMovingRuleAccumulation = result[i + valueRight];
			
			result[i - valueLeft] += 1;
			if (result[i - valueLeft] > maximumMovingRuleAccumulation)
				maximumMovingRuleAccumulation = result[i - valueLeft];
		}
		
		this.movingRuleAccumulations = result;
	}
	
	public void addToBus(String variable, String bus) {
		if (this.vars.contains(variable)) {
			if (!this.buses.containsKey(bus)) {
				this.buses.put(bus, new HashSet<String>());
				this.busNames.add(bus);
			}
			
			this.buses.get(bus).add(variable);
		}
	}
	
	public Vector<String> getBuses() {
		return this.busNames;
	}
	
	public boolean isMemberOfBus(String variableName, String busName) {
		return (this.buses.get(busName).contains(variableName));
	}
	
	/**
	 * Calculates a variables bus.
	 * Notice that atm a variable does not hold that information, so it needs to
	 * be retrieved from the list of buses and might therefore be expensive.
	 * @param variable the variable to get the bus it belongs to from
	 * @return this variable's bus or null if there is none
	 */
	public String getBus(String variable) {
		for (String busname : this.buses.keySet()) {
			if (this.buses.get(busname).contains(variable))
				return busname;
		}
		return null;
	}

	public int calculateMaximumMobility() {
		if (recalculateMobility) {
			cachedMaxMobility = 0;
			for (int i = 0; i < this.gates.size(); i++) {
				if (this.gates.get(i).getMobilityLeft() > cachedMaxMobility)
					cachedMaxMobility = this.gates.get(i).getMobilityLeft();
				if (this.gates.get(i).getMobilityRight() > cachedMaxMobility)
					cachedMaxMobility = this.gates.get(i).getMobilityRight();
			}
			recalculateMobility = false;
		}
		return cachedMaxMobility;
	}
	
	//TODO finish sub circuit parsing!
	public void insertSubCircuit(String name, Vector<String> variableMap) {
		int start = this.gates.size();
		ReversibleCircuit sub = this.subCircuits.get(name);
		for (ToffoliGate g : sub.getGates()) {
			ToffoliGate newGate = new ToffoliGate();
			for (String input : g.getInputs()) {
				int ownIndex = sub.getVars().indexOf(input);
				String parentVar = variableMap.get(ownIndex);
				
				newGate.addInput(parentVar);
			}
			int ownOutputIndex = sub.getVars().indexOf(g.output);
			newGate.output = variableMap.get(ownOutputIndex);
			this.addGate(newGate);
		}
		int end = this.gates.size();
		this.insertedCircuits.add(new subCircuitDimensions(start, end, name));
	}
	
	public void addSubCircuit(ReversibleCircuit sub, String name) {
		subCircuits.put(name, sub);
	}
	
	public boolean hasSubCircuit(String name) {
		return this.subCircuits.containsKey(name);
	}
	
	public class subCircuitDimensions {
		public int startAt, endAt;
		public String name;
		
		public subCircuitDimensions(int startAt, int endAt, String name) {
			this.startAt = startAt;
			this.endAt = endAt;
			this.name = name;
		}
	}
	
	public Vector<subCircuitDimensions> getSubCircuits() {
		return this.insertedCircuits;
	}
}
