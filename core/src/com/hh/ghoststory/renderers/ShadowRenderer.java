package com.hh.ghoststory.renderers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.hh.ghoststory.screens.AbstractScreen;

/**
 * Created by nils on 7/23/15.
 */
public class ShadowRenderer {
    public AbstractScreen screen;

    public ShadowRenderer(AbstractScreen screen) {
        this.screen = screen;
    }
    public void render() {
        Gdx.gl.glClearColor(screen.clearRed, screen.clearGreen, screen.clearBlue, screen.clearAlpha);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
    }
}
