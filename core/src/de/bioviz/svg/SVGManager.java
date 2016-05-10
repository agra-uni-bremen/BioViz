package de.bioviz.svg;


import com.badlogic.gdx.graphics.Color;
import de.bioviz.structures.Biochip;
import de.bioviz.structures.Net;
import de.bioviz.structures.Point;
import de.bioviz.structures.Source;
import de.bioviz.ui.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

/**
 * @author malu, keszocze Texture cache class.
 *         <p>
 *         This class manages texture themes. One can specify a folder in which
 *         the png files that are loaded as textures are stored. These images
 *         are thenn either loaded on demand or returned from the cache.
 */
public class SVGManager {

	/**
	 * svgExportSettings.
	 */
	private static SVGExportSettings svgExportSettings = SVGExportSettings
			.getInstance();

	/**
	 * logger.
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(
			SVGManager.class);

	/**
	 * svgCoreCreator.
	 */
	private SVGCoreCreator svgCoreCreator;

	/**
	 * hashMap for uncolored svg elements.
	 */
	private HashMap<TextureE, String> svgs = new HashMap<>();
	/**
	 * hashMap for colored svg elements.
	 */
	private HashMap<String, String> colSvgs = new HashMap<>();

	/**
	 * folder in which the svgs are.
	 */
	private String svgFolder;

	/**
	 * folder in which all images are.
	 */
	private final String baseFolder = "images";

	/*
	Warning: magic numbers ahead
	 */
	/**
	 * scaleFactor.
	 */
	private final double scaleFactor = 1;
	/**
	 * size of one block in pixels.
	 */
	private final int coordinateMultiplier = 256;

	// font options
	/**
	 * the font for the exported texts.
	 */
	private final String font = "Helvetica";
	/**
	 * the font size for text.
	 */
	private final int fontSize = 90;
	/**
	 * the font size for the info string.
	 */
	private final int fontSizeInfoString = 100;

	/**
	 * font color.
	 */
	private final String fontColor = SVGCoreCreator.colorToSVG(Color.WHITE);
	/**
	 * font color for the info String.
	 */
	private final String fontColorInfoString = SVGCoreCreator.colorToSVG(Color
																				 .BLACK);

	/**
	 * hard coded stroke color.
	 */
	private final Color strokeColor = null; // means don't change svg stroke
	// color
	/**
	 * color for the stepMarkers.
	 */
	private final Color stepMarkerColor = Color.BLACK;

	/**
	 * min coordinate for the exported svg.
	 */
	private Point topLeftCoord;
	/**
	 * max coordinate for the exported svg.
	 */
	private Point bottomRightCoord;

	/**
	 * ViewBox X coordinate.
	 */
	private int viewBoxX;
	/**
	 * ViewBox Y coordinate.
	 */
	private int viewBoxY;
	/**
	 * ViewBox width.
	 */
	private int viewBoxWidth;
	/**
	 * ViewBox height.
	 */
	private int viewBoxHeight;

	/**
	 * Reference to the drawableCircuit.
	 */
	private DrawableCircuit circuit;

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
	 * 		<p>
	 * 		The location is relative to the assets folder.
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
	 * @param params
	 * 		the transformation params
	 * @return a string for an svg transform
	 */
	private String getTransformation(final String params) {
		return " transform=\"" + params + "\" ";
	}

	/**
	 * creates a transformation with the scale factor.
	 *
	 * @return a string with a svg scaling transformation
	 */
	private String getScaleTransformation() {
		return getTransformation(getScale());
	}

	/**
	 * creates a svg scaling string.
	 *
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
	 *
	 * @note The name of the png files must match the names of the values
	 * specified in the TextureE enum
	 * @warning The folder name must not begin or end with a slash!
	 */
	private void createCores() {
		svgs.clear();

		for (final TextureE s : TextureE.values()) {

			if (s != TextureE.BlackPixel) {
				svgs.put(s, svgCoreCreator.getSVGCode(s, null, null));
			}

		}
	}

	/**
	 * Creates an ID consisting of a base part and the given color.
	 *
	 * @param baseName
	 * 		The part of thename in front of the '-'
	 * @param c
	 * 		The color that will be put after the '-'
	 * @return "<baseName>-<color>"
	 */
	private static String generateColoredID(final String baseName, final Color
			c) {
		if (svgExportSettings.getColorfulExport()) {
			return baseName + "-" + SVGCoreCreator.colorToSVG(c);
		}
		else {
			return baseName;
		}
	}

	/**
	 * Calculates min and max values for the resulting svg viewbox.
	 */
	private void calculateViewboxDimensions() {
		Point minCoord = circuit.getData().getMinCoord();
		Point maxCoord = circuit.getData().getMaxCoord();

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
	 * @param circ
	 * 		The circuit to export
	 * @param timeStep
	 * 		The timeStep for the export
	 * @return svg string representation
	 */
	public String toSVG(final DrawableCircuit circ, final int timeStep) {

		circuit = circ;

		// set the export timeStep
		circuit.setCurrentTime(timeStep);

		calculateViewboxDimensions();

		if (circuit.getDisplayOptions().getOption(BDisplayOptions
														  .Coordinates)) {
			int coordinateOffsetX = (int) (coordinateMultiplier * 0.75);
			int coordinateOffsetY = (int) (coordinateMultiplier * 0.75);
			viewBoxX -= coordinateOffsetX;
			viewBoxY -= coordinateOffsetY;
			viewBoxWidth += coordinateOffsetX;
			viewBoxHeight += coordinateOffsetY;
		}

		if (svgExportSettings.getInformationString()) {
			int infoStringOffset = fontSizeInfoString * 2;
			viewBoxHeight += infoStringOffset;
		}

		LOGGER.debug("[SVG] Starting to create SVG String");
		StringBuilder sb = new StringBuilder();

		sb.append(
				"<?xml version=\"1.1\" encoding=\"UTF-8\" " +
				"standalone=\"yes\"?>\n" +
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

		// create all def strings and save them in colSvgs
		createDefCores();

		// append all colored cores to the svg string
		svgs.forEach((name, svgcode) -> sb.append(svgcode));
		if (svgExportSettings.getColorfulExport()) {
			colSvgs.forEach((name, svgcode) -> sb.append(svgcode));
		}
		sb.append("</defs>\n");

		for (final DrawableField field : circuit.getFields()) {
			sb.append(toSVG(field));
		}
		for (final DrawableDroplet drop : circuit.getDroplets()) {
			if (drop.getDisplayColor().a > 0.1f &&
				!circuit.isHidden(drop)) {
				sb.append(toSVG(drop));
			}
		}
		// run over each droplet again and draw the arrows
		// otherwise arrows can get under droplets
		for (final DrawableDroplet drop : circuit.getDroplets()) {
			if (circuit.getDisplayOptions().getOption(BDisplayOptions
															  .LongNetIndicatorsOnDroplets)) {

				if (!circuit.isHidden(drop)) {
					sb.append(createDropletArrows(drop));
				}
			}
			// append longNetIndicatorsOnFields when needed
			if (circuit.getDisplayOptions().getOption(BDisplayOptions
															  .LongNetIndicatorsOnFields)) {
				sb.append(createSourceTargetArrow(drop));
			}
		}


		// export msg strings for fields
		for (final DrawableField field : circuit.getFields()) {
			sb.append(createFieldMsg(field));
		}
		// export msg strings for droplets
		for (final DrawableDroplet drop : circuit.getDroplets()) {
			if (!circuit.isHidden(drop)) {
				sb.append(createDropletMsg(drop));
			}
		}


		if (svgExportSettings.getInformationString()) {
			sb.append(createInfoString());
		}

		if (circuit.getDisplayOptions().getOption(
				BDisplayOptions.Coordinates)) {
			sb.append(createCoordinates());
		}

		sb.append("</svg>\n");

		return sb.toString();
	}

	/**
	 * Exports a drawableField to svg.
	 *
	 * @param field
	 * 		The field to export
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

		Point pos = getFieldPosInSVGCoords(field);
		int yCoord = pos.snd;
		int xCoord = pos.fst;

		DisplayValues vals = field.getDisplayValues();

		Color fieldCol = vals.getColor();

		if (field.isHovered()) {
			fieldCol.sub(Colors.HOVER_DIFF_COLOR);
		}

		String fieldID = generateColoredID(vals.getTexture().toString(),
										   fieldCol);
		String fieldSvg = "<use x=\"" + xCoord + "\" y=\"" + yCoord + "\"" +
						  getScaleTransformation() + " xlink:href=\"#" +
						  fieldID +
						  "\" />\n";

		fieldSvg += createGradient(field);

		return fieldSvg;
	}

	/**
	 * Exports drawableDroplets as svg.
	 *
	 * @param drawableDrop
	 * 		The drawableDroplet to export
	 * @return svg string representation of the drop
	 */
	private String toSVG(final DrawableDroplet drawableDrop) {

		Point dropletPos = getDropletPosInSVGCoords(drawableDrop);
		String route = toSVG(drawableDrop.route);

		String dropletID = generateColoredID("Droplet", drawableDrop.getColor
				());
		String dropShape = "<use x=\"" + dropletPos.fst + "\" " + "y=\"" +
						   dropletPos.snd + "\"" +
						   getScaleTransformation() + " xlink:href=\"#" +
						   dropletID + "\" />\n";

		return route + dropShape;
	}

	/**
	 * Creates svg code to draw a route.
	 * <p>
	 * Note that the svg code generated by this method references an earlier
	 * definition of the routes symbol that needs to be stored in the svg file
	 * in order to display anything.
	 *
	 * @param drawableRoute
	 * 		The route to export to svg
	 * @return SVG code for that particular route
	 */
	private String toSVG(final DrawableRoute drawableRoute) {
		StringBuilder sb = new StringBuilder();

		DrawableDroplet droplet = drawableRoute.droplet;

		int currentTime = droplet.droplet.getSpawnTime();
		//int currentTime = droplet.parentCircuit.currentTime;
		int displayAt;

		int displayLength = DrawableRoute.routeDisplayLength;

		Biochip biochip = circuit.getData();

		Color routeColor = drawableRoute.getColor();

		/*
		The prevoius code did some weird stuff here. The new rationale is
		that we go from the currentTime either as long as there actually
		is time or until we reached the end of the display length of the
		route, whatever happens first.
		 */
		int nSteps = Math.min(displayLength, biochip.getMaxT()) - 1;
		LOGGER.debug("nSteps: {}", nSteps);

		for (int i = 0; i < nSteps; ++i) {

			LOGGER.debug("i: {}", i);

			// TODO possible problem here due to casting
			float alpha = 1 - (Math.abs((float) i) / ((float) displayLength));

			displayAt = currentTime + i;

			LOGGER.debug("displayAt {}", displayAt);

			Point p1 = droplet.droplet.getSafePositionAt(displayAt);
			Point p2 = droplet.droplet.getSafePositionAt(displayAt + 1);

			LOGGER.debug("p1 {}; p2 {}", p1, p2);

			if (p1 != null && p2 != null) {
				int x1 = p1.fst * coordinateMultiplier;
				int x2 = p2.fst * coordinateMultiplier;
				int y1 = (-p1.snd + biochip.getMaxCoord().snd) *
						 coordinateMultiplier;
				int y2 = (-p2.snd + biochip.getMaxCoord().snd) *
						 coordinateMultiplier;

				float targetX = x1 + (0.5f * coordinateMultiplier);
				float targetY = y1;

				String position =
						" x=\"" + targetX + "\" y=\"" + targetY + "\" ";
				String widthHeight = " width=\"1\" height=\"1\" ";
				String transFormParams = getScale();
				String opacity = " opacity=\"" + alpha + "\" ";
				boolean app = true;

				if (x1 < x2 && y1 == y2) {
					//intentionally do nothing here
				}
				else if (y1 == y2 && x2 < x1) {
					transFormParams +=
							" rotate(180 " + targetX + " " +
							(targetY + 0.5f * coordinateMultiplier) + ") ";
				}
				else if (x1 == x2 && y2 > y1) {
					transFormParams +=
							" rotate(90 " + targetX + " " +
							(targetY + 0.5f * coordinateMultiplier) + ") ";
				}
				else if (x1 == x2 && y2 < y1) {
					transFormParams +=
							"rotate(270 " + targetX + " " +
							(targetY + 0.5f * coordinateMultiplier) + ") ";
				}
				else {
					app = false;
				}
				if (app) {
					String routeID =
							generateColoredID("StepMarker", routeColor);
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
	 * Creates svg arrows from all net sources to their net targets.
	 *
	 * @return svg string containing all start end arrows
	 */
	private String createSourceTargetArrow(DrawableDroplet drawableDrop) {

		Net net = drawableDrop.droplet.getNet();
		String arrow = "";

		if (net != null) {
			Point startPoint = drawableDrop.droplet.getFirstPosition();
			Point endPoint = net.getTarget();

			Color arrowColor = Color.BLACK;
			arrow = createSVGArrow(startPoint, endPoint, arrowColor);
		}

		return arrow;
	}

	/**
	 * Creates the svg string for the longNetIndicator arrows.
	 *
	 * @param drawableDrop
	 * 		the drop
	 * @return svg string
	 */
	private String createDropletArrows(final DrawableDroplet drawableDrop) {

		Net net = drawableDrop.droplet.getNet();

		String arrows = "";
		if (net != null) {

			int time = circuit.getCurrentTime();
			Point startPoint = drawableDrop.droplet.getFirstPosition();
			Point endPoint = net.getTarget();
			Point dropletPos = drawableDrop.droplet.getSafePositionAt(time);

			Color dropColor = drawableDrop.getColor();

			if (startPoint != null && dropletPos != null &&
				!startPoint.equals(dropletPos)) {
				Color arrowColor = dropColor.cpy().sub(
						Colors.LONG_NET_INDICATORS_ON_DROPLET_DIFF);
				arrows += createSVGArrow(startPoint, dropletPos, arrowColor);
			}

			if (dropletPos != null && endPoint != null &&
				!dropletPos.equals(endPoint)) {
				Color arrowColor = dropColor.cpy().add(
						Colors.LONG_NET_INDICATORS_ON_DROPLET_DIFF);
				arrows += createSVGArrow(dropletPos, endPoint, arrowColor);
			}
		}

		return arrows;
	}

	/**
	 * This creates the message element for a droplet.
	 *
	 * @param drawableDrop
	 * 		The droplet
	 * @return Svg text element
	 */
	private String createDropletMsg(final DrawableDroplet drawableDrop) {
		Point dropPos = getDropletPosInSVGCoords(drawableDrop);
		String msg = "";
		if (drawableDrop.getMsg() != null) {
			msg += "<text text-anchor=\"middle\" " +
				   "x=\"" + (dropPos.fst + coordinateMultiplier / 2) + "\" " +
				   "y=\"" + (dropPos.snd + coordinateMultiplier / 2 +
							 (fontSize / 2)) +
				   "\" " +
				   "font-family=\"" + font + "\" font-size=\"" + fontSize +
				   "\" " +
				   "fill=\"#" + fontColor + "\">" + drawableDrop.getMsg() +
				   "</text>\n";
		}
		return msg;
	}

	/**
	 * Creates an svg text field with the displayValues for a Field.
	 *
	 * @param field
	 * 		the field
	 * @return svg text element
	 */
	private String createFieldMsg(final DrawableField field) {
		Point fieldPos = getFieldPosInSVGCoords(field);

		DisplayValues vals = field.getDisplayValues();
		// create the msg text for the svg
		// use the text-anchor middle to get a centered position
		String fieldSvg = "";
		if (vals.getMsg() != null) {
			fieldSvg += "<text text-anchor=\"middle\" x=\"" +
						(fieldPos.fst + coordinateMultiplier / 2) + "\" y=\"" +
						(fieldPos.snd + coordinateMultiplier / 2 +
						 (fontSize / 2)) +
						"\" font-family=\"" + font + "\" font-size=\"" +
						fontSize +
						"\" fill=\"#" + fontColor + "\">" + vals.getMsg() +
						"</text>\n";
		}
		return fieldSvg;
	}

	/**
	 * Create the svg rect for a gradient for a given field.
	 *
	 * @param field
	 * 		the field
	 * @return an svg rect string
	 */
	private String createGradient(final DrawableField field) {
		String gradientSvg = "";
		if (circuit.getDisplayOptions().getOption(
				BDisplayOptions.NetColorOnFields)) {
			for (final Net n : circuit.getData().getNetsOf(field.getField())) {
				GradDir dir = getGradientDirection(field, n);
				Point fieldPos = getFieldPosInSVGCoords(field);
				if (dir != null) {
					gradientSvg += "<rect x=\"" + (fieldPos.fst + 24) + "\" " +
								   "y=\"" + (fieldPos.snd + 24) +
								   "\" rx=\"24\" ry=\"24\" " +
								   "height=\"208\" width=\"208\" fill=\"url" +
								   "(#grad-" +
								   dir.toString() + "-" +
								   SVGCoreCreator.colorToSVG(n
																	 .getColor
																			 ().buildGdxColor()) +
								   ")\" />\n";

				}
			}
		}
		return gradientSvg;
	}

	/**
	 * Checks if the given field is on the edge of the given net and returns
	 * the
	 * gradient direction.
	 *
	 * @param field
	 * 		the field to check
	 * @param net
	 * 		the net to which the field belongs
	 * @return the gradient direction
	 */
	private GradDir getGradientDirection(final DrawableField field, final Net
			net) {
		Point fieldPos = field.getField().pos;

		for (final GradDir dir : GradDir.values()) {
			List<Point> dirs = dir.getOrientation();
			boolean dirMatch = true;
			for (final Point p : dirs) {
				dirMatch &= !circuit.getData().hasFieldAt(fieldPos.add(p)) ||
							!net.containsField(circuit.getData().getFieldAt
									(fieldPos.add(p)));
			}
			if (dirMatch) {
				return dir;
			}
		}

		return null;
	}

	/**
	 * Creates an svgArrow with the given start and endpoint.
	 *
	 * @param startPoint
	 * 		the startpoint for the arrow
	 * @param endPoint
	 * 		the endpoint for the arrow
	 * @param color
	 * 		the color for the arrow
	 * @return svg string of an arrow
	 */
	private String createSVGArrow(final Point startPoint, final Point endPoint,
								  final Color color) {

		Point start = toSVGCoords(startPoint);
		Point end = toSVGCoords(endPoint);
		int x1 = start.fst;
		int y1 = start.snd;

		int x2 = end.fst;
		int y2 = end.snd;

		// move startingPoint to the center of the field
		x1 += coordinateMultiplier / 2;
		y1 += coordinateMultiplier / 2;
		// move endPoint to the center of the field
		x2 += coordinateMultiplier / 2;
		y2 += coordinateMultiplier / 2;

		double length =
				Math.sqrt(Math.pow((x2 - x1), 2) + Math.pow((y2 - y1), 2));
		double angle = Math.asin((double) (y2 - y1) / length);

		// move endPoint a bit back on the arrow so the arrowHead won't reach
		// over the center of the field
		double xDiff = 25 * Math.cos(angle);
		double yDiff = 25 * Math.sin(angle);

		if (x2 > x1) {
			x2 -= xDiff;
		}
		else if (x2 < x1) {
			x2 += xDiff;
		}
		y2 -= yDiff;

		String line = "<line x1=\"" + x1 + "\" y1=\"" + y1 +
					  "\" x2=\"" + x2 + "\" " + "y2=\"" + y2 +
					  "\" stroke=\"#" + SVGCoreCreator.colorToSVG(color) +
					  "\" stroke-width=\"10\" marker-end=\"url(#" +
					  generateColoredID("ArrowHead", color) + ")\" />\n";

		return line;
	}

	/**
	 * Creates a string with informations about this svg.
	 *
	 * @return information string
	 */
	private String createInfoString() {

		String coordinates =
				"x=\"" + (topLeftCoord.fst * coordinateMultiplier) + "\" " +
				"y=\"" + (bottomRightCoord.snd * coordinateMultiplier + 1.5 *
																		fontSizeInfoString) +
				"\" ";

		String circName = circuit.getParent().getFileName();
		String timeStep = String.valueOf(circuit.getCurrentTime());

		return "<text " + coordinates + "fill=\"" + fontColorInfoString +
			   "\"" +
			   " " +
			   "font-family=\"" + font + "\" font-size=\"" +
			   fontSizeInfoString +
			   "\">" +
			   "Filename: " + circName + " Timestep: " + timeStep +
			   "</text>\n";
	}

	/**
	 * Creates coordinates around the grid.
	 *
	 * @return String containing the svg code for the coordinates
	 */
	private String createCoordinates() {
		StringBuilder coords = new StringBuilder();
		int coordSize = fontSizeInfoString;
		for (int xCoord = topLeftCoord.fst; xCoord <= bottomRightCoord.fst;
			 ++xCoord) {
			coords.append("<text text-anchor=\"middle\" ");
			coords.append("x=\"" + (xCoord * coordinateMultiplier +
									0.5 * coordinateMultiplier) + "\" ");
			coords.append("y=\"" + (topLeftCoord.snd *
									coordinateMultiplier - coordSize) + "\" ");
			coords.append(
					"font-family=\"" + font + "\" font-size=\"" + coordSize +
					"\">");
			coords.append(xCoord);
			coords.append("</text>\n");
		}

		for (int yCoord = topLeftCoord.snd + 1; yCoord <= bottomRightCoord.snd;
			 ++yCoord) {
			coords.append("<text text-anchor=\"middle\" ");
			coords.append("y=\"" + ((yCoord - 1) * coordinateMultiplier +
									0.5 * coordSize +
									0.5 * coordinateMultiplier) + "\" ");
			coords.append("x=\"" + (topLeftCoord.fst * coordinateMultiplier -
									coordSize) + "\" ");
			coords.append(
					"font-family=\"" + font + "\" font-size=\"" + coordSize +
					"\">");
			// FIXME the call to getMinCoord() is computing the stuff again
			// that is unnecessary but I was too stupid to fastly figure out
			// what you do with the stuff before you fill the bottomRightCoord
			// and topLeftCoord variables.
			coords.append(bottomRightCoord.snd - yCoord +
						  circuit.getData().getMinCoord().snd);
			coords.append("</text>\n");
		}
		return coords.toString();
	}

	/**
	 * Creates all colored core strings and saves them into colSvgs.
	 */
	private void createDefCores() {
		if (svgExportSettings.getColorfulExport()) {
			LOGGER.debug("[SVG] Creating all needed colored cores.");

			String key = "";

			// create all needed svg defs for the fields
			for (final DrawableField f : circuit.getFields()) {

				Color fieldColor = f.getColor();

				// reverse colorChanges for hoveredFields
				if (f.isHovered()) {
					fieldColor = fieldColor.sub(Colors.HOVER_DIFF_COLOR);
				}

				key = generateColoredID(f.getDisplayValues().getTexture()
												.toString(), fieldColor);
				// don't create the svg core code twice
				if (!colSvgs.containsKey(key)) {
					colSvgs.put(key,
								svgCoreCreator.getSVGCode(
										f.getDisplayValues().getTexture(),
										fieldColor, strokeColor));
				}
				Set<Net> nets = circuit.getData().getNetsOf(f.getField());
				for (final Net n : nets) {
					for (final GradDir dir : GradDir.values()) {
						key = generateColoredID("grad-" + dir.toString(),
												n.getColor().buildGdxColor());
						if (!colSvgs.containsKey(key)) {
							colSvgs.put(key, svgCoreCreator
									.getSVGLinearGradient(key, dir,
														  n.getColor()
																  .buildGdxColor()));
						}
					}
				}
			}

			// create all needed svg defs for the droplets
			// and droplet based features
			for (final DrawableDroplet d : circuit.getDroplets()) {
				key = generateColoredID("Droplet", d.getColor());
				// don't create the svg core code twice
				if (!colSvgs.containsKey(key)) {
					colSvgs.put(key,
								svgCoreCreator.getSVGCode(TextureE.Droplet,
														  d.getColor(),
														  strokeColor));
				}

				// Add every needed color for the arrowheads
				if (circuit.getDisplayOptions().getOption(BDisplayOptions
																  .LongNetIndicatorsOnDroplets)) {
					List<Color> colors = new ArrayList<>();
					Color diffColor =
							Colors.LONG_NET_INDICATORS_ON_DROPLET_DIFF;
					colors.add(d.getColor().add(diffColor));
					colors.add(d.getColor().sub(diffColor));

					for (final Color color : colors) {
						key = generateColoredID("ArrowHead", color);
						if (!colSvgs.containsKey(key)) {
							colSvgs.put(key,
										svgCoreCreator.getArrowHead(key,
																	color));
						}
					}
				}

				if (d.route != null) {
					Color routeColor = d.route.getColor();
					key = generateColoredID("StepMarker", routeColor);
					if (!colSvgs.containsKey(key)) {
						colSvgs.put(key,
									svgCoreCreator.getSVGCode(
											TextureE.StepMarker,
											routeColor, strokeColor));
					}
				}
			}

			// create the svg def for the arrowhead for the source target
			// arrows
			if (circuit.getDisplayOptions().getOption(BDisplayOptions
															  .LongNetIndicatorsOnFields)) {
				// this is needed for source target arrows
				Color color = Color.BLACK;
				key = generateColoredID("ArrowHead", color);
				if (!colSvgs.containsKey(key)) {
					colSvgs.put(key,
								svgCoreCreator.getArrowHead(key, color));
				}
			}

			LOGGER.debug("[SVG] Done creating colored cores.");
		}
	}

	/**
	 * Gets the position of a field in svg coordinates.
	 *
	 * @param drawableField
	 * 		the drawableField
	 * @return A Point with the position.
	 */
	private Point getFieldPosInSVGCoords(final DrawableField drawableField) {
		return toSVGCoords(drawableField.getField().pos);
	}

	/**
	 * Gets the position for a droplet in svg coordinates.
	 *
	 * @param drawableDrop
	 * 		the droplet
	 * @return A Point with the position
	 */
	private Point getDropletPosInSVGCoords(final DrawableDroplet
												   drawableDrop) {
		return toSVGCoords(drawableDrop.droplet.getSafePositionAt(circuit
																		  .getCurrentTime()));
	}

	/**
	 * Transforms a point to svgCoordinates.
	 *
	 * @param point
	 * 		the point to transform
	 * @return Point with SVG coordinates
	 */
	private Point toSVGCoords(final Point point) {
		int yCoord = -point.snd + circuit.getData().getMaxCoord().snd;
		int xCoord = point.fst;

		xCoord *= coordinateMultiplier;
		yCoord *= coordinateMultiplier;

		return new Point(xCoord, yCoord);
	}
}
