package com.hh.ghoststory;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Screen;
import com.hh.ghoststory.screens.CreateScreen;
import com.hh.ghoststory.screens.IsometricScreen;
import com.hh.ghoststory.screens.MainScreen;

public class GhostStory extends Game {
	@Override
	public void create() {
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
