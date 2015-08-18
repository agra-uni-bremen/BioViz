package de.bioviz.ui;

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
import com.badlogic.gdx.graphics.glutils.ShaderProgram;

import de.bioviz.messages.MessageCenter;
import de.bioviz.structures.Biochip;
import de.bioviz.parser.BioParser;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.nio.ByteBuffer;
import java.util.Date;
import java.util.Random;
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
	private Vector<BioVizEvent> loadedFileListeners = new Vector<BioVizEvent>();
	private Vector<BioVizEvent> saveFileListeners = new Vector<BioVizEvent>();
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
		batch = new SpriteBatch(1000, this.createDefaultShader());
		
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
			FileHandle fh = Gdx.files.getFileHandle("examples/default_grid.bio", Files.FileType.Internal);
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
			logger.info("Done loading file {}", filename);
			currentCircuit.zoomExtents();
		} catch (Exception e) {
			logger.error("Could not load " + BioViz.singleton.filename + ": " + e.getMessage());
			e.printStackTrace();
		}
		// clear on screen messages as they would otherwise remain visible
		mc.clearHUDMessages();
		

		this.callLoadedFileListeners();
	}
	
	public static void loadNewFile(File f) {
		logger.trace("Scheduling loading of file " + f);
		BioViz.singleton.filename = f;
		BioViz.singleton.loadFileOnUpdate = true;
	}
	
	public void addTimeChangedListener(BioVizEvent listener) {
		timeChangedListeners.add(listener);
	}
	
	private void callTimeChangedListeners() {
		logger.trace("Calling " + this.loadedFileListeners.size() + " listeners for timeChanged");
		for (BioVizEvent listener : this.timeChangedListeners) {
			listener.bioVizEvent();
		}
	}
	
	public void addLoadFileListener(BioVizEvent listener) {
		loadFileListeners.add(listener);
	}
	
	void callLoadFileListeners() {
		logger.debug("Calling " + this.loadedFileListeners.size() + " listeners for load");
		for (BioVizEvent listener : this.loadFileListeners) {
			listener.bioVizEvent();
		}
	}
	public void addLoadedFileListener(BioVizEvent listener) {
		loadedFileListeners.add(listener);
	}
	
	void callLoadedFileListeners() {
		logger.trace("Calling " + this.loadedFileListeners.size() + " listeners for loaded");
		for (BioVizEvent listener : this.loadedFileListeners) {
			listener.bioVizEvent();
		}
	}
	
	public void addSaveFileListener(BioVizEvent listener) {
		saveFileListeners.add(listener);
	}
	
	void callSaveFileListeners() {
		logger.debug("Calling " + this.saveFileListeners.size() + " listeners for save");
		for (BioVizEvent listener : this.saveFileListeners) {
			listener.bioVizEvent();
		}
	}
	
	public void saveSVG(String path) {
		try {
			String svg = BioViz.singleton.currentCircuit.generateSVG();
			FileHandle handle = Gdx.files.absolute(path);
			handle.writeString(svg, false);
			logger.info("Stored SVG at {}", handle.path());
		} catch (Exception e) {
			logger.error("Could not store SVG: {}", e.getMessage());
		}
	}

	static public ShaderProgram createDefaultShader () {
		String vertexShader = "attribute vec4 " + ShaderProgram.POSITION_ATTRIBUTE + ";\n" //
				+ "attribute vec4 " + ShaderProgram.COLOR_ATTRIBUTE + ";\n" //
				+ "attribute vec2 " + ShaderProgram.TEXCOORD_ATTRIBUTE + "0;\n" //
				+ "uniform mat4 u_projTrans;\n" //
				+ "varying vec4 v_color;\n" //
				+ "varying vec2 v_texCoords;\n" //
				+ "\n" //
				+ "void main()\n" //
				+ "{\n" //
				+ "   v_color = " + ShaderProgram.COLOR_ATTRIBUTE + ";\n" //
				+ "   v_color.a = v_color.a * (255.0/254.0);\n" //
				+ "   v_texCoords = " + ShaderProgram.TEXCOORD_ATTRIBUTE + "0;\n" //
				+ "   gl_Position =  u_projTrans * " + ShaderProgram.POSITION_ATTRIBUTE + ";\n" //
				+ "}\n";
		String fragmentShader = "#ifdef GL_ES\n" //
				+ "#define LOWP lowp\n" //
				+ "precision mediump float;\n" //
				+ "#else\n" //
				+ "#define LOWP \n" //
				+ "#endif\n" //
				+ "varying LOWP vec4 v_color;\n" //
				+ "varying vec2 v_texCoords;\n" //
				+ "uniform sampler2D u_texture;\n" //
				+ "void main()\n"//
				+ "{\n" //
				+ "  vec4 fctr = texture2D(u_texture, v_texCoords);" //
				+ "  fctr = abs(fctr - 0.5) * -2.0 + 1.0;" //
				+ "  gl_FragColor = v_color * fctr + texture2D(u_texture, v_texCoords) * (1.0 - fctr);\n" //
				+ "  gl_FragColor.a = texture2D(u_texture, v_texCoords).a * v_color.a;"//
				+ "}";

		ShaderProgram shader = new ShaderProgram(vertexShader, fragmentShader);
		if (shader.isCompiled() == false) throw new IllegalArgumentException("Error compiling shader: " + shader.getLog());
		return shader;
	}
	
	public FileHandle getApplicationIcon() {
		Random rnd = new Random();
		int r = rnd.nextInt(3);
		FileHandle handle;
		if (r == 0)
			handle = Gdx.files.internal("Droplet.png");
		else if (r == 1)
			handle = Gdx.files.internal("Source.png");
		else
			handle = Gdx.files.internal("Sink.png");
		return handle;
	}
}
