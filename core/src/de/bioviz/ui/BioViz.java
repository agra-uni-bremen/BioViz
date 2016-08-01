package de.bioviz.ui;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import de.bioviz.messages.MessageCenter;
import de.bioviz.messages.MsgAppender;
import de.bioviz.parser.BioParser;
import de.bioviz.structures.Biochip;
import de.bioviz.svg.SVGManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.Vector;


public class BioViz implements ApplicationListener {

    /**
     * The internal logger.
     */
    private static Logger logger = LoggerFactory.getLogger(BioViz.class);



    public BioVizSpriteBatch batch;

	/**
     * Handles displaying of messages during interactive exploration of biochips.
     */
    public MessageCenter messageCenter;
    public DrawableDroplet selectedDroplet;


	/**
     * The currently displayed biochip.
     */
    public DrawableCircuit currentBiochip;

    OrthographicCamera camera;

	/**
     * Manages the textures that are used to display the biochip.
     */
    private TextureManager textures;
    private HashMap<String, DrawableCircuit> loadedBiochips;
    private Vector<Drawable> drawables = new Vector<>();


    private File bioFile;
    private BioVizInputProcessor inputProcessor;

	/**
     * Manages exporting the biochips to svg images.
     */
    private SVGManager svgManager;

    /**
     * The desired framerate in fps.
     * The update/render thread is laid to sleep if the machine renders too
     * fast, thus saving cpu cycles and letting fans calm down a little.
     */
    private int targetFramerate = 60;


    private List<BioVizEvent> timeChangedListeners = new ArrayList<>();
    private List<BioVizEvent> loadFileListeners = new ArrayList<>();
    private List<BioVizEvent> reloadFileListeners = new ArrayList<>();
    private List<BioVizEvent> loadedFileListeners = new ArrayList<>();
    private List<BioVizEvent> saveFileListeners = new ArrayList<>();
    private List<BioVizEvent> closeFileListeners = new ArrayList<>();
    private List<BioVizEvent> pickColorListeners = new ArrayList<>();
    private List<BioVizEvent> nextTabListeners = new ArrayList<>();
    private List<BioVizEvent> previousTabListeners = new ArrayList<>();

    private boolean loadFileOnUpdate = true;
    private boolean firstRun = true;

    public BioViz() {
        super();
        logger.info(
                "Starting withouth filename being specified; loading example");
        logger.info("Usage: java -jar BioViz.jar <filename>");
        this.bioFile = null;
        loadedBiochips = new HashMap<>();
        textures = new TextureManager();

        // Weird static assignment needed to enable parameterless construction
        // of appender in order to allow its creation from logger framework
        MsgAppender.setMessageViz(this);
    }

    public BioViz(final File bioFile) {
        this();
        logger.info("Starting with file \"{}\"", bioFile.getAbsolutePath());
        this.bioFile = bioFile;
    }


    /**
     * Sets the duration of intermediate animations in ms.
     *
     * @param newDuration The new animation duration.
     */
    public static void setAnimationDuration(final int newDuration) {
        DrawableDroplet.setTransitionDuration(newDuration);
        DrawableSprite.setColorTransitionDuration(newDuration);
    }

    /**
     * Returns the duration of intermediate animations in ms.
     *
     * @return Duration of intermediate animations in ms.
     */
    public static int getAnimationDuration() {
        return DrawableDroplet.getTransitionDuration();
    }

    public String getFileName() {
        if (bioFile == null) {
            return "Default example";
        } else {
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
    private void spawnSVGManager() {
        if (svgManager == null) {
            svgManager = new SVGManager();
        }
    }

    @Override
    public void create() {
        messageCenter = new MessageCenter(this);


        float w = Gdx.graphics.getWidth();
        float h = Gdx.graphics.getHeight();

        camera = new OrthographicCamera(1, h / w);
        batch = new BioVizSpriteBatch(
                new SpriteBatch(1000, BioViz.createDefaultShader()));

        inputProcessor = new BioVizInputProcessor(this);
        Gdx.input.setInputProcessor(inputProcessor);

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
            loadNewFile();
            loadFileOnUpdate = false;
        }

        Gdx.gl.glClearColor(1, 1, 1, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        camera.update();


        // clear previous frame
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.setProjectionMatrix(camera.combined);
        batch.begin();

        for (final Drawable drawable : drawables) {
            drawable.draw();
        }

        messageCenter.render();

        batch.end();

        /*
        We limit the frame rate to targetFramerate here
         */
        long waitUntil = currentTimestamp + (1000 / this.targetFramerate);
        try {
            Thread.sleep(Math.max(0, waitUntil - new Date().getTime()));
        } catch (final InterruptedException e) {
            logger.info("Sleep was interrupted");
        }
    }


    @Override
    public void resize(final int width, final int height) {
        camera.viewportHeight = height;
        camera.viewportWidth = width;
        if (firstRun && currentBiochip != null) {
            currentBiochip.zoomExtents();
            firstRun = false;
        }
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    public void unloadFile(final File f) {
        try {
            String path = f.getCanonicalPath();
            if (this.loadedBiochips.containsKey(path)) {
                DrawableCircuit c = loadedBiochips.get(path);

                // remove the visualization if it is currently active.
                if (currentBiochip == c) {
                    this.drawables.remove(c);
                    currentBiochip = new DrawableCircuit(
                            new Biochip(), this);
                }
                logger.info("Removing {}", path);
                this.loadedBiochips.remove(path);
            }
        } catch (final Exception e) {
            logger.error("Could not unload file \"{}\"", f);
        }
    }

	/**
     * Loads a new file.
     *
     *
     */
    private void loadNewFile() {
        Biochip bc;

        try {
            drawables.remove(currentBiochip);
            if (bioFile != null) {
                if (this.loadedBiochips.containsKey(
                        bioFile.getCanonicalPath())) {
                    logger.debug("re-fetching previously loaded file " +
                            bioFile.getCanonicalPath());
                    currentBiochip =
                            this.loadedBiochips.get(
                                    bioFile.getCanonicalPath());
                } else {
                    logger.debug("Loading \"{}\"", bioFile);
                    bc = BioParser.parseFile(bioFile);

                    if (bc == null) {
                        logger.error("Could not parse file {}", bioFile);
                        logger.info("Creating empty Biochip to display");
                        bc = new Biochip();
                    }
                    logger.debug("Creating drawable elements...");
                    DrawableCircuit newCircuit = new DrawableCircuit(bc, this);
                    currentBiochip = newCircuit;
                    this.loadedBiochips.put(bioFile.getCanonicalPath(),
                                            newCircuit);
                    currentBiochip.zoomExtents();
                }
                logger.debug("Drawable created, replacing old elements...");
                drawables.add(currentBiochip);
                currentBiochip.getData().recalculateAdjacency = true;

                logger.info("Done loading file {}", bioFile);
            } else {
                logger.debug(
                        "File to be set is empty, setting empty " +
                                "visualization" +
                                ".");
                currentBiochip = new DrawableCircuit(new Biochip(), this);
            }
        } catch (final Exception e) {
            logger.error("Error when parsing {}:\n{}", bioFile, e.getMessage
                    ());
        }

        // clear on screen messages as they would otherwise remain visible
        messageCenter.clearHUDMessages();


        this.callLoadedFileListeners();
    }

	/**
     * Schedules the loading of a new file.
     * @param f The file to load.
     */
    public void scheduleLoadingOfNewFile(final File f) {

        // only do stuff if there actually was a file provided
        if (f != null) {
            logger.debug("Scheduling loading of file " + f);
            bioFile = f;
            loadFileOnUpdate = true;
        }
    }

    public void addTimeChangedListener(final BioVizEvent listener) {
        timeChangedListeners.add(listener);
    }


    public void addLoadFileListener(final BioVizEvent listener) {
        loadFileListeners.add(listener);
    }


    public void addReloadFileListener(final BioVizEvent listener) {
        reloadFileListeners.add(listener);
    }

    public void callReloadFileListeners() {
        callListeners(reloadFileListeners);
    }


    void callTimeChangedListeners() {
        logger.trace("Calling " + timeChangedListeners.size() +
                " listeners for timeChanged");
        callListeners(timeChangedListeners);
    }

    void callLoadFileListeners() {
        logger.trace("Calling " + loadFileListeners.size() +
                " listeners for load");
        callListeners(loadFileListeners);
    }

    public void addLoadedFileListener(final BioVizEvent listener) {
        loadedFileListeners.add(listener);
    }



    public void addSaveFileListener(final BioVizEvent listener) {
        saveFileListeners.add(listener);
    }

    void callSaveFileListeners() {
        logger.trace("Calling " + saveFileListeners.size() +
                " listeners for save");
        callListeners(saveFileListeners);
    }

    public void addCloseFileListener(final BioVizEvent listener) {
        closeFileListeners.add(listener);
    }

    void callCloseFileListeners() {
        logger.trace("Calling " + closeFileListeners.size() +
                " listeners for close");
        callListeners(closeFileListeners);
    }

    public void addPickColourListener(final BioVizEvent listener) {
        pickColorListeners.add(listener);
    }

    void callPickColourListeners() {
        logger.trace("Calling " + closeFileListeners.size() +
                " listeners for picking a colour");
        callListeners(closeFileListeners);

    }

    private void callLoadedFileListeners() {
        logger.trace("Calling " + loadedFileListeners.size() +
                     " listeners for loaded");
        callListeners(loadedFileListeners);
    }


	/**
     * Exports the currently displayed biochip to a svg image file.
     *
     * @param path The path where the image is stored.
     * @param timeStep The timestep of the current biochip to store.
     */
    public void saveSVG(final String path, final int timeStep) {
        spawnSVGManager();
        logger.debug("[SVG] Within saveSVG(String) method");
        logger.debug("[SVG] svgManager: {}", svgManager);

        try {
            String svg = svgManager.toSVG(currentBiochip, timeStep);
            //logger.debug("[SVG] generated SVG: {}",svg);
            FileHandle handle = Gdx.files.absolute(path);
            logger.debug("[SVG] File handle for storing the SVG: {}", handle);
            handle.writeString(svg, false);
            logger.info("[SVG] Stored SVG at {}", handle.path());
        } catch (final Exception e) {
            logger.error("[SVG] Could not store SVG; exception message: {}",
                    e);
        }
    }

	/**
     * Choses the icon used by the application.
     *
     * There is a dice roll deciding which icon from {droplet, dispenser, sink}
     * will be used.
     *
     * @return The icon to use for the application
     */
    public FileHandle getApplicationIcon() {
        Random rnd = new Random();
        int r = rnd.nextInt(3);
        FileHandle handle;
        if (r == 0) {
            handle = textures.getFileHandle(TextureE.Droplet);
        } else if (r == 1) {
            handle = textures.getFileHandle(TextureE.Dispenser);
        } else {
            handle = textures.getFileHandle(TextureE.Sink);
        }
        logger.debug("Setting application icon to " + handle.name());
        return handle;
    }

    /**
     * Add a listener for tab changes to the next tab.
     *
     * @param listener the listener
     */
    public void addNextTabListener(final BioVizEvent listener) {
        nextTabListeners.add(listener);
    }

    /**
     * Call the nextTab listeners.
     */
    void callNextTabListeners() {
        logger.trace("Calling " + nextTabListeners.size() +
                " listeners to change to the next tab.");
        callListeners(nextTabListeners);
    }

    /**
     * Add a listener for tab changes to the previous tab.
     *
     * @param listener the listener
     */
    public void addPreviousTabListener(final BioVizEvent listener) {
        previousTabListeners.add(listener);
    }

    /**
     * Call the previousTab listeners.
     */
    void callPreviousTabListeners() {
        logger.trace("Calling " + previousTabListeners.size() +
                " listeners to change to the next tab.");
        previousTabListeners.forEach(BioVizEvent::bioVizEvent);
    }


	/**
     * Creates the defailt shader program.
     * @return The default shader program.
     */
    private static ShaderProgram createDefaultShader() {
        FileHandle vertexShaderHandle = Gdx.files.internal("vertexShader.shd");
        FileHandle fragmentShaderHandle =
                Gdx.files.internal("fragmentShader.shd");

        ShaderProgram shader =
                new ShaderProgram(vertexShaderHandle, fragmentShaderHandle);
        if (!shader.isCompiled()) {
            throw new IllegalArgumentException(
                    "Error compiling shader: " + shader.getLog());
        }
        return shader;
    }


    /**
     * Calls the listeners of a list of events.
     *
     * @param events The events that are to be called/triggered.
     */
    private void callListeners(final List<BioVizEvent> events) {
        events.forEach(BioVizEvent::bioVizEvent);
    }

}
