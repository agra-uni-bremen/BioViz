/*
 * BioViz, a visualization tool for digital microfluidic biochips (DMFB).
 *
 * Copyright (c) 2017 Oliver Keszocze, Jannis Stoppe, Maximilian Luenert
 *
 * This file is part of BioViz.
 *
 * BioViz is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free
 * Software Foundation, either version 2 of the License, or (at your option)
 * any later version.
 *
 * BioViz is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE.
 *
 * See the GNU General Public License for more details. You should have
 * received a copy of the GNU
 * General Public License along with BioViz.
 * If not, see <http://www.gnu.org/licenses/>.
 */

package de.bioviz.ui;

/**
 * Custom color class to bypass some of libgdx's builtin limitations. Currently,
 * the most pressing point was that libgdx autoclamps values after all
 * operations, limiting values at all times to the range of 0 to 1 (which is
 * kind of a bummer if you want to calculate something like an average color).
 *
 * @author Jannis Stoppe
 */
@SuppressWarnings("checkstyle:visibilitymodifier")
public class Color {

	/**
	 * The alpha (i.e. transparency value).
	 */
	public float a;

	/**
	 * The red channel.
	 */
	public float r;

	/**
	 * The green channel.
	 */
	public float g;

	/**
	 * The blue channel.
	 */
	public float b;

	/**
	 * Creates an empty color, all values set to 0.
	 */
	public Color() {
		r = 0;
		g = 0;
		g = 0;
		a = 0;
	}

	/**
	 * Creates a new color based on the values being passed on.
	 *
	 * @param r
	 * 		red
	 * @param g
	 * 		green
	 * @param b
	 * 		blue
	 * @param a
	 * 		alpha
	 */
	public Color(final float r, final float g, final float b, final float a) {
		this.r = r;
		this.g = g;
		this.b = b;
		this.a = a;
	}

	/**
	 * Creates a new color based on the libgdx color instance.
	 *
	 * @param c
	 * 		the color to create a new color from
	 */
	public Color(final com.badlogic.gdx.graphics.Color c) {
		this.r = c.r;
		this.g = c.r;
		this.b = c.b;
		this.a = c.a;
	}

	/**
	 * Multiplies a color with another color (by channel). Notice that this
	 * *happens in place*, i.e. this instance is being altered by this method.
	 * The return value is used to be able to chain operations.
	 *
	 * @param c
	 * 		the color to multiply this color with.
	 * @return this instance for chaining operations.
	 */
	public Color mul(final Color c) {
		this.a *= c.a;
		this.r *= c.r;
		this.g *= c.g;
		this.b *= c.b;
		return this;
	}

	/**
	 * Multiplies this color by a given factor. Notice that this *happens in
	 * place*, i.e. this instance is being altered by this method. The return
	 * value is used to be able to chain operations.
	 *
	 * @param f
	 * 		the scalar
	 * @return this color for chaining operations
	 */
	public Color mul(final Float f) {
		this.a *= f;
		this.r *= f;
		this.g *= f;
		this.b *= f;
		return this;
	}

	/**
	 * Adds another color to this color. Notice that this *happens in place*,
	 * i.e. this instance is being altered by this method. The return value is
	 * used to be able to chain operations.
	 *
	 * @param c
	 * 		the color to add to this color
	 * @return this color for chaining operations
	 */
	public Color add(final Color c) {
		this.a += c.a;
		this.r += c.r;
		this.g += c.g;
		this.b += c.b;
		return this;
	}

	/**
	 * Adds another color to this color. Notice that this *happens in place*,
	 * i.e. this instance is being altered by this method. The return value is
	 * used to be able to chain operations.
	 *
	 * @param r
	 * 		red
	 * @param g
	 * 		green
	 * @param b
	 * 		blue
	 * @param a
	 * 		alpha
	 * @return this color for chaining operations
	 */
	@SuppressWarnings("checkstyle:hiddenfield")
	public Color add(final float r,
					 final float g,
					 final float b,
					 final float a) {
		this.a += a;
		this.r += r;
		this.g += g;
		this.b += b;
		return this;
	}

	/**
	 * Adds another color to this color. Notice that this *happens in place*,
	 * i.e. this instance is being altered by this method. The return value is
	 * used to be able to chain operations.
	 *
	 * @param c
	 * 		the libgdx color instance to add to this color
	 * @return this color for chaining operations.
	 */
	public Color add(final com.badlogic.gdx.graphics.Color c) {
		this.a += c.a;
		this.r += c.r;
		this.g += c.g;
		this.b += c.b;
		return this;
	}

	/**
	 * Clamps a color to values between 0 and 1. Notice that this *happens in
	 * place*, i.e. this instance is being altered by this method. The return
	 * value is used to be able to chain operations.
	 *
	 * @return this color for chaining operations
	 */
	public Color clamp() {
		this.a = Math.min(1, Math.max(0, a));
		this.r = Math.min(1, Math.max(0, r));
		this.g = Math.min(1, Math.max(0, g));
		this.b = Math.min(1, Math.max(0, b));
		return this;
	}

	/**
	 * Creates a new copy of this color.
	 *
	 * @return a new color with the same values as this one.
	 */
	public Color cpy() {
		return new Color(r, g, b, a);
	}

	/**
	 * Creates a new libgdx color instance from the values being present in
	 * this
	 * color.
	 *
	 * @return the new libgdx color instance
	 */
	public com.badlogic.gdx.graphics.Color buildGdxColor() {
		return new com.badlogic.gdx.graphics.Color(r, g, b, a);
	}

	/**
	 * A string representation of this color.
	 *
	 * @return String representation of the color in the form $r/$g/$b/$a
	 */
	public String toString() {
		return "" + r + "/" + g + "/" + b + "/" + a;
	}
}
