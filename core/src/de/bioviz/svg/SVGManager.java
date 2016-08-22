package de.bioviz.svg;


import com.badlogic.gdx.graphics.Color;
import de.bioviz.structures.Biochip;
import de.bioviz.structures.Net;
import de.bioviz.structures.Point;
import de.bioviz.ui.BDisplayOptions;
import de.bioviz.ui.Colors;
import de.bioviz.ui.DisplayValues;
import de.bioviz.ui.DrawableAssay;
import de.bioviz.ui.DrawableDroplet;
import de.bioviz.ui.DrawableField;
import de.bioviz.ui.DrawableRoute;
import de.bioviz.ui.TextureE;
import de.bioviz.util.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Set;

/**
 * @author Maximilian Luenert, Oliver Keszocze Texture cache class.
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


	/*
	Warning: magic numbers ahead
	 */
	/**
	 * ScaleFactor.
	 */
	private static final double SCALE_FACTOR = 1;
	/**
	 * size of one block in pixels.
	 */
	private static final int COORDINATE_MULTIPLIER = 256;

	// font options
	/**
	 * the font for the exported texts.
	 */
	private static final String FONT = "Helvetica";
	/**
	 * the font size for text.
	 */
	private static final int FONT_SIZE = 90;
	/**
	 * the font size for the info string.
	 */
	private static final int FONT_SIZE_INFO_STRING = 100;

	/**
	 * font color.
	 */
	private static final String FONT_COLOR = SVGUtils.colorToSVG(Color.WHITE);
	/**
	 * font color for the info String.
	 */
	private static final String FONT_COLOR_INFO_STRING =
			SVGUtils.colorToSVG(Color.BLACK);


	/**
	 * svgCoreCreator.
	 */
	private SVGCoreCreator svgCoreCreator;

	/**
	 * hashMap for uncolored svg elements.
	 */
	private HashMap<String, String> svgs = new HashMap<>();


	/**
	 * min coordinate for the exported svg.
	 */
	private Point topLeftCoord;
	/**
	 * max coordinate for the exported svg.
	 */
	private Point bottomRightCoord;

	/**
	 * Stores the position for the coordinate system.
	 */
	private Point coordPos;

	/**
	 * Stores the position for the info string.
	 */
	private Point infoPos;

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
	private DrawableAssay circuit;

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
		return "scale(" + SCALE_FACTOR + " " + SCALE_FACTOR + ")";
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
				svgs.put(s.toString(), svgCoreCreator.getSVGCode(s, null,
																 null));
			}

		}
	}

	/**
	 * Calculates min and max values for the resulting svg viewbox.
	 */
	private void calculateViewboxDimensions() {
		Point minCoord = circuit.getData().getMinCoord();
		Point maxCoord = circuit.getData().getMaxCoord();

		LOGGER.trace("Min X: " + minCoord.fst + " Min Y: " + minCoord.snd);
		LOGGER.trace("Max X: " + maxCoord.fst + " Max Y: " + maxCoord.snd);

		int minX = minCoord.fst;
		int minY = minCoord.snd;
		int maxX = maxCoord.fst;
		int maxY = maxCoord.snd;

		topLeftCoord = new Point(minX, minY);
		bottomRightCoord = new Point(maxX, maxY);

		viewBoxX = minX * COORDINATE_MULTIPLIER;
		viewBoxY = (minY == 0 ? minY : (minY - 1)) *
				COORDINATE_MULTIPLIER;

		viewBoxWidth = (minX == 0 ? (maxX + 1) : maxX) *
				COORDINATE_MULTIPLIER;
		viewBoxHeight = (minY == 0 ? (maxY + 1) : maxY) *
				COORDINATE_MULTIPLIER;

		coordPos = new Point(viewBoxX, viewBoxY);
		infoPos = new Point(viewBoxX, viewBoxHeight);

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
	public String toSVG(final DrawableAssay circ, final int timeStep) {

		circuit = circ;

		// set the export timeStep
		circuit.setCurrentTime(timeStep);

		calculateViewboxDimensions();

		if (circuit.getDisplayOptions().getOption(BDisplayOptions
														  .Coordinates)) {
			int coordinateOffsetX = (int) (COORDINATE_MULTIPLIER * 0.75);
			int coordinateOffsetY = (int) (COORDINATE_MULTIPLIER * 0.75);
			viewBoxX -= coordinateOffsetX;
			viewBoxY -= coordinateOffsetY;
			viewBoxWidth += coordinateOffsetX;
			viewBoxHeight += coordinateOffsetY;
		}

		if (svgExportSettings.getInformationString()) {
			int infoStringOffset = FONT_SIZE_INFO_STRING * 2;
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

		// append all cores to the svg string
		svgs.forEach((name, svgcode) -> sb.append(svgcode));
		sb.append("</defs>\n");

		for (final DrawableField field : circuit.getFields()) {
			sb.append(toSVG(field));
		}
		for (final DrawableDroplet drop : circuit.getDroplets()) {
			if (SVGUtils.isNotHiddenOrInvisible(drop)) {
				sb.append(toSVG(drop));
			}
		}
		// run over each droplet again and draw the arrows
		// otherwise arrows can get under droplets
		for (final DrawableDroplet drop : circuit.getDroplets()) {
			if (circuit.getDisplayOptions().
					getOption(BDisplayOptions.LongNetIndicatorsOnDroplets) &&
					SVGUtils.isNotHiddenOrInvisible(drop)) {
					sb.append(createDropletArrows(drop));
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
			//
			if (SVGUtils.isNotHiddenOrInvisible(drop)) {
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
		// .currentAssay.data.field[0].length - 1)"?
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

		String fieldID =
				SVGUtils.generateColoredID(vals.getTexture().toString(),
										   fieldCol);
		String fieldSvg = "<use x=\"" + xCoord + "\" y=\"" + yCoord + "\"" +
						  getScaleTransformation() + " xlink:href=\"#" +
						  fieldID +
						  "\" />\n";

		if (circuit.getDisplayOptions().getOption(
				BDisplayOptions.NetColorOnFields)) {
			fieldSvg += createGradient(field);
		}

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

		Pair<Integer, Integer> scaleFactors = SVGUtils.getScaleFactors(drawableDrop,
				circuit.getCurrentTime());

		String scale = "scale(" + scaleFactors.fst + " " + scaleFactors.snd + ")";

		String translateToZero = "translate(" + dropletPos.fst + " " +
				dropletPos.snd + ")";
		String translateBack = "translate(-" + dropletPos.fst + " " + "-" +
				dropletPos.snd + ")";

		// move the object to 0,0 then scale it and move it back to its
		// original position
		String transformation = getTransformation(translateToZero + " " +
				scale + " " +	translateBack);

		String route = toSVG(drawableDrop.route);

		String dropletID = SVGUtils.generateColoredID("Droplet", drawableDrop
				.getColor());

		String dropShape = "<use x=\"" + dropletPos.fst + "\" " + "y=\"" +
						   dropletPos.snd + "\"" + transformation + " " +
				"xlink:href=\"#" + dropletID + "\" />\n";

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

			float alpha = 1 - (Math.abs((float) i) / ((float) displayLength));

			displayAt = currentTime + i;

			LOGGER.debug("displayAt {}", displayAt);

			Point p1 = droplet.droplet.getSafePositionAt(displayAt)
					.upperLeft();
			Point p2 = droplet.droplet.getSafePositionAt(
					displayAt + 1).upperLeft();

			LOGGER.debug("p1 {}; p2 {}", p1, p2);

			if (p1 != null && p2 != null) {
				int x1 = p1.fst * COORDINATE_MULTIPLIER;
				int x2 = p2.fst * COORDINATE_MULTIPLIER;
				int y1 = (-p1.snd + biochip.getMaxCoord().snd) *
						COORDINATE_MULTIPLIER;
				int y2 = (-p2.snd + biochip.getMaxCoord().snd) *
						COORDINATE_MULTIPLIER;

				float targetX = x1 + (0.5f * COORDINATE_MULTIPLIER);
				float targetY = y1;

				String position =
						" x=\"" + targetX + "\" y=\"" + targetY + "\" ";
				String widthHeight = " width=\"1\" height=\"1\" ";
				String transFormParams = getScale();
				String opacity = " opacity=\"" + alpha + "\" ";
				boolean app = true;

				if (x1 < x2 && y1 == y2) {
					//intentionally do nothing here
				} else if (y1 == y2 && x2 < x1) {
					transFormParams +=
							" rotate(180 " + targetX + " " +
							(targetY + 0.5f * COORDINATE_MULTIPLIER) + ") ";
				} else if (x1 == x2 && y2 > y1) {
					transFormParams +=
							" rotate(90 " + targetX + " " +
							(targetY + 0.5f * COORDINATE_MULTIPLIER) + ") ";
				} else if (x1 == x2 && y2 < y1) {
					transFormParams +=
							"rotate(270 " + targetX + " " +
							(targetY + 0.5f * COORDINATE_MULTIPLIER) + ") ";
				} else {
					app = false;
				}
				if (app) {
					String routeID =
							SVGUtils.generateColoredID("StepMarker",
													   routeColor);

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
	 * @param drawableDrop
	 * 		the droplet
	 * @return svg string containing all start end arrows
	 */
	private String createSourceTargetArrow(final DrawableDroplet
												   drawableDrop) {

		Net net = drawableDrop.droplet.getNet();
		String arrow = "";

		if (net != null) {
			Pair<Float, Float> startPoint = drawableDrop.droplet.getFirstPosition()
					.centerFloat();
			Pair<Float, Float> endPoint = net.getTarget().centerFloat();

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
			Pair<Float, Float> startPoint = drawableDrop.droplet.getFirstPosition()
					.centerFloat();
			Pair<Float, Float> endPoint = net.getTarget().centerFloat();
			Pair<Float, Float> dropletPos = drawableDrop.droplet.
					getSafePositionAt(time).centerFloat();

			Color dropColor = drawableDrop.getColor();

			if (startPoint != null && dropletPos != null &&
				!startPoint.equals(dropletPos)) {
				Color arrowColor =
						SVGUtils.getLighterLongNetIndicatorColor(dropColor);

				arrows += createSVGArrow(startPoint, dropletPos, arrowColor);
			}

			if (dropletPos != null && endPoint != null &&
				!dropletPos.equals(endPoint)) {
				Color arrowColor =
						SVGUtils.getDarkerLongNetIndicatorColor(dropColor);
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
				   "x=\"" + (dropPos.fst + COORDINATE_MULTIPLIER / 2) + "\" " +
				   "y=\"" + (dropPos.snd + COORDINATE_MULTIPLIER / 2 +
							 (FONT_SIZE / 2)) +
				   "\" " +
				   "font-family=\"" + FONT + "\" font-size=\"" + FONT_SIZE +
				   "\" " +
				   "fill=\"#" + FONT_COLOR + "\">" + drawableDrop.getMsg() +
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
						(fieldPos.fst + COORDINATE_MULTIPLIER / 2) + "\" y=\"" +
						(fieldPos.snd + COORDINATE_MULTIPLIER / 2 +
						 (FONT_SIZE / 2)) +
						"\" font-family=\"" + FONT + "\" font-size=\"" +
					FONT_SIZE +
						"\" fill=\"#" + FONT_COLOR + "\">" + vals.getMsg() +
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

		for (final Net n : circuit.getData().getNetsOf(field.getField())) {
			GradDir dir = getGradientDirection(field, n);
			Point fieldPos = getFieldPosInSVGCoords(field);
			if (dir != null) {
				gradientSvg += "<rect x=\"" + (fieldPos.fst + 24) + "\" " +
							   "y=\"" + (fieldPos.snd + 24) +
							   "\" rx=\"24\" ry=\"24\" " +
							   "height=\"208\" width=\"208\" fill=\"url(#" +
							   SVGUtils
									   .generateColoredID(
											   "Gradient-" + dir.toString(),
											   SVGUtils.getNetColor(n)) +
							   ")\" " +
							   "/>\n";
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
							!net.containsField(circuit.getData().
									getFieldAt(fieldPos.add(p)));
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
	private String createSVGArrow(final Pair<Float, Float> startPoint,
																final	Pair<Float, Float> endPoint,
																final Color	color) {
		Pair<Float, Float> start = SVGUtils.toSVGCoords(startPoint, circuit,
				COORDINATE_MULTIPLIER);
		Pair<Float, Float> end = SVGUtils.toSVGCoords(endPoint, circuit,
				COORDINATE_MULTIPLIER);

		float x1 = start.fst;
		float y1 = start.snd;

		float x2 = end.fst;
		float y2 = end.snd;

		// move startingPoint to the center of the field
		x1 += COORDINATE_MULTIPLIER / 2;
		y1 += COORDINATE_MULTIPLIER / 2;
		// move endPoint to the center of the field
		x2 += COORDINATE_MULTIPLIER / 2;
		y2 += COORDINATE_MULTIPLIER / 2;

		double length =
				Math.sqrt(Math.pow(x2 - x1, 2) + Math.pow(y2 - y1, 2));
		double angle = Math.asin((double) (y2 - y1) / length);

		// move endPoint a bit back on the arrow so the arrowHead won't reach
		// over the center of the field
		double xDiff = 25 * Math.cos(angle);
		double yDiff = 25 * Math.sin(angle);

		if (x2 > x1) {
			x2 -= xDiff;
		} else if (x2 < x1) {
			x2 += xDiff;
		}
		y2 -= yDiff;

		final String line = "<line x1=\"" + x1 + "\" y1=\"" + y1 +
				"\" x2=\"" + x2 + "\" " + "y2=\"" + y2 +
				"\" stroke=\"#" + SVGUtils.colorToSVG(color) +
				"\" stroke-width=\"10\" marker-end=\"url(#" +
				SVGUtils.generateColoredID("ArrowHead", color) +
				")\" />\n";

		return line;
	}

	/**
	 * Creates a string with informations about this svg.
	 *
	 * @return information string
	 */
	private String createInfoString() {

		String coordinates =
				"x=\"" + infoPos.fst + "\" " +
				"y=\"" + (infoPos.snd + 1.5 *
						FONT_SIZE_INFO_STRING) +
				"\" ";

		String circName = circuit.getParent().getFileName();
		String timeStep = String.valueOf(circuit.getCurrentTime());

		return "<text " + coordinates + "fill=\"" + FONT_COLOR_INFO_STRING +
			   "\"" +
			   " " +
			   "font-family=\"" + FONT + "\" font-size=\"" +
				FONT_SIZE_INFO_STRING +
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
		int coordSize = FONT_SIZE_INFO_STRING;
		for (int xCoord = topLeftCoord.fst; xCoord <= bottomRightCoord.fst;
			 ++xCoord) {
			coords.append("<text text-anchor=\"middle\" ");
			coords.append("x=\"" + (xCoord * COORDINATE_MULTIPLIER +
									0.5 * COORDINATE_MULTIPLIER) + "\" ");
			coords.append("y=\"" + (coordPos.snd - coordSize) + "\" ");
			coords.append(
					"font-family=\"" + FONT + "\" font-size=\"" + coordSize +
					"\">");
			coords.append(xCoord);
			coords.append("</text>\n");
		}

		for (int yCoord = topLeftCoord.snd; yCoord <= bottomRightCoord.snd;
			 ++yCoord) {
			coords.append("<text text-anchor=\"middle\" ");
			coords.append("y=\"" + ((bottomRightCoord.snd - yCoord) *
					COORDINATE_MULTIPLIER +
									0.5 * coordSize +
									0.5 * COORDINATE_MULTIPLIER) + "\" ");
			coords.append("x=\"" + (coordPos.fst -	coordSize) + "\" ");
			coords.append(
					"font-family=\"" + FONT + "\" font-size=\"" + coordSize +
					"\">");
			coords.append(yCoord);
			coords.append("</text>\n");
		}
		return coords.toString();
	}

	/**
	 * Creates all colored core strings and saves them into colSvgs.
	 */
	private void createDefCores() {

		LOGGER.debug("[SVG] Creating all needed colored cores.");

		createCores();

		// create the svg def for the arrowhead for the source target arrows
		if (circuit.getDisplayOptions().getOption(BDisplayOptions
														  .LongNetIndicatorsOnFields)) {
			// this is needed for source target arrows
			svgCoreCreator.appendSourceTargetArrowHead(svgs);
		}

		// create all needed svg defs for the fields
		for (final DrawableField f : circuit.getFields()) {

			svgCoreCreator.appendFieldSVG(svgs, f);

			Set<Net> nets = circuit.getData().getNetsOf(f.getField());
			for (final Net n : nets) {
				for (final GradDir dir : GradDir.values()) {
					svgCoreCreator.appendGradSVG(svgs, n, dir);
				}
			}
		}

		// create all needed svg defs for the droplets
		// and droplet based features
		for (final DrawableDroplet d : circuit.getDroplets()) {
			svgCoreCreator.appendDropletSVG(svgs, d);

			// Add every needed color for the arrowheads
			if (circuit.getDisplayOptions().getOption(BDisplayOptions
															  .LongNetIndicatorsOnDroplets)) {
				svgCoreCreator.appendArrowheads(svgs, d.getColor());

			}

			if (d.route != null) {
				svgCoreCreator.appendRoute(svgs, d);
			}
		}

		LOGGER.debug("[SVG] Done creating colored cores.");
	}

	/**
	 * Gets the position of a field in svg coordinates.
	 *
	 * @param drawableField
	 * 		the drawableField
	 * @return A Point with the position.
	 */
	private Point getFieldPosInSVGCoords(final DrawableField drawableField) {
		return SVGUtils.toSVGCoords(drawableField.getField().pos,
									circuit, COORDINATE_MULTIPLIER);
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
		return SVGUtils.toSVGCoords(
				drawableDrop.droplet.getSafePositionAt(
						circuit.getCurrentTime()).upperLeft(), circuit,
				COORDINATE_MULTIPLIER);
	}
}
