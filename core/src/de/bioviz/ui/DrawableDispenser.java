package de.bioviz.ui;

import de.bioviz.structures.Dispenser;
import de.bioviz.util.Pair;

import java.util.ArrayList;

import static de.bioviz.ui.BDisplayOptions.DispenserFluidID;
import static de.bioviz.ui.BDisplayOptions.DispenserFluidName;
import static de.bioviz.ui.BDisplayOptions.DispenserIcon;

/**
 * @author Oliver Keszocze
 */
public class DrawableDispenser extends DrawableField {

	public DrawableDispenser(final Dispenser dispenser, final DrawableCircuit parent) {
		super(dispenser,parent);
	}

	@Override
	public Pair<String, TextureE> getMsgTexture() {
		String fieldHUDMsg = null;
		TextureE texture = TextureE.GridMarker;
		if (getOption(DispenserIcon)) {
			texture = TextureE.Dispenser;
		}

		int fluidID = ((Dispenser)field).fluidID;
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
		System.out.println("Correct getMsgTextre()");
		return Pair.mkPair(fieldHUDMsg,texture);
	}
}
