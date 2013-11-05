package de.dfki.revlibReader.optimization;

import java.util.Vector;

import de.dfki.revlibReader.ReversibleCircuit;

public class OrderOptimizer {
	
	static class VarsAndValue {
		public Vector<String> vars;
		public long value;
		public VarsAndValue (Vector<String> vars, long value) {
			this.vars = vars;
			this.value = value;
		}
	}
	
	public static void optimizeBF(ReversibleCircuit c) {
		//String[] originalOrder = (String[]) c.getVars().toArray();
		
		minValueTotal = Long.MAX_VALUE;
		VarsAndValue best = optimizeBF(c, new Vector<String>(), c.getVars());
		
		c.setVars(best.vars);
	}
	
	private static long minValueTotal;
	
	private static VarsAndValue optimizeBF(ReversibleCircuit c, Vector<String> setVars, Vector<String> remainingVars) {
		if (remainingVars.size() <= 0) {
			c.setVars(setVars);
			Vector<String> resultVars = new Vector<String>();
			for (int i = 0; i < setVars.size(); i++) {
				resultVars.add(i, setVars.get(i));
			}
			VarsAndValue result = new VarsAndValue(resultVars, c.totalDistance());
			return result;
		} else {
			long minValue = Long.MAX_VALUE;
			VarsAndValue best = null;
			for (int i = 0; i < remainingVars.size(); i++) {
				setVars.add(remainingVars.get(i));
				remainingVars.remove(i);
				
				VarsAndValue current = optimizeBF(c, setVars, remainingVars);
				if (current.value < minValue) {
					minValue = current.value;
					best = current;
					
					if (minValue < minValueTotal) {
						minValueTotal = minValue;
						System.out.print("New best result found: " + best.value + " ");
						for (int j = 0; j < best.vars.size(); j++) {
							System.out.print(best.vars.get(j)+ ", ");
						}
						System.out.println();
					}
				}
				
				remainingVars.add(i, setVars.get(setVars.size()-1));
				setVars.removeElementAt(setVars.size() - 1);
			}
			return best;
		}
	}
	
	public static void optimizeNN(ReversibleCircuit c) {
		Vector<String> remainingVars = c.getVars();
		String[] allVars = new String[remainingVars.size()];
		for (int i = 0; i < allVars.length; i++) {
			allVars[i] = remainingVars.get(i);
		}
		Vector<String> bestSolution = new Vector<String>();
		long bestTotalCost = Long.MAX_VALUE;
		long currentCost = Long.MAX_VALUE;
		for (int j = 0; j < allVars.length; j++) {
			
			c.setVars(new Vector<String>());
			String startingVar = allVars[j];
			c.getVars().add(startingVar);
			remainingVars = new Vector<String>();
			for (int i = 0; i < allVars.length; i++) {
				remainingVars.add(allVars[i]);
			}
			remainingVars.remove(j);
			
			while(remainingVars.size() > 0) {
				long bestCost = Long.MAX_VALUE;
				int bestIndex = 0;
				for (int i = 0; i < remainingVars.size(); i++) {
					c.getVars().add(remainingVars.get(i));
					currentCost = c.totalDistance();
					if (currentCost < bestCost) {
						bestCost = currentCost;
						bestIndex = i;
					}
					c.getVars().remove(c.getVars().size() - 1);
				}
				c.getVars().add(remainingVars.get(bestIndex));
				remainingVars.remove(bestIndex);
			}
			
			if (currentCost < bestTotalCost) {
				bestSolution = new Vector<String>();
				bestTotalCost = currentCost;
				for (int i = 0; i < c.getVars().size(); i++) {
					bestSolution.add(c.getVars().get(i));
				}
			}
		}
		
		c.setVars(bestSolution);
	}
}
