package de.bioviz.structures;

/**
 * Created by keszocze on 27.07.15.
 */
public class Source {

	public final Point startPosition;
	public final int dropletID;
	public final int spawnTime;

	public Source(int dropletID, Point pos) {
		this(dropletID,pos,1);
	}

	public Source(int dropletID, Point pos, int spawnTime) {
		this.dropletID=dropletID;
		this.startPosition=pos;
		this.spawnTime=spawnTime;
	}
}
