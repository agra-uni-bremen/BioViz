package de.bioviz.svg;


import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.utils.StringBuilder;
import de.bioviz.structures.Biochip;
import de.bioviz.structures.Net;
import de.bioviz.structures.Point;
import de.bioviz.structures.Source;
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
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
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
	private static final int FONT_SIZE = 150;
	/**
	 * the font size for the info string.
	 */
	private static final int FONT_SIZE_INFO_STRING = 100;

	/**
	 * Precalculated offset for font in y direction.
	 */
	private static final int FONT_OFFSET_Y = COORDINATE_MULTIPLIER / 2 +
			(FONT_SIZE / 2) - 24;

	/**
	 * Precalculated offset for fonts in x direction.
	 */
	private static final int FONT_OFFSET_X = COORDINATE_MULTIPLIER / 2;

	/**
	 * font color.
	 */
	private static final String FONT_COLOR = SVGUtils.colorToSVG(Color.WHITE);
	/**
	 * font color for the info String.
	 */
	private static final String FONT_COLOR_INFO_STRING =
			SVGUtils.colorToSVG(Color.BLACK);

	// often used svg parts
	/**
	 * Text anchor middle tag.
	 */
	private static final String TEXT_ANCHOR_STR = "text-anchor=\"middle\" ";
	/**
	 * Font family tag.
	 */
	private static final String FONT_FAMILY_STR = "font-family=\"";
	/**
	 * Font size tag.
	 */
	private static final String FONT_SIZE_STR = "font-size=\"";

	/**
	 * svgCoreCreator.
	 */
	private SVGCoreCreator svgCoreCreator;

	/**
	 * hashMap for uncolored svg elements.
	 */
	private Map<String, Element> svgs = new HashMap<>();


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
	private DrawableAssay assay;

	private Document doc;
	private DocumentBuilder docBuilder;
	private Element rootNode;


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
		try {
			final DocumentBuilderFactory docFactory =
													DocumentBuilderFactory.newInstance();
			docBuilder = docFactory.newDocumentBuilder();
			doc = docBuilder.newDocument();
		} catch (ParserConfigurationException e) {
			LOGGER.error("Could not create xml document builder. " + e.getMessage());
		}

		svgCoreCreator = new SVGCoreCreator(doc);
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
				Element elem = svgCoreCreator.getSVGCode(s, null,
						null);
				if (elem != null) {
					svgs.put(s.toString(), elem);
				}
			}

		}
	}

	/**
	 * Calculates min and max values for the resulting svg viewbox.
	 */
	private void calculateViewboxDimensions() {
		Point minCoord = assay.getData().getMinCoord();
		Point maxCoord = assay.getData().getMaxCoord();

		LOGGER.trace("Min X: {} Min Y: {}", minCoord.fst, minCoord.snd);
		LOGGER.trace("Max X: {} Max Y: {}", maxCoord.fst, maxCoord.snd);

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
	 * Exports the created SVG into the given file.
	 * Handles the creation of an SVG series export.
	 *
	 * @param file
	 *					The File to store the SVG in
	 * @param circ
	 * 					The circuit to export
	 * @param timeStep
	 * 					The timestep for the export
	 */
	public void exportSVG(final File file,
												 final DrawableAssay circ,
												 final int timeStep) {
		//check if the series export option is active
		if (svgExportSettings.getExportSeries()) {
			int oldTime = circ.getCurrentTime();
			// this is problematic if the file contains
			// .svg inside the name
			int svgPosition = file.getAbsolutePath().indexOf(".svg");
			// initialize with absolute path
			String pathWithoutSuffix = file.getAbsolutePath();
			// check if suffix was found, if not the path
			// is already without a suffix
			if (svgPosition != -1) {
				pathWithoutSuffix = file.getAbsolutePath().substring(0, svgPosition);
			}
			final int maxT = circ.getData().getMaxT();
			final int numDigits = (int) (Math.log10(maxT) + 1);
			final String numberFormatString = "%0" + numDigits + "d";

			// create a series of files
			for (int t = 1; t <= maxT; t++) {
				String filePath = pathWithoutSuffix + "_ts" +
						String.format(numberFormatString, t) + ".svg";
				File curFile = new File(filePath);
				this.saveSVG(curFile, circ, t);
			}
			// restore time from start
			circ.setCurrentTime(oldTime);
		} else {
			this.saveSVG(file, circ, timeStep);
		}

	}

	/**
	 * Saves an SVG to a given file.
	 *
	 * @param file
	 * 					The File to store the svg in.
	 * @param circ
	 * 					The circuit to export.
	 * @param timeStep
	 * 					The timestep for the export.
	 */
	private void saveSVG(final File file, final DrawableAssay circ, final int
			timeStep) {
		doc = docBuilder.newDocument();
		svgCoreCreator.setDocument(doc);
		rootNode = doc.createElement("svg");
		try {
			FileWriter fileWriter = new FileWriter(file, false);
			fileWriter.write(this.toSVG(circ, timeStep));
			fileWriter.close();
		} catch (final IOException e) {
			LOGGER.error("Failed to write file: {}", file.getAbsolutePath());
			LOGGER.error("Exception: ", e.getMessage());
		}
	}

	/**
	 * Export the assay to svg.
	 *
	 * @param circ
	 * 		The assay to export
	 * @param timeStep
	 * 		The timeStep for the export
	 * @return svg string representation
	 */
	private String toSVG(final DrawableAssay circ, final int timeStep) {

		assay = circ;

		// set the export timeStep
		assay.setCurrentTime(timeStep);

		calculateViewboxDimensions();

		if (assay.getDisplayOptions().getOption(BDisplayOptions
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

		rootNode.setAttribute("width", "100%");
		rootNode.setAttribute("height", "100%");
		rootNode.setAttribute("viewBox",
									viewBoxX + " " + viewBoxY + " " +
									viewBoxWidth + " " + viewBoxHeight
									);
		rootNode.setAttribute("version", "1.0");
		rootNode.setAttribute("xmlns", "http://www.w3.org/2000/svg");
		rootNode.setAttribute("xmlns:xlink", "http://www.w3.org/1999/xlink");

		doc.appendChild(rootNode);

		// simply always put every definition in the file. File size and/or
		// computation time does not really matter here.
		Element defs = doc.createElement("defs");


		// create all def elements and save them in svgs
		createDefCores();

		// append all cores to the svg
		svgs.forEach((name, svgcode) -> defs.appendChild(svgcode));

		rootNode.appendChild(defs);

		for (final DrawableField field : assay.getFields()) {
			toSVG(field).forEach(fieldElem -> rootNode.appendChild(fieldElem));
		}
		for (final DrawableDroplet drop : assay.getDroplets()) {
			if (SVGUtils.isNotHiddenOrInvisible(drop)) {
				toSVG(drop).forEach(dropElem -> rootNode.appendChild(dropElem));
			}
		}
		// run over each droplet again and draw the arrows
		// otherwise arrows can get under droplets
		for (final DrawableDroplet drop : assay.getDroplets()) {
			if (assay.getDisplayOptions().
					getOption(BDisplayOptions.LongNetIndicatorsOnDroplets) &&
				SVGUtils.isNotHiddenOrInvisible(drop)) {
				createDropletArrows(drop).forEach(arrow -> rootNode.appendChild
				(arrow));
			}
		}

		// append longNetIndicatorsOnFields when needed
		for (final Net net : assay.getData().getNets()) {
			if (assay.getDisplayOptions().getOption(BDisplayOptions
															.LongNetIndicatorsOnFields)) {
				createSourceTargetArrow(net).forEach(arrow -> rootNode.appendChild
				(arrow));
			}
		}

		// export msg strings for fields
		for (final DrawableField field : assay.getFields()) {
			Element fieldMsg = createFieldMsg(field);
			if (fieldMsg != null) {
				rootNode.appendChild(fieldMsg);
			}
		}
		// export msg strings for droplets
		for (final DrawableDroplet drop : assay.getDroplets()) {
			//
			if (SVGUtils.isNotHiddenOrInvisible(drop)) {
				Element dropMsg = createDropletMsg(drop);
				if(dropMsg != null) {
					rootNode.appendChild(dropMsg);
				}
			}
		}


		if (svgExportSettings.getInformationString()) {
			rootNode.appendChild(createInfoString());
		}

		if (assay.getDisplayOptions().getOption(
				BDisplayOptions.Coordinates)) {
			createCoordinates().forEach(coord -> rootNode.appendChild(coord));
		}

		return transformXML();
	}

	/**
	 * Transforms the xml document into a string that is then written.
	 *
	 * @return
	 */
	public String transformXML(){
		StreamResult result = new StreamResult();
		StringWriter writer = new StringWriter();
		try {

			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = null;
			transformer = transformerFactory.newTransformer();
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");

			DOMSource source = new DOMSource(doc);

			// Output to stream writer
			result = new StreamResult(writer);

			transformer.transform(source, result);

		} catch (TransformerException e) {
			e.printStackTrace();
		}
		System.out.println("File saved!");
		return writer.toString();
	}

	/**
	 * Exports a drawableField to svg.
	 *
	 * @param field
	 * 		The field to export
	 * @return svg string representation of a field
	 */
	private List<Element> toSVG(final DrawableField field) {
		// why would we need to acces " (-this.field.y + BioViz.singleton
		// .currentAssay.data.field[0].length - 1)"?
		// @jannis please check and fix
		// @keszocze Because the coordinate system in SVG is inverted on its
		//		y-axis. I need to first put it upside down (-this.field.y) and
		//		then add the total height of the assay to have the element
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

		List<Element> fieldElems = new ArrayList<>();

		Element elem = doc.createElement("use");
		elem.setAttribute("x", String.valueOf(xCoord));
		elem.setAttribute("y", String.valueOf(yCoord));
		elem.setAttribute("transform",
											"scale(" + SCALE_FACTOR + " " +
													SCALE_FACTOR + ")"
		);
		elem.setAttribute("xlink:href", "#" + fieldID);

		fieldElems.add(elem);

		if (assay.getDisplayOptions().getOption(
				BDisplayOptions.NetColorOnFields)) {
			fieldElems.addAll(createGradient(field));
		}

		return fieldElems;
	}

	/**
	 * Exports drawableDroplets as svg.
	 *
	 * @param drawableDrop
	 * 		The drawableDroplet to export
	 * @return svg string representation of the drop
	 */
	private List<Element> toSVG(final DrawableDroplet drawableDrop) {

		Point dropletPos = getDropletPosInSVGCoords(drawableDrop);

		Pair<Integer, Integer> scaleFactors = SVGUtils.getScaleFactors(
				drawableDrop, assay.getCurrentTime());

		String scale =
				"scale(" + scaleFactors.fst + " " + scaleFactors.snd + ")";

		String translateToZero = "translate(" + dropletPos.fst + " " +
								 dropletPos.snd + ")";
		String translateBack = "translate(-" + dropletPos.fst + " " + "-" +
							   dropletPos.snd + ")";

		// move the object to 0,0 then scale it and move it back to its
		// original position
		String transformation = translateToZero + " " +
												  scale + " " + translateBack;

		List<Element> route = toSVG(drawableDrop.route);

		String dropletID = SVGUtils.generateColoredID("Droplet", drawableDrop
				.getColor());

		Element dropShape = doc.createElement("use");
		dropShape.setAttribute("x", String.valueOf(dropletPos.fst));
		dropShape.setAttribute("y", String.valueOf(dropletPos.snd));
		dropShape.setAttribute("transform", transformation);
		dropShape.setAttribute("xlink:href", "#" + dropletID);

		List<Element> dropElements = new ArrayList<>();
		dropElements.add(dropShape);
		dropElements.addAll(route);

		return dropElements;
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
	private List<Element> toSVG(final DrawableRoute drawableRoute) {

		List<Element> elements = new ArrayList<>();
		DrawableDroplet droplet = drawableRoute.droplet;

		int currentTime = droplet.droplet.getSpawnTime();

		int displayAt;

		int displayLength = assay.getDisplayRouteLength();

		Biochip biochip = assay.getData();

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
			float alpha = 1;

			if (!assay.getDisplayOptions().getOption(
					BDisplayOptions.SolidPaths)) {
				alpha -= Math.abs((float) i) / (float) displayLength;
			}

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

				StringBuilder transFormParams = new StringBuilder(getScale());
				// TODO calculate center and angle instead of if else bla
				if (x1 < x2 && y1 == y2) {
					//intentionally do nothing here
				} else if (y1 == y2 && x2 < x1) {
					transFormParams.append(
							" rotate(180 " + targetX + " " +
							(targetY + 0.5f * COORDINATE_MULTIPLIER) + ") "
					);
				} else if (x1 == x2 && y2 > y1) {
					transFormParams.append(
							" rotate(90 " + targetX + " " +
							(targetY + 0.5f * COORDINATE_MULTIPLIER) + ") "
					);
				} else if (x1 == x2 && y2 < y1) {
					transFormParams.append(
							"rotate(270 " + targetX + " " +
							(targetY + 0.5f * COORDINATE_MULTIPLIER) + ") "
					);
				}
				// this case is needed if the coordinates are the same
				else {
					continue;
				}

				String routeID =
						SVGUtils.generateColoredID("StepMarker",
								routeColor);


				Element route = doc.createElement("use");
				route.setAttribute("x", String.valueOf(targetX));
				route.setAttribute("y", String.valueOf(targetY));
				route.setAttribute("width", "1");
				route.setAttribute("height", "1");
				route.setAttribute("transform", transFormParams.toString());
				route.setAttribute("opacity", String.valueOf(alpha));
				route.setAttribute("xlink:href", "#" + routeID);

				elements.add(route);

				LOGGER.debug("[SVG] StepMarker color: {}", routeColor);
			}
		}
		return elements;
	}

	/**
	 * Creates svg arrows from all net sources to their net targets.
	 *
	 * @param net
	 * 		the droplet
	 * @return svg string containing all start end arrows
	 */
	private List<Element> createSourceTargetArrow(final Net net) {

		List<Element> arrows = new ArrayList<>();
		if (net != null) {
			List<Source> startPoints = net.getSources();
			Pair<Float, Float> endPoint = net.getTarget().centerFloat();

			Color arrowColor = Color.BLACK;
			for (final Source startSource : startPoints) {
				Pair<Float, Float> startPoint =
						startSource.startPosition.centerFloat();
				if (!startPoint.equals(endPoint)) {
					arrows.add(createSVGArrow(startPoint, endPoint, arrowColor));
				}
			}
		}

		return arrows;
	}

	/**
	 * Creates the svg string for the longNetIndicator arrows.
	 *
	 * @param drawableDrop
	 * 		the drop
	 * @return svg string
	 */
	private List<Element> createDropletArrows(final DrawableDroplet
	drawableDrop) {

		Net net = drawableDrop.droplet.getNet();

		List<Element> arrows = new ArrayList<>();

		if (net != null) {

			int time = assay.getCurrentTime();
			Pair<Float, Float> startPoint = drawableDrop.droplet
					.getFirstPosition()
					.centerFloat();
			Pair<Float, Float> endPoint = net.getTarget().centerFloat();
			Pair<Float, Float> dropletPos = drawableDrop.droplet.
					getSafePositionAt(time).centerFloat();

			Color dropColor = drawableDrop.getColor();

			if (startPoint != null && dropletPos != null &&
				!startPoint.equals(dropletPos)) {
				Color arrowColor =
						SVGUtils.getLighterLongNetIndicatorColor(dropColor);

				arrows.add(createSVGArrow(startPoint, dropletPos, arrowColor));
			}

			if (dropletPos != null && endPoint != null &&
				!dropletPos.equals(endPoint)) {
				Color arrowColor =
						SVGUtils.getDarkerLongNetIndicatorColor(dropColor);
				arrows.add(createSVGArrow(dropletPos, endPoint, arrowColor));
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
	private Element createDropletMsg(final DrawableDroplet drawableDrop) {
		Point dropPos = getDropletPosInSVGCoords(drawableDrop);

		Element msg = null;

		if (drawableDrop.getMsg() != null) {
			msg = createMsg(dropPos.fst, dropPos.snd, drawableDrop.getMsg());
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
	private Element createFieldMsg(final DrawableField field) {
		Point fieldPos = getFieldPosInSVGCoords(field);

		Element fieldMsg = null;

		DisplayValues vals = field.getDisplayValues();
		// create the msg text for the svg
		// use the text-anchor middle to get a centered position
		if (vals.getMsg() != null) {
			fieldMsg = createMsg(fieldPos.fst, fieldPos.snd, vals.getMsg());
		}
		return fieldMsg;
	}

	/**
	 * Creates an svg text field at the given position with the given message.
	 *
	 * @param x x position of the text
	 * @param y y position of the text
	 * @param message the text to print
	 * @return svg text element
	 */
	private Element createMsg(final int x, final int y, final String message) {
		Element msg = doc.createElement("text");
		msg.setAttribute("text-anchor", "middle");
		msg.setAttribute("x", String.valueOf(x + FONT_OFFSET_X));
		msg.setAttribute("y", String.valueOf(y + FONT_OFFSET_Y));
		msg.setAttribute("font-family", FONT);
		msg.setAttribute("font-size", String.valueOf(FONT_SIZE));
		msg.setAttribute("fill", "#" + FONT_COLOR);
		msg.appendChild(doc.createTextNode(message));

		return msg;
	}

	/**
	 * Create the svg rect for a gradient for a given field.
	 *
	 * @param field
	 * 		the field
	 * @return an svg rect string
	 */
	private List<Element> createGradient(final DrawableField field) {
		List<Element> gradients = new ArrayList<>();

		for (final Net n : assay.getData().getNetsOf(field.getField())) {
			GradDir dir = getGradientDirection(field, n);
			Point fieldPos = getFieldPosInSVGCoords(field);
			if (dir != null) {

				Element gradientSvg = doc.createElement("rect");
				gradientSvg.setAttribute("x", String.valueOf(fieldPos.fst + 24));
				gradientSvg.setAttribute("y", String.valueOf(fieldPos.snd + 24));
				gradientSvg.setAttribute("rx", "24");
				gradientSvg.setAttribute("ry", "24");
				gradientSvg.setAttribute("height", "208");
				gradientSvg.setAttribute("width", "208");

				final String fillUrl = "url(#" +	SVGUtils.generateColoredID(
						"Gradient-" + dir.toString(),
						SVGUtils.getNetColor(n)) + ")";
				gradientSvg.setAttribute("fill", fillUrl);

				gradients.add(gradientSvg);

			}
		}
		return gradients;
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
				dirMatch &= !assay.getData().hasFieldAt(fieldPos.add(p)) ||
							!net.containsField(assay.getData().
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
	private Element createSVGArrow(final Pair<Float, Float> startPoint,
								  final Pair<Float, Float> endPoint,
								  final Color
										  color) {
		Pair<Float, Float> start = SVGUtils.toSVGCoords(startPoint, assay,
														COORDINATE_MULTIPLIER);
		Pair<Float, Float> end = SVGUtils.toSVGCoords(endPoint, assay,
													  COORDINATE_MULTIPLIER);

		float x1 = start.fst;
		float y1 = start.snd;

		float x2 = end.fst;
		float y2 = end.snd;

		// move startingPoint to the center of the field
		x1 += COORDINATE_MULTIPLIER / 2.0;
		y1 += COORDINATE_MULTIPLIER / 2.0;
		// move endPoint to the center of the field
		x2 += COORDINATE_MULTIPLIER / 2.0;
		y2 += COORDINATE_MULTIPLIER / 2.0;

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

		final Element line = doc.createElement("line");
		line.setAttribute("x1", String.valueOf(x1));
		line.setAttribute("y1", String.valueOf(y1));
		line.setAttribute("x2", String.valueOf(x2));
		line.setAttribute("y2", String.valueOf(y2));
		line.setAttribute("stroke", "#" + SVGUtils.colorToSVG(color));
		line.setAttribute("stroke-width", "10");
		final String markerEndUrl = "url(#" +
				SVGUtils.generateColoredID("ArrowHead", color) +
						")";
		line.setAttribute("marker-end", markerEndUrl);

		return line;
	}

	/**
	 * Creates a string with informations about this svg.
	 *
	 * @return information string
	 */
	private Element createInfoString() {

		float xCoord = infoPos.fst;
		float yCoord = infoPos.snd + 1.5f * FONT_SIZE_INFO_STRING;

		String circName = assay.getParent().getFileName();
		String timeStep = String.valueOf(assay.getCurrentTime());
		String fileInfo = "Filename: " + circName + " Timestep: " + timeStep;

		Element textElem = doc.createElement("text");
		textElem.setAttribute("x", String.valueOf(xCoord));
		textElem.setAttribute("y", String.valueOf(yCoord));
		textElem.setAttribute("fill", SVGUtils.colorToSVG(Color.BLACK));
		textElem.setAttribute("font-family", FONT);
		textElem.setAttribute("font-size",
													String.valueOf(FONT_SIZE_INFO_STRING));
		textElem.appendChild(doc.createTextNode(fileInfo));

		return textElem;
	}

	/**
	 * Creates coordinates around the grid.
	 *
	 * @return String containing the svg code for the coordinates
	 */
	private List<Element> createCoordinates() {
		List<Element> coordElems = new ArrayList<>();
		int coordSize = FONT_SIZE_INFO_STRING;

		for (int xCoord = topLeftCoord.fst; xCoord <= bottomRightCoord.fst;
			 ++xCoord) {
			final String xCoordStr = String.valueOf(xCoord * COORDINATE_MULTIPLIER
					+ 0.5f * COORDINATE_MULTIPLIER);
			final String yCoordStr = String.valueOf(coordPos.snd - coordSize);

			Element coord = doc.createElement("text");
			coord.setAttribute("text-anchor", "middle");
			coord.setAttribute("x", xCoordStr);
			coord.setAttribute("y", yCoordStr);
			coord.setAttribute("font-family", FONT);
			coord.setAttribute("font-size", String.valueOf(coordSize));
			coord.appendChild(doc.createTextNode(String.valueOf(xCoord)));

			coordElems.add(coord);
		}

		for (int yCoord = topLeftCoord.snd; yCoord <= bottomRightCoord.snd;
			 ++yCoord) {

			final String xCoordStr = String.valueOf(coordPos.fst - coordSize);
			final String yCoordStr = String.valueOf(
					(bottomRightCoord.snd - yCoord) *	COORDINATE_MULTIPLIER +
					0.5f * coordSize +
					0.5f * COORDINATE_MULTIPLIER);

			Element coord = doc.createElement("text");
			coord.setAttribute("x", xCoordStr);
			coord.setAttribute("y", yCoordStr);
			coord.setAttribute("font-family", FONT);
			coord.setAttribute("font-size", String.valueOf(coordSize));
			coord.appendChild(doc.createTextNode(String.valueOf(yCoord)));

			coordElems.add(coord);
		}
		return coordElems;
	}

	/**
	 * Creates all colored core strings and saves them into colSvgs.
	 */
	private void createDefCores() {

		LOGGER.debug("[SVG] Creating all needed colored cores.");

		createCores();

		// create the svg def for the arrowhead for the source target arrows
		if (assay.getDisplayOptions().getOption(BDisplayOptions
														.LongNetIndicatorsOnFields)) {
			// this is needed for source target arrows
			svgCoreCreator.appendSourceTargetArrowHead(svgs);
		}

		// create all needed svg defs for the fields
		for (final DrawableField f : assay.getFields()) {
			svgCoreCreator.appendFieldSVG(svgs, f);
		}

		// create gradients for every net if netColorOnFields is selected
		if (assay.getDisplayOptions().getOption(BDisplayOptions
				.NetColorOnFields)) {
			Set<Net> nets = assay.getData().getNets();
			for (final Net n : nets) {
				for (final GradDir dir : GradDir.values()) {
					svgCoreCreator.appendGradSVG(svgs, n, dir);
				}
			}
		}

		// create all needed svg defs for the droplets
		// and droplet based features
		for (final DrawableDroplet d : assay.getDroplets()) {
			svgCoreCreator.appendDropletSVG(svgs, d);

			// Add every needed color for the arrowheads
			if (assay.getDisplayOptions().getOption(BDisplayOptions
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
									assay, COORDINATE_MULTIPLIER);
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
						assay.getCurrentTime()).upperLeft(), assay,
				COORDINATE_MULTIPLIER);
	}
}
