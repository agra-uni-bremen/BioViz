package de.dfki.bioviz.ui;

import javax.swing.plaf.basic.BasicInternalFrameTitlePane.MaximizeAction;

import com.badlogic.gdx.graphics.Color;

import de.dfki.bioviz.structures.BiochipField;
import de.dfki.bioviz.structures.Point;


public class DrawableField extends DrawableSprite {
	
	public BiochipField field;
	
	static final Color fieldDefaultColor = new Color(0.5f, 0.5f, 0.75f, 1f);
	static final Color sinkDefaultColor = new Color(0.75f, 0.5f, 0.5f, 1f);
	static final Color sourceDefaultColor = new Color(0.5f, 0.75f, 0.5f, 1f);
	static final Color fieldAdjacentActivationColor = new Color(1f/ 2f, 1f / 3f, 0, 1); //218-165-32
	static final Color blockedColor = new Color(1f / 2f, 0, 0, 1);
	
	private boolean drawSink = false, drawSource = false, drawBlockage = false;
	
	private DrawableSprite adjacencyOverlay;

	public DrawableField(BiochipField field) {
		super("GridMarker.png");
		this.field = field;
		super.addLOD(8, "BlackPixel.png");
		adjacencyOverlay = new AdjacencyOverlay("AdjacencyMarker.png");
	}

	@Override
	public String generateSVG() {
		if (field.isEnabled) {
			// FIXME why would we need to acces " (-this.field.y + BioViz.singleton.currentCircuit.data.field[0].length - 1)"?
			// @jannis please check and fix
//			return "<image x=\"" + this.field.x + "\" y=\"" + (-this.field.y + BioViz.singleton.currentCircuit.data.field[0].length - 1) + "\" width=\"1\" height=\"1\" xlink:href=\"field.svg\" />";
			return "";
		} else {
			return "";
		}
	}
	
	@Override
	public void draw() {
		if (this.field.isEnabled) {
			if (this.field.isSink && !drawSink) {
				this.addLOD(Float.MAX_VALUE, "Sink.png");
				drawSink = true;
			}
			else if (this.field.isDispenser && ! drawSource) {
				this.addLOD(Float.MAX_VALUE, "Source.png");
				drawSource = true;
			} else if (this.field.isPotentiallyBlocked() && ! drawBlockage) {
				this.addLOD(Float.MAX_VALUE, "Blockage.png");
				drawBlockage = true;
			}
			float xCoord = BioViz.singleton.currentCircuit.xCoordOnScreen(field.x());
			float yCoord = BioViz.singleton.currentCircuit.yCoordOnScreen(field.y());

			this.x = xCoord;
			this.y = yCoord;
			this.scaleX = BioViz.singleton.currentCircuit.smoothScaleX;
			this.scaleY = BioViz.singleton.currentCircuit.smoothScaleY;

			int colorOverlayCount = 0;
			this.color = new Color(0,0,0,1);

			if (field.isBlocked((int)BioViz.singleton.currentCircuit.currentTime)) {
				this.color.add(blockedColor);
				colorOverlayCount++;
			}
			
			if (colorOverlayCount == 0) {
				if (this.field.isSink) {
					this.color.add(sinkDefaultColor);
					colorOverlayCount++;
				} else if (this.field.isDispenser) {
					this.color.add(sourceDefaultColor);
					colorOverlayCount++;
				} else {
					this.color.add(fieldDefaultColor);
					colorOverlayCount++;				
				}
			}
			
			this.color.mul(1f / (float)colorOverlayCount);

			this.color.clamp();
			
			super.draw();
			
			if (BioViz.singleton.currentCircuit.getHighlightAdjacency() && BioViz.singleton.currentCircuit.data.getAdjacentActivations().contains(this.field)) {
				this.adjacencyOverlay.x = this.x;
				this.adjacencyOverlay.y = this.y;
				this.adjacencyOverlay.scaleX = this.scaleX;
				this.adjacencyOverlay.scaleY = this.scaleY;
				this.adjacencyOverlay.color = Color.RED.cpy();
				this.adjacencyOverlay.draw();
			}
		}
	}
	
	private class AdjacencyOverlay extends DrawableSprite {

		public AdjacencyOverlay(String textureFilename) {
			super(textureFilename);
		}

		@Override
		public String generateSVG() {
			return null;
		}
		
	}
}
