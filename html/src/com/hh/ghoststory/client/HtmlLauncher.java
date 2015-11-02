package com.hh.ghoststory.client;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.backends.gwt.GwtApplication;
import com.badlogic.gdx.backends.gwt.GwtApplicationConfiguration;
import com.hh.ghoststory.GhostStory;

public class HtmlLauncher extends GwtApplication {

    @Override
    public GwtApplicationConfiguration getConfig () {
        GwtApplicationConfiguration config = new GwtApplicationConfiguration(800, 400);
        config.stencil = true;
        return config;
    }

    @Override
    public ApplicationListener getApplicationListener () {
        return new GhostStory();
    }
}