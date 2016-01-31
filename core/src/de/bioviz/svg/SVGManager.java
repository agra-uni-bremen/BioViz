package de.bioviz.svg;


import com.badlogic.gdx.Gdx;
import de.bioviz.structures.Biochip;
import de.bioviz.structures.Point;
import de.bioviz.ui.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;

/**
 * @author Oliver Keszöcze
 * @brief Texture cache class.
 * <p>
 * This class manages texture themes. One can specify a folder in which the png
 * files that are loaded as textures are stored. These images are thenn either
 * loaded on demand or returned from the cache.
 */
public class SVGManager {


	private static Logger logger = LoggerFactory.getLogger(SVGManager.class);

	private HashMap<SVGE, String> svgs = new HashMap<>();


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
	int size = 40;

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
	public SVGManager(String folder) {
		setFolder(folder);
	}

	/**
	 * @brief SVGManager loading the default theme
	 */
	public SVGManager() {
		this("default");
	}

	/**
	 * @param folder
	 * 		The name of the folder containing the theme, relative to the assets
	 * 		folder
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
	public void setFolder(String folder) {
		svgs.clear();

		svgFolder = folder;
		for (SVGE s : SVGE.values()) {

			// TODO Add BlackPixel svg!
			if (s != SVGE.BlackPixel) {
				String svgCoreFile = baseFolder + "/" + svgFolder + "/" +
									 s +
									 ".core";
				logger.debug("Loading SVG core for {}", svgCoreFile);
				Path svgCoreFilePath =
						Gdx.files.internal(svgCoreFile).file().toPath();

				try {
					String svgCore =
							new String(Files.readAllBytes(svgCoreFilePath));
					svgs.put(s, svgCore);
				} catch (IOException e) {
					// TODO log if stuff goes wrong
					e.printStackTrace();
				}

			}
		}
	}

	public String toSVG(DrawableCircuit circ) {


		logger.debug("[SVG] Starting to create SVG String");
		StringBuilder sb = new StringBuilder();

		Point minCoord = circ.data.getMinCoord();
		Point maxCoord = circ.data.getMaxCoord();


		sb.append(
				"<svg width=\"100%\" height=\"100%\" viewBox=\"" +
						(minCoord.fst) * coordinateMultiplier + " " +
						(minCoord.snd == 0 ? minCoord.snd : (minCoord.snd - 1)) * coordinateMultiplier + " " +
						(minCoord.fst == 0 ? (maxCoord.fst + 1) : maxCoord.fst) * coordinateMultiplier + " " +
						(minCoord.snd == 0 ? (maxCoord.snd + 1) : maxCoord.snd) * coordinateMultiplier +
						"\" version=\"1.1\" xmlns=\"http://www.w3.org/2000/svg\" " +
						"xmlns:xlink=\"http://www.w3.org/1999/xlink\">\n");

//		sb.append("<svg xmlns=\"http://www.w3.org/2000/svg\" " +
//				  "xmlns:xlink=\"http://www.w3.org/1999/xlink\">\n");

		// simply always put every definition in the file. File size and/or
		// computation time does not really matter here.
		sb.append("<defs>\n");
		logger.debug("svgs: {}",svgs);
		// need to modify the svgcodes here to get the color into them
		// the color code should be added to the name
		svgs.forEach((name, svgcode) -> sb.append(svgcode));
		sb.append("</defs>\n");

		for (DrawableField field : circ.fields) {
			sb.append(toSVG(field));
		}
		for (DrawableDroplet drop : circ.droplets) {
			if(drop.getDisplayColor().a > 0.1f && !circ.hiddenDroplets.contains(drop)) {
				sb.append(toSVG(drop));
			}
		}
		
		sb.append("</g>\n");
		sb.append("</svg>\n");

		return sb.toString();
	}

	private String toSVG(DrawableField field) {
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
				-field.field.y() + field.parentCircuit.data.getMaxCoord().snd;
		int xCoord = field.field.x();
		yCoord = yCoord * coordinateMultiplier;
		xCoord = xCoord * coordinateMultiplier;

		DisplayValues vals = field.getDisplayValues();

		String msg = "<text text-anchor=\"middle\" x=\"" + (xCoord+ coordinateMultiplier/2) + "\" y=\"" + (yCoord+coordinateMultiplier/2+(size/2)) +
				"\" font-family=\"" + font + "\" font-size=\""+ size + "\" fill=\"white\">"+ (vals.msg == null ? "" : vals.msg) + "</text>\n";

		logger.debug("Color: {}", vals.color);
		
		return "<use x=\"" + xCoord + "\" y=\"" + yCoord + "\"" +
			   getScaleTransformation() + " xlink:href=\"#" + vals.texture + // the colorcode should be added here with a preceding minus
			   "\" />\n" + msg;
	}

	private String toSVG(DrawableDroplet drawableDrop) {
		float yCoord = -drawableDrop.droplet.smoothY +
					   drawableDrop.parentCircuit.data.getMaxCoord().snd;
		float xCoord = drawableDrop.droplet.smoothX;

		logger.debug("(x,y) = ({},{})", yCoord, xCoord);
		yCoord = ((int) yCoord) * coordinateMultiplier;
		xCoord = ((int) xCoord) * coordinateMultiplier;

		String msg = "<text text-anchor=\"middle\" x=\"" + (xCoord+ coordinateMultiplier/2) + "\" y=\"" + (yCoord+coordinateMultiplier/2+(size/2)) +
				"\" font-family=\"" + font + "\" font-size=\""+ size + "\" fill=\"white\">"+ (drawableDrop.getMsg() == null ? "" : drawableDrop.getMsg()) + "</text>\n";

		String route = toSVG(drawableDrop.route);
		return
				"<use x=\"" + xCoord + "\" " + "y=\"" + yCoord + "\"" +
				getScaleTransformation() + " xlink:href=\"#Droplet\" />\n" +
				route + msg;
	}

	private String toSVG(DrawableRoute drawableRoute) {
		StringBuilder sb = new StringBuilder();


		DrawableDroplet droplet = drawableRoute.droplet;

		int currentTime = droplet.parentCircuit.currentTime;
		int displayAt;

		int displayLength = drawableRoute.routeDisplayLength;

		Biochip circ = droplet.parentCircuit.data;

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

			int x1 = p1.fst*coordinateMultiplier;
			int x2 = p2.fst*coordinateMultiplier;
			int y1 = p1.snd*coordinateMultiplier;
			int y2 = p2.snd*coordinateMultiplier;

			float targetX = x1 + (0.5f*coordinateMultiplier);
			float targetY = -y1 + (circ.getMaxCoord().snd - 1)*coordinateMultiplier;

			String position = " x=\"" + targetX + "\" y=\"" + targetY + "\" ";
			String widthHeight = " width=\"1\" height=\"1\" ";
			String transFormParams = getScale();
			String opacity = " opacity=\"" + alpha + "\" ";
			boolean app = true;

			if (y1 == y2 && x2 > x1) {
				// intentionally do nothing here
			}
			else if (y1 == y2 && x2 < x1) {
				transFormParams +=
						" rotate(180 " + targetX + " " + (targetY + 0.5f) +
						") ";
			}
			else if (x1 == x2 && y2 > y1) {
				transFormParams +=
						" rotate(270 " + targetX + " " + (targetY + 0.5f) +
						") ";
			}
			else if (x1 == x2 && y2 < y1) {
				transFormParams +=
						"rotate(90 " + targetX + " " + (targetY + 0.5f) + ") ";
			}
			else {
				app = false;
			}
			if (app) {
				sb.append("<use");
				sb.append(position);
				sb.append(widthHeight);
				sb.append(getTransformation(transFormParams));
				sb.append(opacity);
				sb.append("xlink:href=\"#StepMarker\"");
				sb.append(" />\n");
			}
		}
		return sb.toString();
	}
}
