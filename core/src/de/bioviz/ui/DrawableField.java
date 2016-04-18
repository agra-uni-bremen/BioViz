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

import static de.bioviz.ui.BDisplayOptions.*;

/**
 * <p> The {@link DrawableField} class implements the element that draws its
 * corresponding {@link BiochipField} structure.</p> <p>The core element in this
 * case is the {@link BiochipField} variable called {@link DrawableField}'s
 * field variable, which links back to the original structural object. The
 * {@link DrawableField} then contains the additional information that is needed
 * to draw the field, which currently merely means some color information and
 * some drawing-related methods.</p>
 *
 * @author jannis
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
	 * Overlay color used for fields that have adjacent activations.
	 */
	public static final Color ADJACENT_ACTIVATION_COLOR =
			new Color(0.5f, -0.5f, -0.5f, 0);

	/**
	 * The zoom level at which fields resort to drawing boxes instead of actual
	 * structures.
	 */
	public static final float PIXELIZED_ZOOM_LEVEL = 8;

	/**
	 * Used to log anything related to the {@link DrawableField} activities.
	 */
	private static Logger logger = LoggerFactory.getLogger(DrawableField
																   .class);

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
	 * separation of Drawable-something vs structural classes. This class needs
	 * the structural information what it's supposed to draw (given via the
	 * field parameter) and the drawable parent circuit instance that it
	 * belongs
	 * to (via the parent parameter). We currently silently assume that the
	 * structures are consistent (i.e. that the given field's parent circuit is
	 * the one that is drawn via this instance's drawable parent) but do not
	 * enforce any checks in this way, so don't break it.</p>
	 *
	 * @param field
	 * 		the field that is supposed to be drawn by this instance
	 * @param parent
	 * 		this field's drawable parent
	 */
	public DrawableField(
			final BiochipField field, final DrawableCircuit parent) {
		super(TextureE.GridMarker, parent.parent);
		this.setParentCircuit(parent);
		this.setField(field);
		super.addLOD(PIXELIZED_ZOOM_LEVEL, TextureE.BlackPixel);
		//adjacencyOverlay = new AdjacencyOverlay("AdjacencyMarker.png");
	}

	/**
	 * Retrieves information about this field: the color, the message being
	 * displayed on top and the texture. All contained in a {@link
	 * DisplayValues} instance.
	 *
	 * @return the current color, message and texture
	 */
	public DisplayValues getDisplayValues() {
		Pair<String, TextureE> msgTexture = getMsgTexture();
		Color color = getColor();
		return new DisplayValues(color, msgTexture.fst, msgTexture.snd);
	}

	/**
	 * Retrieves this field's texture and the message being displayed on top.
	 *
	 * @return a {@link Pair} of message and texture.
	 */
	public Pair<String, TextureE> getMsgTexture() {

		String fieldHUDMsg = null;
		DrawableCircuit circ = getParentCircuit();
		int t = circ.currentTime;
		float xCoord = circ.xCoordOnScreen(getField().x());
		float yCoord = circ.yCoordOnScreen(getField().y());
		TextureE texture = TextureE.GridMarker;
		this.setX(xCoord);
		this.setY(yCoord);
		this.setScaleX(circ.smoothScaleX);
		this.setScaleY(circ.smoothScaleY);


		// TODO what happens if some of these options overlap?
		/*
		Right now, the first options that is tested and set to true determines
		the returned strings. This means that there might be a display of
		inconsistant data. For example source/target IDs may interfere with a
		detector ID. This is a real use case as a detector is a very valid
		 routing target.

		 There is an execption: the cell usage count overwrites any previous
		 text. I really dislike this case by case hard coding  :/
		 */
		if (this.getField().isSink && option(SinkIcon)) {
			texture = TextureE.Sink;
		}
		else if (this.getField().isDispenser) {
			if (option(DispenserIcon)) {
				texture = TextureE.Dispenser;
			}
			if (option(DispenserID)) {
				fieldHUDMsg = Integer.toString(getField().fluidID);
			}
		}
		else if (this.getField().isPotentiallyBlocked()) {
			texture = TextureE.Blockage;
		}
		else if (this.getField().getDetector() != null &&
				 option(DetectorIcon)) {
			texture = TextureE.Detector;
		}
		else if (!this.getField().source_ids.isEmpty()) {
			if (option(SourceTargetIcons)) {
				texture = TextureE.Start;
			}

			if (option(SourceTargetIDs)) {
				ArrayList<Integer> sources = this.getField().source_ids;
				fieldHUDMsg = sources.get(0).toString();
				if (sources.size() > 1) {
					for (int i = 2; i < sources.size(); i++) {
						fieldHUDMsg += ", " + sources.get(i);
					}
				}
			}
		}
		else if (!this.getField().target_ids.isEmpty()) {
			if (option(SourceTargetIcons)) {
				texture = TextureE.Target;
			}
			if (option(SourceTargetIDs)) {
				ArrayList<Integer> targets = this.getField().target_ids;
				fieldHUDMsg = targets.get(0).toString();
				if (targets.size() > 1) {
					for (int i = 1; i < targets.size(); i++) {
						fieldHUDMsg += ", " + targets.get(i);
					}
				}
			}
		}

		// NOTE the cell usage count *overwrites* any previous text!
		if (option(CellUsageCount)) {
			int usage = getField().usage;
			if (usage > 0) {
				fieldHUDMsg = Integer.toString(usage);
			}
		}


		// note: this overwrites any previous message
		// TODO we really need some kind of mechanism of deciding when to show
		// what
		if (option(Pins)) {
			if (this.getField().pin != null) {
				fieldHUDMsg = Integer.toString(this.getField().pin.pinID);
			}
		}

		return Pair.mkPair(fieldHUDMsg, texture);
	}

	/**
	 * Calculates the current color based on the parent circuit's
	 * displayOptions.
	 *
	 * @return the field's color.
	 */
	public Color getColor() {
		int colorOverlayCount = 0;
		/*
		We need to create a copy of the FIELD_EMPTY_COLOR as that value is
		final
		 and thus can not be modified.
		If that value is unchangeable, the cells all stay white
		 */
		de.bioviz.ui.Color result = new de.bioviz.ui.Color(Color.BLACK);

		if (getField().isBlocked(getParentCircuit().currentTime)) {
			result.add(BLOCKED_COLOR);
			colorOverlayCount++;
		}

		/**
		 * The NetColorOnFields display option is a little special and thus
		 * gets quite some amount of code here.
		 * The idea is that we use the sprite's corner vertices and colorize
		 * them separately *if* they are part of a net's edge. At first, these
		 * colors are stored in the cornerColors array. When drawing, this
		 * array is checked for existence and if it isn't null, each none-black
		 * color *completely overrides* the given field color at this corner.
		 */
		if (option(NetColorOnFields)) {
			if (cornerColors == null) {
				cornerColors = new Color[4];    // one color for each corner
			}
			for (int i = 0; i < cornerColors.length; i++) {
				// Create non-null array contents
				cornerColors[i] = Color.BLACK.cpy();
			}
			for (final Net n : this.getParentCircuit().getData().
					getNetsOf(this.getField())) {
				de.bioviz.ui.Color netCol = n.getColor().cpy();

				// Increase brightness for hovered nets
				if (this.parentCircuit.getHoveredField() != null) {
					if (this.getParentCircuit().getData().getNetsOf
							(this.getParentCircuit().getHoveredField().field).
							contains(n)) {
						netCol.add(0.5f, 0.5f, 0.5f, 0);
					}
				}
				Point top = new Point(
						this.getField().x(), this.getField().y() + 1);
				Point bottom = new Point(
						this.getField().x(), this.getField().y() - 1);
				Point left = new Point(
						this.getField().x() - 1, this.getField().y());
				Point right = new Point(
						this.getField().x() + 1, this.getField().y());

				final int bottomleft = 0;
				final int topleft = 1;
				final int topright = 2;
				final int bottomright = 3;
				if (!getParentCircuit().getData().hasFieldAt(top) ||
						!n.containsField(
								getParentCircuit().getData().getFieldAt(top))) {
					this.cornerColors[topleft].add(netCol.buildGdxColor());
					this.cornerColors[topright].add(netCol.buildGdxColor());
				}
				if (!getParentCircuit().getData().hasFieldAt(bottom) ||
						!n.containsField(
								getParentCircuit().getData().getFieldAt(bottom))) {
					this.cornerColors[bottomleft].add(netCol.buildGdxColor());
					this.cornerColors[bottomright].add(netCol.buildGdxColor());
				}
				if (!getParentCircuit().getData().hasFieldAt(left) ||
						!n.containsField(
								getParentCircuit().getData().getFieldAt(left))) {
					this.cornerColors[bottomleft].add(netCol.buildGdxColor());
					this.cornerColors[topleft].add(netCol.buildGdxColor());
				}
				if (!getParentCircuit().getData().hasFieldAt(right) ||
						!n.containsField(
								getParentCircuit().getData().getFieldAt(right))) {
					this.cornerColors[topright].add(netCol.buildGdxColor());
					this.cornerColors[bottomright].add(netCol.buildGdxColor());
				}
			}
			for (int i = 0; i < cornerColors.length; i++) {
				if (cornerColors[i].equals(Color.BLACK)) {
					cornerColors[i] = super.getColor();
				}
			}
		}
		else {
			cornerColors = null;
		}

		if (option(CellUsage)) {
			// TODO clevere Methode zum Bestimmen der Farbe wählen (evtl. max
			// Usage verwenden)
			float scalingFactor = this.parentCircuit.getData().getMaxUsage();
			result.add(new Color(
					this.getField().usage / scalingFactor,
					this.getField().usage / scalingFactor,
					this.getField().usage / scalingFactor,
					0));
			++colorOverlayCount;
		}

		/** Colours the interference region **/
		if (option(InterferenceRegion)) {
			int amountOfInterferenceRegions = 0;
			for (final Droplet d : getParentCircuit().getData().getDroplets()) {
				if (isPartOfInterferenceRegion(d)) {
					boolean interferenceViolation = false;
					for (DrawableDroplet d2 : parentCircuit.droplets) {
						if (d2.droplet.getPositionAt(this.parentCircuit.currentTime) != null &&
								d2.droplet.getNet() != d.getNet() &&
								d2.droplet.getPositionAt(this.parentCircuit.currentTime).equals(this.field.pos)) {
							result.add(Colors.INTERFERENCE_REGION_OVERLAP_COLOR);
							++colorOverlayCount;
							interferenceViolation = true;
						}
					}
					if (!interferenceViolation) {
						++amountOfInterferenceRegions;
					}
				}
			}
			
			if (amountOfInterferenceRegions > 0) {
				result.add(new de.bioviz.ui.Color(Colors.INTERFERENCE_REGION_COLOR).mul((float)Math.sqrt(amountOfInterferenceRegions)));
				++colorOverlayCount;
			}
		}

		int t = getParentCircuit().currentTime;
		if (option(Actuations)) {
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

				for (final Mixer m : this.getField().mixers) {
					if (m.timing.inRange(t)) {
						result.add(MIXER_DEFAULT_COLOR);
					}
				}
			}
		}

		if (option(Adjacency) &&
			getParentCircuit().getData().getAdjacentActivations().contains(
					this.getField())) {
			result.add(ADJACENT_ACTIVATION_COLOR);
		}
		
		if (colorOverlayCount > 0) {
			result.mul(1f / ((float) colorOverlayCount));
			result.clamp();
		} else {
			result = new de.bioviz.ui.Color(Colors.FIELD_COLOR);
		}
		
		if (this.isHovered()) {
			result.add(0.2f, 0.2f, 0.2f, 0);
		}

		return result.buildGdxColor();
	}

	@Override
	public void draw() {
		DisplayValues vals = getDisplayValues();

		displayText(vals.getMsg());
		setColor(vals.getColor());

		// this call is actually necessary to draw any textures at all!
		this.addLOD(Float.MAX_VALUE, vals.getTexture());

		super.draw();

		if (option(LongNetIndicatorsOnFields)) {
			for (Net net : this.parentCircuit.getData().getNetsOf(this.field)) {
				for (Source s : net.getSources()) {
					if (this.field.pos.equals(s.startPosition)) {
						Pair<Float, Float> target = new Pair<Float, Float>(
								net.getTarget().fst.floatValue(),
								net.getTarget().snd.floatValue());

						Pair<Float, Float> source = new Pair<Float, Float>(
								s.startPosition.fst.floatValue(),
								s.startPosition.snd.floatValue());


						// draw to target
						DrawableLine.draw(
								source, target,
								Color.BLACK.cpy().sub(0, 0, 0, 0.5f));
					}
				}
			}
		}
	}

	/**
	 * Calculates whether or not this field is part of a droplet's interference
	 * region.
	 * @param d the droplet to calculate it for
	 * @return whether or not this field is part of its interference region
	 */
	private boolean isPartOfInterferenceRegion(Droplet d) {
		Point cur_pos = d.getPositionAt(getParentCircuit().currentTime);
		Point prev_pos = d.getPositionAt(getParentCircuit().currentTime-1);
		if(parentCircuit.displayOptions
				.getOption(BDisplayOptions.LingeringInterferenceRegions)) {
			return 	(cur_pos != null &&
					cur_pos.adjacent(this.getField().pos)) ||
					(prev_pos != null &&
					prev_pos.adjacent(this.getField().pos));
		} else {
			return 	(cur_pos != null &&
					cur_pos.adjacent(this.getField().pos));
		}
	}


	/**
	 * Retrieves the *structural* field that is drawn by this {@link
	 * DrawableField}.
	 *
	 * @return the field that is drawn by this {@link DrawableField}
	 */
	public BiochipField getField() {
		return field;
	}

	/**
	 * Sets the field that is drawn by this {@link DrawableField}. This
	 * shouldn't really be used at any point after the {@link DrawableCircuit}
	 * has been fully initialized.
	 *
	 * @param field
	 * 		the field that should be drawn by this {@link DrawableField}
	 */
	public void setField(final BiochipField field) {
		this.field = field;
	}

	/**
	 * Retrieves the parent circuit of this field.
	 *
	 * @return the circuit that contains this field
	 */
	public DrawableCircuit getParentCircuit() {
		return parentCircuit;
	}

	/**
	 * Sets the parent circuit of this field. This shouldn't really be used
	 * after the whole circuit has been initialized.
	 *
	 * @param parentCircuit
	 * 		the circuit that contains this field.
	 */
	public void setParentCircuit(final DrawableCircuit parentCircuit) {
		this.parentCircuit = parentCircuit;
	}

	/**
	 * Convenience function for checking options.
	 * <p>
	 * It basically is syntactic sugar to make the huge amount of checking
	 * above
	 * easier to read.
	 *
	 * @param opt
	 * 		The option to check
	 * @return whether the option is set
	 */
	private boolean option(BDisplayOptions opt) {
		return getParentCircuit().displayOptions.getOption(opt);
	}
}
