package de.bioviz.svg;

import com.badlogic.gdx.graphics.Color;
import de.bioviz.structures.Point;
import de.bioviz.ui.Colors;
import de.bioviz.ui.DrawableCircuit;
import de.bioviz.ui.DrawableField;

/**
 * Created by max on 4/26/16.
 */
public class SVGUtils {

	private final static SVGExportSettings SVG_EXPORT_SETTINGS =
			SVGExportSettings.getInstance();

	/**
	 * Converts a libGDX color to a SVG-usable format.
	 *
	 * What it basically does is to throw away the last to characters, i.e.
	 * the alpha channel.
	 *
	 * @param c Color to transform
	 * @return Color in format to be used by SVG
	 */
	public static String colorToSVG(final Color c) {
		return c.toString().substring(0, 6);
	}

	/**
	 * Creates an ID consisting of a base part and the given color.
	 * @param baseName The part of thename in front of the '-'
	 * @param c The color that will be put after the '-'
	 * @return "<baseName>-<color>"
	 */
	public static String generateColoredID(final String baseName,
																				 final Color c) {
		if (SVG_EXPORT_SETTINGS.getColorfulExport()) {
			return baseName + "-" + colorToSVG(c);
		} else {
			return baseName;
		}
	}

	/**
	 * Transforms a point to svgCoordinates.
	 *
	 * @param point the point to transform
	 * @return Point with SVG coordinates
	 */
	public static Point toSVGCoords(final Point point,
																	final DrawableCircuit circuit,
																	final int coordinateMultiplier) {
		int yCoord = -point.snd + circuit.getData().getMaxCoord().snd;
		int xCoord = point.fst;

		xCoord *= coordinateMultiplier;
		yCoord *= coordinateMultiplier;

		return new Point(xCoord, yCoord);
	}

	/**
	 * Returns the color of the field in an unhovered state.
	 *
	 * @param field the field
	 * @return the unhovered color
	 */
	public static Color getUnhoveredColor(DrawableField field){
		Color fieldCol = field.getColor().cpy();
		if(field.isHovered()){
			fieldCol = fieldCol.sub(Colors.HOVER_DIFF_COLOR);
		}
		return fieldCol;
	}
}
