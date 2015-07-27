package de.dfki.bioviz.structures;

/**
 * Created by keszocze on 27.07.15.
 */
public class Source {

	Point startPosition;
	int dropletID;
	int spawnTime;

	public Source(int dropletID, Point pos) {
		this(dropletID,pos,1);
	}

	public Source(int dropletID, Point pos, int spawnTime) {
		this.dropletID=dropletID;
		this.startPosition=pos;
		this.spawnTime=spawnTime;
	}
}
