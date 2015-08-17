package de.bioviz.structures;

import de.bioviz.util.Pair;

/**
 * Created by keszocze on 17.08.15.
 */
public class Mixer {

	public final int id;
	public final Rectangle positions;
	public final Range timing;

	public Mixer(int id, Rectangle pos, Range timing) {
		this.id=id;
		this.positions=pos;
		this.timing=timing;
	}

}
