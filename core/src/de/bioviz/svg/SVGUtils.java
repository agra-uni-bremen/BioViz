/*
 * BioViz, a visualization tool for digital microfluidic biochips (DMFB).
 *
 * Copyright (c) 2017 Oliver Keszocze, Jannis Stoppe, Maximilian Luenert
 *
 * This file is part of BioViz.
 *
 * BioViz is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free
 * Software Foundation, either version 2 of the License, or (at your option)
 * any later version.
 *
 * BioViz is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE.
 *
 * See the GNU General Public License for more details. You should have
 * received a copy of the GNU
 * General Public License along with BioViz.
 * If not, see <http://www.gnu.org/licenses/>.
 */

package de.bioviz.svg;

import com.badlogic.gdx.graphics.Color;
import de.bioviz.structures.Net;
import de.bioviz.structures.Point;
import de.bioviz.structures.Rectangle;
import de.bioviz.ui.Colors;
import de.bioviz.ui.DrawableAssay;
import de.bioviz.ui.DrawableDroplet;
import de.bioviz.ui.DrawableField;
import de.bioviz.util.Pair;

/**
 * @author Maximilian Luenert
 */
public final class SVGUtils {

	/**
	 * svgExportSettings instance.
	 */
	private static final SVGExportSettings SVG_EXPORT_SETTINGS =
			SVGExportSettings.getInstance();

	/**
	 * Constructor to satisfy checkstyle.
	 */
	private SVGUtils() {

	}

	/**
	 * Converts a libGDX color to a SVG-usable format.
	 * <p>
	 * What it basically does is to throw away the last two characters, i.e.
	 * the alpha channel.
	 *
	 * @param c
	 * 		Color to transform
	 * @return Color in format to be used by SVG
	 */
	public static String colorToSVG(final Color c) {
		return c.toString().substring(0, 6);
	}

	/**
	 * Gets the color of a net.
	 *
	 * @param net
	 * 		the net
	 * @return the color of the net or Color.DARK_GREY if the colorfulExport is
	 * off
	 */
	public static Color getNetColor(final Net net) {
		if (SVG_EXPORT_SETTINGS.getColorfulExport()) {
			return net.getColor().buildGdxColor();
		} else {
			return Color.DARK_GRAY;
		}
	}

	/**
	 * Get the color for a lighter longNetIndicator.
	 *
	 * @param dropColor
	 * 		the droplet to get the base color
	 * @return the color for the lighter arrowHead or Color.DARK_GREY if the
	 * colorfulExport is off
	 */
	public static Color getLighterLongNetIndicatorColor(final Color
																dropColor) {
		if (SVG_EXPORT_SETTINGS.getColorfulExport()) {
			return dropColor.cpy().sub(
					Colors.LONG_NET_INDICATORS_ON_DROPLET_DIFF);
		} else {
			return Color.BLACK;
		}
	}

	/**
	 * Get the color for a darker longNetIndicator.
	 *
	 * @param dropColor
	 * 		the droplet to get the base color
	 * @return the color for the darker arrowHead or Color.DARK_GREY if the
	 * colorfulExport is off
	 */
	public static Color getDarkerLongNetIndicatorColor(final Color dropColor) {
		if (SVG_EXPORT_SETTINGS.getColorfulExport()) {
			return dropColor.cpy().add(
					Colors.LONG_NET_INDICATORS_ON_DROPLET_DIFF);
		} else {
			return Color.BLACK;
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
	public static String generateColoredID(final String baseName,
										   final Color c) {
		if (SVG_EXPORT_SETTINGS.getColorfulExport()) {
			return baseName + "-" + colorToSVG(c);
		} else {
			return baseName;
		}
	}

	/**
	 * Transforms a Pair<Float, Float> into svgCoords.
	 *
	 * @param point
	 * 		the coord to transform
	 * @param assay
	 * 		the current assay
	 * @param coordinateMultiplier
	 * 		the coordinate multiplier
	 * @return Pair<Float, Float> in svg coordinates
	 */
	public static Pair<Float, Float> toSVGCoords(
			final Pair<Float, Float> point,
			final DrawableAssay assay,
			final int coordinateMultiplier) {
		float yCoord = -point.snd + assay.getData().getMaxCoord().snd;
		float xCoord = point.fst;

		xCoord *= coordinateMultiplier;
		yCoord *= coordinateMultiplier;

		return new Pair<>(xCoord, yCoord);
	}

	/**
	 * Transforms a point to svgCoordinates.
	 *
	 * @param point
	 * 		the point to transform
	 * @param assay
	 * 		the actual assay
	 * @param coordinateMultiplier
	 * 		the used coordinateMultiplier
	 * @return Point with SVG coordinates
	 */
	public static Point toSVGCoords(
			final Point point,
			final DrawableAssay assay,
			final int coordinateMultiplier) {
		// The coordinate system in SVG is inverted on its
		// y-axis. We need to first put it upside down (-point.snd) and
		// then add the total height of the assay to have the element
		// put back into the positive coordinate range in order to be placed
		// on the canvas.
		int yCoord = -point.snd + assay.getData().getMaxCoord().snd;
		int xCoord = point.fst;

		xCoord *= coordinateMultiplier;
		yCoord *= coordinateMultiplier;

		return new Point(xCoord, yCoord);
	}

	/**
	 * Calculates the needed scale factors for a droplet.
	 *
	 * @param droplet
	 * 		the droplet
	 * @param timeStep
	 * 		the timestep
	 * @return Pair of Integers with x and y scale factor
	 */
	public static Pair<Integer, Integer> getScaleFactors(final DrawableDroplet
																 droplet,
														 final
														 int
																 timeStep) {

		Rectangle position = droplet.droplet.getPositionAt(timeStep);

		int xScale = position.size().fst;
		int yScale = position.size().snd;

		return new Pair<>(xScale, yScale);
	}

	/**
	 * Returns the color of the field in an unhovered state.
	 *
	 * @param field
	 * 		the field
	 * @return the unhovered color
	 */
	public static Color getUnhoveredColor(final DrawableField field) {
		Color fieldCol = field.getColor().cpy();
		if (field.isHovered()) {
			fieldCol = fieldCol.sub(Colors.HOVER_DIFF_COLOR);
		}
		return fieldCol;
	}

	/**
	 * Checks if a droplet is hidden or invisible.
	 *
	 * @param droplet
	 * 		the droplet to check
	 * @return true if it is hidden or invisible, false otherwise
	 */
	public static boolean isNotHiddenOrInvisible(final DrawableDroplet
														 droplet) {
		return droplet.getDisplayColor().a > 0.1f &&
			   !droplet.parentAssay.isHidden(droplet);
	}
}
