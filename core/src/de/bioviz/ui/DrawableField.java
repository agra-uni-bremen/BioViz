package de.bioviz.ui;

import com.badlogic.gdx.graphics.Color;

import de.bioviz.structures.BiochipField;
import de.bioviz.structures.Droplet;
import de.bioviz.structures.Mixer;
import de.bioviz.structures.Net;
import de.bioviz.structures.Point;
import de.bioviz.structures.Source;
import de.bioviz.util.Pair;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;


public class DrawableField extends DrawableSprite {

	private static Logger logger = LoggerFactory.getLogger(DrawableField.class);

	public BiochipField field;

	static final Color fieldDefaultColor = Colors.FIELD_COLOR;
	static final Color sinkDefaultColor = Colors.SINK_COLOR;
	static final Color sourceDefaultColor = Colors.SOURCE_COLOR;
	static final Color mixerDefaultColor = Colors.MIXER_COLOR;
	static final Color blockedColor = Colors.BLOCKED_COLOR;


	//private DrawableSprite adjacencyOverlay;

	public DrawableCircuit parentCircuit;

	public DrawableField(BiochipField field, DrawableCircuit parent) {
		super(TextureE.GridMarker, parent.parent);
		this.parentCircuit = parent;
		this.field = field;
		super.addLOD(8, TextureE.BlackPixel);
		//adjacencyOverlay = new AdjacencyOverlay("AdjacencyMarker.png");
	}


	public DisplayValues getDisplayValues() {


		Pair<String, TextureE> msgTexture = getMsgTexture();

		Color color = getColor();

		return new DisplayValues(color, msgTexture.fst, msgTexture.snd);
	}

	public Pair<String, TextureE> getMsgTexture() {

		String fieldHUDMsg = null;
		DrawableCircuit circ = parentCircuit;
		int t = circ.currentTime;
		float xCoord = circ.xCoordOnScreen(field.x());
		float yCoord = circ.yCoordOnScreen(field.y());
		TextureE texture = TextureE.GridMarker;
		this.x = xCoord;
		this.y = yCoord;
		this.scaleX = circ.smoothScaleX;
		this.scaleY = circ.smoothScaleY;


		// TODO what happens if some of these options overlap?
		// Right now only the first occurrence according the order below is
		// taken. This might not be what is intended
		// In general, a detector, for example, is a very valid routing target
		if (this.field.isSink && circ.displayOptions.getOption(BDisplayOptions.SinkIcon)) {
			texture = TextureE.Sink;
		}
		else if (this.field.isDispenser) {
			if (circ.displayOptions.getOption(BDisplayOptions.DispenserIcon)) {
				texture = TextureE.Dispenser;
			}
			if (circ.displayOptions.getOption(BDisplayOptions.DispenserID)) {
				fieldHUDMsg = Integer.toString(field.fluidID);
			}
		}
		else if (this.field.isPotentiallyBlocked()) {
			texture = TextureE.Blockage;
		}
		else if (this.field.getDetector() != null &&
				 circ.displayOptions.getOption(BDisplayOptions.DetectorIcon)) {
			texture = TextureE.Detector;
		}
		else if (!this.field.source_ids.isEmpty()) {
			if (circ.displayOptions.getOption(BDisplayOptions
													  .SourceTargetIcons)) {
				texture = TextureE.Start;
			}

			if (circ.displayOptions.getOption(
					BDisplayOptions.SourceTargetIDs)) {
				ArrayList<Integer> sources = this.field.source_ids;
				fieldHUDMsg = sources.get(0).toString();
				if (sources.size() > 1) {
					for (int i = 2; i < sources.size(); i++) {
						fieldHUDMsg += ", " + sources.get(i);
					}
				}
			}
		}
		else if (!this.field.target_ids.isEmpty()) {
			if (circ.displayOptions.getOption(
					BDisplayOptions.SourceTargetIcons)) {
				texture = TextureE.Target;
			}
			if (circ.displayOptions.getOption(
					BDisplayOptions.SourceTargetIDs)) {
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
		// TODO we really need some kind of mechanism of deciding when to show
		// what
		if (circ.displayOptions.getOption(BDisplayOptions.Pins)) {
			if (this.field.pin != null) {
				fieldHUDMsg = Integer.toString(this.field.pin.pinID);
			}
		}

		return Pair.mkPair(fieldHUDMsg, texture);
	}

	public Color getColor() {


		int colorOverlayCount = 0;
		/*
		We need to create a copy of the FIELD_EMPTY_COLOR as that value is final
		 and thus can not be modified.
		If that value is unchangeable, the cells all stay white
		 */
		Color result = new Color(Colors.FIELD_EMPTY_COLOR);

		if (field.isBlocked(parentCircuit.currentTime)) {
			result.add(blockedColor);
			colorOverlayCount++;
		}
		
		if (parentCircuit.displayOptions.getOption(
				BDisplayOptions.NetColorOnFields)) {
			if (cornerColors == null) {
				cornerColors = new Color[4];
			}
			for (int i = 0; i < cornerColors.length; i++) {
				cornerColors[i] = Color.BLACK.cpy();
			}
			for (Net n : this.parentCircuit.data.getNetsOf(this.field)) {
				Point top = new Point(this.field.x(), this.field.y() + 1);
				Point bottom = new Point(this.field.x(), this.field.y() - 1);
				Point left = new Point(this.field.x() - 1, this.field.y());
				Point right = new Point(this.field.x() + 1, this.field.y());

				if (!parentCircuit.data.hasFieldAt(top) ||
						!n.containsField(parentCircuit.data.getFieldAt(top))) {
					this.cornerColors[1].add(new Color(n.getColor()));
					this.cornerColors[2].add(new Color(n.getColor()));
				}
				if (!parentCircuit.data.hasFieldAt(bottom) ||
						!n.containsField(parentCircuit.data.getFieldAt(bottom))) {
					this.cornerColors[0].add(new Color(n.getColor()));
					this.cornerColors[3].add(new Color(n.getColor()));
				}
				if (!parentCircuit.data.hasFieldAt(left) ||
						!n.containsField(parentCircuit.data.getFieldAt(left))) {
					this.cornerColors[0].add(new Color(n.getColor()));
					this.cornerColors[1].add(new Color(n.getColor()));
				}
				if (!parentCircuit.data.hasFieldAt(right) ||
						!n.containsField(parentCircuit.data.getFieldAt(right))) {
					this.cornerColors[2].add(new Color(n.getColor()));
					this.cornerColors[3].add(new Color(n.getColor()));
				}
			}
			for (int i = 0; i < cornerColors.length; i++) {
				if (!cornerColors[i].equals(Color.BLACK)) {
					cornerColors[i] = cornerColors[i].mul(0.5f).add(
							super.getColor().cpy().mul(0.5f));
				} else {
					cornerColors[i] = super.getColor();
				}
			}
		} else {
			cornerColors = null;
		}


		if (parentCircuit.displayOptions.getOption(BDisplayOptions
														   .CellUsage)) {
			// TODO clevere Methode zum Bestimmen der Farbe wählen (evtl. max
			// Usage verwenden)
			float scalingFactor = 2f;

			result.add(new Color(this.field.usage / scalingFactor, this.field.usage / scalingFactor, this.field.usage / scalingFactor, 0));
			++colorOverlayCount;
		}
		
		/** Colours the interference region **/
		if (parentCircuit.displayOptions.getOption(
				BDisplayOptions.InterferenceRegion)) {
			boolean hasNeighbouringDroplet = false;
			for (Droplet d: parentCircuit.data.getDroplets()) {
				Point p = d.getPositionAt(parentCircuit.currentTime);
				if (p != null && p.adjacent(this.field.pos)) {
					result.add(Colors.INTERFERENCE_REGION_COLOR);
				}
			}
		}


		int t = parentCircuit.currentTime;
		if (parentCircuit.displayOptions.getOption(
				BDisplayOptions.Actuations)) {
			if (field.isActuated(t)) {
				result.add(Colors.ACTAUTED_COLOR);
				++colorOverlayCount;
			}
		}

		// TODO why do we only add something if the count is zero? Save
		// computation time?
		// nope it seems that the cell usage is supposed to override the other
		// overlays
		if (colorOverlayCount == 0) {
			if (this.field.isSink) {
				result.add(sinkDefaultColor);
				colorOverlayCount++;
			}
			else if (this.field.isDispenser) {
				result.add(sourceDefaultColor);
				colorOverlayCount++;
			}
			else {
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

		if (parentCircuit.displayOptions.getOption(BDisplayOptions
														   .Adjacency) &&
			parentCircuit.data.getAdjacentActivations().contains(this.field)) {
			result.add(0.5f, -0.5f, -0.5f, 0);
		}

		result.mul(1f / (float) colorOverlayCount);

		result.clamp();


		return result;
	}

	@Override
	public void draw() {


		DisplayValues vals = getDisplayValues();

		displayText(vals.getMsg());
		this.addLOD(Float.MAX_VALUE, vals.getTexture());


		setColor(vals.getColor());

		super.draw();
		
		if (parentCircuit.displayOptions
				.getOption(BDisplayOptions.LongNetIndicatorsOnFields)) {
			for (Net net : this.parentCircuit.data.getNetsOf(this.field)) {
				for (Source s : net.getSources()) {
					if (this.field.pos.equals(s.startPosition)) {
						Pair<Float, Float> target = new Pair<Float, Float> (
								net.getTarget().fst.floatValue(),
								net.getTarget().snd.floatValue());

						Pair<Float, Float> source = new Pair<Float, Float> (
								s.startPosition.fst.floatValue(),
								s.startPosition.snd.floatValue());

						
						// draw to target
						DrawableLine.draw(source, target,
								Color.BLACK.cpy().sub(0, 0, 0, 0.5f));
					}
				}
			}
		}
	}
}
