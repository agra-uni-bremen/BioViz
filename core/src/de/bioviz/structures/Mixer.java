package de.bioviz.structures;

import de.bioviz.util.Pair;

/**
 * Created by keszocze on 17.08.15.
 */
public class Mixer {

	public final int id;
	public final Rectangle positions;
	public final Pair<Integer,Integer> timing;

	public Mixer(int id, Rectangle pos, Pair<Integer,Integer> timing) {
		this.id=id;
		this.positions=pos;
		this.timing=timing;
	}

}
