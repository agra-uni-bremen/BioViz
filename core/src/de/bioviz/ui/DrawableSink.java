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
