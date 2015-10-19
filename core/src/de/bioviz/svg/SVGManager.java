package de.bioviz.svg;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import de.bioviz.structures.Biochip;
import de.bioviz.structures.Droplet;
import de.bioviz.structures.Point;
import de.bioviz.ui.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
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
	private final String baseFolder = ".";


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
		this("");
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
			String svgCoreFile = baseFolder + "/" + svgFolder + "/" +
								 s +
								 ".core";
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

	public static String toSVG(DrawableCircuit circ) {
		StringBuilder sb = new StringBuilder();

		Point minCoord = circ.data.getMinCoord();
		Point maxCoord = circ.data.getMaxCoord();


		sb.append(
				"<svg width=\"100%\" height=\"100%\" viewBox=\"" +
				minCoord.fst + " " +
				(minCoord.snd - 1) + " " +
				(maxCoord.fst + 1) + " " +
				(maxCoord.snd + 1) +
				"\" version=\"1.1\" xmlns=\"http://www.w3.org/2000/svg\" " +
				"xmlns:xlink=\"http://www.w3.org/1999/xlink\">\n");

		for (DrawableField field : circ.fields) {
			sb.append(SVGManager.toSVG(field));
		}
		for (DrawableDroplet drop : circ.droplets) {
			sb.append(SVGManager.toSVG(drop));
		}

		sb.append("</svg>\n");
		return sb.toString();
	}

	public static String toSVG(DrawableField field) {
		// TODO When merged, use the TextureManager to get the filename
		// TODO must be able to display sinks etc.
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
		return "<image x=\"" + xCoord + "\" y=\"" + yCoord +
			   "\" width=\"1\" height=\"1\" xlink:href=\"GridMarker.svg\" " +
			   "/>\n";
	}

	public static String toSVG(DrawableDroplet drawableDrop) {
		// TODO hier auch entsprechend auf die SVG Dateinamen zurückgreifen
		// (über nen Manager)
		float yCoord = -drawableDrop.droplet.smoothY +
					   drawableDrop.parentCircuit.data.getMaxCoord().snd;
		float xCoord = drawableDrop.droplet.smoothX;
		String route = SVGManager.toSVG(drawableDrop.route);
		return
				"<image x=\"" + xCoord + "\" " +
				"y=\"" + yCoord + "\" " +
				"width=\"1\" height=\"1\" xlink:href=\"Droplet.svg\" />\n" +
				route;
	}

	public static String toSVG(DrawableRoute drawableRoute) {
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

			int x1 = p1.fst;
			int x2 = p2.fst;
			int y1 = p1.snd;
			int y2 = p2.snd;

			float targetX = x1 + 0.5f;
			float targetY = -y1 + circ.getMaxCoord().snd - 1;
			if (y1 == y2 && x2 > x1) {
				sb.append("<image x=\"" + targetX + "\" y=\"" + targetY +
						  "\" width=\"1\" height=\"1\" " +
						  "xlink:href=\"StepMarker" +
						  ".svg\" />\n");
			}
			else if (y1 == y2 && x2 < x1) {
				sb.append("<image x=\"" + targetX + "\" y=\"" + targetY +
						  "\" width=\"1\" height=\"1\" transform=\"rotate" +
						  "(180" +
						  " " +
						  targetX + " " + (targetY + 0.5f) + " )\" " +
						  "opacity=\"" +
						  alpha + "\" xlink:href=\"StepMarker.svg\" />\n");
			}
			else if (x1 == x2 && y2 > y1) {
				sb.append("<image x=\"" + targetX + "\" y=\"" + targetY +
						  "\" width=\"1\" height=\"1\" transform=\"rotate" +
						  "(270" +
						  " " +
						  targetX + " " + (targetY + 0.5f) + " )\" " +
						  "opacity=\"" +
						  alpha + "\" xlink:href=\"StepMarker.svg\" />\n");
			}
			else if (x1 == x2 && y2 < y1) {
				sb.append("<image x=\"" + targetX + "\" y=\"" + targetY +
						  "\" width=\"1\" height=\"1\" transform=\"rotate(90" +
						  " " +
						  targetX + " " + (targetY + 0.5f) + " )\" " +
						  "opacity=\"" +
						  alpha + "\" xlink:href=\"StepMarker.svg\" />\n");
			}
			else {
				continue;
			}
		}
		return sb.toString();
	}


	public String getSVG(SVGE svg) {
		// TODO no error handling, let's see how far that gets us :|
		return svgs.get(svg);
	}

}
