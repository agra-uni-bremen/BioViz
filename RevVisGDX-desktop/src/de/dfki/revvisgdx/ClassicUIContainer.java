package de.dfki.revvisgdx;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import com.badlogic.gdx.Files;
import com.badlogic.gdx.backends.lwjgl.LwjglAWTCanvas;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

import de.dfki.revvisgdx.buttons.FunctionButton;

public class ClassicUIContainer extends JFrame {

	public ClassicUIContainer() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		final Container container = getContentPane();
		container.setLayout(new BorderLayout());
		
		RevVisGDX revVis = new RevVisGDX();

		LwjglAWTCanvas canvas = new LwjglAWTCanvas(revVis, false);
		
		
		JPanel panel = new JPanel();
		panel.setLayout(new FlowLayout());
		panel.setPreferredSize(new Dimension(256, 600));

		JLabel label = new JLabel("<html><body>Totally classic<br/>UI elements<br/>for Olli &lt;3</body></html>");

		JButton defaultButton = new JButton();
		defaultButton.setText("  Default Preset  ");
		defaultButton.addActionListener(new ActionListener() { 
		    public void actionPerformed(ActionEvent e) { 
		        Presets.setDefault();
		    } 
		});
		
		JButton constGarbage = new JButton();
		constGarbage.setText("Const as Garbage");
		constGarbage.addActionListener(new ActionListener() { 
		    public void actionPerformed(ActionEvent e) { 
		        Presets.setConstGarbage();
		    } 
		});
		
		JButton boxesAndUsage = new JButton();
		boxesAndUsage.setText("Boxes and Usage");
		boxesAndUsage.addActionListener(new ActionListener() { 
		    public void actionPerformed(ActionEvent e) { 
		        Presets.setBoxesAndUsage();
		    } 
		});
		
		JButton colourizedUsage = new JButton();
		colourizedUsage.setText("Colourized Usage");
		colourizedUsage.addActionListener(new ActionListener() { 
		    public void actionPerformed(ActionEvent e) { 
		        Presets.setColourizedUsage();
		    } 
		});
		
		JButton greyNeighboursWithBlackTargets = new JButton();
		greyNeighboursWithBlackTargets.setText("Black Targets");
		greyNeighboursWithBlackTargets.addActionListener(new ActionListener() { 
		    public void actionPerformed(ActionEvent e) { 
		        Presets.setGreyNeighboursWithBlackTargets();
		    } 
		});
		
		JButton colourizeLineType = new JButton();
		colourizeLineType.setText("Colourized Line Type");
		colourizeLineType.addActionListener(new ActionListener() { 
		    public void actionPerformed(ActionEvent e) { 
		        Presets.setColourizeLineType();
		    } 
		});
		
		JButton movingRuleBoxOverlay = new JButton();
		movingRuleBoxOverlay.setText("Moving Rule Box Overlay");
		movingRuleBoxOverlay.addActionListener(new ActionListener() { 
		    public void actionPerformed(ActionEvent e) { 
		        Presets.setMovingRuleBoxOverlay();
		    } 
		});
		
		JButton movingRuleColoured = new JButton();
		movingRuleColoured.setText("Moving Rule Coloured");
		movingRuleColoured.addActionListener(new ActionListener() { 
		    public void actionPerformed(ActionEvent e) { 
		        Presets.setMovingRuleColoured();
		    } 
		});
		
		JButton colourizeUsageAbsolute = new JButton();
		colourizeUsageAbsolute.setText("Colourized Usage Abs");
		colourizeUsageAbsolute.addActionListener(new ActionListener() { 
		    public void actionPerformed(ActionEvent e) { 
		        Presets.setColourizeUsageAbsolute();
		    } 
		});
		
		JButton movingRuleColouredAbsolute = new JButton();
		movingRuleColouredAbsolute.setText("Moving Rule Coloured Abs");
		movingRuleColouredAbsolute.addActionListener(new ActionListener() { 
		    public void actionPerformed(ActionEvent e) { 
		        Presets.setMovingRuleColouredAbsolute();
		    } 
		});
		
		
		JButton loadFile = new JButton();
		loadFile.setText("  Load new File  ");
		loadFile.addActionListener(new ActionListener() { 
		    public void actionPerformed(ActionEvent e) { 
		    	RevVisGDX.loadNewFile();
		    } 
		});

		
		panel.add(label);
		panel.add(loadFile);
		panel.add(defaultButton);
		panel.add(constGarbage);
		panel.add(boxesAndUsage);
		panel.add(colourizedUsage);
		panel.add(greyNeighboursWithBlackTargets);
		panel.add(colourizeLineType);
		panel.add(movingRuleBoxOverlay);
		panel.add(movingRuleColoured);
		panel.add(colourizeUsageAbsolute);
		panel.add(movingRuleColouredAbsolute);
		
		container.add(panel, BorderLayout.WEST);
		container.add(canvas.getCanvas(), BorderLayout.CENTER);

		pack();
		setVisible(true);
		setSize(800, 600);
	}

	public static void main(String[] args) {

		JFrame frame = new ClassicUIContainer();

//		JPanel panel = new JPanel();
//		panel.setLayout(new FlowLayout());
//
//		JLabel label = new JLabel("This is a label!");
//
//		JButton button = new JButton();
//		button.setText("Press me");
//
//		panel.add(label);
//		panel.add(button);
//
//		frame.add(panel);
//		frame.setSize(300, 300);
//		frame.setLocationRelativeTo(null);
//		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//		frame.pack();
//		frame.setVisible(true);
	}
}
