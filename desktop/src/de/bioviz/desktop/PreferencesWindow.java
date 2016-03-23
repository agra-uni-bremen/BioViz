package de.bioviz.desktop;

import de.bioviz.ui.BioViz;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowEvent;


/**
 * Simple window for choosing the animation speed of the droplets.
 * <p>
 * Unintuitively, a larger value means a slower movement as the slider
 * represents the animation duration.
 *
 * @author Jannis Stoppe, Oliver Keszocze
 */
public class PreferencesWindow extends JFrame {

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
	 * @author Jannis Stoppe, Oliver Keszocze
	 */
	public PreferencesWindow(final BioViz viz) throws HeadlessException {
		super("Preferences");


		final int minAnimationDuration = 0;
		final int maxAnimationDuration = 1000;
		final int defaultAnimationDuration = 500;

		final int rows = 5;
		final int columns = 2;
		final int horizontalGap = 4;
		final int verticalGap = 4;

		GridLayout layout =
				new GridLayout(rows, columns, horizontalGap, verticalGap);
		this.setLayout(layout);

		this.add(new JLabel("Time between steps in s:"));
		JSlider animSlider =
				new JSlider(SwingConstants.HORIZONTAL, minAnimationDuration,
							maxAnimationDuration,
							defaultAnimationDuration);
		animSlider.addChangeListener(
				e -> {
					BioViz.setAnimationDuration(animSlider.getValue());
				});
		this.add(animSlider);


		this.add(new JLabel("Animation duration in ms:"));
		JSlider dropMovementSpeedSlider =
				new JSlider(SwingConstants.HORIZONTAL, minAnimationDuration,
							maxAnimationDuration,
							defaultAnimationDuration);
		dropMovementSpeedSlider.addChangeListener(
				e -> {
					BioViz.setAnimationDuration(dropMovementSpeedSlider.getValue());
				});
		this.add(dropMovementSpeedSlider);

		try {
			this.setIconImage(
					ImageIO.read(viz.getApplicationIcon().file()));
		} catch (final Exception e) {
			logger.error("Could not set application icon: " + e.getMessage());
		}


		JButton closeButton = new JButton("Close");
		closeButton.addActionListener(e -> this.dispatchEvent(
				new WindowEvent(this, WindowEvent.WINDOW_CLOSING)));
		this.add(closeButton);

		pack();
		setVisible(true);
		final int xSize = 320;
		final int ySize = 240;
		setSize(xSize, ySize);

	}

}
