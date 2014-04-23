package de.dfki.revlibReader;

import java.util.List;
import java.util.Vector;

/**
 * <p>This class represents a single gate of a reversible circuit. Basically,
 * it consists of an output string and several input strings that are references
 * to the variable names used to identify the variables of the ReversibleCircuit this
 * gate is contained in.</p>
 * 
 * @author jannis
 *
 */
public class ToffoliGate {	
	public String output;
	private Vector<String> inputs = new Vector<String>();
	private int mobilityLeft = -1;
	private int mobilityRight = -1;
	
	/**
	 * The cached value for the gate mobility to the left
	 * @return the gate mobility to the left
	 */
	public int getMobilityLeft() {return mobilityLeft;}
	
	/**
	 * The cached value for the gate mobility to the right
	 * @return the gate mobility to the right
	 */
	public int getMobilityRight() {return mobilityRight;}
	
	/**
	 * Sets the cached value for the gate mobility to the left
	 * @param mobilityValue this gate's mobility to the left
	 */
	public void setMobilityLeft(int mobilityValue) {this.mobilityLeft = mobilityValue;}
	
	/**
	 * Sets the cached value for the gate mobility to the right
	 * @param mobilityValue this gate's mobility to the right
	 */
	public void setMobilityRight(int mobilityValue) {this.mobilityRight = mobilityValue;}
	
	/**
	 * Gives you access to this gate's control lines
	 * @return this gate's control lines
	 */
	public List<String> getInputs() {
		return inputs;
	}
	
	public void addInput(String input) {
		if (!inputs.contains(input))
			inputs.add(input);
	}
	
	boolean putTargetsOnTop = true;
	
	/**
	 * Calculates the height of this gate. Basically this is the maximum
	 * distance between any two target/control lines addressed by this gate.
	 * @param orderOfVars the current order of lines (which is of course needed
	 * to provide a distance between addressed lines)
	 * @return the distance this gate covers
	 */
	public long calculateDistance(List<String> orderOfVars) {
		
		int minIndex = 0, maxIndex = 0;
		for (int i = 0; i < orderOfVars.size(); i++) {
			if (this.inputs.contains(orderOfVars.get(i)) || orderOfVars.get(i).equals(output)) {
				minIndex = i;
				break;
			}
		}
		
		for (int i = orderOfVars.size() - 1; i >= 0; i--) {
			if (this.inputs.contains(orderOfVars.get(i)) || orderOfVars.get(i).equals(output)) {
				maxIndex = i;
				break;
			}
		}
		
		int unusedVars = 0;
		for (int i = 0; i < inputs.size(); i++) {
			if (!(orderOfVars.contains(inputs.get(i))))
				unusedVars++;
		}
		if (!(orderOfVars.contains(output)))
			unusedVars++;
		
		long targetPenalty = 0;
		if (putTargetsOnTop)
			targetPenalty = calculateTargetPenalty(orderOfVars);
		
		if (unusedVars == 0) {
			return maxIndex - minIndex + targetPenalty;
		} else {
			return orderOfVars.size() - minIndex + unusedVars + targetPenalty;
		}
	}
	
	private long calculateTargetPenalty(List<String> orderOfVars) {
		boolean countFollowers = false;
		long result = 0;
		int distance = 0;
		for (int i = 0; i < orderOfVars.size(); i++) {
			if (orderOfVars.get(i) == this.output)
				countFollowers = true;
			if (countFollowers) {
				distance++;
				if (this.inputs.contains(orderOfVars.get(i))) {
					result += distance * 2;
				}
			}
		}
		for (int i = 0; i < this.inputs.size(); i++) {
			if (!orderOfVars.contains(inputs.get(i))) {
				result += distance * 2;
				distance++;
			}
		}
		return result;
	}
}
