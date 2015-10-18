package de.bioviz.ui;

import com.badlogic.gdx.graphics.Color;
import de.bioviz.structures.BiochipField;
import de.bioviz.structures.Mixer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;


public class DrawableField extends DrawableSprite {

	private static Logger logger = LoggerFactory.getLogger(DrawableField.class);

	public BiochipField field;

	static final Color fieldDefaultColor = Colors.fieldColor;
	static final Color sinkDefaultColor = Colors.sinkColor;
	static final Color sourceDefaultColor = Colors.sourceColor;
	static final Color mixerDefaultColor = Colors.mixerColor;
	static final Color blockedColor = Colors.blockedColor;

	private boolean drawSink = false;
	private boolean drawBlockage = false;
	private boolean drawDetector = false;

	//private DrawableSprite adjacencyOverlay;

	DrawableCircuit parentCircuit;

	public DrawableField(BiochipField field, DrawableCircuit parent) {
		super(TextureE.GridMarker, parent.parent);
		this.parentCircuit = parent;
		this.field = field;
		super.addLOD(8,TextureE.BlackPixel);
		//adjacencyOverlay = new AdjacencyOverlay("AdjacencyMarker.png");
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
		return "<image x=\"" + this.field.x() + "\" y=\"" + (-this.field.y() + parentCircuit.data.getMaxCoord().snd - 1) + "\" width=\"1\" height=\"1\" xlink:href=\"field.svg\" />";
	}


	public DisplayValues getDisplayValues() {
		String fieldHUDMsg = null;
		DrawableCircuit circ = parentCircuit;

		float xCoord = circ.xCoordOnScreen(field.x());
		float yCoord = circ.yCoordOnScreen(field.y());

		this.x = xCoord;
		this.y = yCoord;
		this.scaleX = circ.smoothScaleX;
		this.scaleY = circ.smoothScaleY;


		// TODO what happens if some of these options overlap?
		// Right now only the first occurrence according the order below is taken. This might not be what is intended
		// In general, a detector, for example, is a very valid routing target
		if (this.field.isSink && !drawSink) {
			this.addLOD(Float.MAX_VALUE, TextureE.Sink);
			drawSink = true;
		} else if (this.field.isDispenser) {
			this.addLOD(Float.MAX_VALUE, TextureE.Dispenser);
			fieldHUDMsg = Integer.toString(field.fluidID);
		} else if (this.field.isPotentiallyBlocked() && !drawBlockage) {
			this.addLOD(Float.MAX_VALUE, TextureE.Blockage);
			drawBlockage = true;
		} else if (this.field.getDetector() != null && !drawDetector) {
			this.addLOD(Float.MAX_VALUE, TextureE.Detector);
			drawDetector = true;
		} else if (!this.field.source_ids.isEmpty()) {
			if (circ.displayOptions.getOption(BDisplayOptions
													  .SourceTargetIcons)) {
				this.addLOD(Float.MAX_VALUE,  TextureE.Start);
			} else {
				this.addLOD(Float.MAX_VALUE, TextureE.GridMarker);
			}
			if (circ.displayOptions.getOption(BDisplayOptions.SourceTargetIDs)) {
				ArrayList<Integer> sources = this.field.source_ids;
				fieldHUDMsg = sources.get(0).toString();
				if (sources.size() > 1) {
					for (int i = 2; i < sources.size(); i++) {
						fieldHUDMsg += ", " + sources.get(i);
					}
				}
			}
		} else if (!this.field.target_ids.isEmpty()) {
			if (circ.displayOptions.getOption(BDisplayOptions.SourceTargetIcons)) {
				this.addLOD(Float.MAX_VALUE,TextureE.Target);
			} else {
				this.addLOD(Float.MAX_VALUE, TextureE.GridMarker);
			}
			if (circ.displayOptions.getOption(BDisplayOptions.SourceTargetIDs)) {
				ArrayList<Integer> targets = this.field.target_ids;
				fieldHUDMsg = targets.get(0).toString();
				if (targets.size() > 1) {
					for (int i = 1; i < targets.size(); i++) {
						fieldHUDMsg += ", " + targets.get(i);
					}
				}
			}
		}


		// note: this overwrites any previous message
		// TODO we really need some kind of mechanism of deciding when to show what
		if (circ.displayOptions.getOption(BDisplayOptions.Pins)) {
			if (this.field.pin != null) {
				fieldHUDMsg = Integer.toString(this.field.pin.pinID);
			}
		}

		return new DisplayValues(null,fieldHUDMsg,null);
	}

	@Override
	public void draw() {


		DisplayValues vals = getDisplayValues();

		displayText(vals.msg);



		int colorOverlayCount = 0;
		/*
		We need to create a copy of the fieldEmptyColor as that value is final and thus can not be modified.
		If that value is unchangeable, the cells all stay white
		 */
		Color result = new Color(Colors.fieldEmptyColor);

		if (field.isBlocked(parentCircuit.currentTime)) {
			result.add(blockedColor);
			colorOverlayCount++;
		}


		if (parentCircuit.displayOptions.getOption(BDisplayOptions.CellUsage)) {
			// TODO clevere Methode zum Bestimmen der Farbe wählen (evtl. max Usage verwenden)
			float scalingFactor = 4f;

			result.add(new Color(0, this.field.usage / scalingFactor, 0, 0));
			++colorOverlayCount;
		}


		int t = parentCircuit.currentTime;
		if (parentCircuit.displayOptions.getOption(BDisplayOptions.Actuations)) {
			if (field.isActuated(t)) {
				result.add(Colors.actautedColor);
				++colorOverlayCount;
			}
		}

		// TODO why do we only add something if the count is zero? Save computation time?
		// nope it seems that the cell usage is supposed to override the other overlays
		if (colorOverlayCount == 0) {
			if (this.field.isSink) {
				result.add(sinkDefaultColor);
				colorOverlayCount++;
			} else if (this.field.isDispenser) {
				result.add(sourceDefaultColor);
				colorOverlayCount++;
			} else {
				result.add(fieldDefaultColor);
				colorOverlayCount++;
			}
			if (!this.field.mixers.isEmpty()) {

				for (Mixer m : this.field.mixers) {
					if (m.timing.inRange(t)) {
						result.add(mixerDefaultColor);
					}
				}
			}
		}

		if (parentCircuit.displayOptions.getOption(BDisplayOptions.Adjacency) && parentCircuit.data.getAdjacentActivations().contains(this.field)) {
			result.add(0.5f, -0.5f, -0.5f, 0);
		}

		result.mul(1f / (float) colorOverlayCount);

		result.clamp();

		setColor(result);

		super.draw();
	}
}
