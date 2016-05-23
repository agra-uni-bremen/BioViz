package de.bioviz.structures;

import de.bioviz.ui.BDisplayOptions;
import de.bioviz.ui.DrawableCircuit;
import de.bioviz.ui.DrawableField;
import de.bioviz.ui.TextureE;
import de.bioviz.util.Pair;

/**
 * Specialization of a DrawableField for sinks
 * @author Oliver Keszocze
 */
public class DrawableSink extends DrawableField {
	/**
	 * \copydoc DrawableField::DrawableField()
	 *
	 * Note that the field must be of type Sink!
	 */
	public DrawableSink(final Sink sink, final DrawableCircuit parent) {
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
