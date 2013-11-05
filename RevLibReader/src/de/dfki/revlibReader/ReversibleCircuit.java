package de.dfki.revlibReader;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Vector;

public class ReversibleCircuit {
	private Vector<ToffoliGate> gates = new Vector<ToffoliGate>();
	
	private Vector<String> vars = new Vector<String>();
	
	public void addGate(ToffoliGate g) {
		this.gates.add(g);
		
		for (int i = 0; i < g.getInputs().size(); i++) {
			if (!(vars.contains(g.getInputs().get(i))))
				vars.add(g.getInputs().get(i));
		}
		if (!(vars.contains(g.output)))
			vars.add(g.output);
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
		for (int i = 0; i < gates.size(); i++) {
			if (gates.get(i).getInputs().contains(var))
				return false;
		}
		return true;
	}
	
	public boolean isInputOnly(String var) {
		for (int i = 0; i < gates.size(); i++) {
			if (gates.get(i).output.equals(var))
				return false;
		}
		return true;
	}
}
