package com.hh.ghoststory.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.hh.ghoststory.GhostStory;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
//		config.vSyncEnabled = false;
//		config.foregroundFPS = 0;
//		config.backgroundFPS = 0;
		config.stencil = 8;
		new LwjglApplication(new GhostStory(), config);
	}
}
