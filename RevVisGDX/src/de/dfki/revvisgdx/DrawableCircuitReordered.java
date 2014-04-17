package de.dfki.revvisgdx;

import java.util.HashMap;
import java.util.Vector;

import de.dfki.revlibReader.ReversibleCircuit;

public class DrawableCircuitReordered extends DrawableCircuit {
	
	private Vector<ReorderInfo> reorders = new Vector<ReorderInfo>();

	public DrawableCircuitReordered(ReversibleCircuit toDraw) {
		super(toDraw);
		
		ReorderInfo ri1 = new ReorderInfo();
		ri1.index = 0;
		for (int i = 0; i < this.data.getVars().size(); i++) {
			ri1.toggledVars.add(this.data.getVars().get(i));
		}
		this.reorders.add(ri1);
		
		HashMap<Integer, Vector<String>> startAt = new HashMap<Integer, Vector<String>>();
//		HashMap<Integer, Vector<String>> endAt = new HashMap<Integer, Vector<String>>();
		for (int i = 0; i < this.data.getVars().size(); i++) {
			String line = this.data.getVars().get(i);
			int start = data.getCoordOfGate(this.data.getFirstGateOnLine(line));
			int end = data.getCoordOfGate(this.data.getLastGateOnLine(line)) + 1;
			if (!startAt.containsKey(start)) {
				startAt.put(start, new Vector<String>());
			}
			if (!startAt.containsKey(end)) {
				startAt.put(end, new Vector<String>());
			}
			startAt.get(start).add(line);
			startAt.get(end).add(line);
		}
		
		for (Integer i : startAt.keySet()) {
			ReorderInfo ri = new ReorderInfo();
			ri.index = i;
			for (String s : startAt.get(i)) {
				ri.toggledVars.add(s);
			}
			this.reorders.add(ri);
		}
	}
	
	@Override
	protected float xCoordOnScreen(int i) {
		int i2 = i;
		for (int j = 0; j < this.reorders.size(); j++) {
			if (reorders.get(j).index <= i)
				i2++;
		}
		
		float xCoord = i2;
		xCoord += offsetX;
		xCoord *= smoothScaleX;
		return xCoord;
	}
	
	@Override
	protected float getLineYScreenCoord(String line, int index) {
		int indexOfLine = this.data.getVars().indexOf(line);
		for (int i = 0; i < this.data.getVars().size(); i++) {
			if (this.data.getVars().get(i).equals(line)) {
				break;
			}
			int toggleCount = isVisibleAt(this.data.getVars().get(i), index);
			if (toggleCount % 2 == 0) {
//				System.out.println("" + line + " is invisible at " + index + "(" + toggleCount + ")");
				indexOfLine--;
			}
		}
		float y = (indexOfLine - (data.getAmountOfVars() / 2)) - offsetY; //+ RevVisGDX.singleton.camera.viewportHeight;
		y *= smoothScaleY;
		return y;
	}
	
	private int isVisibleAt(String line, int index) {
		int result = 0;
		for (int i = 0; i < reorders.size(); i++) {
			if (reorders.get(i).index <= index && reorders.get(i).toggledVars.contains(line)) {
				result++;
			}
		}
		
		return result;
	}

}
