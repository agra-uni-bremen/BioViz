package de.bioviz.svg;


import com.badlogic.gdx.graphics.Color;
import de.bioviz.structures.Biochip;
import de.bioviz.structures.Point;
import de.bioviz.ui.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static de.bioviz.svg.SVGCoreCreator.generateID;

import java.util.HashMap;

/**
 * @author malu, keszocze
 */
public class SVGManager {


	private static Logger logger = LoggerFactory.getLogger(SVGManager.class);

	private SVGCoreCreator svgCoreCreator;

	private HashMap<TextureE, String> svgs = new HashMap<>();
	//private HashMap<ColoredCore, String> colSvgs = new HashMap<>();
	private HashMap<String, String> colSvgs = new HashMap<>();


	private String svgFolder;

	// TODO currently unused; use it after Jannis finally accepts some merge
	// requests
	private final String baseFolder = "images";

	/*
	Warning: magic numbers ahead
	 */

	private final double scaleFactor = 1;
	private final int coordinateMultiplier = 256;

	// font options
	String font = "Helvetica";
	int size = 60;
	int fontSizeIds = 60;
	Color fColor = Color.BLACK;
	String fontColor = SVGCoreCreator.colorToSVG(fColor);

	// hard coded colors
	Color strokeColor = null; // means don't change svg stroke color

	public String getTransformation(String params) {
		return " transform=\"" + params + "\" ";
	}

	public String getScaleTransformation() {
		return getTransformation(getScale());
	}

	public String getScale() {
		return "scale(" + scaleFactor + " " + scaleFactor + ")";
	}







	/**
	 * @param folder
	 * 		The name of the folder containing the theme, relative to the assets
	 * 		folder
	 * @brief SVGManager loading the theme at the specified location.
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
	 * @brief SVGManager loading the default theme
	 */
	public SVGManager() {
		this("default");
	}

	/**
	 * @brief Tells the manager where to find the svgs (i.e. .svg images).
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

		for (TextureE s : TextureE.values()) {

			// TODO Add BlackPixel svg!
			if (s != TextureE.BlackPixel) {
				svgs.put(s, svgCoreCreator.getSVGCode(s, null, null));
			}

		}
	}

	/**
	 * Export the circuit to svg.
	 *
	 * @param circ The circuit to export
	 * @return svg string representation
	 */
	public String toSVG(final DrawableCircuit circ) {

		logger.debug("[SVG] Creating all needed colored cores.");

		for(DrawableField f : circ.fields){
			String 	key = generateID(f.getDisplayValues().getTexture().toString(),f.getColor());
			// don't create the svg core code twice
			if(!colSvgs.containsKey(key)) {
				colSvgs.put(key,
						svgCoreCreator.getSVGCode(f.getDisplayValues().getTexture(), f.getColor(), strokeColor));
			}
		}
		for(DrawableDroplet d : circ.droplets){
			// TODO why do you add "-"? What is wrong with "Droplet-"? keszocze
			String key = generateID("Droplet",d.getColor());
			// don't create the svg core code twice
			if(!colSvgs.containsKey(key)) {
				colSvgs.put(key,
						svgCoreCreator.getSVGCode(TextureE.Droplet, d.getColor(), strokeColor));
			}

			if (d.route != null) {
				Color routeColor = d.route.getColor();
				key = generateID("StepMarker",routeColor);
				if (!colSvgs.containsKey(key)) {
					colSvgs.put(key,
								svgCoreCreator.getSVGCode(TextureE.StepMarker, routeColor, strokeColor));
				}
			}
		}


		logger.debug("[SVG] Done creating colored cores.");

		logger.debug("[SVG] Starting to create SVG String");
		StringBuilder sb = new StringBuilder();

		Point minCoord = circ.data.getMinCoord();
		Point maxCoord = circ.data.getMaxCoord();

		sb.append("<?xml version=\"1.1\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" +
						"<!DOCTYPE svg PUBLIC \"-//W3C//DTD SVG 1.1//EN\" \n" +
						"  \"http://www.w3.org/Graphics/SVG/1.1/DTD/svg11.dtd\">" +
				"<svg width=\"100%\" height=\"100%\" viewBox=\"" +
						(minCoord.fst) * coordinateMultiplier + " " +
						(minCoord.snd == 0 ? minCoord.snd : (minCoord.snd - 1)) * coordinateMultiplier + " " +
						(minCoord.fst == 0 ? (maxCoord.fst + 1) : maxCoord.fst) * coordinateMultiplier + " " +
						(minCoord.snd == 0 ? (maxCoord.snd + 1) : maxCoord.snd) * coordinateMultiplier +
						"\" version=\"1.1\" xmlns=\"http://www.w3.org/2000/svg\" " +
						"xmlns:xlink=\"http://www.w3.org/1999/xlink\">\n");

		// simply always put every definition in the file. File size and/or
		// computation time does not really matter here.
		sb.append("<defs>\n");
		//logger.debug("svgs: {}",svgs);
		svgs.forEach((name, svgcode) -> sb.append(svgcode));
		colSvgs.forEach((name, svgcode) -> sb.append(svgcode));
		sb.append("</defs>\n");

		for (DrawableField field : circ.fields) {
			sb.append(toSVG(field));
		}
		for (DrawableDroplet drop : circ.droplets) {
			if(drop.getDisplayColor().a > 0.1f && !circ.hiddenDroplets.contains(drop)) {
				sb.append(toSVG(drop));
			}
		}

		//sb.append("</g>\n");
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

		int yCoord =
				-field.getField().y() + field.getParentCircuit().data.getMaxCoord().snd;
		int xCoord = field.getField().x();
		yCoord = yCoord * coordinateMultiplier;
		xCoord = xCoord * coordinateMultiplier;

		DisplayValues vals = field.getDisplayValues();

		String fieldID = generateID(vals.getTexture().toString(),vals.getColor());
		String field_svg = "<use x=\"" + xCoord + "\" y=\"" + yCoord + "\"" +
						   getScaleTransformation() + " xlink:href=\"#" + fieldID +
						   "\" />\n";

		// create the msg text for the svg
		// use the text-anchor middle to get a centered position
		if(vals.getMsg() != null) {
			String msg = "<text text-anchor=\"middle\" x=\"" + (xCoord + coordinateMultiplier / 2)
					+ "\" y=\"" + (yCoord + coordinateMultiplier / 2 + (size / 2)) +
					"\" font-family=\"" + font + "\" font-size=\"" + size + "\" fill=\"#" + fontColor + "\">"
					+ vals.getMsg() + "</text>\n";
			field_svg += msg;
		}

		return field_svg;
	}

	/**
	 * Export drawableDroplets as svg.
	 *
	 * @param drawableDrop The drawableDroplet to export
	 * @return svg string representation of the drop
	 */
	private String toSVG(final DrawableDroplet drawableDrop) {
		float yCoord = -drawableDrop.droplet.smoothY +
					   drawableDrop.parentCircuit.data.getMaxCoord().snd;
		float xCoord = drawableDrop.droplet.smoothX;

		logger.debug("(x,y) = ({},{})", yCoord, xCoord);
		yCoord = ((int) yCoord) * coordinateMultiplier;
		xCoord = ((int) xCoord) * coordinateMultiplier;

		String route = toSVG(drawableDrop.route);
		String dropletID = generateID("Droplet",drawableDrop.getColor());
		String drop_shape = "<use x=\"" + xCoord + "\" " + "y=\"" + yCoord + "\"" + getScaleTransformation()
							+ " xlink:href=\"#" + dropletID + "\" />\n";

		String drop_svg = route + drop_shape;

		if(drawableDrop.getMsg() != null) {
			String msg = "<text text-anchor=\"middle\" x=\"" + (xCoord + coordinateMultiplier / 2)
					+ "\" y=\"" + (yCoord + coordinateMultiplier / 2 + (size / 2)) +
					"\" font-family=\"" + font + "\" font-size=\"" + size + "\" fill=\"#" + fontColor + "\">"
					+ drawableDrop.getMsg() + "</text>\n";
			drop_svg += msg;
		}
		return drop_svg;
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
		logger.debug("nSteps: {}", nSteps);

		for (int i = 0; i < nSteps; i++) {

			logger.debug("i: {}", i);

			// TODO possible problem here due to casting
			float alpha = 1 - (Math.abs((float) i) / ((float) displayLength));

			displayAt = currentTime + i;

			logger.debug("displayAt {}", displayAt);

			Point p1 = droplet.droplet.getPositionAt(displayAt);
			Point p2 = droplet.droplet.getPositionAt(displayAt + 1);

			logger.debug("p1 {}; p2 {}", p1, p2);

			if(p1!=null && p2!=null) {
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

				if (y1 == y2 && x2 > x1) {
					// intentionally do nothing here
				} else if (y1 == y2 && x2 < x1) {
					transFormParams +=
							" rotate(180 " + targetX + " " + (targetY + 0.5f * coordinateMultiplier) +
									") ";
				} else if (x1 == x2 && y2 > y1) {
					transFormParams +=
							" rotate(90 " + targetX + " " + (targetY + 0.5f * coordinateMultiplier) +
									") ";
				} else if (x1 == x2 && y2 < y1) {
					transFormParams +=
							"rotate(270 " + targetX + " " + (targetY + 0.5f * coordinateMultiplier) + ") ";
				} else {
					app = false;
				}
				if (app) {
					String routeID = generateID("StepMarker",routeColor);
					sb.append("<use");
					sb.append(position);
					sb.append(widthHeight);
					sb.append(getTransformation(transFormParams));
					sb.append(opacity);
					sb.append("xlink:href=\"#" + routeID + "\"");
					sb.append(" />\n");
					logger.debug("[SVG] StepMarker color: {}",routeColor);
				}
			}
		}
		return sb.toString();
	}
}
