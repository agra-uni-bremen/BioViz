package de.dfki.bioviz;

public class DrawableField extends DrawableSprite {

	public DrawableField(float sizeX, float sizeY) {
		super("GridMarker.png", sizeX, sizeY);
	}

	public DrawableField() {
		super("GridMarker.png");
	}

	@Override
	public String generateSVG() {
		// TODO Auto-generated method stub
		return "";
	}

}
