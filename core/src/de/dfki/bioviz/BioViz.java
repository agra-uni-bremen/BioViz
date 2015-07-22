package de.dfki.bioviz;

import java.awt.event.KeyEvent;
import java.nio.ByteBuffer;
import java.util.Date;
import java.util.Vector;

import javax.swing.JFileChooser;

import de.dfki.bioviz.structures.Biochip;
import de.dfki.bioviz.structures.Droplet;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Files.FileType;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.assets.AssetManager;
//import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.PixmapIO;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class BioViz implements ApplicationListener {
	public OrthographicCamera camera;
	public SpriteBatch batch;
	
	
	public DrawableCircuit currentCircuit;
	public static BioViz singleton;
	private Vector<Drawable> drawables = new Vector<Drawable>();
	
	AssetManager manager = new AssetManager();
	public MessageCenter mc = new MessageCenter();
	
	private String filename;
	private BioVizInputProcessor inputProcessor;
	public InputProcessor getInputProcessor(){return inputProcessor;}
	
	boolean runFullPresetScreenshots = false;
	float fullPresetScreenshotsScaling = 6f;
	

	private Vector<BioVizEvent> timeChangedListeners = new Vector<BioVizEvent>();
	
	public BioViz() {
		super();
		mc.addMessage("Starting without filename being specified; loading example.\nUsage: java -jar BioViz.jar <filename>", MessageCenter.SEVERITY_INFO);
		singleton = this;
	}
	
	public BioViz(String filename) {
		this();
		mc.addMessage("Starting BiochipVis, loading currently disabled", MessageCenter.SEVERITY_WARNING);
		this.filename = filename;
	}
	
	@Override
	public void create() {
		
//	    File path = new File("assets/textures");
//
//	    File [] files = path.listFiles();
//	    for (int i = 0; i < files.length; i++){
//	        if (files[i].isFile() && files[i].getName().endsWith(".png")){
//	            System.out.println("loading " + files[i].toString() + " as texture");
//	            manager.load(files[i].toString(), Texture.class);
//	        }
//	    }
	    
//		manager.finishLoading();
		
		float w = Gdx.graphics.getWidth();
		float h = Gdx.graphics.getHeight();
		
		camera = new OrthographicCamera(1, h/w);
		batch = new SpriteBatch();
		
		Biochip c;
		//TODO loading stuff
		c = new Biochip(10, 5);
		c.disableFieldAt(9, 4);
		c.disableFieldAt(8, 4);
		c.disableFieldAt(9, 3);
		currentCircuit = new DrawableCircuit(c);
		drawables.add(currentCircuit);
		
		currentCircuit.addTimeChangedListener(() -> BioViz.singleton.callTimeChangedListeners());
		
		Droplet b = c.addDroplet();
		b.addPosition(0, 0, 0);
		b.addPosition(1, 1, 0);
		b.addPosition(2, 1, 1);
		b.addPosition(5, 2, 1);
		
		Droplet b2 = c.addDroplet();
		b2.addPosition(0, 0, 3);
		b2.addPosition(1, 1, 3);
		b2.addPosition(2, 2, 3);
		b2.addPosition(3, 2, 2);
		
		c.recalculateAdjacency = true;
		
		inputProcessor = new BioVizInputProcessor();
		Gdx.input.setInputProcessor(inputProcessor);
		
		//this.menu = new Menu();
		//this.drawables.add(menu);
		
		mc.addMessage("BiochipVis started", MessageCenter.SEVERITY_INFO);
	}

	@Override
	public void dispose() {
		batch.dispose();
		manager.dispose();
		Gdx.app.exit();
	}

	@Override
	public void render() {
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
		if (firstRun) {
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
		mc.addMessage("saved screenshot: " + fh.path(), MessageCenter.SEVERITY_INFO);
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
	
	public static void loadNewFile() {
		final JFileChooser fc = new JFileChooser();
		fc.showOpenDialog(null);
		String filename = fc.getSelectedFile().toString();
		try {
			Biochip c;
			if (filename != null && filename.equals("")) {
//				c = RevlibFileReader.readRealFile(filename);
//				RevVisGDX.singleton.drawables.remove(RevVisGDX.singleton.currentCircuit);
//				RevVisGDX.singleton.currentCircuit = new DrawableCircuitReordered(c);
//				RevVisGDX.singleton.drawables.add(RevVisGDX.singleton.currentCircuit);
//				RevVisGDX.singleton.currentCircuit.zoomExtents();
			} else {
				System.out.println("Error: could not load " + filename);
			}
		} catch (Exception e) {
			System.out.println("Error: could not load " + filename);
		}
	}
	
	public void addTimeChangedListener(BioVizEvent listener) {
		timeChangedListeners.add(listener);
	}
	
	private void callTimeChangedListeners() {
		for (BioVizEvent listener : this.timeChangedListeners) {
			listener.bioVizEvent();
		}
	}
}
