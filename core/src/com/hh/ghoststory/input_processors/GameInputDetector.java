package com.hh.ghoststory.input_processors;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.Ray;
import com.hh.ghoststory.screens.AbstractScreen;

/**
 * Created by nils on 2/9/14.
 */
public class GameInputDetector extends GestureDetector {
	private AbstractScreen screen;

	public GameInputDetector(GameInputListener listener) {
		super(20, 0.4f, 1.1f, 0.15f, listener);
		this.screen = listener.screen;
	}

	public GameInputDetector(float halfTapSquareSize, float tapCountInterval, float longPressDuration, float maxFlingDelay, GameInputListener listener) {
		super(halfTapSquareSize, tapCountInterval, longPressDuration, maxFlingDelay, listener);
		this.screen = listener.screen;
	}

	@Override
	public boolean keyDown(int keycode) {
		return false;
	}

	@Override
	public boolean keyUp(int keycode) {
		return false;
	}

	@Override
	public boolean keyTyped(char character) {
		return false;
	}

	@Override
	public boolean mouseMoved(int screenX, int screenY) {
		return false;
	}

	@Override
	public boolean scrolled(int amount) {
		//Zoom out
		if (amount > 0 && this.screen.camera.zoom < 1) {
			this.screen.camera.zoom += 0.1f;
		}

		//Zoom in
		if (amount < 0 && this.screen.camera.zoom > 0.1) {
			this.screen.camera.zoom -= 0.1f;
		}

		return true;
//        return false;
	}
}
