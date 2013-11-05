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
		
		if (!(vars.contains(g.inputA)))
			vars.add(g.inputA);
		if (!(vars.contains(g.inputB)))
			vars.add(g.inputB);
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
	
	public List<String> getVars() {
		return vars;
	}
}
