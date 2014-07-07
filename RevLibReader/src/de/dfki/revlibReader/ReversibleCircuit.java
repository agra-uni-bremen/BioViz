package de.dfki.revlibReader;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Vector;

/**
 * <p>Represents a reversible circuit, consisting mostly of a set of variables
 * (which are stored as a list of strings and can be accessed using the
 * getVars() method) and a list of gates (that are stored as ToffoliGate
 * instances and can be retrieved using the getGates() method).</p>
 * 
 * <p>Those gates consist of an output (i.e. a target line) and n inputs
 * (i.e. control lines), each of which is just a string and references
 * the according variable.</p>
 * 
 * @author Jannis Stoppe
 *
 */
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
	private int minimumLineUsage = Integer.MAX_VALUE;
	private int amountOfVars = -1;
	private HashMap<String, HashSet<String>> buses = new HashMap<String, HashSet<String>>();
	private Vector<String> busNames = new Vector<String>();
	boolean recalculateMobility = true;
	int cachedMaxMobility = 0;
	int cachedMaxMobilityTotal = 0;
	int cachedMinMobilityTotal = Integer.MAX_VALUE;
	private HashMap<String, ReversibleCircuit> subCircuits = new HashMap<String, ReversibleCircuit>();
	private Vector<subCircuitDimensions> insertedCircuits = new Vector<subCircuitDimensions>();

	/**
	 * Adds a new gate to this circuit. Be careful to add them in the
	 * correct order.
	 * @param g the gate to be added.
	 */
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

	/**
	 * Gets the gate located at position gateNumber of this circuit.
	 * @param gateNumber the gate's index
	 * @return the according gate.
	 */
	public ToffoliGate getGate(int gateNumber) {
		return gates.get(gateNumber);
	}

	/**
	 * Gives you the number of variables in this circuit.
	 * @return the amount of variables in this circuit
	 */
	public int getAmountOfVars() {
		return vars.size();
	}

	/**
	 * This circuit's gates.
	 * @return this circuit's gates.
	 */
	public Collection<ToffoliGate> getGates() {
		return gates;
	}

	/**
	 * This circuit's variables
	 * @return this circuit's variables
	 */
	public Vector<String> getVars() {
		return vars;
	}

	/**
	 * Completely overwrites all variables and sets the given
	 * set as the variables instead.
	 * @param allVars the new variables
	 */
	public void setVars(Vector<String> allVars) {
		this.vars = allVars;
	}
	
	public void setVarAmount(int varCount) {
		this.amountOfVars = varCount;
	}
	
	/**
	 * If the variable count has been set explicitly using setVarAmount, returns
	 * the value that was set there. Otherwise returns the amount of variables that
	 * have been added to the circuit so far.
	 * @return
	 */
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

	/**
	 * Calculates the total distance covered by the gates. A single gate that
	 * connects two adjacent lines has a distance of 1, because it crosses 1 gap.
	 * If there was a variable between those two lines, it would cover a distance
	 * of 2 etc..
	 * @return The total absolute distance covered by the gates in this circuit.
	 */
	public long totalDistance() {
		long result = 0;
		for (int i = 0; i < gates.size(); i++) {
			result += gates.get(i).calculateDistance(this.vars);
		}
		return result;
	}

	/**
	 * 
	 * @param var the variable to check
	 * @return true if there are only target gates on that line, else false.
	 */
	public boolean isTargetOnly(String var) {
		return outputOnly.contains(var);
	}

	/**
	 * 
	 * @param var the variable to check
	 * @return true if there are only control gates on that line, else false
	 */
	public boolean isInputOnly(String var) {
		return inputOnly.contains(var);
	}

	/**
	 * Marks a given line as designated garbage line
	 * @param line a line that contains garbage
	 */
	public void addGarbageLine(String line) {
		this.garbageLines.add(line);
	}

	/**
	 * Marks a given line as designated function line
	 * @param line a line that stores a function result
	 */
	public void addFunctionLine(String line) {
		this.functionLines.add(line);
	}

	/**
	 * Checks if a line was marked as garbage
	 * @param line the variable to check
	 * @return true if that line has been designated as garbage, else false
	 */
	public boolean isGarbageLine(String line) {
		return this.garbageLines.contains(line);
	}

	/**
	 * Checks if a line was marked as a function line
	 * @param line the variable to check
	 * @return true if that line has been designated as a function, else false
	 */
	public boolean isFunctionLine(String line) {
		return this.functionLines.contains(line);
	}

	/**
	 * Calculates the first gate that somehow uses the given variable (either
	 * as a control or a target variable, it does not matter how the line is
	 * used, just the fact that it is).
	 * 
	 * @param line the line to check
	 * @return the first gate on that line
	 */
	public ToffoliGate getFirstGateOnLine(String line) {
		return this.firstGates.get(line);
	}

	/**
	 * Calculates the last gate that somehow uses the given variable (either
	 * as a control or a target variable, it does not matter how the line is
	 * used, just the fact that it is).
	 * 
	 * @param line the line to check
	 * @return the last gate on that line
	 */
	public ToffoliGate getLastGateOnLine(String line) {
		return this.lastGates.get(line);
	}

	/**
	 * Calculates the index of a specific gate. The first time this is accessed
	 * might take some time as the list of gates needs to be searched for a specific
	 * one, the result is cached though, so subsequent calls to this method should
	 * be adequately fast.
	 * 
	 * @param gate the gate to get the index from
	 * @return the index of the gate
	 */
	public int getCoordOfGate(ToffoliGate gate) {
		if (!indicesOfGates.containsKey(gate))
			indicesOfGates.put(gate, gates.indexOf(gate));
		return indicesOfGates.get(gate);
	}

	/**
	 * Adds a new variable to this circuit. Make sure that the names remain
	 * unique, as a second variable with the same name will not be added and gates
	 * using the variable will instead reference the first (previously added) one.
	 * @param varName the unique identifier of the line.
	 */
	public void addLine(String varName) {
		varName = varName.trim();
		if (!varName.equals("")) {
			if (!this.vars.contains(varName))
				this.vars.add(varName);
		}
	}
	
	/**
	 * 
	 * @return the amount of designated garbage lines
	 */
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
	
	/**
	 * Marks a given line as constant
	 * @param line the line to mark as constant
	 * @param value the value on that line
	 */
	public void addConstLine(String line, int value) {
		this.constOnly.put(line, value);
	}
	
	/**
	 * Calculates how far a given gate can be moved to the left.
	 * @param gate the gate to check the mobility of
	 * @return the amount of indices this gate can be moved to the left
	 * without altering the circuit's result
	 */
	public int calculateGateMobilityLeft(int gate) {
		return calculateGateMobility(gate, false);
	}
	
	/**
	 * Calculates how far a given gate can be moved to the right.
	 * @param gate the gate to check the mobility of
	 * @return the amount of indices this gate can be moved to the right
	 * without altering the circuit's result
	 */
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
	
	/**
	 * Calculates the moving rule of each gate and returns the result in an array.
	 * The result is the sum of all gates that can be moved to a particular position; if
	 * 6 gates can be moved to the beginning of the circuit, the resulting array will
	 * have the value 6 at index 0. Note that the information *which* gate can be
	 * moved to that position is not returned. Also note that, of course, the resulting
	 * array will be one element larger than the amount of gates.
	 * 
	 * @return the accumulated moving rule targets
	 */
	public int[] getMovingRuleAccumulations() {
		if (this.movingRuleAccumulations == null)
			this.recalculateMovingRuleAccumulations();
		return this.movingRuleAccumulations;
	}
	
	/**
	 * The maximum amount of gates that can be moved to one particular position. If 6 gates
	 * can be moved to the beginning of the circuit and there is no position to which more than
	 * 6 gates can be moved in this circuit, this method returns 6.
	 * 
	 * @return the maximum amount of gates that can be moved to one particular position
	 */
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
			if (result < minimumLineUsage)
				minimumLineUsage = result;
			this.lineUsage.put(line, result);
		}
	}
	
	/**
	 * Calculates how often a line is used.
	 * @param line the variable to calculate the value for
	 * @return the amount of gates that use this variable
	 */
	public int getLineUsage(String line) {
		if (this.lineUsage == null) {
			lineUsage = new HashMap<String, Integer>();
			calculateLineUsage();
		}
		
		// TODO: Check if usage contains string?
		return this.lineUsage.get(line);
	}
	
	/**
	 * Calculates the maximum amount of times any line is used.
	 * @return the amount of times the mostly used variable is accessed.
	 */
	public int getMaximumLineUsage() {
		return maximumLineUsage;
	}
	
	/**
	 * Calculates the minimum amount of times any line is used.
	 * @return the amount of times the least used variable is accessed.
	 */
	public int getMinimumLineUsage() {
		return minimumLineUsage;
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
	
	/**
	 * Adds a variable to a bus for later grouping
	 * @param variable the variable to be added to the bus
	 * @param bus the bus to add the variable to
	 */
	public void addToBus(String variable, String bus) {
		if (this.vars.contains(variable)) {
			if (!this.buses.containsKey(bus)) {
				this.buses.put(bus, new HashSet<String>());
				this.busNames.add(bus);
			}
			
			this.buses.get(bus).add(variable);
		}
	}
	
	/**
	 * Returns the names of all previously defined buses
	 * @return all previously defined buses
	 */
	public Vector<String> getBuses() {
		return this.busNames;
	}
	
	/**
	 * 
	 * @param variableName the variable to check
	 * @param busName the bus the variable might be part of
	 * @return true if the variable is part of that bus, else false
	 */
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

	/**
	 * Calculates the maximum amount of indices any gate can be moved
	 * @return the maximum value of indices any gate can be moved
	 */
	public int calculateMaximumMobility() {
		if (recalculateMobility) {
			cachedMaxMobility = 0;
			cachedMaxMobilityTotal = 0;
			cachedMinMobilityTotal = Integer.MAX_VALUE;
			recalculateMovingRuleAccumulations();
			for (int i = 0; i < this.gates.size(); i++) {
				if (this.gates.get(i).getMobilityLeft() > cachedMaxMobility)
					cachedMaxMobility = this.gates.get(i).getMobilityLeft();
				if (this.gates.get(i).getMobilityRight() > cachedMaxMobility)
					cachedMaxMobility = this.gates.get(i).getMobilityRight();
				if (this.gates.get(i).getMobilityLeft() + this.gates.get(i).getMobilityRight() > cachedMaxMobilityTotal)
					cachedMaxMobilityTotal = this.gates.get(i).getMobilityLeft() + this.gates.get(i).getMobilityRight();
				if (this.gates.get(i).getMobilityLeft() + this.gates.get(i).getMobilityRight() < cachedMinMobilityTotal)
					cachedMinMobilityTotal = this.gates.get(i).getMobilityLeft() + this.gates.get(i).getMobilityRight();
			}
			recalculateMobility = false;
		}
		return cachedMaxMobility;
	}
	
	/**
	 * Calculates the maximum total amount of gates (i.e. the sum, of both,
	 * movement left and movement right) any gate can be moved
	 * @return the maximum total moveability of all gates 
	 */
	public int calculateMaximumMobilityTotal() {
		if (recalculateMobility)
			calculateMaximumMobility();
		return cachedMaxMobilityTotal;
	}
	
	/**
	 * Calculates the minimum total amount of gates (i.e. the sum, of both,
	 * movement left and movement right) any gate can be moved
	 * @return the minimum total moveability of all gates 
	 */
	public int calculateMinimumMobilityTotal() {
		if (recalculateMobility)
			calculateMaximumMobility();
		return cachedMinMobilityTotal;
	}
	
	/**
	 * Inserts a previously parsed subcircuit at the current position
	 * @param name the name of the subcircuit
	 * @param variableMap the mapping of this (parent's) variables to the 
	 * subcircuit's variables
	 */
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
	
	/**
	 * Adds a subcircuit to the list of previously parsed subcircuits
	 * @param sub the circuit
	 * @param name the name to access that circuit
	 */
	public void addSubCircuit(ReversibleCircuit sub, String name) {
		subCircuits.put(name, sub);
	}
	
	/**
	 * Checks if a given name was used to register a subcircuit
	 * @param name the name to check for
	 * @return true if there is a subcircuit for that name, else false
	 */
	public boolean hasSubCircuit(String name) {
		return this.subCircuits.containsKey(name);
	}
	
	/**
	 * This class is used to mark inserted subcircuits. As subcircuits aren't added
	 * as references but simply as the lot of gates they consist of, there is no information
	 * concerning the positions of subcircuits in the final resulting circuit available. This
	 * class is used to counter this problem for later inspection of the circuit.
	 * @author Jannis Stoppe
	 *
	 */
	public class subCircuitDimensions {
		/**
		 * The indices for this inserted circuit
		 */
		public int startAt, endAt;
		
		/**
		 * the name of this subcircuit
		 */
		public String name;
		
		public subCircuitDimensions(int startAt, int endAt, String name) {
			this.startAt = startAt;
			this.endAt = endAt;
			this.name = name;
		}
	}
	
	/**
	 * 
	 * @return the list of all *inserted* subcircuits (i.e. their indices and names)
	 */
	public Vector<subCircuitDimensions> getSubCircuits() {
		return this.insertedCircuits;
	}
}
