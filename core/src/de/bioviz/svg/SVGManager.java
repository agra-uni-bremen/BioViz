package de.bioviz.svg;


import com.badlogic.gdx.graphics.Color;
import de.bioviz.structures.Biochip;
import de.bioviz.structures.Net;
import de.bioviz.structures.Point;
import de.bioviz.structures.Source;
import de.bioviz.ui.BDisplayOptions;
import de.bioviz.ui.DisplayValues;
import de.bioviz.ui.DrawableCircuit;
import de.bioviz.ui.DrawableDroplet;
import de.bioviz.ui.DrawableField;
import de.bioviz.ui.DrawableRoute;
import de.bioviz.ui.TextureE;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Set;

/**
 * @author malu, keszocze
 * Texture cache class.
 * <p>
 * This class manages texture themes. One can specify a folder in which the png
 * files that are loaded as textures are stored. These images are thenn either
 * loaded on demand or returned from the cache.
 */
public class SVGManager {

	/** svgExportSettings. */
	private static SVGExportSettings svgExportSettings = SVGExportSettings
			.getInstance();

	/** logger. */
	private static final Logger LOGGER = LoggerFactory.getLogger(
			SVGManager.class);

	/** svgCoreCreator. */
	private SVGCoreCreator svgCoreCreator;

	/** hashMap for uncolored svg elements. */
	private HashMap<TextureE, String> svgs = new HashMap<>();
	/** hashMap for colored svg elements. */
	private HashMap<String, String> colSvgs = new HashMap<>();

	/** folder in which the svgs are. */
	private String svgFolder;

	/** folder in which all images are. */
	private final String baseFolder = "images";

	/*
	Warning: magic numbers ahead
	 */
	/** scaleFactor. */
	private final double scaleFactor = 1;
	/** size of one block in pixels. */
	private final int coordinateMultiplier = 256;

	// font options
	/** the font for the exported texts. */
	private final String font = "Helvetica";
	/** the font size for text. */
	private final int size = 60;
	/** the font size for ids. */
	private final int fontSizeIds = 60;


	/** the length of the color string without alpha. */
	private final int colorDigits = 6;
	/** font color. */
	private final Color fColor = Color.BLACK;
	/** color code as string without alpha. */
	private final String fontColor = fColor.toString().substring(0, colorDigits);

	/** hard coded stroke color. */
	private final Color strokeColor = null; // means don't change svg stroke color
	/** color for the stepMarkers. */
	private final Color stepMarkerColor = Color.BLACK;

	/** min coordinate for the exported svg. */
	private Point topLeftCoord;
	/** max coordinate for the exported svg. */
	private Point bottomRightCoord;

	/** ViewBox X coordinate. */
	private int viewBoxX;
	/** ViewBox Y coordinate. */
	private int viewBoxY;
	/** ViewBox width. */
	private int viewBoxWidth;
	/** ViewBox height. */
	private int viewBoxHeight;

	/**
	 * SVGManager loading the default theme.
	 */
	public SVGManager() {
		this("default");
	}

	/**
	 * SVGManager loading the theme at the specified location.
	 *
	 * @param folder
	 * 		The name of the folder containing the theme, relative to the assets
	 * 		folder
	 * <p>
	 * The location is relative to the assets folder.
	 * @warning The folder name must not begin or end with a slash!
	 */
	public SVGManager(final String folder) {
		svgCoreCreator = new SVGCoreCreator();
		svgCoreCreator.setFolder(folder);
		createCores();
	}

	/**
	 * creates a string for an svg transform.
	 *
	 * @param params the transformation params
	 * @return a string for an svg transform
	 */
	public String getTransformation(final String params) {
		return " transform=\"" + params + "\" ";
	}

	/**
	 * creates a transformation with the scale factor.
	 *
	 * @return a string with a svg scaling transformation
	 */
	public String getScaleTransformation() {
		return getTransformation(getScale());
	}

	/**
	 * creates a svg scaling string.
	 * @return a string for an svg scaling operation
	 */
	public String getScale() {
		return "scale(" + scaleFactor + " " + scaleFactor + ")";
	}

	/**
	 * Tells the manager where to find the svgs (i.e. .svg images).
	 * <p>
	 * The location is relative to the assets folder.
	 * <p>
	 * Every time this method is called, all previously loaded textures are
	 * flushed.
	 * @note The name of the png files must match the names of the values
	 * specified in the TextureE enum
	 * @warning The folder name must not begin or end with a slash!
	 */
	public void createCores() {
		svgs.clear();

		for (final TextureE s : TextureE.values()) {

			if (s != TextureE.BlackPixel) {
				svgs.put(s, svgCoreCreator.getSVGCode(s, null, null));
			}

		}
	}

	/**
	 * Creates an ID consisting of a base part and the given color.
	 * @param baseName The part of thename in front of the '-'
	 * @param c The color that will be put after the '-'
	 * @return "<baseName>-<color>"
	 */
	public static String generateColoredID(final String baseName, final Color c) {
		if (svgExportSettings.getColorfulExport()) {
			return baseName + "-" + SVGCoreCreator.colorToSVG(c);
		} else {
			return baseName;
		}
	}

	/**
	 * Calculates min and max values for the resulting svg viewbox.
	 *
	 * @param circ The underlying DrawableCircuit.
	 */
	private void calculateViewboxDimensions(final DrawableCircuit circ) {
		Point minCoord = circ.data.getMinCoord();
		Point maxCoord = circ.data.getMaxCoord();

		int minX = minCoord.fst;
		int minY = minCoord.snd == 0 ? minCoord.snd : (minCoord.snd - 1);
		int maxX = minCoord.fst == 0 ? (maxCoord.fst + 1) : maxCoord.fst;
		int maxY = minCoord.snd == 0 ? (maxCoord.snd + 1) : maxCoord.snd;

		topLeftCoord = new Point(minX, minY);
		bottomRightCoord = new Point(maxX, maxY);

		viewBoxX = topLeftCoord.fst * coordinateMultiplier;
		viewBoxY = topLeftCoord.snd * coordinateMultiplier;

		viewBoxWidth = bottomRightCoord.fst * coordinateMultiplier;
		viewBoxHeight = bottomRightCoord.snd * coordinateMultiplier;
	}

	/**
	 * Export the circuit to svg.
	 *
	 * @param circ The circuit to export
	 * @param timeStep The timeStep for the export
	 * @return svg string representation
	 */
	public String toSVG(final DrawableCircuit circ, final int timeStep) {

		// set the export timeStep
		circ.setCurrentTime(timeStep);

		if (svgExportSettings.getColorfulExport()) {
			LOGGER.debug("[SVG] Creating all needed colored cores.");

			for (final DrawableField f : circ.fields) {
				String 	key = generateColoredID(f.getDisplayValues().getTexture()
						.toString(), f.getColor());
				// don't create the svg core code twice
				if (!colSvgs.containsKey(key)) {
					colSvgs.put(key,
							svgCoreCreator.getSVGCode(f.getDisplayValues().getTexture(),
									f.getColor(), strokeColor));
				}
				Set<Net> nets = circ.data.getNetsOf(f.getField());
				for (final Net n : nets) {
					for (final GradDir dir : GradDir.values()) {
						String id = generateColoredID("grad-" + dir.toString(), n.getColor()
								.buildGdxColor
								());
						if (!colSvgs.containsKey(id)) {
							colSvgs.put(id, svgCoreCreator
									.getSVGLinearGradient(id, dir, n.getColor().buildGdxColor()));
						}
					}
				}
			}
			for (final DrawableDroplet d : circ.droplets) {
				// TODO why do you add "-"? What is wrong with "Droplet-"? keszocze
				String key = generateColoredID("Droplet", d.getColor());
				// don't create the svg core code twice
				if (!colSvgs.containsKey(key)) {
					colSvgs.put(key,
							svgCoreCreator.getSVGCode(TextureE.Droplet,
									d.getColor(), strokeColor));
				}

				// Added it here in case we need it in each droplet color
				if (circ.displayOptions.getOption(BDisplayOptions
						.LongNetIndicatorsOnDroplets) ||
						circ.displayOptions.getOption(BDisplayOptions
								.LongNetIndicatorsOnFields)) {
					key = generateColoredID("ArrowHead", Color.BLACK);
					if (!colSvgs.containsKey(key)) {
						colSvgs.put(key,
								svgCoreCreator.getArrowHead(key, Color.BLACK));
					}
				}

				if (d.route != null) {
					Color routeColor = d.route.getColor();
					key = generateColoredID("StepMarker", routeColor);
					if (!colSvgs.containsKey(key)) {
						colSvgs.put(key,
									svgCoreCreator.getSVGCode(TextureE.StepMarker,
											routeColor, strokeColor));
					}
				}
			}


			LOGGER.debug("[SVG] Done creating colored cores.");
		}

		calculateViewboxDimensions(circ);

		if (circ.displayOptions.getOption(BDisplayOptions.Coordinates)) {
			int coordinateOffsetX = (int) (coordinateMultiplier * 0.75);
			int coordinateOffsetY = (int) (coordinateMultiplier * 0.75);
			viewBoxX -= coordinateOffsetX;
			viewBoxY -= coordinateOffsetY;
			viewBoxWidth += coordinateOffsetX;
			viewBoxHeight += coordinateOffsetY;
		}

		if (svgExportSettings.getInformationString()) {
			int infoStringOffset = size * 2;
			viewBoxHeight += infoStringOffset;
		}

		LOGGER.debug("[SVG] Starting to create SVG String");
		StringBuilder sb = new StringBuilder();

		sb.append("<?xml version=\"1.1\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" +
						"<!DOCTYPE svg PUBLIC \"-//W3C//DTD SVG 1.1//EN\" \n" +
						"  \"http://www.w3.org/Graphics/SVG/1.1/DTD/svg11.dtd\">" +
				"<svg width=\"100%\" height=\"100%\" viewBox=\"" +
				viewBoxX + " " + viewBoxY + " " +
				viewBoxWidth + " " + viewBoxHeight +
						"\" version=\"1.1\" xmlns=\"http://www.w3.org/2000/svg\" " +
						"xmlns:xlink=\"http://www.w3.org/1999/xlink\">\n");

		// simply always put every definition in the file. File size and/or
		// computation time does not really matter here.
		sb.append("<defs>\n");
		svgs.forEach((name, svgcode) -> sb.append(svgcode));
		if (svgExportSettings.getColorfulExport()) {
			colSvgs.forEach((name, svgcode) -> sb.append(svgcode));
		}
		sb.append("</defs>\n");

		for (final DrawableField field : circ.fields) {
			sb.append(toSVG(field));
		}
		for (final DrawableDroplet drop : circ.droplets) {
			if (drop.getDisplayColor().a > 0.1f &&
					!circ.hiddenDroplets.contains(drop)) {
				sb.append(toSVG(drop));
			}
		}

		if (svgExportSettings.getInformationString()) {
			sb.append(infoString(circ));
		}

		if (circ.displayOptions.getOption(BDisplayOptions.Coordinates)) {
			sb.append(createCoordinates(circ));
		}

		sb.append("</svg>\n");

		return sb.toString();
	}

	/**
	 * Exports a drawableField to svg.
	 *
	 * @param field The field to export
	 * @return svg string representation of a field
	 */
	private String toSVG(final DrawableField field) {
		// why would we need to acces " (-this.field.y + BioViz.singleton
		// .currentCircuit.data.field[0].length - 1)"?
		// @jannis please check and fix
		// @keszocze Because the coordinate system in SVG is inverted on its
		//		y-axis. I need to first put it upside down (-this.field.y) and
		//		then add the total height of the circuit to have the element
		// put
		//		back into the positive coordinate range in order to be placed
		//		on the canvas.

		DrawableCircuit circuit = field.getParentCircuit();
		int yCoord =
				-field.getField().y() + circuit.data.getMaxCoord().snd;
		int xCoord = field.getField().x();
		yCoord = yCoord * coordinateMultiplier;
		xCoord = xCoord * coordinateMultiplier;

		DisplayValues vals = field.getDisplayValues();


		String fieldID = generateColoredID(vals.getTexture().toString(),
				vals.getColor());
		String fieldSvg = "<use x=\"" + xCoord + "\" y=\"" + yCoord + "\"" +
						   getScaleTransformation() + " xlink:href=\"#" + fieldID +
						   "\" />\n";

		if (circuit.displayOptions.getOption(
				BDisplayOptions.NetColorOnFields)) {
			for (final Net n : circuit.data.getNetsOf(field.getField())) {
				GradDir dir = getGradientDirection(field, n);

				if (dir != null) {
					String gradient = "<rect x=\"" + (xCoord + 24) + "\" " +
							"y=\"" + (yCoord + 24) + "\" rx=\"24\" ry=\"24\" " +
							"height=\"208\" width=\"208\" fill=\"url(#grad-" +
							dir.toString() + "-" + SVGCoreCreator.colorToSVG(n
							.getColor().buildGdxColor()) +	")\" />\n";

					fieldSvg += gradient;
				}
			}
		}

		// create the msg text for the svg
		// use the text-anchor middle to get a centered position
		if (vals.getMsg() != null) {
			String msg = "<text text-anchor=\"middle\" x=\"" +
					(xCoord + coordinateMultiplier / 2)	+ "\" y=\"" +
					(yCoord + coordinateMultiplier / 2 + (size / 2)) +
					"\" font-family=\"" + font + "\" font-size=\"" + size +
					"\" fill=\"#" + fontColor + "\">"	+ vals.getMsg() + "</text>\n";
			fieldSvg += msg;
		}

		return fieldSvg;
	}

	/**
	 * Exports drawableDroplets as svg.
	 *
	 * @param drawableDrop The drawableDroplet to export
	 * @return svg string representation of the drop
	 */
	private String toSVG(final DrawableDroplet drawableDrop) {
		DrawableCircuit circuit = drawableDrop.parentCircuit;
		float yCoord = -drawableDrop.droplet.getPositionAt(circuit.currentTime).snd +
				circuit.data.getMaxCoord().snd;
		float xCoord = drawableDrop.droplet.getPositionAt(circuit.currentTime).fst;

		LOGGER.debug("(x,y) = ({},{})", yCoord, xCoord);
		yCoord = ((int) yCoord) * coordinateMultiplier;
		xCoord = ((int) xCoord) * coordinateMultiplier;

		String route = toSVG(drawableDrop.route);

		String dropletID = generateColoredID("Droplet", drawableDrop.getColor());
		String dropShape = "<use x=\"" + xCoord + "\" " + "y=\"" + yCoord + "\"" +
				getScaleTransformation() + " xlink:href=\"#" + dropletID + "\" />\n";

		String dropSvg = route + dropShape;

		if (circuit.displayOptions.getOption(BDisplayOptions.LongNetIndicatorsOnDroplets)){
			int time = circuit.currentTime;
			Set<Net> nets = circuit.data.getNetsOf(circuit.data.getFieldAt
					(drawableDrop.droplet.getPositionAt(time)));
			Point startingPoint;
			Point endPoint;
			for (final Net n : nets) {
				for ( Source s : n.getSources()){
					if (s.dropletID != drawableDrop.droplet.getID()) {
						continue;
					}
					startingPoint = s.startPosition;
					endPoint = n.getTarget();
				}
			}

		}

		if (drawableDrop.getMsg() != null) {
			String msg = "<text text-anchor=\"middle\" " +
					"x=\"" + (xCoord + coordinateMultiplier / 2) + "\" " +
					"y=\"" + (yCoord + coordinateMultiplier / 2 + (size / 2)) +	"\" " +
					"font-family=\"" + font + "\" font-size=\"" + size + "\" " +
					"fill=\"#" + fontColor + "\">" +
					drawableDrop.getMsg() + "</text>\n";
			dropSvg += msg;
		}
		return dropSvg;
	}

	/**
	 * Creates svg code to draw a route.
	 *
	 * Note that the svg code generated by this method references an earlier
	 * definition of the routes symbol that needs to be stored in the svg file
	 * in order to display anything.
	 *
	 * @param drawableRoute The route to export to svg
	 * @return SVG code for that particular route
	 */
	private String toSVG(final DrawableRoute drawableRoute) {
		StringBuilder sb = new StringBuilder();

		DrawableDroplet droplet = drawableRoute.droplet;

		int currentTime = droplet.droplet.getSpawnTime();
		//int currentTime = droplet.parentCircuit.currentTime;
		int displayAt;

		int displayLength = DrawableRoute.routeDisplayLength;

		Biochip circ = droplet.parentCircuit.data;

		Color routeColor = drawableRoute.getColor();

		/*
		The prevoius code did some weird stuff here. The new rationale is
		that we go from the currentTime either as long as there actually
		is time or until we reached the end of the display length of the
		route, whatever happens first.
		 */
		int nSteps = Math.min(displayLength, circ.getMaxT()) - 1;
		LOGGER.debug("nSteps: {}", nSteps);

		for (int i = 0; i < nSteps; ++i) {

			LOGGER.debug("i: {}", i);

			// TODO possible problem here due to casting
			float alpha = 1 - (Math.abs((float) i) / ((float) displayLength));

			displayAt = currentTime + i;

			LOGGER.debug("displayAt {}", displayAt);

			Point p1 = droplet.droplet.getPositionAt(displayAt);
			Point p2 = droplet.droplet.getPositionAt(displayAt + 1);

			LOGGER.debug("p1 {}; p2 {}", p1, p2);

			if (p1 != null && p2 != null) {
				int x1 = p1.fst * coordinateMultiplier;
				int x2 = p2.fst * coordinateMultiplier;
				int y1 = (-p1.snd + circ.getMaxCoord().snd) * coordinateMultiplier;
				int y2 = (-p2.snd + circ.getMaxCoord().snd) * coordinateMultiplier;

				float targetX = x1 + (0.5f * coordinateMultiplier);
				float targetY = y1;

				String position = " x=\"" + targetX + "\" y=\"" + targetY + "\" ";
				String widthHeight = " width=\"1\" height=\"1\" ";
				String transFormParams = getScale();
				String opacity = " opacity=\"" + alpha + "\" ";
				boolean app = true;

				if (x1 < x2 && y1 == y2) {
					//intentionally do nothing here
				} else if (y1 == y2 && x2 < x1) {
					transFormParams +=
							" rotate(180 " + targetX + " " +
									(targetY + 0.5f * coordinateMultiplier) +	") ";
				} else if (x1 == x2 && y2 > y1) {
					transFormParams +=
							" rotate(90 " + targetX + " " +
									(targetY + 0.5f * coordinateMultiplier) +	") ";
				} else if (x1 == x2 && y2 < y1) {
					transFormParams +=
							"rotate(270 " + targetX + " " +
									(targetY + 0.5f * coordinateMultiplier) + ") ";
				} else {
					app = false;
				}
				if (app) {
					String routeID = generateColoredID("StepMarker", routeColor);
					sb.append("<use");
					sb.append(position);
					sb.append(widthHeight);
					sb.append(getTransformation(transFormParams));
					sb.append(opacity);
					sb.append("xlink:href=\"#" + routeID + "\"");
					sb.append(" />\n");
					LOGGER.debug("[SVG] StepMarker color: {}", routeColor);
				}
			}
		}
		return sb.toString();
	}

	/**
	 * Checks if the given field is on the edge of the given net and returns
	 * the gradient direction.
	 *
	 * @param field the field to check
	 * @param net the net to which the field belongs
	 * @return the gradient direction
	 */
	private GradDir getGradientDirection(final DrawableField field, final Net
			net) {
		DrawableCircuit circuit = field.getParentCircuit();
		Point fieldPos = field.getField().pos;

		for (final GradDir dir : GradDir.values()) {
			List<Point> dirs = dir.getOrientation();
			boolean dirMatch = true;
			for (final Point p : dirs) {
				dirMatch &= !circuit.data.hasFieldAt(fieldPos.add(p)) ||
						!net.containsField(circuit.data.getFieldAt(fieldPos.add(p)));
			}
			if (dirMatch) {
				return dir;
			}
		}

		return null;
	}

	/**
	 * Creates a string with informations about this svg.
	 *
	 * @param circ the circuit
	 * @return information string
	 */
	private String infoString(final DrawableCircuit circ) {

		String coordinates =
				"x=\"" + (topLeftCoord.fst * coordinateMultiplier) + "\" " +
				"y=\"" + (bottomRightCoord.snd * coordinateMultiplier + 1.5 * size) +
				"\" ";

		String circName = circ.parent.getFileName();
		String timeStep = String.valueOf(circ.currentTime);

		return "<text " + coordinates +	"fill=\"" + fontColor + "\" " +
				"font-family=\"" + font + "\" font-size=\"" + size + "\">" +
				"Filename: " + circName +	" Timestep: " + timeStep + "</text>\n";
	}

	/**
	 * Creates coordinates around the grid.
	 *
	 * @param circ the circuit
	 * @return String containing the svg code for the coordinates
	 */
	private String createCoordinates(final DrawableCircuit circ) {
		StringBuilder coords = new StringBuilder();
		int coordSize = 80;
		for (int xCoord = topLeftCoord.fst; xCoord <= bottomRightCoord.fst;
				 ++xCoord) {
			coords.append("<text text-anchor=\"middle\" ");
			coords.append("x=\"" + (xCoord * coordinateMultiplier +
					0.5 * coordinateMultiplier) +	"\" ");
			coords.append("y=\"" + (topLeftCoord.snd *
					coordinateMultiplier - coordSize) + "\" ");
			coords.append("font-family=\"" + font + "\" font-size=\"" + coordSize +
					"\">");
			coords.append(xCoord);
			coords.append("</text>\n");
		}

		for (int yCoord = topLeftCoord.snd + 1; yCoord <= bottomRightCoord.snd;
				 ++yCoord) {
			coords.append("<text text-anchor=\"middle\" ");
			coords.append("y=\"" + ((yCoord - 1) * coordinateMultiplier +
					0.5 * coordSize +	0.5 * coordinateMultiplier) +	"\" ");
			coords.append("x=\"" + (topLeftCoord.fst * coordinateMultiplier -
					coordSize) + "\" ");
			coords.append("font-family=\"" + font + "\" font-size=\"" + coordSize +
					"\">");
			// FIXME the call to getMinCoord() is computing the stuff again
			// that is unnecessary but I was too stupid to fastly figure out
			// what you do with the stuff before you fill the bottomRightCoord
			// and topLeftCoord variables.
			coords.append(bottomRightCoord.snd - yCoord +
					circ.data.getMinCoord().snd);
			coords.append("</text>\n");
		}
		return coords.toString();
	}
}
