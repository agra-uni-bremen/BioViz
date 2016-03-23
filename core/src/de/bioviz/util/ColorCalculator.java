package de.bioviz.util;

import java.util.Random;

import de.bioviz.ui.Color;

/**
 * Some utility methods for colors.
 * The constructor is private as the idea is to simply provide some static
 * methods that help people with color calculation stuff.
 *
 * @author jannis
 *
 */
public final class ColorCalculator {

	/**
	 * Not really needed.
	 */
	private ColorCalculator() { }

	/**
	 * Calculates the num-th distinct color in a set of total colors.
	 * @param num the number of the color to be calculated, should be < total
	 * @param total the total amount of colors
	 * @return the num-th distinct color
	 */
	public static Color calculateColor(final int num, final int total) {
		Color result = new Color();

		Random calcRnd = new Random(num);
		float hue = (float) num / (float) total;


		float saturation = calcRnd.nextFloat() / 2f + 0.5f;
		float lightness = calcRnd.nextFloat() / 2f + 0.5f;

		float[] vals = hslToRgb(hue, saturation, lightness);
		result.r = vals[0];
		result.g = vals[1];
		result.b = vals[2];

		return result;
	}

	/**
	 * Converts rgb values to a float[3] hsl array.
	 * @param r red
	 * @param g green
	 * @param b blue
	 * @return the hsl array, float[3].
	 */
	public static float[] rgbToHsl(
			final float r,
			final float g,
			final float b) {
		float max = Math.max(Math.max(r, g), b);
		float min = Math.min(Math.min(r, g), b);
		float h;
		float s;
		float l;
		h = (max + min) / 2;
		s = h;
		l = h;

		if (max == min) {
			h = 0;
			s = 0; // achromatic
		} else {
			float d = max - min;
			if (l > 0.5f) {
				s = d / (2 - max - min);
			} else {
				s = d / (max + min);
			}
			if (max == r) {
				if (g < b) {
					h = (g - b) / d + 6;
				} else {
					h = (g - b) / d + 0;
				}
			} else if (max == g) {
				h = (b - r) / d + 2;
			} else if (max == b) {
				h = (r - g) / d + 4;
			} else {
				throw new RuntimeException(
						"max is not one of the original values");
			}
			h /= 6;
		}

		return new float[]{h, s, l};
	}

	/**
	 * Converts hsl values to rgb values.
	 * @param h hue
	 * @param s saturation
	 * @param l lightness
	 * @return a float[3] containing rgb values.
	 */
	public static float[] hslToRgb(
			final float h,
			final float s,
			final float l) {
		float r;
		float g;
		float b;

		if (s == 0) {
			r = l;
			g = l;
			b = l; // achromatic
		} else {
			float q;
			if (l < 0.5f) {
				q = l * (1 + s);
			} else {
				q = l + s - l * s;
			}
			float p = 2 * l - q;
			r = hue2rgb(p, q, h + 1f / 3f);
			g = hue2rgb(p, q, h);
			b = hue2rgb(p, q, h - 1f / 3f);
		}

		return new float[] {r, g, b};
	}

	/**
	 * Converts hue values to rgb.
	 * @param p p
	 * @param q q
	 * @param t t
	 * @return the rgb value
	 */
	private static float hue2rgb(final float p, final float q, final float t) {
		float t2 = t;
		if (t2 < 0f) {
			t2 += 1;
		}
		if (t2 > 1f) {
			t2 -= 1;
		}
		if (t2 < 1f / 6f) {
			return p + (q - p) * 6 * t2;
		} else if (t2 < 1f / 2f) {
			return q;
		} else if (t2 < 2f / 3f) {
			return p + (q - p) * (2 / 3 - t2) * 6;
		} else {
			return p;
		}
	}

}
