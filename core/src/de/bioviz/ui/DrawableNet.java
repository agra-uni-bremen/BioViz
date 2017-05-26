package de.bioviz.ui;

import com.badlogic.gdx.math.Vector2;
import de.bioviz.structures.Net;
import de.bioviz.structures.Source;
import de.bioviz.util.Pair;

import java.util.ArrayList;
import java.util.List;

import static de.bioviz.ui.BDisplayOptions.LongNetIndicatorsOnFields;

/**
 * This class implements a drawableSprite for the handling of the
 * longNetIndicators.
 *
 * @author Maximilian Luenert
 */
public class DrawableNet extends DrawableSprite {

	//private static Logger LOGGER = LoggerFactory.getLogger(DrawableNet
	// .class);

	/**
	 * Stores the net.
	 */
	private Net net;

	/**
	 * Stores the DrawableAssay.
	 */
	private DrawableAssay parentAssay;

	/**
	 * Creates a drawableNet for a given Net.
	 *
	 * @param net
	 * 		the net
	 * @param parent
	 * 		the parent
	 */
	DrawableNet(final Net net, final DrawableAssay parent) {
		super(TextureE.BlackPixel, parent.getParent());
		this.parentAssay = parent;
		this.net = net;
	}

	/**
	 * Draws the lines if the corresponding displayOptions are selected.
	 */
	@Override
	public void draw() {

		if (parentAssay.getDisplayOptions()
				.getOption(BDisplayOptions.LongNetIndicatorsOnDroplets)) {

			// Search for all Droplets that
			List<DrawableDroplet> netDroplets = new ArrayList<>();
			parentAssay.getDroplets().stream().filter(
					d -> net.containsDroplet(d.droplet)).
					forEach(netDroplets::add);

			if (!netDroplets.isEmpty()) {
				for (final DrawableDroplet droplet : netDroplets) {
					DrawableLine toTarget = new DrawableLine(droplet.viz);
					DrawableLine fromSource = new DrawableLine(droplet.viz);

					setForcedLOD(1f);
					final Pair<Float, Float> targetPoint =
							droplet.droplet.getNet().getTarget().centerFloat();
					Vector2 target =
							new Vector2(targetPoint.fst, targetPoint.snd);

					// calculate source point
					final Pair<Float, Float> sourcePoint =
							droplet.droplet.getFirstPosition().centerFloat();
					Vector2 source =
							new Vector2(sourcePoint.fst, sourcePoint.snd);

					// calculate current position
					Vector2 current = new Vector2(
							droplet.smoothX + (droplet.smoothWidth - 1f) / 2f,
							droplet.smoothY - (droplet.smoothHeight - 1f) /
											  2f);

					// draw line from droplet to target
					toTarget.from = current;
					toTarget.to = target;
					toTarget.setColor(droplet.getColor().add(
							Colors.LONG_NET_INDICATORS_ON_DROPLET_DIFF));
					toTarget.draw();

					// draw line from source to droplet
					fromSource.from = source;
					fromSource.to = current;
					fromSource.setColor(droplet.getColor().sub(
							Colors.LONG_NET_INDICATORS_ON_DROPLET_DIFF));
					fromSource.draw();
				}
			}
		}

		if (parentAssay.getDisplayOptions().getOption(
				LongNetIndicatorsOnFields)) {
			DrawableLine netIndicator =
					new DrawableLine(parentAssay.getParent());
			for (final Source s : net.getSources()) {
				Pair<Float, Float> targetCenter =
						net.getTarget().centerFloat();
				Pair<Float, Float> sourceCenter =
						s.startPosition.centerFloat();

				Vector2 target =
						new Vector2(targetCenter.fst, targetCenter.snd);
				Vector2 source =
						new Vector2(sourceCenter.fst, sourceCenter.snd);

				netIndicator.from = source;
				netIndicator.to = target;
				netIndicator.setColor(
						Colors.LONG_NET_INDICATORS_ON_FIELD_COLOR);
				netIndicator.draw();
			}
		}
	}

}
