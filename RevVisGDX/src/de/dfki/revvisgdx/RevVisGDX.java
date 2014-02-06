package de.dfki.revvisgdx;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Date;
import java.util.Vector;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Files.FileType;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.PixmapIO;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;

import de.dfki.revlibReader.ReversibleCircuit;
import de.dfki.revlibReader.RevlibFileReader;
import de.dfki.revlibReader.optimization.OrderOptimizer;

public class RevVisGDX implements ApplicationListener {
	public OrthographicCamera camera;
	public SpriteBatch batch;
	
	
	public DrawableCircuit currentCircuit;
	public static RevVisGDX singleton;
	private Vector<Drawable> drawables = new Vector<Drawable>();
	
	MessageCenter mc = new MessageCenter();
	
	private String filename;
	
	public RevVisGDX() {
		super();
	}
	
	public RevVisGDX(String filename) {
		this();
		this.filename = filename;
	}
	
	@Override
	public void create() {		
		singleton = this;
		float w = Gdx.graphics.getWidth();
		float h = Gdx.graphics.getHeight();
		
		camera = new OrthographicCamera(1, h/w);
		batch = new SpriteBatch();
		
		ReversibleCircuit c;
		if (this.filename != null && this.filename != "") {
			c = RevlibFileReader.readRealFile(filename);
		} else {
			FileHandle handle = Gdx.files.internal("examples/cm85a_209.real");
//			FileHandle handle = Gdx.files.internal("examples/cpu.real");
			String fileContents = handle.readString();
			c = RevlibFileReader.readRealFileContents(fileContents);
			//c = RevlibFileReader.readRealFile("bin/examples/apex2_289.real");	
		}
		currentCircuit = new DrawableCircuit(c);
		drawables.add(currentCircuit);
		
		System.out.println("Total distance: " + c.totalDistance());
		
		//OrderOptimizer.optimizeNN(c);
		
		System.out.println("Total distance NN: " + c.totalDistance());
		
		RevVisInputProcessor inputProcessor = new RevVisInputProcessor();
		Gdx.input.setInputProcessor(inputProcessor);
		
		mc.addMessage("RevVisGDX started");
		mc.addMessage(Messages.helpText);
		
		currentCircuit.zoomExtents();
	}

	@Override
	public void dispose() {
		batch.dispose();
	}

	@Override
	public void render() {		
		Gdx.gl.glClearColor(1, 1, 1, 1);
		Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
		camera.update();
		
        camera.apply(Gdx.gl10);
 
        // clear previous frame
        Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
		
		batch.setProjectionMatrix(camera.combined);
		batch.begin();
		
		for (int i = 0; i < drawables.size(); i++) {
			drawables.get(i).draw();
		}
		
		mc.render();
		
		batch.end();
	}

	@Override
	public void resize(int width, int height) {
		camera.viewportHeight = height;
		camera.viewportWidth = width;
//		camera.update();
		//camera = new OrthographicCamera(1, height/width);
	}

	@Override
	public void pause() {
	}

	@Override
	public void resume() {
	}
	
//	public void saveScreenshotCircuit() {
//		int viewportWidth = (int)(this.currentCircuit.data.getGates().size());
//		int viewportHeight = (int)(this.currentCircuit.data.getAmountOfVars());
//		Gdx.graphics.setDisplayMode(viewportWidth, viewportHeight, false);
//		RevVisGDX.singleton.camera.viewportWidth = viewportWidth;
//		RevVisGDX.singleton.camera.viewportHeight= viewportHeight;
//		RevVisGDX.singleton.camera.update();
//        
//		this.currentCircuit.setScaleImmediately(1, 1);
//		this.currentCircuit.zoomExtentsImmediately();
//		
//		this.render();
//		
//		saveScreenshotFull();
//	}
	
	public void saveScreenshotCircuit() {
		this.mc.hidden = true;
		this.currentCircuit.offsetY = (this.currentCircuit.data.getAmountOfVars() + Gdx.graphics.getHeight() * (1f / currentCircuit.getScaleY())) / 2f;

		while(this.currentCircuit.offsetY > -this.currentCircuit.data.getAmountOfVars() + Gdx.graphics.getHeight() * (1f / currentCircuit.getScaleY()) / 2f) {
			this.currentCircuit.offsetX = 0;
			while(this.currentCircuit.offsetX > - (this.currentCircuit.data.getGates().size() + Gdx.graphics.getWidth() * (1f / currentCircuit.getScaleX()))) {
				saveScreenshotFull();
				this.currentCircuit.offsetX -= Gdx.graphics.getWidth() * (1f / currentCircuit.getScaleX());
			}
			this.currentCircuit.offsetY -= Gdx.graphics.getHeight() * (1f / currentCircuit.getScaleY());
		}
	}
	
	private static int screenshotCount = 0;
	public void saveScreenshotFull() {
		render();
		FileHandle fh = Gdx.files.getFileHandle("screenshots/scr" + new Date().getTime() + "_" + screenshotCount + ".png", FileType.Local);
		screenshotCount++;
		saveScreenshot(fh, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		mc.addMessage("saved screenshot: " + fh.path());
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
		Gdx.gl.glPixelStorei(GL10.GL_PACK_ALIGNMENT, 1);

		final Pixmap pixmap = new Pixmap(w, h, Format.RGBA8888);
		ByteBuffer pixels = pixmap.getPixels();
		Gdx.gl.glReadPixels(x, y, w, h, GL10.GL_RGBA, GL10.GL_UNSIGNED_BYTE, pixels);

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
}
