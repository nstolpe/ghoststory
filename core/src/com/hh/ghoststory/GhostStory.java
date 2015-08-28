package com.hh.ghoststory;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.files.FileHandle;
import com.hh.ghoststory.screen.CreateScreen;
import com.hh.ghoststory.screen.GameScreen;
import com.hh.ghoststory.screen.MainScreen;
import com.hh.ghoststory.screen.PlayScreen;

public class GhostStory extends Game {
	@Override
	public void create() {
		FileHandle file = Gdx.files.local(".ghost_story/character.json");
//		file.file().getParentFile().mkdirs();
//		file.writeString("{}", false);
//		setScreen(getMainScreen());
//		setScreen(getGameScreen());
		setScreen(getPlayScreen());
	}

	/**
	 * ************************************************************************
	 * Screen methods                                                         *
	 * ************************************************************************
	 */
	public MainScreen getMainScreen() {
		return new MainScreen(this);
	}

	public GameScreen getGameScreen() {
		return new GameScreen(this);
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