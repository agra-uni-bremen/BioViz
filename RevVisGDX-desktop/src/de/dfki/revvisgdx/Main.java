package de.dfki.revvisgdx;

import com.badlogic.gdx.Files;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

public class Main {
	public static void main(String[] args) {
		LwjglApplicationConfiguration cfg = new LwjglApplicationConfiguration();
		cfg.title = "RevVisGDX";
		cfg.useGL20 = false;
		cfg.width = 480;
		cfg.height = 320;
		cfg.addIcon("data/icon_win_linux.png", Files.FileType.Internal);
		if (args.length <= 0) {
			new LwjglApplication(new RevVisGDX(), cfg);
		} else {
			new LwjglApplication(new RevVisGDX(args[0]), cfg);
		}
	}
}
