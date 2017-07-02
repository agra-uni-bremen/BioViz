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

package de.bioviz.desktop;

import de.bioviz.ui.BioViz;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JSlider;
import javax.swing.JButton;
import javax.swing.SwingConstants;


import java.awt.HeadlessException;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.WindowEvent;


/**
 * Simple window for choosing the animation speed of the droplets.
 * <p>
 * Unintuitively, a larger value means a slower movement as the slider
 * represents the animation duration.
 *
 * @author Jannis Stoppe, Oliver Keszocze
 */
class PreferencesWindow extends JFrame {

	/**
	 * Used to handle feedback for the user about the program behaviour (and of
	 * course the developer, too). Anything logged using this instance will
	 * report as originating from the DesktopLauncher class.
	 */
	private static Logger logger =
			LoggerFactory.getLogger(PreferencesWindow.class);

	/**
	 * @param viz
	 * 		Reference to the {@see BioViz} instance that opened the window.
	 * @throws HeadlessException
	 */
	PreferencesWindow(final BioViz viz) {
		super("Preferences");


		final int minAnimationDuration = 0;
		final int maxAnimationDuration = 1000;
		final int defaultAnimationDuration = BioViz.getAnimationDuration();

		final int minDurationBetweenSteps = maxAnimationDuration;
		final int maxDurationBetweenSteps = maxAnimationDuration * 10;
		final int defaultDurationBetweenSteps =
				Math.round(viz.currentAssay.getAutoDelay() * 1000);

		try {
			this.setIconImage(
					ImageIO.read(DesktopLauncher.getFileFromStream("/" + viz
							.getApplicationIcon()
							.path())));
		} catch (final Exception e) {
			logger.error("Could not set application icon: " + e.getMessage());
		}

		GridBagLayout layout = new GridBagLayout();
		this.setLayout(layout);

		GridBagConstraints c = new GridBagConstraints();

		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 0;
		c.gridy = 0;
		this.add(new JLabel("Time between steps:"), c);


		c.gridx = 1;
		c.gridy = 0;
		c.anchor = GridBagConstraints.LINE_END;
		JLabel timestepLabel =
				new JLabel(Float.toString(defaultDurationBetweenSteps
										  / 1000f));
		this.add(timestepLabel, c);


		c.gridx = 2;
		c.gridy = 0;
		c.anchor = GridBagConstraints.LINE_START;
		this.add(new JLabel("s"), c);

		JSlider animSlider =
				new JSlider(SwingConstants.HORIZONTAL, minDurationBetweenSteps,
							maxDurationBetweenSteps,
							defaultDurationBetweenSteps);
		animSlider.addChangeListener(
				e -> {
					logger.info("Current speed: {}",
								viz.currentAssay.getAutoDelay());
					int sliderVal = animSlider.getValue();
					float newSpeed = sliderVal / 1000f;
					logger.info("sliderVal: {}, newSpeed: {}", sliderVal,
								newSpeed);
					viz.currentAssay.setAutoDelay(newSpeed);
					timestepLabel.setText(Float.toString(newSpeed));
				});

		c.gridx = 0;
		c.gridy = 1;
		c.gridwidth = 3;
		c.anchor = GridBagConstraints.CENTER;
		this.add(animSlider, c);


		c.gridx = 0;
		c.gridy = 2;
		c.gridwidth = 1;
		this.add(new JLabel("Animation duration:"), c);


		c.gridx = 1;
		c.gridy = 2;
		c.anchor = GridBagConstraints.LINE_END;
		JLabel animLabel =
				new JLabel(Integer.toString(defaultAnimationDuration));
		this.add(animLabel, c);


		c.gridx = 2;
		c.gridy = 2;
		c.anchor = GridBagConstraints.LINE_START;
		this.add(new JLabel("ms"), c);

		JSlider dropMovementSpeedSlider =
				new JSlider(SwingConstants.HORIZONTAL, minAnimationDuration,
							maxAnimationDuration,
							defaultAnimationDuration);
		dropMovementSpeedSlider.addChangeListener(
				e -> {
					int duration = dropMovementSpeedSlider.getValue();
					BioViz.setAnimationDuration(duration);
					animLabel.setText(Integer.toString(duration));
				});

		c.gridx = 0;
		c.gridy = 3;
		c.gridwidth = 3;
		c.anchor = GridBagConstraints.CENTER;
		this.add(dropMovementSpeedSlider, c);

		c.gridx = 0;
		c.gridy = 4;
		c.gridwidth = 1;
		this.add(new JLabel("Font transparency:"), c);
		JSlider fontTransparencySlider =
				new JSlider(SwingConstants.HORIZONTAL, 0,
							100,
							50);
		fontTransparencySlider.addChangeListener(
				e -> viz.messageCenter.setDefaultTextTransparency(
								fontTransparencySlider.getValue() / 100f)
		);
		c.gridx = 0;
		c.gridy = 5;
		c.gridwidth = 3;
		this.add(fontTransparencySlider, c);

		JButton closeButton = new JButton("Close");
		closeButton.addActionListener(e -> this.dispatchEvent(
				new WindowEvent(this, WindowEvent.WINDOW_CLOSING)));
		c.gridx = 0;
		c.gridy = 7;
		c.insets = new Insets(10, 0, 0, 0);
		this.add(closeButton, c);

		pack();
		setVisible(true);
		final int xSize = 320;
		final int ySize = 240;
		setSize(xSize, ySize);

	}

}
