package de.bioviz.desktop;

import java.awt.GraphicsConfiguration;
import java.awt.GridLayout;
import java.awt.HeadlessException;


import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JSlider;
import javax.swing.SwingConstants;


import com.badlogic.gdx.Gdx;
import de.bioviz.ui.BioViz;


public class PreferencesWindow extends JFrame {
	
	public PreferencesWindow() throws HeadlessException {
		super("Preferences");
		
		GridLayout layout = new GridLayout(0, 2, 4, 4);
		this.setLayout(layout);
		
		this.add(new JLabel("Animation duration in ms"));
		JSlider animSlider =
				new JSlider(SwingConstants.HORIZONTAL, 0, 1000, 500);
		animSlider.addChangeListener(
				e -> {BioViz.setAnimationDuration(animSlider.getValue());});
		this.add(animSlider);
		
		pack();
		setVisible(true);
		setSize(
				Gdx.graphics.getDesktopDisplayMode().width / 2,
				Gdx.graphics.getDesktopDisplayMode().height / 2
				);
	}

}
