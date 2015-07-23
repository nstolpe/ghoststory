package com.hh.ghoststory.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.hh.ghoststory.GhostStory;

/**
 * The base class for all game screens.
 */
public abstract class AbstractScreen implements Screen {
	protected final GhostStory game;
	protected Camera camera;
	protected float screenWidth;
	protected float screenHeight;
	public float clearRed = 0f;
	public float clearBlue = 0f;
	public float clearGreen = 0f;
	public float clearAlpha = 1f;

	protected BitmapFont font = new BitmapFont(
			Gdx.files.internal("fonts/crimson.fnt"),
			Gdx.files.internal("fonts/crimson.png"),
			false
	);

	public AbstractScreen(GhostStory game) {
		this.game = game;
		this.screenWidth = Gdx.graphics.getWidth();
		this.screenHeight = Gdx.graphics.getHeight();
	}

	@Override
	public void show() {
	}

	@Override
	public void resize(int width, int height) {
	}

	@Override
	public void render(float delta) {
		Gdx.gl.glClearColor(this.clearRed, this.clearGreen, this.clearBlue, this.clearAlpha);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
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
		this.clearRed = red;
		this.clearGreen = green;
		this.clearBlue = blue;
		this.clearAlpha = alpha;
	}

	/*
	 * Class to create buttons on the main screen. A factory may be better, or use libGDX skin. Or move
	 * to own class.
	 */
	class MainScreenButton {
		NinePatch up = new NinePatch(new Texture("images/up.9.png"), 18, 38, 38, 38);
		NinePatch down = new NinePatch(new Texture("images/down.9.png"), 38, 38, 38, 38);
		TextButtonStyle style = new TextButtonStyle();
		TextButton button;

		public MainScreenButton(String label) {
			style.up = new NinePatchDrawable(up);
			style.down = new NinePatchDrawable(down);
			style.font = font;
			style.fontColor = new Color(1f, 1f, 1f, 1f);

			button = new TextButton(label, style);
		}
	}
}
