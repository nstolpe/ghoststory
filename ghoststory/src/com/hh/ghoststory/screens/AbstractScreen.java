package com.hh.ghoststory.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.hh.ghoststory.GhostStory;

/**
 * The base class for all game screens.
 */
public abstract class AbstractScreen implements Screen {
	protected final GhostStory game;
	protected float screenW;
	protected float screenH;
	protected OrthographicCamera camera;
	protected float clearRed = 0f;
	protected float clearBlue = 0f;
	protected float clearGreen = 0f;
	protected float clearAlpha = 1f;
	
	public static int SCREEN_MAIN = 0;
	public static int SCREEN_TURN = 1;
	public static int SCREEN_CHARACTER = 2;
	public static int SCREEN_ISOMETRIC = 3;

	public AbstractScreen(GhostStory game) {
		this.game = game;
		this.screenW = Gdx.graphics.getWidth();
		this.screenH = Gdx.graphics.getHeight();
	}

	@Override
	public void show() {
	}

	@Override
	public void resize(int width, int height) {
	}

	@Override
	public void render(float delta) {
		Gdx.gl.glClearColor(clearRed, clearGreen, clearBlue, clearAlpha);
		Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
	}

	@Override
	public void hide() {
	}

	@Override
	public void pause() {
	}

	@Override
	public void resume() {
	}

	@Override
	public void dispose() {
	}
	
	protected void setClear(float red, float green, float blue, float alpha) {
		clearRed = red;
		clearGreen = green;
		clearBlue = blue;
		clearAlpha = alpha;
	}
}
