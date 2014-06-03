package de.dfki.revvisgdx;

import de.dfki.revvisgdx.DrawableCircuit.drawLinesColourizedByUsageType;
import de.dfki.revvisgdx.DrawableCircuit.lineGrouping;
import de.dfki.revvisgdx.DrawableCircuit.lineWidth;
import de.dfki.revvisgdx.DrawableCircuit.movingRuleDisplay;
import de.dfki.revvisgdx.DrawableCircuit.movingRuleHighlight;

import com.badlogic.gdx.graphics.Color;

/**
 * This class should not be instanciated. Just use the static methods to
 * set the currently drawn circuit to certain preset settings.
 * 
 * @author jannis
 *
 */
public class Presets {
	
	public static void setDefault() {
		RevVisGDX.singleton.currentCircuit.setAllDefault();
	}

	public static void setConstGarbage() {
		DrawableCircuit dc = RevVisGDX.singleton.currentCircuit;
		dc.setAllDefault();
		dc.colorizeConstants = true;
		dc.colorizeGarbageLine = true;
		dc.hideGates = true;
		dc.lineType = lineWidth.full;
		dc.drawLinesDarkWhenUsed = false;
		dc.highlightHoveredGateMovingRule = movingRuleHighlight.none;
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
		dc.lineBaseColor = new Color(Color.WHITE);
	}
	
	public static void setColourizedUsage() {
		DrawableCircuit dc = RevVisGDX.singleton.currentCircuit;
		dc.setAllDefault();
		dc.drawLinesColourizedWhenUsed = drawLinesColourizedByUsageType.relative;
		dc.lineType = lineWidth.full;
		dc.drawVerticalLines = false;
		dc.hideGates = true;
		dc.lineBaseColor = new Color(Color.WHITE);
	}
	
	public static void setColourizeUsageAbsolute() {
		setColourizedUsage();
		DrawableCircuit dc = RevVisGDX.singleton.currentCircuit;
		dc.drawLinesColourizedWhenUsed = drawLinesColourizedByUsageType.absolute;
	}
	
	public static void setColourizeLineType() {
		DrawableCircuit dc = RevVisGDX.singleton.currentCircuit;
		dc.setAllDefault();
		dc.markVariableTypes = true;
		dc.lineType = lineWidth.full;
		dc.drawVerticalLines = false;
		dc.drawLinesDarkWhenUsed = false;
	}
	
	public static void setMovingRuleColoured() {
		DrawableCircuit dc = RevVisGDX.singleton.currentCircuit;
		dc.setAllDefault();
		dc.colourizeGatesByMobility = movingRuleDisplay.total;
		dc.drawVerticalLines = false;
		dc.lineType = lineWidth.hidden;
	}
	
	public static void setMovingRuleColouredAbsolute() {
		setMovingRuleColoured();
		DrawableCircuit dc = RevVisGDX.singleton.currentCircuit;
		dc.colourizeGatesByMobility = movingRuleDisplay.totalAbsolute;
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
