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

import de.bioviz.structures.Sink;
import de.bioviz.util.Pair;

/**
 * Specialization of a DrawableField for sinks.
 * @author Oliver Keszocze
 */
public class DrawableSink extends DrawableField {
	/**
	 * \copydoc DrawableField::DrawableField()
	 *
	 * Note that the field must be of type Sink!
	 *
	 * @param sink The sink object that is to be drawn.
	 * @param parent The parent circuit.
	 */
	public DrawableSink(final Sink sink, final DrawableAssay parent) {
		super(sink, parent);
	}

	/**
	 * @copydoc DrawableField::getMsgTexture()
	 */
	@Override
	public Pair<String, TextureE> getMsgTexture() {
		TextureE texture = TextureE.GridMarker;
		if (getOption(BDisplayOptions.SinkIcon)) {
			texture = TextureE.Sink;
		}
		return Pair.mkPair(null, texture);
	}
}
