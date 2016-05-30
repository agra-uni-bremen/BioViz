package de.bioviz.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;

public class DrawableLine extends DrawableSprite {

    public Vector2 from;
    public Vector2 to;

    public DrawableLine(final BioViz parent) {
        super(TextureE.BlackPixel, parent);
        this.setZ(DisplayValues.DEFAULT_LINE_DEPTH);
    }

    public void draw() {
        Color col = this.getColor();
        Vector2 toTarget = from.cpy().sub(to);
        final float len = toTarget.len();
        setX(viz.currentCircuit.xCoordOnScreen((to.x + from.x) / 2f));
        setY(viz.currentCircuit.yCoordOnScreen((to.y + from.y) / 2f));
        setScaleX(viz.currentCircuit.getSmoothScale() * len);
        setScaleY(2f);
        setRotation((float) (Math.atan2(toTarget.y, toTarget.x) *
                (180f / Math.PI)));
        setColorImmediately(col);
        super.draw();
    }
}
