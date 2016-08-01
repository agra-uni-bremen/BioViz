package de.bioviz.desktop;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.joran.JoranConfigurator;
import ch.qos.logback.core.joran.spi.JoranException;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.backends.lwjgl.LwjglAWTCanvas;
import com.badlogic.gdx.backends.lwjgl.LwjglAWTInput;
import de.bioviz.parser.BioParser;
import de.bioviz.structures.Biochip;
import de.bioviz.svg.SVGExportSettings;
import de.bioviz.ui.BDisplayOptions;
import de.bioviz.ui.BioViz;
import de.bioviz.ui.BioVizEvent;
import de.bioviz.ui.DrawableRoute;
import de.bioviz.util.BioVizInfo;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JColorChooser;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JSlider;
import javax.swing.JTabbedPane;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.KeyEventDispatcher;
import java.awt.KeyboardFocusManager;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;

/**
 * This class is the single desktop starter class. It starts the cross-platform
 * core application and provides a basic java desktop UI to control it.
 *
 * @author Jannis Stoppe
 */
public class DesktopLauncher extends JFrame {

	/**
	 * The one single instance of the desktop launcher. There shouldn't be a
	 * second visualization window in the same process, so a singleton is
	 * pretty
	 * much a valid approach to manage access to the instance. This is
	 * currently
	 * set to private as it is only used in private subclasses. Feel free to
	 * elevate this to public as needed.
	 */
	private static DesktopLauncher singleton;



	/**
	 * Used to handle feedback for the user about the program behaviour (and of
	 * course the developer, too). Anything logged using this instance will
	 * report as originating from the DesktopLauncher class.
	 */
	private static Logger logger =
			LoggerFactory.getLogger(DesktopLauncher.class);

	/**
	 * Stores the settings of the SVG exporter.
	 */
	private static SVGExportSettings svgExportSettings =
			SVGExportSettings.getInstance();


	/**
	 * The default x width for the BioViz window.
	 */
	private static final int DEFAULT_X_WIDTH = 800;

	/**
	 * The default x width for the BioViz window.
	 */
	private static final int DEFAULT_Y_WIDTH = 600;


	/**
	 * Whether hotkeys can by used.
	 * <p>
	 * This is a workaround for the problem that fileChooser dialogs still
	 * allow
	 * for hotkeys to be used.
	 */
	private static boolean allowHotkeys = true;

	/**
	 * The label to display the current simulation time.
	 */
	JLabel timeInfo = new JLabel("1");

	/**
	 * Callback to be used upon time changes.
	 */
	TimerCallback tc;

	/**
	 * Callback to be used when a new file is supposed to be loaded.
	 */
	LoadFileCallback loadCB;

	/**
	 * Callback to be used when loading a new file is done.
	 */
	LoadedFileCallback loadedCB;

	/**
	 * Callback to be used when the file is supposed to be saved.
	 */
	SaveFileCallback saveCB;

	/**
	 * The element that is used to draw the libgdx core stuff on.
	 */
	LwjglAWTCanvas canvas;

	/**
	 * Needed for input handling.
	 */
	LwjglAWTInput input;

	/**
	 * The visualization instance. From the DesktopLauncher, this field is usd
	 * to get access to any properties of the currently running visualization.
	 * Notice that despite the application seemingly opening several files at
	 * once in tabs, there is still only one visualization which then displays
	 * several different circuits.
	 */
	 BioViz currentViz;

	/**
	 * The infoPanel displaying the statistics.
	 */
	private InfoPanel infoPanel;

	/**
	 * The biovizEditor instance.
	 */
	private BioVizEditor editor;

	/**
	 * The softErrorViewer instance.
	 */
	private ErrorViewer softErrorsViewer;

	/**
	 * The hardErrorViewer instance.
	 */
	private ErrorViewer hardErrorsViewer;

	/**
	 * The annotationViewer instance.
	 */
	private AnnotationViewer annotationViewer;


	/**
	 * This maps the tabs that are open in the visualizationTabs field to the
	 * filenames being open there. As each tab corresponds to a certain file,
	 * clicking a particular tab needs to tell the visualization to display the
	 * according file, hence the required map.
	 */
	private HashMap<Object, File> tabsToFilenames;

	/**
	 * The slider to control the simulation time from the desktop UI.
	 */
	private JSlider timeSlider;

	/**
	 * The slider to control the length of the displayed droplet routes.
	 */
	private JSlider displayRouteLengthSlider;


	/**
	 * Used to display the tabs for open files.
	 */
	private JTabbedPane visualizationTabs;

	/**
	 * Creates a desktop launcher.
	 */
	public DesktopLauncher() {
		this(null);
	}

	/**
	 * Creates a desktop launcher.
	 *
	 * @param file
	 * 		the file to be opened by default
	 */
	public DesktopLauncher(final File file) {
		singleton = this;
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		tabsToFilenames = new HashMap<>();

		if (file == null) {
			currentViz = new BioViz();
		} else {
			currentViz = new BioViz(file);
		}
		canvas = new LwjglAWTCanvas(currentViz);
		editor = new BioVizEditor(currentViz);
		hardErrorsViewer = new ErrorViewer(currentViz, "Parser errors",
				ErrorViewer.ERRORTYPE.HARD);
		softErrorsViewer = new ErrorViewer(currentViz, "Parser warnings",
				ErrorViewer.ERRORTYPE.SOFT);
		annotationViewer = new AnnotationViewer(currentViz);

		currentViz.addCloseFileListener(new CloseFileCallback());

		final Container container = getContentPane();
		container.setLayout(new BorderLayout());

		this.setTitle(BioVizInfo.PROGNAME);

		logger.debug("Starting DesktopLauncher with file \"{}\"", file);

		initializeTabs(file);

		/**
		 * Needed to pipe through the keyboard events to the libgdx application
		 */
		KeyboardFocusManager manager =
				KeyboardFocusManager.getCurrentKeyboardFocusManager();
		manager.addKeyEventDispatcher(new MyDispatcher());

		JPanel panel = initializePanel();

		infoPanel = new InfoPanel(currentViz);

		this.setJMenuBar(initializeMenubar());

		input = new LwjglAWTInput(canvas);

		container.add(panel, BorderLayout.WEST);

		JPanel tabContainer = new JPanel(new BorderLayout());

		container.add(tabContainer, BorderLayout.CENTER);

		container.add(infoPanel, BorderLayout.EAST);

		tabContainer.add(visualizationTabs, BorderLayout.NORTH);
		tabContainer.add(canvas.getCanvas(), BorderLayout.CENTER);

		loadedCB = new LoadedFileCallback();
		currentViz.addLoadedFileListener(loadedCB);

		saveCB = new SaveFileCallback();
		currentViz.addSaveFileListener(saveCB);

		currentViz.addPickColourListener(new ColourPickCallback());

		String iconPath = "";
		try {
			iconPath = "/" + currentViz.getApplicationIcon().path();
			this.setIconImage(
					ImageIO.read(getFileFromStream(iconPath)));
		} catch (final Exception e) {
			logger.error("Could not set application icon: " + e.getMessage() +
						 " with path: " + iconPath);
		}

		editor.setIcon(iconPath);
		softErrorsViewer.setIcon(iconPath);
		hardErrorsViewer.setIcon(iconPath);
		annotationViewer.setIcon(iconPath);

		pack();
		setVisible(true);

		setSize(DEFAULT_X_WIDTH, DEFAULT_Y_WIDTH);


		currentViz.addReloadFileListener(
				() -> {
					reloadTab();
					reloadViewers();
				}
		);
	}

	/**
	 * Initializes and returns the top menu bar.
	 *
	 * @return the menu bar to be displayed at the top of the window
	 */
	private JMenuBar initializeMenubar() {
		JMenuBar result = new JMenuBar();

		JMenu menu = new JMenu("Display Options");
		menu.setMnemonic(KeyEvent.VK_D);
		menu.getAccessibleContext().setAccessibleDescription(
				"This menu triggers all kinds of display options.");
		result.add(menu);

		BDisplayOptions[] enumValues = BDisplayOptions.values();
		Arrays.sort(enumValues, new Comparator<BDisplayOptions>() {

			@Override
			public int compare(final BDisplayOptions left,
							   final BDisplayOptions right) {
				return left.description().compareTo(
						right.description()); //use your criteria here
			}
		});
		for (final BDisplayOptions option : enumValues) {
			BioCheckboxMenuItem menuItem =
					new BioCheckboxMenuItem(option.description(), option);
			menu.add(menuItem);
			currentViz.addLoadedFileListener(() -> {
				menuItem.updateState();
				return;
			});
		}

		return result;
	}

	/**
	 * Initializes the left-hand panel with all UI elements.
	 *
	 * @return the initialized panel to be added to the according UI element
	 */
	private JPanel initializePanel() {
		final int panelWidth = 128;
		final int panelHeight = 600;
		final int routeLengthMax = 32;
		final int preferredButtonHeight = 5;

		JPanel panel = new JPanel();
		panel.setLayout(new FlowLayout());
		panel.setPreferredSize(new Dimension(panelWidth, panelHeight));

        final int buttonWidth = 112;
		final int sliderWidth = buttonWidth;
		final int sliderHeight = new JSlider().getPreferredSize().height;

		JButton autoplayButton = new JButton("Autoplay");
		autoplayButton.setPreferredSize(
				new Dimension(buttonWidth,
							  autoplayButton.getPreferredSize().height)
		);
		autoplayButton.addActionListener(
				e -> currentViz.currentBiochip.toggleAutoAdvance());

		JButton openButton = new JButton("Open File");
		openButton.setPreferredSize(
				new Dimension(buttonWidth,
							  openButton.getPreferredSize().height)
		);
		loadCB = new LoadFileCallback();
		openButton.addActionListener(e -> loadCB.bioVizEvent());
		currentViz.addLoadFileListener(loadCB);

		JButton preferencesButton = new JButton("Preferences");
		preferencesButton.setPreferredSize(
				new Dimension(buttonWidth,
							  preferencesButton.getPreferredSize().height)
		);
		preferencesButton.addActionListener(e -> showSettings(currentViz));

		JButton statisticsButton = new JButton("Statistics");
		statisticsButton.setPreferredSize(
				new Dimension(buttonWidth,
							  statisticsButton.getPreferredSize().height)
		);
		statisticsButton.addActionListener(e -> {
			boolean visible = this.infoPanel.isVisible();
			this.infoPanel.setVisible(!visible);
		});

		JButton saveButton = new JButton("Save SVG");
		saveButton.setPreferredSize(
				new Dimension(buttonWidth,
							  saveButton.getPreferredSize().height)
		);
		saveCB = new SaveFileCallback();
		saveButton.addActionListener(e -> saveCB.bioVizEvent());

		JButton zoomButton = new JButton("Center");
		zoomButton.setPreferredSize(
				new Dimension(buttonWidth,
							  zoomButton.getPreferredSize().height)
		);
		zoomButton.addActionListener(
				e -> currentViz.currentBiochip.zoomExtents());

		timeSlider = new JSlider(JSlider.HORIZONTAL, 1, 1, 1);
		timeSlider.setPreferredSize(new Dimension(sliderWidth, sliderHeight));
		timeSlider.addChangeListener(
				ce -> currentViz.currentBiochip.setCurrentTime(
						((JSlider) ce.getSource()).getValue()));
		tc = new TimerCallback(timeSlider, timeInfo);


		displayRouteLengthSlider =
				new JSlider(JSlider.HORIZONTAL, 0, routeLengthMax,
							DrawableRoute.routeDisplayLength);
		displayRouteLengthSlider.setPreferredSize(
				new Dimension(sliderWidth, sliderHeight));
		displayRouteLengthSlider.addChangeListener(
				ce -> DrawableRoute.routeDisplayLength =
						((JSlider) ce.getSource()).getValue());


		JButton nextStepButton = new JButton("->");
		nextStepButton.addActionListener(
				e -> currentViz.currentBiochip.nextStep());
		JButton prevStepButton = new JButton("<-");
		prevStepButton.addActionListener(
				e -> currentViz.currentBiochip.prevStep());

		JButton editorButton = new JButton("Editor");
		editorButton.setPreferredSize(new Dimension(buttonWidth,
				editorButton.getPreferredSize().height));
		editorButton.addActionListener(
				e -> {
					editor.show();
				}
		);

		JButton annotationsButton = new JButton("Annotations");
		annotationsButton.setPreferredSize(new Dimension(buttonWidth,
				annotationsButton.getPreferredSize().height));
		annotationsButton.addActionListener(
				e -> {
					annotationViewer.show();
				}
		);

		JButton warningsButton = new JButton("Warnings");
		warningsButton.setPreferredSize(new Dimension(buttonWidth, warningsButton
				.getPreferredSize().height));
		warningsButton.addActionListener(
				e -> {
					softErrorsViewer.show();
				}
		);

		JButton errorsButton = new JButton("Errors");
		errorsButton.setPreferredSize(new Dimension(buttonWidth, errorsButton
				.getPreferredSize().height));
		errorsButton.addActionListener(
				e -> {
					hardErrorsViewer.show();
				}
		);



		/*
		For some reason, adding a separator more then once prevents it from
		being displayed more
		than once O_0
		 */
		JSeparator timeSep = new JSeparator(SwingConstants.HORIZONTAL);
		timeSep.setPreferredSize(
				new Dimension(buttonWidth, preferredButtonHeight));
		JSeparator fileSep = new JSeparator(SwingConstants.HORIZONTAL);
		fileSep.setPreferredSize(
				new Dimension(buttonWidth, preferredButtonHeight));
		JSeparator optionsSep = new JSeparator(SwingConstants.HORIZONTAL);
		optionsSep.setPreferredSize(
				new Dimension(buttonWidth, preferredButtonHeight));
		JSeparator invisiSep = new JSeparator(SwingConstants.HORIZONTAL);
		invisiSep.setPreferredSize(new Dimension(buttonWidth, 0));
		JSeparator prefsSep = new JSeparator(SwingConstants.HORIZONTAL);
		prefsSep.setPreferredSize(
				new Dimension(buttonWidth, preferredButtonHeight));


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
		panel.add(invisiSep);
		panel.add(new JLabel("Time"));
		panel.add(timeSep);
		panel.add(new JLabel("Step: "));
		panel.add(timeInfo);
		panel.add(autoplayButton);
		panel.add(prevStepButton);
		panel.add(nextStepButton);
		panel.add(timeSlider);
		panel.add(prefsSep);
		panel.add(preferencesButton);
		panel.add(statisticsButton);
		panel.add(editorButton);
		panel.add(annotationsButton);
		panel.add(warningsButton);
		panel.add(errorsButton);
		return panel;
	}

	/**
	 * Reloads all external viewers.
	 */
	private void reloadViewers() {
		hardErrorsViewer.reload();
		softErrorsViewer.reload();
		annotationViewer.reload();
	}

	/**
	 * Initializes the tab UI elements.
	 *
	 * @param file
	 * 		the first file to be open
	 */
	private void initializeTabs(final File file) {
		visualizationTabs = new JTabbedPane();
		visualizationTabs.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);

		visualizationTabs.addChangeListener(
				l -> {
					currentViz.scheduleLoadingOfNewFile(
							tabsToFilenames.get(
									((JTabbedPane) l.getSource())
											.getSelectedComponent()));
					// load new file in editor
					editor.setFile(tabsToFilenames.get(
							((JTabbedPane) l.getSource())
									.getSelectedComponent()));
					reloadViewers();
				}
		);

		// nextTabListener
		currentViz.addNextTabListener(
				() -> {
					int nextIndex = visualizationTabs.getSelectedIndex() + 1;
					// wrap around at the end
					if (nextIndex > visualizationTabs.getTabCount() - 1) {
						nextIndex = 0;
					}
					// load new file
					currentViz.scheduleLoadingOfNewFile(
							tabsToFilenames.get(
									visualizationTabs.getComponentAt(nextIndex)
							)
					);
					// load new file in editor
					editor.setFile(
							tabsToFilenames.get(
									visualizationTabs.getComponentAt(nextIndex)
							)
					);
					reloadViewers();
					// change to the correct tab in the ui
					visualizationTabs.setSelectedIndex(nextIndex);
				}
		);

		// previousTabListener
		currentViz.addPreviousTabListener(
				() -> {
					int prevIndex = visualizationTabs.getSelectedIndex() - 1;
					// wrap around at the beginning
					if (prevIndex < 0) {
						prevIndex = visualizationTabs.getTabCount() - 1;
					}
					// load new file
					currentViz.scheduleLoadingOfNewFile(
							tabsToFilenames.get(
									visualizationTabs.getComponentAt(prevIndex)
							)
					);
					// load new file in editor
					editor.setFile(
							tabsToFilenames.get(
									visualizationTabs.getComponentAt(prevIndex)
							)
					);
					reloadViewers();
					// change to the correct tab in the ui
					visualizationTabs.setSelectedIndex(prevIndex);
				}
		);


		visualizationTabs.addMouseListener(new MouseListener() {

			@Override
			public void mouseReleased(final MouseEvent e) {
				// Nothing yet
			}

			@Override
			public void mousePressed(final MouseEvent e) {
				// Nothing yet

			}

			@Override
			public void mouseExited(final MouseEvent e) {
				// Nothing yet

			}

			@Override
			public void mouseEntered(final MouseEvent e) {
				// Nothing yet

			}

			@Override
			public void mouseClicked(final MouseEvent e) {
				logger.debug("got button " + e.getButton() + " in tabs");
				if (e.getButton() == MouseEvent.BUTTON2) {
					closeTab(visualizationTabs.getSelectedIndex());
				}
			}
		});

		addNewTab(file);
	}

	/**
	 * Adds a new tab that contains a certain file. This calls the
	 * scheduleLoadingOfNewFile(file) method for this DesktopLauncher's bioViz
	 * field to open the given file.
	 *
	 * @param fileForTab
	 * 		the file to be opened
	 */
	private void addNewTab(final File fileForTab) {
		File file = fileForTab;
		if (file == null) {
			file = Gdx.files.internal("examples/default_grid.bio").file();


			/*
			file does not exist as we started BioViz from the command line (at
			least that we will assume now.

			So what we do is
				1) open a stream to the file within the jar file
				2) create a temporary file that will be deleted after the JVM
				   stops running this process
				3) copy the content of the stream into that file and use it as
				   a regular file
				4) be annoyed by Java a lot
			 */
			if (!file.exists()) {
				file = getFileFromStream("/examples/default_grid.bio");
			}


		}
		logger.debug("Adding new tab to UI for " + file.getName());
		JPanel dummyPanel = new JPanel();
		dummyPanel.setPreferredSize(new Dimension());
		visualizationTabs.addTab(file.getName(), dummyPanel);
		visualizationTabs.setSelectedIndex(
				visualizationTabs.getTabCount() - 1);
		tabsToFilenames.put(dummyPanel, file);
		editor.setFile(file);
		this.currentViz.scheduleLoadingOfNewFile(file);
	}


	/**
	 * Reloads the tab if a file is reloaded.
	 */
	private void reloadTab() {
		int index = visualizationTabs.getSelectedIndex();
		logger.info("Reloading Tab {}", index);
		File file = tabsToFilenames.get(
				visualizationTabs.getSelectedComponent());

		if (file != null) {
			currentViz.unloadFile(file);
			editor.setFile(file);
			reloadViewers();
			currentViz.scheduleLoadingOfNewFile(file);

		} else {
			logger.info("Nothing to reload");
		}
	}


	/**
	 * Closes a tab at a given index. Notice that by closing tabs at given
	 * indices, there is no definite map from a tab's index to its contents as
	 * consecutive tabs have their indices altered when another tab is closed.
	 *
	 * @param index
	 * 		the index to be closed
	 */
	private void closeTab(final int index) {
		logger.info("Closing Tab {}", index);
		File file = tabsToFilenames.get(
				visualizationTabs.getSelectedComponent());

		if (file != null) {
			currentViz.unloadFile(file);
			tabsToFilenames.remove(visualizationTabs.getSelectedComponent());
			visualizationTabs.removeTabAt(index);
		} else {
			logger.info("Nothing to close");
		}
	}

	/**
	 * Closes the currently opened tab.
	 */
	private void closeTab() {
		int index = visualizationTabs.getSelectedIndex();
		closeTab(index);
	}

	/**
	 * Starting point for the application.
	 *
	 * @param args
	 * 		console arguments, parsed using the {@link Options} class.
	 */
	public static void main(final String[] args) {
		Options opts = new Options();
		CmdLineParser parser = new CmdLineParser(opts);
		try {
			parser.parseArgument(args);
		} catch (final CmdLineException e) {
			String argsLine = String.join(" ", args);
			System.err.println(
                    "Unable to parse arguments: \"" + argsLine + "\"");
			System.err.println("\nusage:");
			parser.printUsage(System.err);
			System.exit(1);
		}

		if (opts.help) {
			parser.printUsage(System.out);
			System.exit(0);
		}
		if (opts.version) {
			System.out.println("This is BioViz version " + BioVizInfo.VERSION);
		}

		if (opts.authors) {
			System.out.println("BioViz is written by:");
			for (final String author : BioVizInfo.authors()) {
				System.out.println("\t" + author);
			}
		}

		if (opts.check != null) {
			startErrorChecker(opts.check);
		}
		// in the end we start the gui. If the option wasn't set, the default
		// file will be opened.
		startGUI(opts.file);
	}

	/**
	 * Checks a BioGram file for errors and prints them to STDOUT.
	 *
	 * @param f
	 * 		The file to check.
	 */
	static void startErrorChecker(final File f) {


		//#########################
		// The following stuff resets the logger so that no unnecessary
		// messages
		// are printed

		// assume SLF4J is bound to logback in the current environment
		LoggerContext context =
				(LoggerContext) LoggerFactory.getILoggerFactory();


		JoranConfigurator configurator = new JoranConfigurator();
		configurator.setContext(context);
		// Call context.reset() to clear any previous configuration, e.g.
		// default
		// configuration. For multi-step configuration, omit calling context
		// .reset().
		context.reset();

		//#########################


		Biochip chip = BioParser.parseFile(f);
		if (!chip.errors.isEmpty()) {
			logger.error("Found errors in file \"{}\":\n", f.getAbsolutePath());
			for (final String error : chip.errors) {
                logger.error(error);
			}
		}
	}

	/**
	 * Starts the BioViz GUI.
	 *
	 * @param file
	 * 		the file to load on startup.
	 */
	static void startGUI(final File file) {
		try {
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					initializeLogback();

					try {
						// Set System L&F
						UIManager.setLookAndFeel(
								UIManager.getSystemLookAndFeelClassName());
					} catch (final UnsupportedLookAndFeelException e) {
						logger.error("System look and feel is unsupported: " +
									 e.getMessage() + "\n" +
									 e.getStackTrace());
					} catch (final Exception e) {
						logger.error(
								"Cannot set look and feel: " + e.getMessage() +
								"\n" + e.getStackTrace());
					}

					JFrame frame = new DesktopLauncher(file);


					singleton.addWindowListener(new WindowAdapter() {
						@Override
						public void windowClosing(final WindowEvent e) {
							singleton.canvas.stop();
						}
					});
				}
			});
		} catch (final Exception e) {
			logger.error("Could not start the application: " +
						 e.getStackTrace());
		}
	}


	/**
	 * @param pathPrefName
	 * 		The name of of value stored in the Preferences object.
	 * @param load
	 * 		if true, opens a 'file open dialog', if false opens a 'file store
	 * 		dialog'
	 * @return File object pointing to the selected file or null
	 * @author Oliver Keszocze
	 */
	private static File askForFile(final String pathPrefName,
								   final boolean load) {
		allowHotkeys = false;

		java.util.prefs.Preferences prefs =
				java.util.prefs.Preferences.userNodeForPackage(DesktopLauncher
																	   .class);
		File path = new File(prefs.get(pathPrefName, "."));
		File selectedPath = null;
		logger.debug("Open file choose with path {}", path);

		JFileChooser fileDialog = new JFileChooser(path);
		int choice;

		if (load) {
			choice = fileDialog.showOpenDialog(DesktopLauncher.singleton);
		} else {
			// add the svg export options as an accessory to the fileChooser
			JPanel accessory = new JPanel(new BorderLayout());

			JCheckBox exportColors = new JCheckBox("Export colors");
			exportColors.setSelected(true);
			JCheckBox exportInfoString = new JCheckBox("Export info tag");
			exportInfoString.setSelected(true);
			JCheckBox exportSeries = new JCheckBox("Export series");
			exportSeries.setSelected(false);

			JPanel checkBoxes = new JPanel(new GridLayout(0, 1));
			checkBoxes.add(exportColors);
			checkBoxes.add(exportInfoString);
			checkBoxes.add(exportSeries);

			accessory.add(checkBoxes);

			fileDialog.setAccessory(accessory);

			choice = fileDialog.showSaveDialog(DesktopLauncher.singleton);

			svgExportSettings.setColorfulExport(exportColors.isSelected());
			svgExportSettings.setExportSeries(exportSeries.isSelected());
			svgExportSettings.setInformationString(
					exportInfoString.isSelected());
		}

		if (choice == JFileChooser.APPROVE_OPTION) {
			selectedPath = fileDialog.getSelectedFile();

			prefs.put(pathPrefName, selectedPath.getAbsolutePath());

		}

		allowHotkeys = true;
		return selectedPath;
	}

	/**
	 * Returns a File object for the given path. The path must start with a '/'
	 * also when it is a local path.
	 *
	 * @param fileName
	 * 		the fileName
	 * @return A File Object if the file exists. Null otherwise.
	 */
	public static File getFileFromStream(final String fileName) {
		File file = null;
		try {
			InputStream in =
					DesktopLauncher.class.getResourceAsStream(fileName);

			if (in != null) {
				file = File.createTempFile(fileName, ".BioViz_tmp");
				file.deleteOnExit();

				OutputStream fout = new FileOutputStream(file);

				byte[] buffer = new byte[1024];

				int length;
				//copy the file content in bytes
				while ((length = in.read(buffer)) > 0) {

					fout.write(buffer, 0, length);

				}


				fout.close();

				// be even more annoyed by java because the following code
				// does *not* work! (why would it..)
				//java.nio.file.Files.copy(in, file.toPath());
			}

		} catch (final IOException e) {
			logger.error("Could not even locate/create default file");
		}

		return file;
	}


	/**
	 * Initializes the logger.
	 */
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
			configurator.doConfigure(
					DesktopLauncher.class.getResourceAsStream(
							"/config/logback.xml"));
		} catch (final JoranException je) {
			// StatusPrinter will handle this
			System.err.println(
					"Error setting up logger: " + je.getStackTrace());
		}


	}

	/**
	 * Displays the settings window.
	 *
	 * @param viz
	 * 		The parent of the settings window.
	 */
	private static void showSettings(final BioViz viz) {
		logger.debug("Opening preferences window...");
		new PreferencesWindow(viz);
		logger.debug("Done opening preferences window.");
	}

	/**
	 * Allows to disable or enable the hotkeys from outside of DesktopLauncher.
	 * @param allow allow hotkeys or not
	 */
	static void setAllowHotkeys(final boolean allow) {
		allowHotkeys = allow;
	}


	/**
	 * Translates a java.awt keycode to a libgdx keycode.
	 *
	 * @param keyCode
	 * 		the awt code
	 * @return the libgdx code
	 */
	protected static int translateKeyCode(final int keyCode) {
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
			return Input.Keys.DEL;
		}
		if (keyCode == java.awt.event.KeyEvent.VK_LEFT) {
			return Input.Keys.DPAD_LEFT;
		}
		if (keyCode == java.awt.event.KeyEvent.VK_RIGHT) {
			return Input.Keys.DPAD_RIGHT;
		}
		if (keyCode == java.awt.event.KeyEvent.VK_UP) {
			return Input.Keys.DPAD_UP;
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

	/**
	 * Used for communicating time changes between visualization core and the
	 * desktop UI.
	 */
	private class TimerCallback implements BioVizEvent {
		/**
		 * The slider element that is used to adjust the simulation time in the
		 * desktop UI.
		 */
		private JSlider time;

		/**
		 * The label that displays some information about the time.
		 */
		private JLabel timeInfo;

		/**
		 * Constructor. Slider and panel are set the the instance of the
		 * callback is added to the visualization's time changed listeners, so
		 * the bioVizEvent method is called when the visualization somehow
		 * alters the current simulation time
		 *
		 * @param slider
		 * 		the slider to display the current time
		 * @param info
		 * 		the label that should give information about the time
		 */
		TimerCallback(final JSlider slider, final JLabel info) {
			this.time = slider;
			this.timeInfo = info;
			currentViz.addTimeChangedListener(this);
		}

		/**
		 * Called when the event happens, in this case the changed time. This
		 * method then sets slider and label to display the according
		 * information
		 */
		@Override
		public void bioVizEvent() {
			logger.trace("Received timer event (" +
						 currentViz.currentBiochip.getCurrentTime() + ")");
			this.time.setValue(currentViz.currentBiochip.getCurrentTime());
			this.timeInfo.setText(
					Integer.toString(
							currentViz.currentBiochip.getCurrentTime()));

		}
	}

	/**
	 * The callback for picking colors.
	 */
	private class ColourPickCallback implements BioVizEvent {

		@Override
		public void bioVizEvent() {
			try {
				SwingUtilities.invokeLater(new Runnable() {
					@Override
					public void run() {
						Color c =
								JColorChooser.showDialog(
										null, "Choose a Color", Color.red);
						currentViz.selectedDroplet.setDropletColor(
								new com.badlogic.gdx.graphics.Color(
										c.getRed() / 255f, c.getGreen() / 255f,
										c.getBlue() / 255f, 1f));
					}
				});
			} catch (final Exception e) {
				logger.error(
						"Could not start colour picker:\n" +
						e.getStackTrace());
			}

		}

	}

	/**
	 * Used to communicate the desire to load a file from the visualization to
	 * the surrounding UI. That means that if this class's bioVizEvent() method
	 * is called, the UI should somehow ask for the file to be loaded (int this
	 * case using the askForFile() method) and then send the according events
	 * back (which is done in the addNewTab() method).
	 *
	 * @author jannis
	 */
	private class LoadFileCallback implements BioVizEvent {

		/**
		 * Default constructor, does nothing.
		 */
		LoadFileCallback() {
		}

		/**
		 * Asks the user which file to load and then adds a new tab.
		 */
		@Override
		public void bioVizEvent() {
			try {
				SwingUtilities.invokeLater(new Runnable() {
					@Override
					public void run() {
						File f = askForFile("lastFilePath", true);
						if (f != null) {
							addNewTab(f);
						}
					}
				});
			} catch (final Exception e) {
				logger.error("Could not load file: " + e.getStackTrace());
			}
		}
	}

	/**
	 * Used to retrieve any close-file actions from the visualization and
	 * closes
	 * the currently opened tab.
	 *
	 * @author jannis
	 */
	private class CloseFileCallback implements BioVizEvent {

		/**
		 * Default constructor, does nothing.
		 */
		CloseFileCallback() {
		}

		/**
		 * Called by the visualization, calls the closeTab() method.
		 */
		@Override
		public void bioVizEvent() {
			DesktopLauncher.singleton.closeTab();
		}
	}

	/**
	 * Used by the visualization to notify the system-specific UI that a new
	 * file has been loaded. This includes switching between tabs, so this is
	 * where all the UI adjustments for switching between tabs goes.
	 *
	 * @author jannis
	 */
	private class LoadedFileCallback implements BioVizEvent {

		/**
		 * Default constructor, does nothing.
		 */
		LoadedFileCallback() {
		}

		/**
		 * Called when a new file is loaded. This includes tabs, so the UI
		 * elements need to be adjusted accordingly (to display the according
		 * info about the current circuit).
		 */
		@Override
		public void bioVizEvent() {
			logger.trace("calling desktop LoadedFileCallback()");
			if (currentViz.currentBiochip != null) {

				reloadViewers();

				logger.trace(
						"Desktop received loaded event, setting slider...");
				int oldTime = currentViz.currentBiochip.getCurrentTime();

				DesktopLauncher d = DesktopLauncher.singleton;

				// altering the max/min values already invokes the timer
				// event, thus altering the currentBiochip's currenTime value.
				// In order to still be able to set the current value as it
				// was before, the oldTime value is being stored above and then
				// used to set the slider's value, thus again reverting the
				// currentBiochip's currentTime value to its original state.
				// This means we're actually changing its time back and forth,
				// but although this is a little ugly, it doesn't seem to have
				// any problematic effect.
				d.timeSlider.setMaximum(
						currentViz.currentBiochip.getData().getMaxT());
				d.timeSlider.setMinimum(1);
				logger.trace("setting time slider to " + oldTime);
				d.timeSlider.setValue(oldTime);

				d.displayRouteLengthSlider.setMaximum(
						currentViz.currentBiochip.getData().getMaxRouteLength());
				d.displayRouteLengthSlider.setMinimum(0);
				d.displayRouteLengthSlider.setValue(0);

				d.setTitle(d.currentViz.getFileName() + " - " + BioVizInfo.PROGNAME);

				logger.debug("Initializing infoPanel.");
				d.infoPanel.refreshPanelData();
			} else {
				logger.trace("Last file closed, no more file to display.");
				DesktopLauncher d = DesktopLauncher.singleton;
				d.timeSlider.setMaximum(1);
				d.timeSlider.setMinimum(1);
				d.timeSlider.setValue(1);

				d.displayRouteLengthSlider.setMaximum(0);
				d.displayRouteLengthSlider.setMinimum(0);
				d.displayRouteLengthSlider.setValue(0);

				d.setTitle(BioVizInfo.PROGNAME);
			}
		}
	}

	/**
	 * Used to communicate the intent of saving the currently open file as a
	 * printable file somewhere.
	 */
	private class SaveFileCallback implements BioVizEvent {

		/**
		 * Empty constructor, does nothing.
		 */
		public SaveFileCallback() {
		}

		/**
		 * Called when the file is supposed to be saved. Asks the user where to
		 * save the file and then calls the visualization to generate the svg
		 * and store it at the given location.
		 */
		@Override
		public void bioVizEvent() {
			allowHotkeys = false;
			try {
				SwingUtilities.invokeLater(new Runnable() {
					public void run() {

						File f = askForFile("saveFolder", false);

						if (f != null) {
							if (svgExportSettings.getExportSeries()) {

								int oldTime = currentViz.currentBiochip
										.getCurrentTime();
								// this is problematic if the file contains
								// .svg inside the name
								int svgPosition = f.getAbsolutePath().indexOf(".svg");
								// initialize with absolute path
								String pathWithoutSuffix = f.getAbsolutePath();
								// check if suffix was found, if not the path
								// is already
								// without a suffix
								if (svgPosition != -1) {
									pathWithoutSuffix =
											f.getAbsolutePath().
													substring(0,
															  svgPosition);
								}
								// create a series of files
								for (int t = 1; t <=
												currentViz.currentBiochip
														.getData().getMaxT();
									 t++) {
									currentViz.saveSVG(
											pathWithoutSuffix + "_ts" + t +
											".svg", t);
								}
								// restore time from start
								currentViz.currentBiochip.setCurrentTime(
										oldTime);
							} else {
								currentViz.saveSVG(f.getAbsolutePath(),
												   currentViz
														   .currentBiochip
														   .getCurrentTime());
							}
						}
					}
				});
			} catch (final Exception e) {
				logger.error("Could not save file: " + e.getMessage() + "\n" +
						e.getStackTrace());
			}
			allowHotkeys = true;
		}
	}

	/**
	 * Needed to fetch *all* pressed keys that are caught somewhere in this
	 * frame in order to pipe them through to the libgdx program within.
	 *
	 * @author Jannis Stoppe
	 */
	private class MyDispatcher implements KeyEventDispatcher {

		/**
		 * Constructor, does nothing.
		 */
		MyDispatcher() {
			// Does nothing
		}

		/**
		 * Sends the received key event to the visualization if the
		 * visualization itself wasn't the active UI element in the first place
		 * just so that user interaction still happens even if e.g. some slider
		 * was clicked beforehand.
		 */
		@Override
		public boolean dispatchKeyEvent(final KeyEvent e) {

			// this should prevent hotkeys to be send from fileChooser dialogs
			if (!allowHotkeys) {
				return false;
			}

			if (input.getInputProcessor() == null) {
				input.setInputProcessor(currentViz.getInputProcessor());
			}
			//Additional check to avoid having events fire twice (once from
			// here and once from libgdx)
			if (DesktopLauncher.singleton.getFocusOwner()
				!= DesktopLauncher.singleton.canvas.getCanvas()) {

				if (e.getID() == KeyEvent.KEY_PRESSED) {
					currentViz.getInputProcessor().keyDown(
							translateKeyCode(e.getKeyCode()));
				} else if (e.getID() == KeyEvent.KEY_RELEASED) {
					currentViz.getInputProcessor().keyUp(
							translateKeyCode(e.getKeyCode()));
				} else if (e.getID() == KeyEvent.KEY_TYPED) {
					// That thing might not have been initiliazed yet
					if (currentViz.getInputProcessor() != null) {
						currentViz.getInputProcessor().keyTyped(e.getKeyChar());
					}
				}
			}
			return false;
		}
	}

	/**
	 * This class implements a custom JCheckBoxMenuItem.
	 */
	private class BioCheckboxMenuItem extends JCheckBoxMenuItem {
		/**
		 * A BDisplayOption to store the checkboxValues.
		 */
		private BDisplayOptions option;

		/**
		 * Constructs a new BioCheckBoxMenuItem.
		 *
		 * @param label the label for the new item
		 * @param option the connected BDisplayOptions item
		 */
		BioCheckboxMenuItem(final String label,
							final BDisplayOptions option) {
			super(label);
			this.option = option;

			this.addActionListener(l -> {
				currentViz.currentBiochip.getDisplayOptions().toggleOption(
						option);
				setState(
						currentViz.currentBiochip.getDisplayOptions()
								.getOption(
										option));
			});
		}

		/**
		 * Updates the state of the selected option.
		 */
		void updateState() {
			setState(currentViz.currentBiochip.
					getDisplayOptions().getOption(option));
		}


	}

}
