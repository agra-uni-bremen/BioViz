package de.bioviz.ui;

import de.bioviz.structures.Dispenser;
import de.bioviz.util.Pair;

import java.util.ArrayList;

import static de.bioviz.ui.BDisplayOptions.DispenserFluidID;
import static de.bioviz.ui.BDisplayOptions.DispenserFluidName;
import static de.bioviz.ui.BDisplayOptions.DispenserIcon;

/**
 * Specialization of a DrawableField for dispensers
 *
 * @author Oliver Keszocze
 */
public class DrawableDispenser extends DrawableField {

	/**
	 * \copydoc DrawableField::DrawableField()
	 * <p>
	 * Note that the field must be of type Dispenser!
	 */
	public DrawableDispenser(final Dispenser dispenser,
							 final DrawableCircuit parent) {
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
					getParentCircuit().getData().fluidType(fluidID);
			if (fluidName != null) {
				msgs.add(fluidName);
			}
		}


		fieldHUDMsg = String.join(" - ", msgs);
		return Pair.mkPair(fieldHUDMsg, texture);
	}
}
