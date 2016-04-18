package de.bioviz.desktop;

import de.bioviz.ui.BioViz;

import javax.swing.*;
import java.awt.*;


/**
 * Simple window for choosing the animation speed of the droplets.
 * <p>
 * Unintuitively, a larger value means a slower movement as the slider
 * represents the animation duration.
 *
 * @author jannis
 */
public class PreferencesWindow extends JFrame {

	/**
	 *
	 * @author jannis
	 * @throws HeadlessException
	 */
	public PreferencesWindow() throws HeadlessException {
		super("Preferences");


		final int minAnimationDuration = 0;
		final int maxAnimationDuration = 1000;
		final int defaultAnimationDuration = 500;

		final int rows = 0;
		final int columns = 2;
		final int horizontalGap = 4;
		final int verticalGap = 4;

		GridLayout layout =
				new GridLayout(rows, columns, horizontalGap, verticalGap);
		this.setLayout(layout);

		this.add(new JLabel("Animation duration in ms"));
		JSlider animSlider =
				new JSlider(SwingConstants.HORIZONTAL, minAnimationDuration,
							maxAnimationDuration,
							defaultAnimationDuration);
		animSlider.addChangeListener(
				e -> {
					BioViz.setAnimationDuration(animSlider.getValue());
				});
		this.add(animSlider);

		pack();
		setVisible(true);
		final int xSize = 320;
		final int ySize = 240;
		setSize(xSize, ySize);
	}

}
