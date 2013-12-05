package de.dfki.revvisgdx;

import java.util.Vector;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

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
	
	public static String helpText = "p: pixel wide lines\ng: garbage lines color\nn: neighbouring targets as groups\nh: hide gates\nc: colorize constant inputs\ns: square alignment\nmouse wheel: zoom horizontally\nmouse wheel + ctrl: zoom vertically\nleft drag: move viewport";
	
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
			FileHandle handle = Gdx.files.internal("examples/apex2_289.real");
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
		mc.addMessage(helpText);
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
}
