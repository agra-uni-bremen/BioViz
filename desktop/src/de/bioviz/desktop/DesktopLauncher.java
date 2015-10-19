package de.bioviz.desktop;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.KeyEventDispatcher;
import java.awt.KeyboardFocusManager;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.util.HashMap;

import javax.imageio.ImageIO;
import javax.swing.*;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.joran.JoranConfigurator;
import ch.qos.logback.core.joran.spi.JoranException;

import com.badlogic.gdx.Files;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.backends.lwjgl.LwjglAWTCanvas;
import com.badlogic.gdx.backends.lwjgl.LwjglAWTInput;

import de.bioviz.ui.BioViz;
import de.bioviz.ui.BioVizEvent;
import de.bioviz.ui.DrawableRoute;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class DesktopLauncher extends JFrame {

	public JSlider timeSlider;
	protected JSlider displayRouteLengthSlider;

	JLabel timeInfo = new JLabel("1");

	timerCallback tc;
	loadFileCallback load_cb;
	loadedFileCallback loaded_cb;
	saveFileCallback save_cb;
	public static DesktopLauncher singleton;
	private BioViz bioViz;
	LwjglAWTCanvas canvas;
	LwjglAWTInput input;

	public final String programName = "BioViz";

	private static JFileChooser fileDialog = null;

	private static Logger logger =
			LoggerFactory.getLogger(DesktopLauncher.class);

	private JTabbedPane visualizationTabs;

	BioViz currentViz;

	private HashMap<Object, File> tabsToFilenames;


	/**
	 * Needed to fetch *all* pressed keys that are caught somewhere in this
	 * frame in order to pipe them through to the libgdx program within.
	 *
	 * @author Jannis Stoppe
	 */
	private class MyDispatcher implements KeyEventDispatcher {
		@Override
		public boolean dispatchKeyEvent(KeyEvent e) {
			if (input.getInputProcessor() == null) {
				input.setInputProcessor(bioViz.getInputProcessor());
			}
			//Additional check to avoid having events fire twice (once from
			// here and once from libgdx)
			if (DesktopLauncher.singleton.getFocusOwner() !=
				DesktopLauncher.singleton.canvas.getCanvas()) {
				if (e.getID() == KeyEvent.KEY_PRESSED) {
					bioViz.getInputProcessor().keyDown(
							translateKeyCode(e.getKeyCode()));
				}
				else if (e.getID() == KeyEvent.KEY_RELEASED) {
					bioViz.getInputProcessor().keyUp(
							translateKeyCode(e.getKeyCode()));
				}
				else if (e.getID() == KeyEvent.KEY_TYPED) {
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
		tabsToFilenames = new HashMap<Object, File>();

		if (file == null) {
			bioViz = new BioViz();
		}
		else {
			bioViz = new BioViz(file);
		}
		currentViz = bioViz;
		canvas = new LwjglAWTCanvas(bioViz);

		currentViz.addCloseFileListener(new closeFileCallback());

		final Container container = getContentPane();
		container.setLayout(new BorderLayout());

		this.setTitle(programName);

		logger.debug("Starting DesktopLauncher with file \"{}\"", file);


		visualizationTabs = new JTabbedPane();
		visualizationTabs.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);

		visualizationTabs.addChangeListener(
				l -> bioViz.loadNewFile(
						tabsToFilenames.get(
								((JTabbedPane) l.getSource())
										.getSelectedComponent())));

		visualizationTabs.addMouseListener(new MouseListener() {

			@Override
			public void mouseReleased(MouseEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void mousePressed(MouseEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void mouseExited(MouseEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void mouseEntered(MouseEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void mouseClicked(MouseEvent e) {
				logger.debug("got button " + e.getButton() + " in tabs");
				if (e.getButton() == MouseEvent.BUTTON2) {
					closeTab(visualizationTabs.getSelectedIndex());
				}
			}
		});

		addNewTab(file);

		/**
		 * Needed to pipe through the keyboard events to the libgdx application
		 */
		KeyboardFocusManager manager =
				KeyboardFocusManager.getCurrentKeyboardFocusManager();
		manager.addKeyEventDispatcher(new MyDispatcher());

		final int panelWidth = 128;
		final int panelHeight = 600;

		JPanel panel = new JPanel();
		panel.setLayout(new FlowLayout());
		panel.setPreferredSize(new Dimension(panelWidth, panelHeight));


		// This text was completely useless. I leave the code here as a
		// reference on how to add labels with some kind
		// of formatting.
//		JLabel label = new JLabel("<html><body>Totally classic<br/>UI
// elements<br/></body></html>");


		final int buttonWidth = 112;
		final int sliderWidth = buttonWidth;
		final int sliderHeight = new JSlider().getPreferredSize().height;

		JButton autoplaytButton = new JButton("Autoplay");
		autoplaytButton.setPreferredSize(new Dimension(buttonWidth,
													   autoplaytButton
															   .getPreferredSize().height));
		autoplaytButton.addActionListener(
				e -> currentViz.currentCircuit.autoAdvance =
						!currentViz.currentCircuit.autoAdvance);

		JButton openButton = new JButton("Open File");
		openButton.setPreferredSize(new Dimension(buttonWidth,
												  openButton.getPreferredSize
														  ().height));
		load_cb = new loadFileCallback();
		openButton.addActionListener(e -> load_cb.bioVizEvent());

		JButton saveButton = new JButton("Save SVG");
		saveButton.setPreferredSize(new Dimension(buttonWidth,
												  saveButton.getPreferredSize
														  ().height));
		save_cb = new saveFileCallback();
		saveButton.addActionListener(e -> save_cb.bioVizEvent());

		JButton zoomButton = new JButton("Center");
		zoomButton.setPreferredSize(new Dimension(buttonWidth,
												  zoomButton.getPreferredSize
														  ().height));
		zoomButton.addActionListener(
				e -> currentViz.currentCircuit.zoomExtents());

		JButton dropletButton = new JButton("Droplets");
		dropletButton.setPreferredSize(new Dimension(buttonWidth,
													 dropletButton
															 .getPreferredSize
																	 ()
															 .height));
		dropletButton.addActionListener(
				e -> currentViz.currentCircuit.toggleShowDroplets());

		JButton usageButton = new JButton("Cell Usage");
		usageButton.setPreferredSize(new Dimension(buttonWidth,
												   usageButton
														   .getPreferredSize()
														   .height));
		usageButton.addActionListener(
				e -> currentViz.currentCircuit.toggleShowUsage());

		JButton actuationButton = new JButton("Actuations");
		actuationButton.setPreferredSize(new Dimension(buttonWidth,
													   actuationButton
															   .getPreferredSize().height));
		actuationButton.addActionListener(
				e -> currentViz.currentCircuit.toggleShowActuations());


		timeSlider = new JSlider(JSlider.HORIZONTAL, 1, timeMax, 1);
		timeSlider.setPreferredSize(new Dimension(sliderWidth, sliderHeight));
		timeSlider.addChangeListener(
				ce -> currentViz.currentCircuit.setCurrentTime(
						((JSlider) ce.getSource()).getValue()));
		tc = new timerCallback(timeSlider, timeInfo);


		displayRouteLengthSlider =
				new JSlider(JSlider.HORIZONTAL, 0, 32, DrawableRoute
						.timesteps);
		displayRouteLengthSlider.setPreferredSize(
				new Dimension(sliderWidth, sliderHeight));
		displayRouteLengthSlider.addChangeListener(
				ce -> DrawableRoute.timesteps =
						((JSlider) ce.getSource()).getValue());
		//tc = new timerCallback(timeSlider);

		JButton adjacencyButton = new JButton("Adjacency");
		adjacencyButton.setPreferredSize(new Dimension(buttonWidth,
													   adjacencyButton
															   .getPreferredSize().height));
		adjacencyButton.addActionListener(
				e -> currentViz.currentCircuit.toggleHighlightAdjacency());


		JButton displayDropletIDsButton = new JButton("Drop IDs");
		displayDropletIDsButton.setPreferredSize(new Dimension(buttonWidth,
															   displayDropletIDsButton.getPreferredSize().height));
		displayDropletIDsButton.addActionListener(
				e -> currentViz.currentCircuit.toggleDisplayDropletIDs());

		JButton displayFluidIDsButton = new JButton("Fluid IDs");
		displayFluidIDsButton.setPreferredSize(new Dimension(buttonWidth,
															 displayFluidIDsButton.getPreferredSize().height));
		displayFluidIDsButton.addActionListener(
				e -> currentViz.currentCircuit.toggleDisplayFluidIDs());

		JButton pinButton = new JButton("Pins");
		pinButton.setPreferredSize(new Dimension(buttonWidth,
												 pinButton.getPreferredSize()
														 .height));
		pinButton.addActionListener(
				e -> currentViz.currentCircuit.toggleShowPins());

		JButton stIconButton = new JButton("Source/Target icons");
		stIconButton.setPreferredSize(new Dimension(buttonWidth,
													stIconButton
															.getPreferredSize
																	()
															.height));
		stIconButton.addActionListener(
				e -> currentViz.currentCircuit.toggleShowSourceTargetIcons());

		JButton stIDButton = new JButton("Source/Target IDs");
		stIDButton.setPreferredSize(new Dimension(buttonWidth,
												  stIDButton.getPreferredSize
														  ().height));
		stIDButton.addActionListener(
				e -> currentViz.currentCircuit.toggleShowSourceTargetIDs());


		JButton nextStepButton = new JButton("->");
		nextStepButton.addActionListener(
				e -> currentViz.currentCircuit.nextStep());
		JButton prevStepButton = new JButton("<-");
		prevStepButton.addActionListener(
				e -> currentViz.currentCircuit.prevStep());

		/*
		For some reason, adding a speparator more then once prevents it from
		being displayed more
		than once O_0
		 */
		JSeparator timeSep = new JSeparator(SwingConstants.HORIZONTAL);
		timeSep.setPreferredSize(new Dimension(buttonWidth, 5));
		JSeparator fileSep = new JSeparator(SwingConstants.HORIZONTAL);
		fileSep.setPreferredSize(new Dimension(buttonWidth, 5));
		JSeparator optionsSep = new JSeparator(SwingConstants.HORIZONTAL);
		optionsSep.setPreferredSize(new Dimension(buttonWidth, 5));
		JSeparator invisiSep = new JSeparator(SwingConstants.HORIZONTAL);
		invisiSep.setPreferredSize(new Dimension(buttonWidth, 0));


		panel.add(new JLabel("Files"));
		panel.add(fileSep);
		panel.add(openButton);
		panel.add(saveButton);
		panel.add(invisiSep);
		panel.add(new JLabel("Options"));
		panel.add(optionsSep);
		panel.add(new JLabel("Route length"));
		panel.add(displayRouteLengthSlider);
		panel.add(zoomButton);
		panel.add(dropletButton);
		panel.add(displayDropletIDsButton);
		panel.add(displayFluidIDsButton);
		panel.add(pinButton);
		panel.add(actuationButton);
		panel.add(adjacencyButton);
		panel.add(usageButton);
		panel.add(stIconButton);
		panel.add(stIDButton);
		panel.add(invisiSep);
		panel.add(new JLabel("Time"));
		panel.add(timeSep);
		panel.add(new JLabel("Step: "));
		panel.add(timeInfo);
		panel.add(autoplaytButton);
		panel.add(prevStepButton);
		panel.add(nextStepButton);
		panel.add(timeSlider);


		input = new LwjglAWTInput(canvas.getCanvas());

		container.add(panel, BorderLayout.WEST);

		JPanel tabContainer = new JPanel(new BorderLayout());

		container.add(tabContainer, BorderLayout.CENTER);

		tabContainer.add(visualizationTabs, BorderLayout.NORTH);
		tabContainer.add(canvas.getCanvas(), BorderLayout.CENTER);

		loaded_cb = new loadedFileCallback();
		currentViz.addLoadedFileListener(loaded_cb);

		save_cb = new saveFileCallback();
		currentViz.addSaveFileListener(save_cb);

		try {
			this.setIconImage(
					ImageIO.read(currentViz.getApplicationIcon().file()));
		} catch (Exception e) {
			logger.error("Could not set application icon: " + e.getMessage());
		}

		pack();
		setVisible(true);
		setSize(800, 600);
	}

	private void addNewTab(File file) {
		if (file == null) {
			file = Gdx.files.getFileHandle("examples/default_grid.bio",
										   Files.FileType.Internal).file();
		}
		logger.debug("Adding new tab to UI for " + file.getName());
		JPanel dummyPanel = new JPanel();
		dummyPanel.setPreferredSize(new Dimension());
		visualizationTabs.addTab(file.getName(), dummyPanel);
		visualizationTabs.setSelectedIndex(visualizationTabs.getTabCount() -
										   1);
		tabsToFilenames.put(dummyPanel, file);
		this.bioViz.loadNewFile(file);
	}

	private void closeTab(int index) {
		logger.info("Closing file (" + index + ")");
		bioViz.unloadFile(
				tabsToFilenames.get(visualizationTabs.getSelectedComponent()));
		tabsToFilenames.remove(visualizationTabs.getSelectedComponent());
		visualizationTabs.removeTabAt(index);
	}

	private void closeTab() {
		int index = visualizationTabs.getSelectedIndex();
		closeTab(index);
	}

	public static void main(String[] args) {

		initializeLogback();

		try {
			// Set System L&F
			UIManager.setLookAndFeel(
					UIManager.getSystemLookAndFeelClassName());
		} catch (UnsupportedLookAndFeelException e) {
			// handle exception
		} catch (ClassNotFoundException e) {
			// handle exception
		} catch (InstantiationException e) {
			// handle exception
		} catch (IllegalAccessException e) {
			// handle exception
		}

		File file = askForFile();
		JFrame frame = new DesktopLauncher(10, file);


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

		if (fileDialog == null) {
			fileDialog = new JFileChooser(path);
		}
		int choice = fileDialog.showOpenDialog(null);
		if (choice == JFileChooser.APPROVE_OPTION) {
			return fileDialog.getSelectedFile();

		}

		return null;
	}

	private static void initializeLogback() {


		// assume SLF4J is bound to logback in the current environment
		LoggerContext context =
				(LoggerContext) LoggerFactory.getILoggerFactory();

		try {
			JoranConfigurator configurator = new JoranConfigurator();
			configurator.setContext(context);
			// Call context.reset() to clear any previous configuration, e.g.
			// default
			// configuration. For multi-step configuration, omit calling
			// context.reset().
			context.reset();
			configurator.doConfigure("config/logback.xml");
		} catch (JoranException je) {
			// StatusPrinter will handle this
		}
		//StatusPrinter.printInCaseOfErrorsOrWarnings(context);

	}

	protected static int translateKeyCode(int keyCode) {
		if (keyCode == java.awt.event.KeyEvent.VK_ADD) {
			return Input.Keys.PLUS;
		}
		if (keyCode == java.awt.event.KeyEvent.VK_SUBTRACT) {
			return Input.Keys.MINUS;
		}
		if (keyCode == java.awt.event.KeyEvent.VK_0) {
			return Input.Keys.NUM_0;
		}
		if (keyCode == java.awt.event.KeyEvent.VK_1) {
			return Input.Keys.NUM_1;
		}
		if (keyCode == java.awt.event.KeyEvent.VK_2) {
			return Input.Keys.NUM_2;
		}
		if (keyCode == java.awt.event.KeyEvent.VK_3) {
			return Input.Keys.NUM_3;
		}
		if (keyCode == java.awt.event.KeyEvent.VK_4) {
			return Input.Keys.NUM_4;
		}
		if (keyCode == java.awt.event.KeyEvent.VK_5) {
			return Input.Keys.NUM_5;
		}
		if (keyCode == java.awt.event.KeyEvent.VK_6) {
			return Input.Keys.NUM_6;
		}
		if (keyCode == java.awt.event.KeyEvent.VK_7) {
			return Input.Keys.NUM_7;
		}
		if (keyCode == java.awt.event.KeyEvent.VK_8) {
			return Input.Keys.NUM_8;
		}
		if (keyCode == java.awt.event.KeyEvent.VK_9) {
			return Input.Keys.NUM_9;
		}
		if (keyCode == java.awt.event.KeyEvent.VK_A) {
			return Input.Keys.A;
		}
		if (keyCode == java.awt.event.KeyEvent.VK_B) {
			return Input.Keys.B;
		}
		if (keyCode == java.awt.event.KeyEvent.VK_C) {
			return Input.Keys.C;
		}
		if (keyCode == java.awt.event.KeyEvent.VK_D) {
			return Input.Keys.D;
		}
		if (keyCode == java.awt.event.KeyEvent.VK_E) {
			return Input.Keys.E;
		}
		if (keyCode == java.awt.event.KeyEvent.VK_F) {
			return Input.Keys.F;
		}
		if (keyCode == java.awt.event.KeyEvent.VK_G) {
			return Input.Keys.G;
		}
		if (keyCode == java.awt.event.KeyEvent.VK_H) {
			return Input.Keys.H;
		}
		if (keyCode == java.awt.event.KeyEvent.VK_I) {
			return Input.Keys.I;
		}
		if (keyCode == java.awt.event.KeyEvent.VK_J) {
			return Input.Keys.J;
		}
		if (keyCode == java.awt.event.KeyEvent.VK_K) {
			return Input.Keys.K;
		}
		if (keyCode == java.awt.event.KeyEvent.VK_L) {
			return Input.Keys.L;
		}
		if (keyCode == java.awt.event.KeyEvent.VK_M) {
			return Input.Keys.M;
		}
		if (keyCode == java.awt.event.KeyEvent.VK_N) {
			return Input.Keys.N;
		}
		if (keyCode == java.awt.event.KeyEvent.VK_O) {
			return Input.Keys.O;
		}
		if (keyCode == java.awt.event.KeyEvent.VK_P) {
			return Input.Keys.P;
		}
		if (keyCode == java.awt.event.KeyEvent.VK_Q) {
			return Input.Keys.Q;
		}
		if (keyCode == java.awt.event.KeyEvent.VK_R) {
			return Input.Keys.R;
		}
		if (keyCode == java.awt.event.KeyEvent.VK_S) {
			return Input.Keys.S;
		}
		if (keyCode == java.awt.event.KeyEvent.VK_T) {
			return Input.Keys.T;
		}
		if (keyCode == java.awt.event.KeyEvent.VK_U) {
			return Input.Keys.U;
		}
		if (keyCode == java.awt.event.KeyEvent.VK_V) {
			return Input.Keys.V;
		}
		if (keyCode == java.awt.event.KeyEvent.VK_W) {
			return Input.Keys.W;
		}
		if (keyCode == java.awt.event.KeyEvent.VK_X) {
			return Input.Keys.X;
		}
		if (keyCode == java.awt.event.KeyEvent.VK_Y) {
			return Input.Keys.Y;
		}
		if (keyCode == java.awt.event.KeyEvent.VK_Z) {
			return Input.Keys.Z;
		}
		if (keyCode == java.awt.event.KeyEvent.VK_ALT) {
			return Input.Keys.ALT_LEFT;
		}
		if (keyCode == java.awt.event.KeyEvent.VK_ALT_GRAPH) {
			return Input.Keys.ALT_RIGHT;
		}
		if (keyCode == java.awt.event.KeyEvent.VK_BACK_SLASH) {
			return Input.Keys.BACKSLASH;
		}
		if (keyCode == java.awt.event.KeyEvent.VK_COMMA) {
			return Input.Keys.COMMA;
		}
		if (keyCode == java.awt.event.KeyEvent.VK_DELETE) {
			return Input.Keys
					.DEL;
		}
		if (keyCode == java.awt.event.KeyEvent.VK_LEFT) {
			return Input.Keys.DPAD_LEFT;
		}
		if (keyCode == java.awt.event.KeyEvent.VK_RIGHT) {
			return Input.Keys.DPAD_RIGHT;
		}
		if (keyCode == java.awt.event.KeyEvent.VK_UP) {
			return Input.Keys
					.DPAD_UP;
		}
		if (keyCode == java.awt.event.KeyEvent.VK_DOWN) {
			return Input.Keys.DPAD_DOWN;
		}
		if (keyCode == java.awt.event.KeyEvent.VK_ENTER) {
			return Input.Keys.ENTER;
		}
		if (keyCode == java.awt.event.KeyEvent.VK_HOME) {
			return Input.Keys.HOME;
		}
		if (keyCode == java.awt.event.KeyEvent.VK_MINUS) {
			return Input.Keys.MINUS;
		}
		if (keyCode == java.awt.event.KeyEvent.VK_PERIOD) {
			return Input.Keys.PERIOD;
		}
		if (keyCode == java.awt.event.KeyEvent.VK_PLUS) {
			return Input.Keys.PLUS;
		}
		if (keyCode == java.awt.event.KeyEvent.VK_SEMICOLON) {
			return Input.Keys.SEMICOLON;
		}
		if (keyCode == java.awt.event.KeyEvent.VK_SHIFT) {
			return Input.Keys.SHIFT_LEFT;
		}
		if (keyCode == java.awt.event.KeyEvent.VK_SLASH) {
			return Input.Keys.SLASH;
		}
		if (keyCode == java.awt.event.KeyEvent.VK_SPACE) {
			return Input.Keys.SPACE;
		}
		if (keyCode == java.awt.event.KeyEvent.VK_TAB) {
			return Input.Keys.TAB;
		}
		if (keyCode == java.awt.event.KeyEvent.VK_BACK_SPACE) {
			return Input.Keys.DEL;
		}
		if (keyCode == java.awt.event.KeyEvent.VK_CONTROL) {
			return Input.Keys.CONTROL_LEFT;
		}
		if (keyCode == java.awt.event.KeyEvent.VK_ESCAPE) {
			return Input.Keys.ESCAPE;
		}
		if (keyCode == java.awt.event.KeyEvent.VK_END) {
			return Input.Keys.END;
		}
		if (keyCode == java.awt.event.KeyEvent.VK_INSERT) {
			return Input.Keys.INSERT;
		}
		if (keyCode == java.awt.event.KeyEvent.VK_NUMPAD5) {
			return Input.Keys.DPAD_CENTER;
		}
		if (keyCode == java.awt.event.KeyEvent.VK_PAGE_UP) {
			return Input.Keys.PAGE_UP;
		}
		if (keyCode == java.awt.event.KeyEvent.VK_PAGE_DOWN) {
			return Input.Keys.PAGE_DOWN;
		}
		if (keyCode == java.awt.event.KeyEvent.VK_F1) {
			return Input.Keys.F1;
		}
		if (keyCode == java.awt.event.KeyEvent.VK_F2) {
			return Input.Keys.F2;
		}
		if (keyCode == java.awt.event.KeyEvent.VK_F3) {
			return Input.Keys.F3;
		}
		if (keyCode == java.awt.event.KeyEvent.VK_F4) {
			return Input.Keys.F4;
		}
		if (keyCode == java.awt.event.KeyEvent.VK_F5) {
			return Input.Keys.F5;
		}
		if (keyCode == java.awt.event.KeyEvent.VK_F6) {
			return Input.Keys.F6;
		}
		if (keyCode == java.awt.event.KeyEvent.VK_F7) {
			return Input.Keys.F7;
		}
		if (keyCode == java.awt.event.KeyEvent.VK_F8) {
			return Input.Keys.F8;
		}
		if (keyCode == java.awt.event.KeyEvent.VK_F9) {
			return Input.Keys.F9;
		}
		if (keyCode == java.awt.event.KeyEvent.VK_F10) {
			return Input.Keys.F10;
		}
		if (keyCode == java.awt.event.KeyEvent.VK_F11) {
			return Input.Keys.F11;
		}
		if (keyCode == java.awt.event.KeyEvent.VK_F12) {
			return Input.Keys.F12;
		}
		if (keyCode == java.awt.event.KeyEvent.VK_COLON) {
			return Input.Keys.COLON;
		}
		if (keyCode == java.awt.event.KeyEvent.VK_NUMPAD0) {
			return Input.Keys.NUM_0;
		}
		if (keyCode == java.awt.event.KeyEvent.VK_NUMPAD1) {
			return Input.Keys.NUM_1;
		}
		if (keyCode == java.awt.event.KeyEvent.VK_NUMPAD2) {
			return Input.Keys.NUM_2;
		}
		if (keyCode == java.awt.event.KeyEvent.VK_NUMPAD3) {
			return Input.Keys.NUM_3;
		}
		if (keyCode == java.awt.event.KeyEvent.VK_NUMPAD4) {
			return Input.Keys.NUM_4;
		}
		if (keyCode == java.awt.event.KeyEvent.VK_NUMPAD5) {
			return Input.Keys.NUM_5;
		}
		if (keyCode == java.awt.event.KeyEvent.VK_NUMPAD6) {
			return Input.Keys.NUM_6;
		}
		if (keyCode == java.awt.event.KeyEvent.VK_NUMPAD7) {
			return Input.Keys.NUM_7;
		}
		if (keyCode == java.awt.event.KeyEvent.VK_NUMPAD8) {
			return Input.Keys.NUM_8;
		}
		if (keyCode == java.awt.event.KeyEvent.VK_NUMPAD9) {
			return Input.Keys.NUM_9;
		}

		return Input.Keys.UNKNOWN;
	}

	private class timerCallback implements BioVizEvent {
		private JSlider time;
		private JLabel timeInfo;

		public timerCallback(JSlider slider, JLabel info) {
			this.time = slider;
			this.timeInfo = info;
			currentViz.addTimeChangedListener(this);
		}

		@Override
		public void bioVizEvent() {
			this.time.setValue(currentViz.currentCircuit.currentTime);
			this.timeInfo.setText(
					Integer.toString(currentViz.currentCircuit.currentTime));

		}
	}

	private class loadFileCallback implements BioVizEvent {
		public loadFileCallback() {
		}

		@Override
		public void bioVizEvent() {
			File f = askForFile();
			if (f != null) {
				addNewTab(f);
			}
		}
	}

	private class closeFileCallback implements BioVizEvent {
		public closeFileCallback() {
		}

		@Override
		public void bioVizEvent() {
			DesktopLauncher.singleton.closeTab();
		}
	}

	private class loadedFileCallback implements BioVizEvent {
		public loadedFileCallback() {
		}

		@Override
		public void bioVizEvent() {
			if (currentViz.currentCircuit != null) {
				logger.trace(
						"Desktop received loaded event, setting slider...");

				DesktopLauncher d = DesktopLauncher.singleton;

				d.timeSlider.setMaximum(currentViz.currentCircuit.data.getMaxT());
				d.timeSlider.setMinimum(1);
				d.timeSlider.setValue(0);

				d.displayRouteLengthSlider.setMaximum(
						currentViz.currentCircuit.data.getMaxRouteLength());
				d.displayRouteLengthSlider.setMinimum(0);
				d.displayRouteLengthSlider.setValue(0);

				d.setTitle(d.bioViz.getFileName() + " - " + d.programName);

			}
			else {
				logger.trace("Last file closed, no more file to display.");
				DesktopLauncher d = DesktopLauncher.singleton;
				d.timeSlider.setMaximum(1);
				d.timeSlider.setMinimum(1);
				d.timeSlider.setValue(1);

				d.displayRouteLengthSlider.setMaximum(0);
				d.displayRouteLengthSlider.setMinimum(0);
				d.displayRouteLengthSlider.setValue(0);

				d.setTitle(d.programName);
			}
		}
	}

	private class saveFileCallback implements BioVizEvent {
		public saveFileCallback() {
		}

		@Override
		public void bioVizEvent() {
			try {
				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						Preferences prefs =
								Gdx.app.getPreferences("BioVizPreferences");
						logger.debug(
								"Desktop received save event, opening " +
								"dialog...");

						String name = prefs.getString("saveFolder", ".");
						if (fileDialog == null) {
							fileDialog = new JFileChooser();
						}
						int fcresult = fileDialog.showSaveDialog(null);

						if (fcresult == JFileChooser.APPROVE_OPTION) {
							prefs.putString("saveFolder",
											fileDialog.getSelectedFile()
													.getAbsolutePath());
							currentViz.saveSVG(
									fileDialog.getSelectedFile()
											.getAbsolutePath());
						}
					}
				});
			} catch (Exception e) {
				logger.error("Could not save file: " + e.getMessage() + "\n" +
							 e.getStackTrace());
			}
		}
	}
}
