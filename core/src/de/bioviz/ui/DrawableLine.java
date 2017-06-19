package de.bioviz.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;

import static de.bioviz.ui.TextureE.ArrowHead;

/**
 * A simple line segment defined by the start and end point.
 *
 * This class is used to draw the source->target lines etc.
 *
 * @author Jannis Stoppe
 */
public class DrawableLine extends DrawableSprite {

	/**
     * The starting point of the line.
     */
    public Vector2 from;

	/**
	 * The end point of the line.
	 */
    public Vector2 to;

    private DrawableArrowHead head;

	/**
	 * Creates a drawable line with no start/end points.
	 * @param parent The parent BioViz instances.
	 */
    public DrawableLine(final BioViz parent) {
        super(TextureE.BlackPixel, parent);
        this.setZ(DisplayValues.DEFAULT_LINE_DEPTH);
        head = new DrawableArrowHead(parent);
    }

	/**
	 * Draws the line on the biochip.
	 */
    public void draw() {
        Color col = this.getColor();
        Vector2 toTarget = from.cpy().sub(to);
        final float len = toTarget.len();
        setX(viz.currentAssay.xCoordOnScreen((to.x + from.x) / 2f));
        setY(viz.currentAssay.yCoordOnScreen((to.y + from.y) / 2f));
        setScaleX(viz.currentAssay.getSmoothScale() * len);
        setScaleY(3f);
				float rotation = (float) (Math.atan2(toTarget.y, toTarget.x) *
					(180f / Math.PI));
        setRotation(rotation);
        setColorImmediately(col);

				head.setColorImmediately(col);
				head.setX(viz.currentAssay.xCoordOnScreen(to.x));
				head.setY(viz.currentAssay.yCoordOnScreen(to.y));
				head.setScaleX(viz.currentAssay.getSmoothScale());
				head.setScaleY(viz.currentAssay.getSmoothScale() * 0.5f);
				head.setRotation(rotation+180f);
				head.draw();
        super.draw();
    }
}
