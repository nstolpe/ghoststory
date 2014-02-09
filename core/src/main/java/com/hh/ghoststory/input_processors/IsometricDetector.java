package com.hh.ghoststory.input_processors;

import com.badlogic.gdx.input.GestureDetector;
import com.hh.ghoststory.screens.AbstractScreen;

/**
 * Created by nils on 2/9/14.
 */
public class IsometricDetector extends GestureDetector {
    private AbstractScreen screen;

    public IsometricDetector (GestureListener listener, AbstractScreen screen) {
        super(20, 0.4f, 1.1f, 0.15f, listener);
        this.screen = screen;
    }

    private IsometricDetector (GestureListener listener) {
        super(20, 0.4f, 1.1f, 0.15f, listener);
    }

    private IsometricDetector (float halfTapSquareSize, float tapCountInterval, float longPressDuration, float maxFlingDelay,
                            GestureListener listener) {
        super(halfTapSquareSize, tapCountInterval, longPressDuration, maxFlingDelay, listener);
    }

    @Override
    public boolean keyDown (int keycode) {
        return false;
    }

    @Override
    public boolean keyUp (int keycode) {
        return false;
    }

    @Override
    public boolean keyTyped (char character) {
        return false;
    }

    @Override
    public boolean mouseMoved (int screenX, int screenY) {
        return false;
    }

    @Override
    public boolean scrolled (int amount) {
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
