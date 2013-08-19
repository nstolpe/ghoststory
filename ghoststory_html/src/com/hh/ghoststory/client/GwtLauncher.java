package com.hh.ghoststory.client;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.backends.gwt.GwtApplication;
import com.badlogic.gdx.backends.gwt.GwtApplicationConfiguration;
import com.hh.ghoststory.GhostStory;

public class GwtLauncher extends GwtApplication {
	@Override
	public GwtApplicationConfiguration getConfig () {
		GwtApplicationConfiguration cfg = new GwtApplicationConfiguration(480, 320);
		cfg.fps = 60;
		return cfg;
	}

	@Override
	public ApplicationListener getApplicationListener () {
		return new GhostStory();
	}
}