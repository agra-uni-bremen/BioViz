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
	static final Color fieldAdjacentActivationColor = new Color(1f / 2f, 1f / 3f, 0, 1); //218-165-32
	static final Color blockedColor = new Color(1f / 2f, 0, 0, 1);

	private boolean drawSink = false, drawSource = false, drawBlockage = false, drawDetector=false;

	private DrawableSprite adjacencyOverlay;

	public DrawableField(BiochipField field) {
		super("GridMarker.png");
		this.field = field;
		super.addLOD(8, "BlackPixel.png");
		adjacencyOverlay = new AdjacencyOverlay("AdjacencyMarker.png");
	}

	@Override
	public String generateSVG() {
		// FIXME why would we need to acces " (-this.field.y + BioViz.singleton.currentCircuit.data.field[0].length - 1)"?
		// @jannis please check and fix
		// @keszocze Because the coordinate system in SVG is inverted on its
		//		y-axis. I need to first put it upside down (-this.field.y) and
		//		then add the total height of the circuit to have the element put
		//		back into the positive coordinate range in order to be placed
		//		on the canvas.
			return "<image x=\"" + this.field.x() + "\" y=\"" + (-this.field.y() + BioViz.singleton.currentCircuit.data.getMaxCoord().second - 1) + "\" width=\"1\" height=\"1\" xlink:href=\"field.svg\" />";
	}

	@Override
	public void draw() {
		if (this.field.isSink && !drawSink) {
			this.addLOD(Float.MAX_VALUE, "Sink.png");
			drawSink = true;
		} else if (this.field.isDispenser && !drawSource) {
			this.addLOD(Float.MAX_VALUE, "Source.png");
			drawSource = true;
		} else if (this.field.isPotentiallyBlocked() && !drawBlockage) {
			this.addLOD(Float.MAX_VALUE, "Blockage.png");
			drawBlockage = true;
		} else if (this.field.getDetector() != null && !drawDetector) {
			this.addLOD(Float.MAX_VALUE, "Detector.png");
			drawDetector = true;
		}

		DrawableCircuit circ = BioViz.singleton.currentCircuit;

		float xCoord = circ.xCoordOnScreen(field.x());
		float yCoord = circ.yCoordOnScreen(field.y());

		this.x = xCoord;
		this.y = yCoord;
		this.scaleX = circ.smoothScaleX;
		this.scaleY = circ.smoothScaleY;

		int colorOverlayCount = 0;
		this.color = new Color(0, 0, 0, 1);

		if (field.isBlocked((int) circ.currentTime)) {
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
			}
			else {
				this.color.add(fieldDefaultColor);
				colorOverlayCount++;
			}
		}

		this.color.mul(1f / (float) colorOverlayCount);

		this.color.clamp();

		super.draw();

		if (circ.getHighlightAdjacency() && circ.data.getAdjacentActivations().contains(this.field)) {
			this.adjacencyOverlay.x = this.x;
			this.adjacencyOverlay.y = this.y;
			this.adjacencyOverlay.scaleX = this.scaleX;
			this.adjacencyOverlay.scaleY = this.scaleY;
			this.adjacencyOverlay.color = Color.RED.cpy();
			this.adjacencyOverlay.draw();
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
