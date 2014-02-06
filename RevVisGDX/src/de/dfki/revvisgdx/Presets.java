package de.dfki.revvisgdx;

import de.dfki.revvisgdx.DrawableCircuit.lineGrouping;
import de.dfki.revvisgdx.DrawableCircuit.lineWidth;
import de.dfki.revvisgdx.DrawableCircuit.movingRuleDisplay;
import de.dfki.revvisgdx.DrawableCircuit.movingRuleHighlight;
import com.badlogic.gdx.graphics.Color;

public class Presets {
	public static void setConstGarbage() {
		DrawableCircuit dc = RevVisGDX.singleton.currentCircuit;
		dc.setAllDefault();
		dc.colorizeConstants = true;
		dc.colorizeGarbageLine = true;
		dc.hideGates = true;
		dc.lineType = lineWidth.full;
		dc.drawLinesDarkWhenUsed = false;
		dc.highlightHoveredGateMovingRule = movingRuleHighlight.none;
		dc.drawLinesColourizedWhenUsed = false;
		dc.lineBaseColor = new Color(Color.WHITE);
	}
	
	public static void setBoxesAndUsage() {
		DrawableCircuit dc = RevVisGDX.singleton.currentCircuit;
		dc.setAllDefault();
		dc.lineType = lineWidth.full;
		dc.drawVerticalLines = false;
		dc.drawLinesDarkWhenUsed = true;
		dc.hideGates = false;
		dc.colorizeConstants = false;
		dc.colorizeGarbageLine = false;
		dc.highlightHoveredGateMovingRule = movingRuleHighlight.none;
		dc.drawLinesColourizedWhenUsed = false;
		dc.lineBaseColor = new Color(Color.WHITE);
	}
	
	public static void setColourizedUsage() {
		DrawableCircuit dc = RevVisGDX.singleton.currentCircuit;
		dc.setAllDefault();
		dc.drawLinesColourizedWhenUsed = true;
		dc.lineType = lineWidth.full;
		dc.drawVerticalLines = false;
	}
	
	public static void setColourizeLineType() {
		DrawableCircuit dc = RevVisGDX.singleton.currentCircuit;
		dc.setAllDefault();
		dc.markVariableTypes = true;
		dc.lineType = lineWidth.full;
	}
	
	public static void setMovingRuleColoured() {
		DrawableCircuit dc = RevVisGDX.singleton.currentCircuit;
		dc.setAllDefault();
		dc.colourizeGatesByMobility = movingRuleDisplay.total;
		dc.drawVerticalLines = false;
		dc.lineType = lineWidth.hidden;
	}
	
	public static void setMovingRuleBoxOverlay() {
		DrawableCircuit dc = RevVisGDX.singleton.currentCircuit;
		dc.setAllDefault();
		dc.highlightHoveredGateMovingRule = movingRuleHighlight.boxes;
		dc.lineType = lineWidth.hidden;
	}
	
	public static void setGreyNeighboursWithBlackTargets() {
		DrawableCircuit dc = RevVisGDX.singleton.currentCircuit;
		dc.setAllDefault();
		dc.neighbourhoodGrouping = lineGrouping.singleGreyscale;
		dc.lineType = lineWidth.hidden;
	}
}
