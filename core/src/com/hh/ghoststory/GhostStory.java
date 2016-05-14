package com.hh.ghoststory;

import com.badlogic.ashley.core.Engine;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.files.FileHandle;
import com.hh.ghoststory.lib.utility.Config;
import com.hh.ghoststory.screen.*;

public class GhostStory extends Game {
    public Config config;

	@Override
	public void create() {
        // some config ill be pulled in to this constructor.
        config = new Config();
        config.populateEntities();

//		FileHandle file = Gdx.files.local(".ghost_story/character.json");
//		file.file().getParentFile().mkdirs();
//		file.writeString("{}", false);
//		setScreen(getMainScreen());
//		setScreen(getCreateScreen());
//		setScreen(getPlayScreen());
//		setScreen(new PlayScreen(this));
		setScreen(new CelShaderScreen(this));
//		setScreen(new CelTutorialScreen(this));
//		setScreen(new TestScreen(this));
	}

	/**
	 * ************************************************************************
	 * Screen methods                                                         *
	 * ************************************************************************
	 */
	public MainScreen getMainScreen() {
		return new MainScreen(this);
	}

	public PlayScreen getPlayScreen() {
		return new PlayScreen(this);
	}
	public CreateScreen getCreateScreen() {
		return new CreateScreen(this);
	}

	/**
	 * ************************************************************************
	 * Overriden methods                                                        *
	 * *************************************************************************
	 */

	@Override
	public void render() {
		super.render();
		config.engine.update(Gdx.graphics.getDeltaTime());
	}

	@Override
	public void dispose() {
		super.dispose();
	}

	@Override
	public void resize(int width, int height) {
		super.resize(width, height);
	}

	@Override
	public void pause() {
		super.pause();
	}

	@Override
	public void resume() {
		super.resume();
	}

	@Override
	public void setScreen(Screen screen) {
		super.setScreen(screen);
	}
}