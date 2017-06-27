/*
 * BioViz, a visualization tool for digital microfluidic biochips (DMFB).
 *
 * Copyright (c) 2017 Oliver Keszocze, Jannis Stoppe, Maximilian Luenert
 *
 * This file is part of BioViz.
 *
 * BioViz is free software: you can redistribute it and/or modify it under the terms of the
 * GNU General Public License as published by the Free Software Foundation, either version 2 of the
 * License, or (at your option) any later version.
 *
 * BioViz is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *
 * See the GNU General Public License for more details. You should have received a copy of the GNU
 * General Public License along with BioViz. If not, see <http://www.gnu.org/licenses/>.
 */

package de.bioviz.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;

/**
 * A simple line segment defined by the start and end point.
 *
 * This class is used to draw the source->target lines etc.
 *
 * @author Jannis Stoppe
 */
public class DrawableLine extends DrawableSprite {

	/**
     * The starting point of the line.
     */
    public Vector2 from;

	/**
	 * The end point of the line.
	 */
    public Vector2 to;

	/**
	 * The arrowHead that is drawn at the end of the line.
	 */
	private DrawableArrowHead arrowHead;

	/**
	 * Enables or disables the arrowHead.
	 */
	private boolean showHead;

	/**
	 * Creates a drawable line with no start/end points.
	 * @param parent
	 * 						The parent BioViz instances.
	 * @param showHead
	 * 						Selects if line should have an arrowHead at the end.
	 *
	 */
    public DrawableLine(final BioViz parent, final boolean showHead) {
        super(TextureE.BlackPixel, parent);
        this.setZ(DisplayValues.DEFAULT_LINE_DEPTH);
        arrowHead = new DrawableArrowHead(parent);
        this.showHead = showHead;
    }

	/**
	 * Draws the line on the biochip.
	 */
    public void draw() {
        Color col = this.getColor();
        col.a = 1f;
        Vector2 toTarget = from.cpy().sub(to);
        final float len = toTarget.len();
        final DrawableAssay assay = viz.currentAssay;
        setX(assay.xCoordOnScreen((to.x + from.x) / 2f));
        setY(assay.yCoordOnScreen((to.y + from.y) / 2f));
        setScaleX(assay.getSmoothScale() * len);
        setScaleY(3f);
				float rotation = (float) (Math.atan2(toTarget.y, toTarget.x) *
					(180f / Math.PI));
        setRotation(rotation);
        setColorImmediately(col);

        if (showHead) {

					arrowHead.setColorImmediately(col);
					arrowHead.setX(assay.xCoordOnScreen(to.x));
					arrowHead.setY(assay.yCoordOnScreen(to.y));

					// the file has a 2 to 1 ratio
					arrowHead.setScaleX(assay.getSmoothScale() * 0.6f);
					arrowHead.setScaleY(assay.getSmoothScale() * 0.3f);

					// the image points into the opposite direction
					// therefore we rotate it by 180 degrees
					arrowHead.setRotation(rotation + 180f);
					arrowHead.draw();
				}
        super.draw();
    }
}
