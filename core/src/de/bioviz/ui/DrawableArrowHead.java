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

package de.bioviz.ui;

/**
 * This class implements an arrowhead that can be used e.g. for DrawableLines.
 *
 * @author Maximilian Luenert
 */
public class DrawableArrowHead extends DrawableSprite {

	/**
	 * This constructor checks if the given texture has been loaded before and
	 * does so if that's not the case. A sprite is initialized accordingly.
	 *
	 * @param parent
	 * 					the parent BioViz
	 */
	public DrawableArrowHead(final BioViz parent) {
		super(TextureE.ArrowHead, parent);
		this.setZ(DisplayValues.DEFAULT_LINE_DEPTH);
	}

	/**
	 * Draws the arrowHead on the biochip.
	 */
	public void draw() {
		super.draw();
	}
}
