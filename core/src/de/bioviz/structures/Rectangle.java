package de.bioviz.structures;

import java.util.ArrayList;

/**
 * Created by keszocze on 22.07.15.
 */
public class Rectangle {

    int x1,x2,y1,y2;

    public Point fstCorner() {
        return new Point(x1,y1);
    }
    public Point sndCorner() {
        return new Point(x2,y2);
    }

    public Rectangle(Point p1, Point p2) {
        this(p1.fst,p1.snd,p2.fst,p2.snd);
    }

    public Rectangle(int x1, int y1, int x2, int y2) {
        this.x1=x1;
        this.x2=x2;
        this.y1=y1;
        this.y2=y2;
    }

    public boolean contains(Point p) {
        return contains(p.fst, p.snd);
    }

    public boolean contains(int x, int y) {
        int minX = Math.min(x1,x2);
        int minY = Math.min(y1, y2);
        int maxX = Math.max(x1,x2);
        int maxY = Math.max(y1,y2);

        return minX <= x && x <= maxX && minY <= y && y <= maxY;
    }

    public ArrayList<Point> positions() {
        int minX = Math.min(x1,x2);
        int minY = Math.min(y1, y2);
        int maxX = Math.max(x1,x2);
        int maxY = Math.max(y1,y2);

        ArrayList<Point> result = new ArrayList<Point>();

        for (int x = minX; x<=maxX; x++) {
            for (int y = minY; y<= maxY; y++) {
                result.add(new Point(x,y));
            }
        }

        return result;
    }

    public String toString() {
        return "Rect["+ new Point(x1,y1) + " " + new Point(x2,y2)+"]";
    }



}
