package com.hh.ghoststory.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.ai.msg.MessageDispatcher;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.utils.Array;
import com.hh.ghoststory.GhostStory;
import com.hh.ghoststory.scene.lights.core.Caster;

/**
 * The base class for all game screens.
 */
public abstract class AbstractScreen implements Screen {
	protected final GhostStory game;
	protected float screenWidth;
	protected float screenHeight;
	public float clearRed = 1f;
	public float clearBlue = 1f;
	public float clearGreen = 1f;
	public float clearAlpha = 1f;
    protected boolean loading;
	public Array<Caster> casters = new Array<Caster>();
	public MessageDispatcher frameworkDispatcher = new MessageDispatcher();

	protected BitmapFont font = new BitmapFont(
			Gdx.files.internal("fonts/crimson.fnt"),
			Gdx.files.internal("fonts/crimson.png"),
			false
	);

	public AbstractScreen(GhostStory game) {
		this.game = game;
		screenWidth = Gdx.graphics.getWidth();
		screenHeight = Gdx.graphics.getHeight();
	}

	@Override
	public void show() {
	}

	@Override
	public void resize(int width, int height) {
	}

    /**
     * This might not be doing anything necessary. Commentintgthe gl calls doesn't
     * change anything on render for PlayScreen at least. Might not be needed in descendant either.
     * @param delta
     */
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
        for (Caster caster : casters)
            caster.dispose();
	}

	protected void setClear(float red, float green, float blue, float alpha) {
		this.clearRed = red;
		this.clearGreen = green;
		this.clearBlue = blue;
		this.clearAlpha = alpha;
	}

    protected void doneLoading() {
        loading = false;
    }

    /*
     * Class to create buttons on the main screen. A factory may be better, or use libGDX skin. Or move
     * to own class.
     *
     * Used in create and main. Move this somewhere else.
     */
	public class MainScreenButton {
		NinePatch up = new NinePatch(new Texture("images/up.9.png"), 18, 38, 38, 38);
		NinePatch down = new NinePatch(new Texture("images/down.9.png"), 38, 38, 38, 38);
		TextButtonStyle style = new TextButtonStyle();
		public TextButton button;

		public MainScreenButton(String label) {
			style.up = new NinePatchDrawable(up);
			style.down = new NinePatchDrawable(down);
			style.font = font;
			style.fontColor = new Color(1f, 1f, 1f, 1f);

			button = new TextButton(label, style);
		}
	}
}
