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

import de.bioviz.structures.Dispenser;
import de.bioviz.util.Pair;

import java.util.ArrayList;

import static de.bioviz.ui.BDisplayOptions.DispenserFluidID;
import static de.bioviz.ui.BDisplayOptions.DispenserFluidName;
import static de.bioviz.ui.BDisplayOptions.DispenserIcon;

/**
 * Specialization of a DrawableField for dispensers.
 *
 * @author Oliver Keszocze
 */
public class DrawableDispenser extends DrawableField {

	/**
	 * \copydoc DrawableField::DrawableField()
	 * <p>
	 * Note that the field must be of type Dispenser!
	 *
	 * @param dispenser
	 * 		The dispenser data object this drawable represents.
	 * @param parent
	 * 		The parent circuit, i.e. the circuit this dispenser belongs to.
	 */
	public DrawableDispenser(final Dispenser dispenser,
							 final DrawableAssay parent) {
		super(dispenser, parent);
	}

	/**
	 * @copydoc DrawableField::getMsgTexture()
	 */
	@Override
	public Pair<String, TextureE> getMsgTexture() {
		String fieldHUDMsg = null;
		TextureE texture = TextureE.GridMarker;
		if (getOption(DispenserIcon)) {
			texture = TextureE.Dispenser;
		}

		int fluidID = ((Dispenser) field).fluidID;
		ArrayList<String> msgs = new ArrayList<>();

		if (getOption(DispenserFluidID)) {
			msgs.add(Integer.toString(fluidID));
		}
		if (getOption(DispenserFluidName)) {
			String fluidName =
					getParentAssay().getData().fluidType(fluidID);
			if (fluidName != null) {
				msgs.add(fluidName);
			}
		}


		fieldHUDMsg = String.join(" - ", msgs);
		return Pair.mkPair(fieldHUDMsg, texture);
	}
}
