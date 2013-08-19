package com.hh.ghoststory;

import com.badlogic.gdx.Files;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

public class Main {
	public static void main(String[] args) {
		LwjglApplicationConfiguration cfg = new LwjglApplicationConfiguration();
		cfg.title = "ghoststory";
		cfg.useGL20 = true;
		cfg.width = 1280;
		cfg.height = 800;
		cfg.samples = 4;
		cfg.addIcon("icons/128.png", Files.FileType.Internal);
		cfg.addIcon("icons/32.png", Files.FileType.Internal);
		cfg.addIcon("icons/16.png", Files.FileType.Internal);
		new LwjglApplication(new GhostStory(), cfg);
	}
}
