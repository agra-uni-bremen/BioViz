package de.dfki.revvisgdx;

import com.badlogic.gdx.Files;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

public class Main {
	public static void main(String[] args) {
		LwjglApplicationConfiguration cfg = new LwjglApplicationConfiguration();
		cfg.title = "RevVisGDX";
		cfg.useGL20 = false;
		cfg.width = 800;
		cfg.height = 600;
		cfg.addIcon("data/icon_win_linux.png", Files.FileType.Internal);
		if (args.length <= 0) {
			new LwjglApplication(new RevVisGDX(), cfg);
		} else {
			if (args.length >= 1) {
				new LwjglApplication(new RevVisGDX(args[0]), cfg);
				if (args.length >= 2) {
					RevVisGDX.singleton.runFullPresetScreenshots = true;
					try {RevVisGDX.singleton.fullPresetScreenshotsScaling = Float.parseFloat(args[1]);} catch(Throwable t) {}
				}
			}
		}
	}
}
