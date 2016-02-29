package de.bioviz.ui;

/**
 * Custom color class to bypass some of libgdx's builtin limitations. Currently,
 * the most pressing point was that libgdx autoclamps values after all
 * operations, limiting values at all times to the range of 0 to 1 (which is
 * kind of a bummer if you want to calculate something like an average color).
 * @author jannis
 *
 */
public class Color {

	public float a, r, g, b;
	
	public Color() {
		// TODO Auto-generated constructor stub
	}
	
	public Color(float r, float g, float b, float a) {
		this.r = r;
		this.g = g;
		this.b = b;
		this.a = a;
	}
	
	public Color(com.badlogic.gdx.graphics.Color c) {
		this.r = c.r;
		this.g = c.r;
		this.b = c.b;
		this.a = c.a;
	}
	
	public Color mul(Color c) {
		this.a *= c.a;
		this.r *= c.r;
		this.g *= c.g;
		this.b *= c.b;
		return this;
	}
	
	public Color mul(Float f) {
		this.a *= f;
		this.r *= f;
		this.g *= f;
		this.b *= f;
		return this;
	}
	
	public Color add(Color c) {
		this.a += c.a;
		this.r += c.r;
		this.g += c.g;
		this.b += c.b;
		return this;
	}
	
	public Color add(com.badlogic.gdx.graphics.Color c) {
		this.a += c.a;
		this.r += c.r;
		this.g += c.g;
		this.b += c.b;
		return this;
	}
	
	public Color clamp() {
		this.a = Math.min(1, Math.max(0, a));
		this.r = Math.min(1, Math.max(0, r));
		this.g = Math.min(1, Math.max(0, g));
		this.b = Math.min(1, Math.max(0, b));
		return this;
	}
	
	public Color cpy() {
		return new Color(r, g, b, a);
	}
	
	public com.badlogic.gdx.graphics.Color buildGdxColor() {
		return new com.badlogic.gdx.graphics.Color(r, g, b, a);
	}

}
