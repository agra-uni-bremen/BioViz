package de.bioviz.util;

import java.util.Random;

import de.bioviz.ui.Color;

public class ColorCalculator {

	/**
	 * Not really needed.
	 */
	private ColorCalculator() {
		
	}
	
	public static Color calculateColor(int num, int total) {
		Color result = new Color();
		
		Random calcRnd = new Random(num);
	    float hue = (float)num / (float)total;
	    float saturation = calcRnd.nextFloat() / 2f + 0.45f;
	    float lightness = calcRnd.nextFloat() / 2f + 0.45f;
	    
	    float[] vals = hslToRgb(hue, saturation, lightness);
	    result.r = vals[0];
	    result.g = vals[1];
	    result.b = vals[2];
		
		return result;
	}

	public static float[] rgbToHsl(float r, float g, float b){
	    float max = Math.max(Math.max(r, g), b);
	    float min = Math.min(Math.min(r, g), b);
	    float h, s, l;
	    h = (max + min) / 2;
	    s = h;
	    l = h;

	    if (max == min){
	        h = s = 0; // achromatic
	    } else {
	        float d = max - min;
	        s = l > 0.5 ? d / (2 - max - min) : d / (max + min);
	        if (max == r) {
	        	h = (g - b) / d + (g < b ? 6 : 0);
	        } else if (max == g) {
	            h = (b - r) / d + 2;
	        } else if (max == b) {
	            h = (r - g) / d + 4;
	        } else {
	        	throw new RuntimeException
	        		("max is not one of the original values");
	        }
	        h /= 6;
	    }

	    return new float[]{h, s, l};
	}
	
	public static float[] hslToRgb(float h, float s, float l){
	    float r, g, b;

	    if(s == 0){
	        r = l;
	        g = l;
	        b = l; // achromatic
	    } else {
	        float q = l < 0.5 ? l * (1 + s) : l + s - l * s;
	        float p = 2 * l - q;
	        r = hue2rgb(p, q, h + 1f/3f);
	        g = hue2rgb(p, q, h);
	        b = hue2rgb(p, q, h - 1f/3f);
	    }

	    return new float[] {r, g, b};
	}
	
	private static float hue2rgb(float p, float q, float t){
		if(t < 0f) t += 1;
        if(t > 1f) t -= 1;
        if(t < 1f/6f) return p + (q - p) * 6 * t;
        if(t < 1f/2f) return q;
        if(t < 2f/3f) return p + (q - p) * (2/3 - t) * 6;
        return p;
	}

}
