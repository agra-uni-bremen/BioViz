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

			boolean showHead = false;

			// show arrowhead only when not at start or end of the assay
			if (parentAssay.getCurrentTime() > 1 &&
					parentAssay.getCurrentTime() < parentAssay.getData().getMaxT()) {
				showHead = true;
			}

			// Search for all Droplets that
			List<DrawableDroplet> netDroplets = new ArrayList<>();
			parentAssay.getDroplets().stream().filter(
					d -> net.containsDroplet(d.droplet)).
					forEach(netDroplets::add);

			if (!netDroplets.isEmpty()) {
				for (final DrawableDroplet droplet : netDroplets) {
					DrawableLine toTarget = new DrawableLine(droplet.viz, showHead);
					DrawableLine fromSource = new DrawableLine(droplet.viz, showHead);

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
					new DrawableLine(parentAssay.getParent(), true);
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
