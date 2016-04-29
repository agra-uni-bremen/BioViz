package de.bioviz.structures;

import de.bioviz.ui.DrawableCircuit;
import de.bioviz.ui.DrawableField;
import de.bioviz.ui.TextureE;
import de.bioviz.util.Pair;

/**
 * @author Oliver Keszocze
 */
public class DrawableSink extends DrawableField {
	public DrawableSink(final Sink sink, final DrawableCircuit parent) {
		super(sink,parent);
	}

	@Override
	public Pair<String, TextureE> getMsgTexture() {
		return Pair.mkPair(null, TextureE.Sink);
	}
}
