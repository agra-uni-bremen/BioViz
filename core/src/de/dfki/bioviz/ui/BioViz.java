package de.dfki.bioviz.ui;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Files;
import com.badlogic.gdx.Files.FileType;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.PixmapIO;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import de.dfki.bioviz.messages.MessageCenter;
import de.dfki.bioviz.parser.BioParser;
import de.dfki.bioviz.structures.Biochip;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;

import java.io.File;
import java.nio.ByteBuffer;
import java.util.Date;
import java.util.Vector;



public class BioViz implements ApplicationListener {
	public OrthographicCamera camera;
	public SpriteBatch batch;
	
	
	public DrawableCircuit currentCircuit;
	public static BioViz singleton;
	private Vector<Drawable> drawables = new Vector<Drawable>();
	
	AssetManager manager = new AssetManager();
	public MessageCenter mc = new MessageCenter();
	
	private File filename;
	private BioVizInputProcessor inputProcessor;
	public InputProcessor getInputProcessor(){return inputProcessor;}
	
	boolean runFullPresetScreenshots = false;
	float fullPresetScreenshotsScaling = 6f;
	

	private Vector<BioVizEvent> timeChangedListeners = new Vector<BioVizEvent>();
	private Vector<BioVizEvent> loadFileListeners = new Vector<BioVizEvent>();
	static Logger logger = LoggerFactory.getLogger(BioViz.class);
	
	private boolean loadFileOnUpdate = true;

	public BioViz() {
		super();
		logger.info("Starting withouth filename being specified; loading example");
		logger.info("Usage: java -jar BioViz.jar <filename>");

		this.filename = null;

		singleton = this;
	}
	
	public BioViz(File filename) {
		this();
		logger.info("Starting with file \"{}\"",filename.getAbsolutePath());
		this.filename = filename;
	}
	
	@Override
	public void create() {
		

		float w = Gdx.graphics.getWidth();
		float h = Gdx.graphics.getHeight();
		
		camera = new OrthographicCamera(1, h/w);
		batch = new SpriteBatch();
		
		inputProcessor = new BioVizInputProcessor();
		Gdx.input.setInputProcessor(inputProcessor);
		
		//this.menu = new Menu();
		//this.drawables.add(menu);
		logger.trace("BioViz started");
	}

	@Override
	public void dispose() {
		batch.dispose();
		manager.dispose();
		Gdx.app.exit();
	}

	@Override
	public void render() {
		
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

		mc.render();

		batch.end();
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
		FileHandle fh = Gdx.files.getFileHandle("screenshots/" + prefix + "" + new Date().getTime() + "_" + screenshotCount + ".png", FileType.Local);
		screenshotCount++;
		saveScreenshot(fh, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		logger.info("Saved screenshot to {}", fh.path());
	}
	public void saveScreenshotFull() {
		saveScreenshotFull("");
	}

	/**
	 * Taken from http://code.google.com/p/libgdx-users/wiki/Screenshots
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
		Gdx.gl.glReadPixels(x, y, w, h, GL20.GL_RGBA, GL20.GL_UNSIGNED_BYTE, pixels);

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
		} else {
			pixels.clear();
			pixels.get(lines);
		}

		return pixmap;
	}
	
	private void loadNewFileNow() {
		logger.debug("Now loading file " + filename);
		Biochip bc;
		if (BioViz.singleton.filename == null) {
			FileHandle fh = Gdx.files.getFileHandle("default_grid.bio", Files.FileType.Internal);
			bc = BioParser.parse(fh.readString());
		} else {
			bc = BioParser.parseFile(filename);
		}
		
		try {
			logger.debug("loaded file, creating drawable elements...");
			DrawableCircuit newCircuit = new DrawableCircuit(bc);
			logger.debug("drawable created, replacing old elements...");
			drawables.remove(currentCircuit);
			currentCircuit = newCircuit;
			drawables.add(currentCircuit);
			logger.debug("Initializing circuit");
			currentCircuit.addTimeChangedListener(() -> callTimeChangedListeners());
			currentCircuit.data.recalculateAdjacency = true;
			logger.info("Done loading file");
			currentCircuit.zoomExtents();
		} catch (Exception e) {
			logger.error("Could not load " + BioViz.singleton.filename + ": " + e.getMessage());
			e.printStackTrace();
		}
		logger.debug("Done loading file " + filename);
	}
	
	public static void loadNewFile(File f) {
		logger.info("Scheduling loading of file " + f);
		BioViz.singleton.filename = f;
		BioViz.singleton.loadFileOnUpdate = true;
	}
	
	public void addTimeChangedListener(BioVizEvent listener) {
		timeChangedListeners.add(listener);
	}
	
	private void callTimeChangedListeners() {
		for (BioVizEvent listener : this.timeChangedListeners) {
			listener.bioVizEvent();
		}
	}
	
	public void addLoadFileListener(BioVizEvent listener) {
		loadFileListeners.add(listener);
	}
	
	void callLoadFileListeners() {
		for (BioVizEvent listener : this.loadFileListeners) {
			listener.bioVizEvent();
		}
	}
}
