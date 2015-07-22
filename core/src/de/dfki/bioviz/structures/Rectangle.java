package de.dfki.bioviz.structures;

import org.antlr.v4.runtime.misc.Pair;

/**
 * Created by keszocze on 22.07.15.
 */
public class Rectangle {
    public Pair<Integer, Integer> fstCorner, sndCorner;

    public Rectangle(int x1, int y1, int x2, int y2) {
        fstCorner = new Pair<Integer,Integer>(x1,y1);
        sndCorner = new Pair<Integer,Integer>(x2,y2);
    }

}
