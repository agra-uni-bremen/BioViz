package de.bioviz.ui;

import com.badlogic.gdx.graphics.Color;

import de.bioviz.structures.BiochipField;
import de.bioviz.structures.Droplet;
import de.bioviz.structures.Mixer;
import de.bioviz.structures.Net;
import de.bioviz.structures.Point;
import de.bioviz.util.Pair;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;


/**
 * <p> The {@link DrawableField} class implements the element that draws its
 * corresponding {@link BiochipField} structure.</p>
 * <p>The core element in this case is the {@link BiochipField} variable called
 * {@link DrawableField}'s field variable, which links back to the original
 * structural object. The {@link DrawableField} then contains the additional
 * information that is needed to draw the field, which currently merely means
 * some color information and some drawing-related methods.</p>
 *
 * @author jannis
 *
 */
public class DrawableField extends DrawableSprite {

	/**
	 * The default color used for fields.
	 */
	public static final Color FIELD_DEFAULT_COLOR = Colors.FIELD_COLOR;

	/**
	 * The default color used for sinks.
	 */
	public static final Color SINK_DEFAULT_COLOR = Colors.SINK_COLOR;

	/**
	 * The default color used for sources.
	 */
	public static final Color SOURCE_DEFAULT_COLOR = Colors.SOURCE_COLOR;

	/**
	 * The default color used for mixers.
	 */
	public static final Color MIXER_DEFAULT_COLOR = Colors.MIXER_COLOR;

	/**
	 * The default color used for blockages.
	 */
	public static final Color BLOCKED_COLOR = Colors.BLOCKED_COLOR;

	/**
	 * The zoom level at which fields resort to drawing boxes instead of actual
	 * structures.
	 */
	public static final float PIXELIZED_ZOOM_LEVEL = 8;

	/**
	 * Used to log anything related to the {@link DrawableField} activities.
	 */
	private static Logger logger = LoggerFactory.getLogger(DrawableField.class);

	/**
	 * The circuit this field is a part of. This again links to the drawable
	 * version of the circuit, not the structure that represents the circuit
	 * itself.
	 */
	private DrawableCircuit parentCircuit;

	/**
	 * The underlying structure that is drawn by this {@link DrawableField}'s
	 * instance.
	 */
	private BiochipField field;

	/**
	 * <p>Creates an object that draws a given field for a biochip.</p>
	 * <p>Notice that we separate the structure from the drawing, hence the
	 * separation of Drawable-something vs structural classes. This class
	 * needs the structural information what it's supposed to draw (given via
	 * the field parameter) and the drawable parent circuit instance that it
	 * belongs to (via the parent parameter). We currently silently assume that
	 * the structures are consistent (i.e. that the given field's parent circuit
	 * is the one that is drawn via this instance's drawable parent) but do not
	 * enforce any checks in this way, so don't break it.</p>
	 * @param field the field that is supposed to be drawn by this instance
	 * @param parent this field's drawable parent
	 */
	public DrawableField(
			final BiochipField field, final DrawableCircuit parent) {
		super(TextureE.GridMarker, parent.parent);
		this.setParentCircuit(parent);
		this.setField(field);
		super.addLOD(PIXELIZED_ZOOM_LEVEL, TextureE.BlackPixel);
		//adjacencyOverlay = new AdjacencyOverlay("AdjacencyMarker.png");
	}


	public DisplayValues getDisplayValues() {


		Pair<String, TextureE> msgTexture = getMsgTexture();

		Color color = getColor();

		return new DisplayValues(color, msgTexture.fst, msgTexture.snd);
	}

	public Pair<String, TextureE> getMsgTexture() {

		String fieldHUDMsg = null;
		DrawableCircuit circ = getParentCircuit();
		int t = circ.currentTime;
		float xCoord = circ.xCoordOnScreen(getField().x());
		float yCoord = circ.yCoordOnScreen(getField().y());
		TextureE texture = TextureE.GridMarker;
		this.x = xCoord;
		this.y = yCoord;
		this.scaleX = circ.smoothScaleX;
		this.scaleY = circ.smoothScaleY;


		// TODO what happens if some of these options overlap?
		// Right now only the first occurrence according the order below is
		// taken. This might not be what is intended
		// In general, a detector, for example, is a very valid routing target
		if (this.getField().isSink &&
			circ.displayOptions.getOption(BDisplayOptions.SinkIcon)) {
			texture = TextureE.Sink;
		} else if (this.getField().isDispenser) {
			if (circ.displayOptions.getOption(BDisplayOptions.DispenserIcon)) {
				texture = TextureE.Dispenser;
			}
			if (circ.displayOptions.getOption(BDisplayOptions.DispenserID)) {
				fieldHUDMsg = Integer.toString(getField().fluidID);
			}
		} else if (this.getField().isPotentiallyBlocked()) {
			texture = TextureE.Blockage;
		} else if (this.getField().getDetector() != null &&
				 circ.displayOptions.getOption(BDisplayOptions.DetectorIcon)) {
			texture = TextureE.Detector;
		} else if (!this.getField().source_ids.isEmpty()) {
			if (circ.displayOptions.getOption(BDisplayOptions
													  .SourceTargetIcons)) {
				texture = TextureE.Start;
			}

			if (circ.displayOptions.getOption(
					BDisplayOptions.SourceTargetIDs)) {
				ArrayList<Integer> sources = this.getField().source_ids;
				fieldHUDMsg = sources.get(0).toString();
				if (sources.size() > 1) {
					for (int i = 2; i < sources.size(); i++) {
						fieldHUDMsg += ", " + sources.get(i);
					}
				}
			}
		} else if (!this.getField().target_ids.isEmpty()) {
			if (circ.displayOptions.getOption(
					BDisplayOptions.SourceTargetIcons)) {
				texture = TextureE.Target;
			}
			if (circ.displayOptions.getOption(
					BDisplayOptions.SourceTargetIDs)) {
				ArrayList<Integer> targets = this.getField().target_ids;
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
			if (this.getField().pin != null) {
				fieldHUDMsg = Integer.toString(this.getField().pin.pinID);
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

		if (getField().isBlocked(getParentCircuit().currentTime)) {
			result.add(BLOCKED_COLOR);
			colorOverlayCount++;
		}
		
		if (getParentCircuit().displayOptions.getOption(
				BDisplayOptions.NetColorOnFields)) {
			if (cornerColors == null) {
				cornerColors = new Color[4];
			}
			for (int i = 0; i < cornerColors.length; i++) {
				cornerColors[i] = Color.BLACK.cpy();
			}
			for (Net n : this.getParentCircuit().data.
					getNetsOf(this.getField())) {
				Point top = new Point(
						this.getField().x(), this.getField().y() + 1);
				Point bottom = new Point(
						this.getField().x(), this.getField().y() - 1);
				Point left = new Point(
						this.getField().x() - 1, this.getField().y());
				Point right = new Point(
						this.getField().x() + 1, this.getField().y());

				if (!getParentCircuit().data.hasFieldAt(top) ||
						!n.containsField(getParentCircuit().data.getFieldAt(top))) {
					this.cornerColors[1].add(new Color(n.getColor()));
					this.cornerColors[2].add(new Color(n.getColor()));
				}
				if (!getParentCircuit().data.hasFieldAt(bottom) ||
						!n.containsField(getParentCircuit().data.getFieldAt(bottom))) {
					this.cornerColors[0].add(new Color(n.getColor()));
					this.cornerColors[3].add(new Color(n.getColor()));
				}
				if (!getParentCircuit().data.hasFieldAt(left) ||
						!n.containsField(getParentCircuit().data.getFieldAt(left))) {
					this.cornerColors[0].add(new Color(n.getColor()));
					this.cornerColors[1].add(new Color(n.getColor()));
				}
				if (!getParentCircuit().data.hasFieldAt(right) ||
						!n.containsField(getParentCircuit().data.getFieldAt(right))) {
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


		if (getParentCircuit().displayOptions.getOption(BDisplayOptions
														   .CellUsage)) {
			// TODO clevere Methode zum Bestimmen der Farbe wählen (evtl. max
			// Usage verwenden)
			float scalingFactor = 2f;

			result.add(new Color(this.getField().usage / scalingFactor, this.getField().usage / scalingFactor, this.getField().usage / scalingFactor, 0));
			++colorOverlayCount;
		}
		
		/** Colours the interference region **/
		if (getParentCircuit().displayOptions.getOption(
				BDisplayOptions.InterferenceRegion)) {
			boolean hasNeighbouringDroplet = false;
			for (Droplet d: getParentCircuit().data.getDroplets()) {
				Point p = d.getPositionAt(getParentCircuit().currentTime);
				if (p != null && p.adjacent(this.getField().pos)) {
					result.add(Colors.INTERFERENCE_REGION_COLOR);
				}
			}
		}


		int t = getParentCircuit().currentTime;
		if (getParentCircuit().displayOptions.getOption(
				BDisplayOptions.Actuations)) {
			if (getField().isActuated(t)) {
				result.add(Colors.ACTAUTED_COLOR);
				++colorOverlayCount;
			}
		}

		// TODO why do we only add something if the count is zero? Save
		// computation time?
		// nope it seems that the cell usage is supposed to override the other
		// overlays
		if (colorOverlayCount == 0) {
			if (this.getField().isSink) {
				result.add(SINK_DEFAULT_COLOR);
				colorOverlayCount++;
			}
			else if (this.getField().isDispenser) {
				result.add(SOURCE_DEFAULT_COLOR);
				colorOverlayCount++;
			}
			else {
				result.add(FIELD_DEFAULT_COLOR);
				colorOverlayCount++;
			}
			if (!this.getField().mixers.isEmpty()) {

				for (Mixer m : this.getField().mixers) {
					if (m.timing.inRange(t)) {
						result.add(MIXER_DEFAULT_COLOR);
					}
				}
			}
		}

		if (getParentCircuit().displayOptions.getOption(BDisplayOptions
														   .Adjacency) &&
			getParentCircuit().data.getAdjacentActivations().contains(this.getField())) {
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
	}


	public BiochipField getField() {
		return field;
	}


	public void setField(BiochipField field) {
		this.field = field;
	}


	public DrawableCircuit getParentCircuit() {
		return parentCircuit;
	}


	public void setParentCircuit(DrawableCircuit parentCircuit) {
		this.parentCircuit = parentCircuit;
	}
}
