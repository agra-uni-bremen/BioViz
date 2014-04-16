package de.dfki.revvisgdx;

import java.util.Vector;

import de.dfki.revlibReader.ReversibleCircuit;

public class DrawableCircuitReordered extends DrawableCircuit {
	
	private Vector<ReorderInfo> reorders = new Vector<ReorderInfo>();

	public DrawableCircuitReordered(ReversibleCircuit toDraw) {
		super(toDraw);
		
		ReorderInfo ri1 = new ReorderInfo();
		ri1.index = 3;
		this.reorders.add(ri1);
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

}
