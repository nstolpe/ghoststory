package com.hh.ghoststory;

import java.io.IOException;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.files.FileHandle;
import com.hh.ghoststory.screens.CreateScreen;
import com.hh.ghoststory.screens.IsometricScreen;
import com.hh.ghoststory.screens.MainScreen;

public class GhostStory extends Game {
	@Override
	public void create() {
		FileHandle file = Gdx.files.local(".ghost_story/character.json");
		file.file().getParentFile().mkdirs();
		file.writeString("{}", false);
		setScreen(getMainScreen());
	}
	
	/***************************************************************************
     *  Screen methods                                                         *
     **************************************************************************/   
    public MainScreen getMainScreen() {
    	return new MainScreen(this);
    }
    public IsometricScreen getIsometricScreen() {
    	return new IsometricScreen(this);
    }
    public CreateScreen getCreateScreen() {
    	return new CreateScreen(this);
    }
	/***************************************************************************
	* Overriden methods                                                        *
	***************************************************************************/	
	
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
