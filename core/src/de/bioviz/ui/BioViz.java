package de.bioviz.ui;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Files;
import com.badlogic.gdx.Files.FileType;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.PixmapIO;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;

import de.bioviz.messages.MessageCenter;
import de.bioviz.messages.MsgAppender;
import de.bioviz.structures.Biochip;
import de.bioviz.structures.Droplet;
import de.bioviz.parser.BioParser;
import de.bioviz.svg.SVGManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.nio.ByteBuffer;
import java.util.Date;
import java.util.HashMap;
import java.util.Random;
import java.util.Vector;


public class BioViz implements ApplicationListener {
	public OrthographicCamera camera;
	public SpriteBatch batch;
	
	/**
	 * Sets the duration of intermediate animations in ms.
	 */
	public static void setAnimationDuration(int value) {
		Droplet.movementTransitionDuration = value;
		DrawableSprite.setColorTransitionDuration(value);
	}

	/**
	 * Returns the duration of intermediate animations in ms.
	 * @return Duration of intermediate animations in ms.
	 */
	public static int getAnimationDuration() {
		return Droplet.movementTransitionDuration;
	}


	public DrawableCircuit currentCircuit;
	private HashMap<String, DrawableCircuit> loadedCircuits;
	private Vector<Drawable> drawables = new Vector<Drawable>();

	public TextureManager textures;
	public MessageCenter messageCenter;

	private File bioFile;
	private BioVizInputProcessor inputProcessor;
	private SVGManager svgManager;
	public DrawableDroplet selectedDroplet;

	/**
	 * This stores the last time a frame was rendered. Used to limit the
	 * framerate on faster systems to save resources.
	 */
	private long lastRenderTimestamp = 0;

	/**
	 * The desired framerate in fps.
	 */
	private int targetFramerate = 60;

	public String getFileName() {
		if (bioFile == null) {
			return "Default example";
		}
		else {
			return bioFile.getName();
		}
	}

	public InputProcessor getInputProcessor() {
		return inputProcessor;
	}


	/**
	 * This method creates an SVGManager if not already present. The problem is
	 * that the manager tries to access the Gdx.files object, which is not
	 * available when creating a BioViz instance.
	 */
	public void spawnSVGManager() {
		if (svgManager == null) {
			svgManager = new SVGManager();
		}
	}

	boolean runFullPresetScreenshots = false;
	float fullPresetScreenshotsScaling = 6f;


	private Vector<BioVizEvent> timeChangedListeners =
			new Vector<BioVizEvent>();
	private Vector<BioVizEvent> loadFileListeners = new Vector<BioVizEvent>();
	private Vector<BioVizEvent> loadedFileListeners = new
			Vector<BioVizEvent>();
	private Vector<BioVizEvent> saveFileListeners = new Vector<BioVizEvent>();
	private Vector<BioVizEvent> closeFileListeners = new Vector<BioVizEvent>();
	private Vector<BioVizEvent> pickColorListeners = new Vector<BioVizEvent>();
	static Logger logger = LoggerFactory.getLogger(BioViz.class);

	private boolean loadFileOnUpdate = true;

	public BioViz() {
		super();
		logger.info(
				"Starting withouth filename being specified; loading example");
		logger.info("Usage: java -jar BioViz.jar <filename>");
		this.bioFile = null;
		loadedCircuits = new HashMap<>();
		textures = new TextureManager();
		
		// Weird static assignment needed to enable parameterless construction
		// of appender in order to allow its creation from logger framework
		MsgAppender.setMessageViz(this);
	}

	public BioViz(File bioFile) {
		this();
		logger.info("Starting with file \"{}\"", bioFile.getAbsolutePath());
		this.bioFile = bioFile;
	}

	@Override
	public void create() {
		messageCenter = new MessageCenter(this);


		float w = Gdx.graphics.getWidth();
		float h = Gdx.graphics.getHeight();

		camera = new OrthographicCamera(1, h / w);
		batch = new SpriteBatch(1000, this.createDefaultShader());

		inputProcessor = new BioVizInputProcessor(this);
		Gdx.input.setInputProcessor(inputProcessor);
		
		DrawableLine.singleton = new DrawableLine(this);

		//this.menu = new Menu();
		//this.drawables.add(menu);
		logger.trace("BioViz started");
	}

	@Override
	public void dispose() {
		batch.dispose();
		// TODO: Dispose textures?
		Gdx.app.exit();
	}

	@Override
	public synchronized void render() {

		long currentTimestamp = new Date().getTime();


		if (loadFileOnUpdate) {
			loadNewFileNow();
			loadFileOnUpdate = false;
		}

		Gdx.gl.glClearColor(1, 1, 1, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		camera.update();

		//camera.apply(Gdx.gl20);

		// clear previous frame
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		batch.setProjectionMatrix(camera.combined);
		batch.begin();

		for (Drawable drawable : drawables) {
			drawable.draw();
		}
		
		DrawableLine.drawNow();

		messageCenter.render();

		batch.end();

		long waitUntil = currentTimestamp + (1000 / this.targetFramerate);
		try {
			Thread.sleep(Math.max(0, waitUntil - new Date().getTime()));
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private boolean firstRun = true;

	@Override
	public void resize(int width, int height) {
		camera.viewportHeight = height;
		camera.viewportWidth = width;
		if (firstRun && currentCircuit != null) {
			currentCircuit.zoomExtents();
			firstRun = false;
		}
	}

	@Override
	public void pause() {
	}

	@Override
	public void resume() {
	}


	private static int screenshotCount = 0;

	public void saveScreenshotFull(String prefix) {
		render();
		FileHandle fh = Gdx.files.getFileHandle(
				"screenshots/" + prefix + "" + new Date().getTime() + "_" +
				screenshotCount + ".png", FileType.Local);
		screenshotCount++;
		saveScreenshot(fh, 0, 0, Gdx.graphics.getWidth(),
					   Gdx.graphics.getHeight());
		logger.info("Saved screenshot to {}", fh.path());
	}

	public void saveScreenshotFull() {
		saveScreenshotFull("");
	}

	/**
	 * Taken from http://code.google.com/p/libgdx-users/wiki/Screenshots
	 *
	 * @param file
	 * @param x
	 * @param y
	 * @param w
	 * @param h
	 */
	private void saveScreenshot(FileHandle file, int x, int y, int w, int h) {
		Pixmap pixmap = getScreenshot(x, y, w, h, true);
		PixmapIO.writePNG(file, pixmap);
		pixmap.dispose();
	}

	/**
	 * Taken from http://code.google.com/p/libgdx-users/wiki/Screenshots
	 *
	 * @param x
	 * @param y
	 * @param w
	 * @param h
	 * @param flipY
	 * @return
	 */
	private Pixmap getScreenshot(int x, int y, int w, int h, boolean flipY) {
		Gdx.gl.glPixelStorei(GL20.GL_PACK_ALIGNMENT, 1);

		final Pixmap pixmap = new Pixmap(w, h, Format.RGBA8888);
		ByteBuffer pixels = pixmap.getPixels();
		Gdx.gl.glReadPixels(x, y, w, h, GL20.GL_RGBA, GL20.GL_UNSIGNED_BYTE,
							pixels);

		final int numBytes = w * h * 4;
		byte[] lines = new byte[numBytes];
		if (flipY) {
			final int numBytesPerLine = w * 4;
			for (int i = 0; i < h; i++) {
				pixels.position((h - i - 1) * numBytesPerLine);
				pixels.get(lines, i * numBytesPerLine, numBytesPerLine);
			}
			pixels.clear();
			pixels.put(lines);
		}
		else {
			pixels.clear();
			pixels.get(lines);
		}

		return pixmap;
	}

	public void unloadFile(File f) {
		try {
			if (this.loadedCircuits.containsKey(f.getCanonicalPath())) {
				this.loadedCircuits.remove(f.getCanonicalPath());
			}
		} catch (Exception e) {
			logger.error("Could not unload file " + f);
		}
	}

	private void loadNewFileNow() {
		Biochip bc;
		boolean error = false;
		String errorMsg = "";
			

		try {
			drawables.remove(currentCircuit);
			if (bioFile != null) {
				if (this.loadedCircuits.containsKey(
						bioFile.getCanonicalPath())) {
					logger.debug("re-fetching previously loaded file " +
								 bioFile.getCanonicalPath());
					currentCircuit =
							this.loadedCircuits.get(bioFile.getCanonicalPath
									());
				}
				else {
					logger.debug("Loading {}", bioFile);
					bc = BioParser.parseFile(bioFile, this);

					if (bc == null) {
						error = true;
						errorMsg = "Could not parse file" + bioFile;
						bc = new Biochip();
					}
					logger.debug("loaded file, creating drawable elements...");
					DrawableCircuit newCircuit = new DrawableCircuit(bc, this);
					currentCircuit = newCircuit;
					this.loadedCircuits.put(bioFile.getCanonicalPath(),
							newCircuit);
					currentCircuit.zoomExtents();
				}
				logger.debug("drawable created, replacing old elements...");
				drawables.add(currentCircuit);
				logger.debug("Initializing circuit");
				currentCircuit.data.recalculateAdjacency = true;
				if (bioFile == null) {
					logger.info("Done loading default file");
				}
				else {
					logger.info("Done loading file {}", bioFile);
				}
			} else {
				logger.debug(
						"File to be set is empty, setting empty " +
						"visualization" +
						".");
				currentCircuit = new DrawableCircuit(new Biochip(), this);
			}
		} catch (Exception e) {
			error = true;
			errorMsg = "Could not load " + bioFile + ": " + e.getMessage();

			//e.printStackTrace();
		}

		if (error) {
			logger.error(errorMsg);
		}

		// clear on screen messages as they would otherwise remain visible
		messageCenter.clearHUDMessages();


		this.callLoadedFileListeners();
	}

	public void loadNewFile(File f) {
		logger.debug("Scheduling loading of file " + f);
		bioFile = f;
		loadFileOnUpdate = true;
	}

	public void addTimeChangedListener(BioVizEvent listener) {
		timeChangedListeners.add(listener);
	}

	public void callTimeChangedListeners() {
		logger.trace("Calling " + this.loadedFileListeners.size() +
					 " listeners for timeChanged");
		for (BioVizEvent listener : this.timeChangedListeners) {
			listener.bioVizEvent();
		}
	}

	public void addLoadFileListener(BioVizEvent listener) {
		loadFileListeners.add(listener);
	}

	void callLoadFileListeners() {
		logger.trace("Calling " + this.loadFileListeners.size() +
					 " listeners for load");
		for (BioVizEvent listener : this.loadFileListeners) {
			listener.bioVizEvent();
		}
	}

	public void addLoadedFileListener(BioVizEvent listener) {
		loadedFileListeners.add(listener);
	}

	void callLoadedFileListeners() {
		logger.trace("Calling " + this.loadedFileListeners.size() +
					 " listeners for loaded");
		for (BioVizEvent listener : this.loadedFileListeners) {
			listener.bioVizEvent();
		}
	}

	public void addSaveFileListener(BioVizEvent listener) {
		saveFileListeners.add(listener);
	}

	void callSaveFileListeners() {
		logger.trace("Calling " + this.saveFileListeners.size() +
					 " listeners for save");
		for (BioVizEvent listener : this.saveFileListeners) {
			listener.bioVizEvent();
		}
	}

	public void addCloseFileListener(BioVizEvent listener) {
		closeFileListeners.add(listener);
	}

	void callCloseFileListeners() {
		logger.trace("Calling " + this.closeFileListeners.size() +
					 " listeners for close");
		for (BioVizEvent listener : this.closeFileListeners) {
			listener.bioVizEvent();
		}
	}
	
	public void addPickColourListener(BioVizEvent listener) {
		pickColorListeners.add(listener);
	}

	void callPickColourListeners() {
		logger.trace("Calling " + this.closeFileListeners.size() +
				" listeners for picking a colour");
		for (BioVizEvent listener : this.pickColorListeners) {
			listener.bioVizEvent();
		}
	}

	public void saveSVG(String path, int timeStep) {
		spawnSVGManager();
		logger.debug("[SVG] Within saveSVG(String) method");
		logger.debug("[SVG] svgManager: {}",svgManager);

		try {
			String svg = svgManager.toSVG(currentCircuit, timeStep);
			//logger.debug("[SVG] generated SVG: {}",svg);
			FileHandle handle = Gdx.files.absolute(path);
			logger.debug("[SVG] File handle for storing the SVG: {}",handle);
			handle.writeString(svg, false);
			logger.info("[SVG] Stored SVG at {}", handle.path());
		} catch (Exception e) {
			logger.error("[SVG] Could not store SVG; exception message: {}", e);
		}
	}

	static public ShaderProgram createDefaultShader() {
		FileHandle vertexShaderHandle = new FileHandle("vertexShader.shd");
		FileHandle fragmentShaderHandle = new FileHandle("fragmentShader.shd");

		ShaderProgram shader = new ShaderProgram(vertexShaderHandle, fragmentShaderHandle);
		if (shader.isCompiled() == false) {
			throw new IllegalArgumentException(
					"Error compiling shader: " + shader.getLog());
		}
		return shader;
	}

	public FileHandle getApplicationIcon() {
		Random rnd = new Random();
		int r = rnd.nextInt(3);
		FileHandle handle;
		if (r == 0) {
			handle = textures.getFileHandle(TextureE.Droplet);
		}
		else if (r == 1) {
			handle = textures.getFileHandle(TextureE.Dispenser);
		}
		else {
			handle = textures.getFileHandle(TextureE.Sink);
		}
		logger.debug("Setting application icon to " + handle.name());
		return handle;
	}


}
