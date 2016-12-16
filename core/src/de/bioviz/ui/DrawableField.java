package de.bioviz.ui;

import com.badlogic.gdx.graphics.Color;
import de.bioviz.structures.Actuation;
import de.bioviz.structures.Biochip;
import de.bioviz.structures.BiochipField;
import de.bioviz.structures.Dispenser;
import de.bioviz.structures.Droplet;
import de.bioviz.structures.FluidicConstraintViolation;
import de.bioviz.structures.Mixer;
import de.bioviz.structures.Net;
import de.bioviz.structures.Point;
import de.bioviz.structures.Rectangle;
import de.bioviz.structures.Sink;
import de.bioviz.util.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static de.bioviz.ui.BDisplayOptions.Actuations;
import static de.bioviz.ui.BDisplayOptions.ActuationSymbols;
import static de.bioviz.ui.BDisplayOptions.Adjacency;
import static de.bioviz.ui.BDisplayOptions.CellUsage;
import static de.bioviz.ui.BDisplayOptions.CellUsageCount;
import static de.bioviz.ui.BDisplayOptions.DetectorIcon;
import static de.bioviz.ui.BDisplayOptions.HighlightAnnotatedFields;
import static de.bioviz.ui.BDisplayOptions.InterferenceRegion;
import static de.bioviz.ui.BDisplayOptions.LingeringInterferenceRegions;
import static de.bioviz.ui.BDisplayOptions.NetColorOnFields;
import static de.bioviz.ui.BDisplayOptions.Pins;
import static de.bioviz.ui.BDisplayOptions.SourceTargetIDs;
import static de.bioviz.ui.BDisplayOptions.SourceTargetIcons;


/**
 * <p> The {@link DrawableField} class implements the element that draws its
 * corresponding {@link BiochipField} structure.</p> <p>The core element in this
 * case is the {@link BiochipField} variable called {@link DrawableField}'s
 * field variable, which links back to the original structural object. The
 * {@link DrawableField} then contains the additional information that is needed
 * to draw the field, which currently merely means some color information and
 * some drawing-related methods.</p>
 *
 * @author Jannis Stoppe
 */
public class DrawableField extends DrawableSprite {


	/**
	 * The zoom level at which fields resort to drawing boxes instead of actual
	 * structures.
	 */
	public static final float PIXELIZED_ZOOM_LEVEL = 8;

	/**
	 * The underlying structure that is drawn by this {@link DrawableField}'s
	 * instance.
	 */
	protected BiochipField field;

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
	private DrawableAssay parentAssay;

	/**
	 * Creates an object that draws a given field for a biochip.
	 * <p/>
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
			final BiochipField field, final DrawableAssay parent) {
		super(TextureE.GridMarker, parent.getParent());
		this.setParentAssay(parent);
		this.setField(field);
		super.addLOD(PIXELIZED_ZOOM_LEVEL, TextureE.BlackPixel);
		this.setZ(DisplayValues.DEFAULT_FIELD_DEPTH);
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
		TextureE texture = TextureE.GridMarker;



        /*
		Right now, the first options that is tested and set to true determines
		the returned strings. This means that there might be a display of
		inconsistant data. For example source/target IDs may interfere with a
		detector ID. This is a real use case as a detector is a very valid
		 routing target.

		 There is an execption: the cell usage count overwrites any previous
		 text. I really dislike this case by case hard coding  :/
		 */
		if (field.isPotentiallyBlocked()) {
			texture = TextureE.Blockage;
		} else if (field.getDetector() != null &&
				   getOption(DetectorIcon)) {
			texture = TextureE.Detector;
		} else if (field.getMagnet() != null) {
			texture = TextureE.Magnet;
		} else if (field.getHeater() != null) {
			texture = TextureE.Heater;
		} else if (field.isSource()) {
			if (getOption(SourceTargetIcons)) {
				texture = TextureE.Start;
			}

			if (getOption(SourceTargetIDs)) {
				ArrayList<Integer> sources = field.sourceIDs;
				fieldHUDMsg = sources.get(0).toString();
				if (sources.size() > 1) {
					for (int i = 2; i < sources.size(); i++) {
						fieldHUDMsg += ", " + sources.get(i);
					}
				}
			}
		} else if (field.isTarget()) {
			if (getOption(SourceTargetIcons)) {
				texture = TextureE.Target;
			}
			if (getOption(SourceTargetIDs)) {
				ArrayList<Integer> targets = field.targetIDs;
				fieldHUDMsg = targets.get(0).toString();
				if (targets.size() > 1) {
					for (int i = 1; i < targets.size(); i++) {
						fieldHUDMsg += ", " + targets.get(i);
					}
				}
			}
		}


		// note: this overwrites any previous message
		if (getOption(Pins) && field.pin != null) {
			fieldHUDMsg = Integer.toString(field.pin.pinID);
		}

		if (getOption(CellUsageCount)) {
			fieldHUDMsg = Integer.toString(field.getUsage());
		}

		int t = getParentAssay().getCurrentTime();
		if (getOption(ActuationSymbols)) {
			Actuation act = field.getActuation(t);

			switch (act) {
				case ON:
					fieldHUDMsg="1";
					break;
				case OFF:
					fieldHUDMsg="0";
					break;
				case DONTCARE:
					fieldHUDMsg="X";
			}
		}


		return Pair.mkPair(fieldHUDMsg, texture);
	}


	/**
	 * Computes the cell coloring based on the usage.
	 *
	 * @param result
	 * 		The color that is to be adjusted by this method.
	 * @return 1 if cell usage was used, 0 otherwise
	 */
	private int cellUsageColoring(de.bioviz.ui.Color result) {
		if (getOption(CellUsage)) {
			float scalingFactor = this.parentAssay.getData().getMaxUsage();
			int usage = field.getUsage();
			float color = usage / scalingFactor;
			result.add(new Color(color, color, color, 0));
			return 1;
		}
		return 0;
	}

	/**
	 * Decorates cells that are on the corners of a net bounding box.
	 */
	private void netColoring() {
		/**
		 * The NetColorOnFields display option is a little special and thus
		 * gets quite some amount of code here.
		 * The idea is that we use the sprite's corner vertices and colorize
		 * them separately *if* they are part of a net's edge. At first, these
		 * colors are stored in the cornerColors array. When drawing, this
		 * array is checked for existence and if it isn't null, each none-black
		 * color *completely overrides* the given field color at this corner.
		 */
		if (getOption(NetColorOnFields)) {
			if (cornerColors == null) {
				cornerColors = new Color[4];    // one color for each corner
			}


			final int bottomleft = 0;
			final int topleft = 1;
			final int topright = 2;
			final int bottomright = 3;


			for (int i = 0; i < cornerColors.length; i++) {
				// Create non-null array contents
				cornerColors[i] = Color.BLACK.cpy();
			}
			for (final Net net : this.getParentAssay().getData().
					getNetsOf(this.getField())) {
				de.bioviz.ui.Color netCol = net.getColor().cpy();

				// Increase brightness for hovered nets
				if (this.parentAssay.getHoveredField() != null &&
					this.getParentAssay().getData().
							getNetsOf(this.getParentAssay().
									getHoveredField().field).contains(net)) {
					netCol.add(Colors.HOVER_NET_DIFF_COLOR);

				}
				Point top = new Point(field.x(), field.y() + 1);
				Point bottom = new Point(field.x(), field.y() - 1);
				Point left = new Point(field.x() - 1, field.y());
				Point right = new Point(field.x() + 1, field.y());

				Color color = netCol.buildGdxColor();


				Biochip parent = getParentAssay().getData();

				boolean fieldAtTop = parent.hasFieldAt(top);
				boolean fieldAtBottom = parent.hasFieldAt(bottom);
				boolean fieldAtLeft = parent.hasFieldAt(left);
				boolean fieldAtRight = parent.hasFieldAt(right);


				boolean containsTop =
						fieldAtTop && net.containsField(parent.getFieldAt
								(top));
				boolean containsBottom = fieldAtBottom &&
										 net.containsField(
												 parent.getFieldAt(bottom));
				boolean containsLeft =
						fieldAtLeft &&
						net.containsField(parent.getFieldAt(left));
				boolean containsRight = fieldAtRight &&
										net.containsField(
												parent.getFieldAt(right));

				if (!fieldAtTop || !containsTop) {
					this.cornerColors[topleft].add(color);
					this.cornerColors[topright].add(color);
				}
				if (!fieldAtBottom || !containsBottom) {
					this.cornerColors[bottomleft].add(color);
					this.cornerColors[bottomright].add(color);
				}
				if (!fieldAtLeft || !containsLeft) {
					this.cornerColors[bottomleft].add(color);
					this.cornerColors[topleft].add(color);
				}
				if (!fieldAtRight || !containsRight) {
					this.cornerColors[topright].add(color);
					this.cornerColors[bottomright].add(color);
				}
			}
			for (int i = 0; i < cornerColors.length; i++) {
				if (cornerColors[i].equals(Color.BLACK)) {
					cornerColors[i] = super.getColor();
				}
			}
		} else {
			cornerColors = null;
		}
	}

	/**
	 * Calculates the current color based on the parent circuit's
	 * displayOptions.
	 *
	 * @return the field's color.
	 */
	@Override
	public Color getColor() {

		/**
		 * This value stores the amount of colors being overlaid in the process
		 * of computing the color. This is currently required to calculate the
		 * average value of all colors at the end of the process (e.g. if three
		 * different colors are being added, the final result needs to be
		 * divided by three).
		 */
		int colorOverlayCount = 0;

		/*
		We need to create a copy of the FIELD_EMPTY_COLOR as that value is
		final and thus can not be modified.
		If that value is unchangeable, the cells all stay white
		 */
		de.bioviz.ui.Color result = new de.bioviz.ui.Color(Color.BLACK);

		if (getField().isBlocked(getParentAssay().getCurrentTime())) {
			result.add(Colors.BLOCKED_COLOR);
			colorOverlayCount++;
		}


		netColoring();

		colorOverlayCount += cellUsageColoring(result);

		colorOverlayCount += inteferenceRegionColoring(result);


		/**
		 * Here we highlight cells based on their actuation value
		 */
		int t = getParentAssay().getCurrentTime();
		if (getOption(Actuations)) {
			Actuation act = field.getActuation(t);

			switch (act) {
				case ON:
					result.add(Colors.ACTAUTION_ON_COLOR);
					break;
				case OFF:
					result.add(Colors.ACTAUTION_OFF_COLOR);
					break;
				case DONTCARE:
					result.add(Colors.ACTAUTION_DONTCARE_COLOR);
			}
			++colorOverlayCount;
		}


		if (colorOverlayCount == 0) {
			colorOverlayCount += typeColoring(result, t);
		}


		if (getOption(Adjacency)) {
			final Stream<FluidicConstraintViolation> violations =
					getParentAssay().getData().getAdjacentActivations()
							.stream();

			if (violations.anyMatch(v -> v.containsField(this.field))) {
				result.add(Colors.ADJACENT_ACTIVATION_COLOR);
			}
		}

		if (colorOverlayCount > 0) {
			result.mul(1f / ((float) colorOverlayCount));
			result.clamp();
		} else {
			result = new de.bioviz.ui.Color(Colors.FIELD_COLOR);
		}

		if (this.isHovered()) {
			result.add(Colors.HOVER_DIFF_COLOR);
		}

		if (getOption(HighlightAnnotatedFields) && field.hasAnnotations()) {
			result = new de.bioviz.ui.Color(Color.VIOLET);
		}

		return result.buildGdxColor().cpy();
	}

	/**
	 * Computes the color based on the type of the field.
	 *
	 * @param result
	 * 		The resulting color.
	 * @param timeStep
	 * 		The current time step.
	 * @return The amount of new color overlays.
	 */
	private int typeColoring(de.bioviz.ui.Color result, final int timeStep) {
		int colorOverlayCount = 0;
		if (field instanceof Sink) {
			result.add(Colors.SINK_COLOR);
			colorOverlayCount++;
		} else if (field instanceof Dispenser) {
			result.add(Colors.SOURCE_COLOR);
			colorOverlayCount++;
		} else {
			result.add(Colors.FIELD_COLOR);
			colorOverlayCount++;
		}

		for (final Mixer m : field.mixers) {
			if (m.timing.inRange(timeStep)) {
				result.add(Colors.MIXER_COLOR);
			}
		}
		return colorOverlayCount;
	}

	/**
	 * Colors based on the interference region.
	 *
	 * @param result
	 * 		The color that results from this method call.
	 * @return The amount of color overlays produced by this method.
	 */
	private int inteferenceRegionColoring(de.bioviz.ui.Color result) {
		int colorOverlayCount = 0;
		/** Colours the interference region **/
		if (getOption(InterferenceRegion)) {
			int amountOfInterferenceRegions = 0;
			final Set<Droplet> dropsSet =
					getParentAssay().getData().getDroplets();

			ArrayList<Droplet> drops = dropsSet.stream().
					filter(d -> isPartOfInterferenceRegion(d)).
					collect(Collectors.toCollection(ArrayList<Droplet>::new));

			for (int i = 0; i < drops.size(); ++i) {
				boolean interferenceViolation = false;
				for (int j = i + 1; j < drops.size(); j++) {
					final Droplet drop1 = drops.get(i);
					final Droplet drop2 = drops.get(j);
					boolean sameNet =
							getParentAssay().getData().sameNet(drop1, drop2);
					if (!sameNet) {
						result.add(Colors.INTERFERENCE_REGION_OVERLAP_COLOR);
						++colorOverlayCount;
						interferenceViolation = true;
					}
				}

				/*
				We only increase the amount of interference regions if no
				violation took place. This makes sense as a violation is
				handled
				differently.
				 */
				if (!interferenceViolation) {
					++amountOfInterferenceRegions;
				}
			}

			if (amountOfInterferenceRegions > 0) {
				float scale = (float) Math.sqrt(amountOfInterferenceRegions);
				Color c = new Color(Colors.INTERFERENCE_REGION_COLOR);
				result.add(c.mul(scale));
				++colorOverlayCount;
			}

		}
		return colorOverlayCount;
	}

	@Override
	public void draw() {
		DisplayValues vals = getDisplayValues();

		displayText(vals.getMsg());
		setColor(vals.getColor());

		DrawableAssay circ = getParentAssay();
		float xCoord = circ.xCoordOnScreen(getField().x());
		float yCoord = circ.yCoordOnScreen(getField().y());
		this.setX(xCoord);
		this.setY(yCoord);
		this.setScaleX(circ.getSmoothScale());
		this.setScaleY(circ.getSmoothScale());

		// this call is actually necessary to draw any textures at all!
		this.addLOD(Float.MAX_VALUE, vals.getTexture());

		super.draw();

		// show the first annotation for this field
		if (isHovered() && field.areaAnnotations.size() > 0) {
			displayText(field.areaAnnotations.get(0).getAnnotation());
		}

		// TODO why is drawing of lines in any way tied to the actual fields?!

	}


	/**
	 * Calculates whether or not this field is part of a droplet's interference
	 * region.
	 *
	 * @param d
	 * 		the droplet to calculate it for
	 * @return whether or not this field is part of its interference region
	 */
	private boolean isPartOfInterferenceRegion(final Droplet d) {
		Rectangle currPos = d.getPositionAt(
				getParentAssay().getCurrentTime()
		);
		Rectangle prevPos = d.getPositionAt(
				getParentAssay().getCurrentTime() - 1
		);

		boolean currAdj = currPos != null && currPos.adjacent(field.pos);

		if (getOption(LingeringInterferenceRegions)) {
			boolean prevAdj = prevPos != null && prevPos.adjacent(field.pos);
			return currAdj || prevAdj;
		} else {
			return currAdj;
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
	 * shouldn't really be used at any point after the {@link DrawableAssay}
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
	public DrawableAssay getParentAssay() {
		return parentAssay;
	}

	/**
	 * Sets the parent circuit of this field. This shouldn't really be used
	 * after the whole circuit has been initialized.
	 *
	 * @param parentAssay
	 * 		the circuit that contains this field.
	 */
	public void setParentAssay(final DrawableAssay parentAssay) {
		this.parentAssay = parentAssay;
	}

	/**
	 * Convenience method for checking options.
	 *
	 * @param optn
	 * 		Option to check
	 * @return true if option is true
	 */
	protected boolean getOption(final BDisplayOptions optn) {
		return getParentAssay().getDisplayOptions().getOption(optn);
	}
}
