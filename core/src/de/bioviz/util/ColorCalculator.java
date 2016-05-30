package de.bioviz.util;

import de.bioviz.ui.Color;

/**
 * Some utility methods for colors. The constructor is private as the idea is to
 * simply provide some static methods that help people with color calculation
 * stuff.
 *
 * @author Jannis Stoppe
 */
public final class ColorCalculator {

    /**
     * Not really needed.
     */
    private ColorCalculator() {
    }

    /**
     * Calculates the num-th distinct color in a set of total colors.
     *
     * @param num   the number of the color to be calculated, should be < total
     * @param total the total amount of colors
     * @return the num-th distinct color
     */
    public static Color calculateColor(final int num, final int total) {
        Color result = new Color();

        float hue = (float) num / (float) total;
        float saturation = 1f;
        float lightness = 1f;

        float[] vals = hslToRgb(hue, saturation, lightness);
        result.r = vals[0];
        result.g = vals[1];
        result.b = vals[2];

        return result;
    }

    /**
     * Converts rgb values to a float[3] hsl array.
     *
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

    //http://stackoverflow.com/questions/7896280/converting-from-hsv-hsb-in
    // -java-to-rgb-without-using-java-awt-color-disallowe

    // TODO Jannis is this converting HSV instead of HSL?
    /**
     * Conververts HSL to RGB.
     *
     * @param hue The hue value
     * @param saturation The saturation value
     * @param value
     * @return
     */
    private static float[] hslToRgb(
            final float hue,
            final float saturation,
            final float value) {

        float hueInternal=hue;
        while (hue >= 1) {
            hueInternal -= 1;
        }
        while (hue < 0) {
            hueInternal += 1;
        }
        int h = (int) (hueInternal * 6);
        float f = hueInternal * 6 - h;
        float p = value * (1 - saturation);
        float q = value * (1 - f * saturation);
        float t = value * (1 - (1 - f) * saturation);

        switch (h) {
            case 0:
                return new float[]{value, t, p, 1};
            case 1:
                return new float[]{q, value, p, 1};
            case 2:
                return new float[]{p, value, t, 1};
            case 3:
                return new float[]{p, q, value, 1};
            case 4:
                return new float[]{t, p, value, 1};
            case 5:
                return new float[]{value, p, q, 1};
            default:
                throw new RuntimeException(
                        "Something went wrong when converting from HSV to " +
                                "RGB. Input was " + hueInternal + ", " +
                                saturation + ", " + value);
        }
    }
}
