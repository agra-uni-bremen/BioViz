package de.bioviz.desktop;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.KeyEventDispatcher;
import java.awt.KeyboardFocusManager;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.util.Random;

import javax.imageio.ImageIO;
import javax.swing.*;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.joran.JoranConfigurator;
import ch.qos.logback.core.joran.spi.JoranException;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.backends.lwjgl.LwjglAWTCanvas;
import com.badlogic.gdx.backends.lwjgl.LwjglAWTInput;

import de.bioviz.ui.BioViz;
import de.bioviz.ui.BioVizEvent;
import de.bioviz.ui.DrawableRoute;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class DesktopLauncher extends JFrame {

	public JSlider time;
	timerCallback tc;
	loadFileCallback load_cb;
	loadedFileCallback loaded_cb;
	saveFileCallback save_cb;
	public static DesktopLauncher singleton;
	private BioViz bioViz;
	LwjglAWTCanvas canvas;
	LwjglAWTInput input;

	private static JFileChooser fileDialogs = null;
	
	private static Logger logger = LoggerFactory.getLogger(DesktopLauncher.class);
	
	/**
	 * Needed to fetch *all* pressed keys that are caught somewhere
	 * in this frame in order to pipe them through to the libgdx
	 * program within.
	 * @author jannis
	 *
	 */
	private class MyDispatcher implements KeyEventDispatcher {
		@Override
		public boolean dispatchKeyEvent(KeyEvent e) {
			if (input.getInputProcessor() == null) {
				input.setInputProcessor(bioViz.getInputProcessor());
			}
			//Additional check to avoid having events fire twice (once from here and once from libgdx)
			if (DesktopLauncher.singleton.getFocusOwner() != DesktopLauncher.singleton.canvas.getCanvas()) {
				if (e.getID() == KeyEvent.KEY_PRESSED) {
					bioViz.getInputProcessor().keyDown(translateKeyCode(e.getKeyCode()));
				} else if (e.getID() == KeyEvent.KEY_RELEASED) {
					bioViz.getInputProcessor().keyUp(translateKeyCode(e.getKeyCode()));
				} else if (e.getID() == KeyEvent.KEY_TYPED) {
					bioViz.getInputProcessor().keyTyped(e.getKeyChar());
				}
			}
			return false;
		}
	}

	public DesktopLauncher(int timeMax) {
		this(timeMax, null);
	}

	public DesktopLauncher(int timeMax, File file) {
		singleton = this;
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		final Container container = getContentPane();
		container.setLayout(new BorderLayout());
		int rnd = new Random().nextInt(21);
		if (rnd <= 9)
			this.setTitle("Olli's BioViz");
		else if (rnd <= 19)
			this.setTitle("Jannis' BioViz");
		else
			this.setTitle("Awesome BioViz");
		
		logger.debug("Starting DesktopLauncher with file \"{}\"", file);
		
		if (file == null) {
			bioViz = new BioViz();
		} else {
			bioViz = new BioViz(file);
		}
		canvas = new LwjglAWTCanvas(bioViz);
		
		/**
		 * Needed to pipe through the keyboard events to the libgdx application
		 */
		KeyboardFocusManager manager = KeyboardFocusManager.getCurrentKeyboardFocusManager();
		manager.addKeyEventDispatcher(new MyDispatcher());

		JPanel panel = new JPanel();
		panel.setLayout(new FlowLayout());
		panel.setPreferredSize(new Dimension(128, 600));


		// This text was completely useless. I leave the code here as a reference on how to add labels with some kind
		// of formatting.
//		JLabel label = new JLabel("<html><body>Totally classic<br/>UI elements<br/></body></html>");

		JButton autoplaytButton = new JButton();
		autoplaytButton.setText("Autoplay");
		autoplaytButton.setPreferredSize(new Dimension(112, autoplaytButton.getPreferredSize().height));
		autoplaytButton.addActionListener(e -> BioViz.singleton.currentCircuit.autoAdvance = !BioViz.singleton.currentCircuit.autoAdvance);
		
		JButton openButton = new JButton();
		openButton.setText("Open File");
		openButton.setPreferredSize(new Dimension(112, openButton.getPreferredSize().height));
		load_cb = new loadFileCallback();
		openButton.addActionListener(e -> load_cb.bioVizEvent());
		
		JButton saveButton = new JButton();
		saveButton.setText("Save SVG");
		saveButton.setPreferredSize(new Dimension(112, saveButton.getPreferredSize().height));
		save_cb = new saveFileCallback();
		saveButton.addActionListener(e -> save_cb.bioVizEvent());
		
		JButton zoomButton = new JButton();
		zoomButton.setText("Reset camera");
		zoomButton.setPreferredSize(new Dimension(112, zoomButton.getPreferredSize().height));
		zoomButton.addActionListener(e -> BioViz.singleton.currentCircuit.zoomExtents());

		JButton usageButton = new JButton();
		usageButton.setText("Show Cell Usage");
		usageButton.setPreferredSize(new Dimension(112, usageButton.getPreferredSize().height));
		usageButton.addActionListener(e -> BioViz.singleton.currentCircuit.toggleShowUsage());
		
		JLabel timeInfo = new JLabel("<html><body>Time</body></html>");
		
		time = new JSlider(JSlider.HORIZONTAL, 0, timeMax, 0);
		time.setPreferredSize(new Dimension(128, 64));
		time.addChangeListener(ce -> BioViz.singleton.currentCircuit.currentTime = ((JSlider) ce.getSource()).getValue());
		tc = new timerCallback(time);
		
		JLabel routeInfo = new JLabel("<html><body>Route length</body></html>");
		
		JSlider routes = new JSlider(JSlider.HORIZONTAL, 0, 32, DrawableRoute.timesteps);
		routes.setPreferredSize(new Dimension(128, 64));
		routes.addChangeListener(ce -> DrawableRoute.timesteps = ((JSlider) ce.getSource()).getValue());
		//tc = new timerCallback(time);
		
		JButton adjacencyButton = new JButton();
		adjacencyButton.setText("Adjacency");
		adjacencyButton.setPreferredSize(new Dimension(112, adjacencyButton.getPreferredSize().height));
		adjacencyButton.addActionListener(e -> BioViz.singleton.currentCircuit.toggleHighlightAdjacency());


		JButton displayDropletIDsButton = new JButton();
		displayDropletIDsButton.setText("Drop IDs");
		displayDropletIDsButton.setPreferredSize(new Dimension(112, displayDropletIDsButton.getPreferredSize().height));
		displayDropletIDsButton.addActionListener(e -> BioViz.singleton.currentCircuit.toggleDisplayDropletIDs());

		JButton displayFluidIDsButton = new JButton();
		displayFluidIDsButton.setText("Fluid IDs");
		displayFluidIDsButton.setPreferredSize(new Dimension(112, displayFluidIDsButton.getPreferredSize().height));
		displayFluidIDsButton.addActionListener(e -> BioViz.singleton.currentCircuit.toggleDisplayFluidIDs());

		JButton pinButton = new JButton();
		pinButton.setText("Pins");
		pinButton.setPreferredSize(new Dimension(112, pinButton.getPreferredSize().height));
		pinButton.addActionListener(e -> BioViz.singleton.currentCircuit.toggleShowPins());

		// see comment above
//		panel.add(label);
		panel.add(autoplaytButton);
		panel.add(openButton);
		panel.add(saveButton);
		panel.add(zoomButton);
		panel.add(adjacencyButton);
		panel.add(usageButton);
		panel.add(displayDropletIDsButton);
		panel.add(displayFluidIDsButton);
		panel.add(pinButton);
		panel.add(timeInfo);
		panel.add(time);
		panel.add(routeInfo);
		panel.add(routes);
		
		
		input = new LwjglAWTInput(canvas.getCanvas());
		
		container.add(panel, BorderLayout.WEST);
		container.add(canvas.getCanvas(), BorderLayout.CENTER);
		
		loaded_cb = new loadedFileCallback();
		BioViz.singleton.addLoadedFileListener(loaded_cb);
		
		save_cb = new saveFileCallback();
		BioViz.singleton.addSaveFileListener(save_cb);

		try {
			this.setIconImage(ImageIO.read(BioViz.singleton.getApplicationIcon().file()));
		} catch (Exception e) {
			logger.error("Could not set application icon: " + e.getMessage());
		}
		
		pack();
		setVisible(true);
		setSize(800, 600);
	}

	public static void main(String[] args) {

		initializeLogback();

		try {
			// Set System L&F
			UIManager.setLookAndFeel(
					UIManager.getSystemLookAndFeelClassName());
		} 
		catch (UnsupportedLookAndFeelException e) {
			// handle exception
		}
		catch (ClassNotFoundException e) {
			// handle exception
		}
		catch (InstantiationException e) {
			// handle exception
		}
		catch (IllegalAccessException e) {
			// handle exception
		}

		File file = askForFile();
		JFrame frame = new DesktopLauncher(10,file);


		singleton.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				singleton.canvas.stop();
			}
		});
	}
	
	private static File askForFile() {
		// TODO Irgendwie den letzten Pfad merken
		File path = null;
		if (path == null) {
			path = new File(System.getProperty("user.dir"));
		}

		if (fileDialogs == null) {
			fileDialogs = new JFileChooser(path);
		}
		fileDialogs.showOpenDialog(null);
		File sFile = fileDialogs.getSelectedFile();

		return sFile;
	}

	private static void initializeLogback() {


		// assume SLF4J is bound to logback in the current environment
		LoggerContext context = (LoggerContext) LoggerFactory.getILoggerFactory();

		try {
			JoranConfigurator configurator = new JoranConfigurator();
			configurator.setContext(context);
			// Call context.reset() to clear any previous configuration, e.g. default
			// configuration. For multi-step configuration, omit calling context.reset().
			context.reset();
			configurator.doConfigure("config/logback.xml");
		} catch (JoranException je) {
			// StatusPrinter will handle this
		}
		//StatusPrinter.printInCaseOfErrorsOrWarnings(context);

	}

	protected static int translateKeyCode(int keyCode) {
		if (keyCode == java.awt.event.KeyEvent.VK_ADD) return Input.Keys.PLUS;
		if (keyCode == java.awt.event.KeyEvent.VK_SUBTRACT) return Input.Keys.MINUS;
		if (keyCode == java.awt.event.KeyEvent.VK_0) return Input.Keys.NUM_0;
		if (keyCode == java.awt.event.KeyEvent.VK_1) return Input.Keys.NUM_1;
		if (keyCode == java.awt.event.KeyEvent.VK_2) return Input.Keys.NUM_2;
		if (keyCode == java.awt.event.KeyEvent.VK_3) return Input.Keys.NUM_3;
		if (keyCode == java.awt.event.KeyEvent.VK_4) return Input.Keys.NUM_4;
		if (keyCode == java.awt.event.KeyEvent.VK_5) return Input.Keys.NUM_5;
		if (keyCode == java.awt.event.KeyEvent.VK_6) return Input.Keys.NUM_6;
		if (keyCode == java.awt.event.KeyEvent.VK_7) return Input.Keys.NUM_7;
		if (keyCode == java.awt.event.KeyEvent.VK_8) return Input.Keys.NUM_8;
		if (keyCode == java.awt.event.KeyEvent.VK_9) return Input.Keys.NUM_9;
		if (keyCode == java.awt.event.KeyEvent.VK_A) return Input.Keys.A;
		if (keyCode == java.awt.event.KeyEvent.VK_B) return Input.Keys.B;
		if (keyCode == java.awt.event.KeyEvent.VK_C) return Input.Keys.C;
		if (keyCode == java.awt.event.KeyEvent.VK_D) return Input.Keys.D;
		if (keyCode == java.awt.event.KeyEvent.VK_E) return Input.Keys.E;
		if (keyCode == java.awt.event.KeyEvent.VK_F) return Input.Keys.F;
		if (keyCode == java.awt.event.KeyEvent.VK_G) return Input.Keys.G;
		if (keyCode == java.awt.event.KeyEvent.VK_H) return Input.Keys.H;
		if (keyCode == java.awt.event.KeyEvent.VK_I) return Input.Keys.I;
		if (keyCode == java.awt.event.KeyEvent.VK_J) return Input.Keys.J;
		if (keyCode == java.awt.event.KeyEvent.VK_K) return Input.Keys.K;
		if (keyCode == java.awt.event.KeyEvent.VK_L) return Input.Keys.L;
		if (keyCode == java.awt.event.KeyEvent.VK_M) return Input.Keys.M;
		if (keyCode == java.awt.event.KeyEvent.VK_N) return Input.Keys.N;
		if (keyCode == java.awt.event.KeyEvent.VK_O) return Input.Keys.O;
		if (keyCode == java.awt.event.KeyEvent.VK_P) return Input.Keys.P;
		if (keyCode == java.awt.event.KeyEvent.VK_Q) return Input.Keys.Q;
		if (keyCode == java.awt.event.KeyEvent.VK_R) return Input.Keys.R;
		if (keyCode == java.awt.event.KeyEvent.VK_S) return Input.Keys.S;
		if (keyCode == java.awt.event.KeyEvent.VK_T) return Input.Keys.T;
		if (keyCode == java.awt.event.KeyEvent.VK_U) return Input.Keys.U;
		if (keyCode == java.awt.event.KeyEvent.VK_V) return Input.Keys.V;
		if (keyCode == java.awt.event.KeyEvent.VK_W) return Input.Keys.W;
		if (keyCode == java.awt.event.KeyEvent.VK_X) return Input.Keys.X;
		if (keyCode == java.awt.event.KeyEvent.VK_Y) return Input.Keys.Y;
		if (keyCode == java.awt.event.KeyEvent.VK_Z) return Input.Keys.Z;
		if (keyCode == java.awt.event.KeyEvent.VK_ALT) return Input.Keys.ALT_LEFT;
		if (keyCode == java.awt.event.KeyEvent.VK_ALT_GRAPH) return Input.Keys.ALT_RIGHT;
		if (keyCode == java.awt.event.KeyEvent.VK_BACK_SLASH) return Input.Keys.BACKSLASH;
		if (keyCode == java.awt.event.KeyEvent.VK_COMMA) return Input.Keys.COMMA;
		if (keyCode == java.awt.event.KeyEvent.VK_DELETE) return Input.Keys.DEL;
		if (keyCode == java.awt.event.KeyEvent.VK_LEFT) return Input.Keys.DPAD_LEFT;
		if (keyCode == java.awt.event.KeyEvent.VK_RIGHT) return Input.Keys.DPAD_RIGHT;
		if (keyCode == java.awt.event.KeyEvent.VK_UP) return Input.Keys.DPAD_UP;
		if (keyCode == java.awt.event.KeyEvent.VK_DOWN) return Input.Keys.DPAD_DOWN;
		if (keyCode == java.awt.event.KeyEvent.VK_ENTER) return Input.Keys.ENTER;
		if (keyCode == java.awt.event.KeyEvent.VK_HOME) return Input.Keys.HOME;
		if (keyCode == java.awt.event.KeyEvent.VK_MINUS) return Input.Keys.MINUS;
		if (keyCode == java.awt.event.KeyEvent.VK_PERIOD) return Input.Keys.PERIOD;
		if (keyCode == java.awt.event.KeyEvent.VK_PLUS) return Input.Keys.PLUS;
		if (keyCode == java.awt.event.KeyEvent.VK_SEMICOLON) return Input.Keys.SEMICOLON;
		if (keyCode == java.awt.event.KeyEvent.VK_SHIFT) return Input.Keys.SHIFT_LEFT;
		if (keyCode == java.awt.event.KeyEvent.VK_SLASH) return Input.Keys.SLASH;
		if (keyCode == java.awt.event.KeyEvent.VK_SPACE) return Input.Keys.SPACE;
		if (keyCode == java.awt.event.KeyEvent.VK_TAB) return Input.Keys.TAB;
		if (keyCode == java.awt.event.KeyEvent.VK_BACK_SPACE) return Input.Keys.DEL;
		if (keyCode == java.awt.event.KeyEvent.VK_CONTROL) return Input.Keys.CONTROL_LEFT;
		if (keyCode == java.awt.event.KeyEvent.VK_ESCAPE) return Input.Keys.ESCAPE;
		if (keyCode == java.awt.event.KeyEvent.VK_END) return Input.Keys.END;
		if (keyCode == java.awt.event.KeyEvent.VK_INSERT) return Input.Keys.INSERT;
		if (keyCode == java.awt.event.KeyEvent.VK_NUMPAD5) return Input.Keys.DPAD_CENTER;
		if (keyCode == java.awt.event.KeyEvent.VK_PAGE_UP) return Input.Keys.PAGE_UP;
		if (keyCode == java.awt.event.KeyEvent.VK_PAGE_DOWN) return Input.Keys.PAGE_DOWN;
		if (keyCode == java.awt.event.KeyEvent.VK_F1) return Input.Keys.F1;
		if (keyCode == java.awt.event.KeyEvent.VK_F2) return Input.Keys.F2;
		if (keyCode == java.awt.event.KeyEvent.VK_F3) return Input.Keys.F3;
		if (keyCode == java.awt.event.KeyEvent.VK_F4) return Input.Keys.F4;
		if (keyCode == java.awt.event.KeyEvent.VK_F5) return Input.Keys.F5;
		if (keyCode == java.awt.event.KeyEvent.VK_F6) return Input.Keys.F6;
		if (keyCode == java.awt.event.KeyEvent.VK_F7) return Input.Keys.F7;
		if (keyCode == java.awt.event.KeyEvent.VK_F8) return Input.Keys.F8;
		if (keyCode == java.awt.event.KeyEvent.VK_F9) return Input.Keys.F9;
		if (keyCode == java.awt.event.KeyEvent.VK_F10) return Input.Keys.F10;
		if (keyCode == java.awt.event.KeyEvent.VK_F11) return Input.Keys.F11;
		if (keyCode == java.awt.event.KeyEvent.VK_F12) return Input.Keys.F12;
		if (keyCode == java.awt.event.KeyEvent.VK_COLON) return Input.Keys.COLON;
		if (keyCode == java.awt.event.KeyEvent.VK_NUMPAD0) return Input.Keys.NUM_0;
		if (keyCode == java.awt.event.KeyEvent.VK_NUMPAD1) return Input.Keys.NUM_1;
		if (keyCode == java.awt.event.KeyEvent.VK_NUMPAD2) return Input.Keys.NUM_2;
		if (keyCode == java.awt.event.KeyEvent.VK_NUMPAD3) return Input.Keys.NUM_3;
		if (keyCode == java.awt.event.KeyEvent.VK_NUMPAD4) return Input.Keys.NUM_4;
		if (keyCode == java.awt.event.KeyEvent.VK_NUMPAD5) return Input.Keys.NUM_5;
		if (keyCode == java.awt.event.KeyEvent.VK_NUMPAD6) return Input.Keys.NUM_6;
		if (keyCode == java.awt.event.KeyEvent.VK_NUMPAD7) return Input.Keys.NUM_7;
		if (keyCode == java.awt.event.KeyEvent.VK_NUMPAD8) return Input.Keys.NUM_8;
		if (keyCode == java.awt.event.KeyEvent.VK_NUMPAD9) return Input.Keys.NUM_9;

		return Input.Keys.UNKNOWN;
	}
	
	private class timerCallback implements BioVizEvent {
		private JSlider time;
		public timerCallback(JSlider slider) {
			this.time = slider;
			BioViz.singleton.addTimeChangedListener(this);
		}
		@Override
		public void bioVizEvent() {
			this.time.setValue((int) BioViz.singleton.currentCircuit.currentTime);
		}
	}
	
	private class loadFileCallback implements BioVizEvent {
		public loadFileCallback() {	}
		@Override
		public void bioVizEvent() {
			File f = askForFile();
			if (f != null)
				BioViz.loadNewFile(f);
		}
	}
	
	private class loadedFileCallback implements BioVizEvent {
		public loadedFileCallback() {	}
		@Override
		public void bioVizEvent() {
			logger.debug("Desktop received loaded event, setting slider...");
			DesktopLauncher.singleton.time.setMaximum((int)BioViz.singleton.currentCircuit.data.getMaxTime());
		}
	}
	
	private class saveFileCallback implements BioVizEvent {
		public saveFileCallback() {	}
		@Override
		public void bioVizEvent() {
			try {
			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					Preferences prefs = Gdx.app.getPreferences("BioVizPreferences");
					logger.debug("Desktop received save event, opening dialog...");

					String name = prefs.getString("saveFolder", ".");
					if (fileDialogs == null) {
						fileDialogs = new JFileChooser();
					}
					int fcresult = fileDialogs.showSaveDialog(null) ;

					if (fcresult == JFileChooser.APPROVE_OPTION) {
						prefs.putString("saveFolder", fileDialogs.getSelectedFile().getAbsolutePath());
						BioViz.singleton.saveSVG(fileDialogs.getSelectedFile().getAbsolutePath());
					}
				}
			});
			} catch (Exception e) {
				logger.error("Could not save file: " + e.getMessage() + "\n" + e.getStackTrace());
			}
		}
	}
}
