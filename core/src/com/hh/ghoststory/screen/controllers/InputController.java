/*******************************************************************************
 * Copyright 2011 See AUTHORS file.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package com.hh.ghoststory.screen.controllers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.Ray;
import com.hh.ghoststory.screen.core.AbstractScreen;
import com.hh.ghoststory.screen.core.DualCameraScreen;

public class InputController extends GestureDetector {
	private InputMultiplexer multiplexer = new InputMultiplexer();
	private DualCameraScreen screen;
	/** The button for rotating the screen.active. */
	public int rotateButton = Input.Buttons.LEFT;
	/** The angle to rotate when moved the full width or height of the screen. */
	public float rotateAngle = 360f;
	/** The button for translating the screen.active along the up/right plane */
	public int translateButton = Input.Buttons.RIGHT;
	/** The units to translate the screen.active when moved the full width or height of the screen. */
	public float translateUnits = 10f; // FIXME auto calculate this based on the target
	/** The button for translating the screen.active along the direction axis */
	public int forwardButton = Input.Buttons.MIDDLE;
	/** The key which must be pressed to activate rotate, translate and forward or 0 to always activate. */
	public int activateKey = 0;
	/** Indicates if the activateKey is currently being pressed. */
	protected boolean activatePressed;
	/** Whether scrolling requires the activeKey to be pressed (false) or always allow scrolling (true). */
	public boolean alwaysScroll = true;
	/** The weight for each scrolled amount. */
	public float scrollFactor = -0.1f;
	/** World units per screen size */
	public float pinchZoomFactor = 10f;
	/** Whether to update the screen.active after it has been changed. */
	public boolean autoUpdate = true;
	/** The target to rotate around. */
	public Vector3 target = new Vector3();
	/** Whether to update the target on translation */
	public boolean translateTarget = true;
	/** Whether to update the target on forward */
	public boolean forwardTarget = true;
	/** Whether to update the target on scroll */
	public boolean lateralTarget = true;
	public boolean zoomTarget = true;
	public boolean scrollTarget = false;
	public int forwardKey = Keys.W;
	protected boolean forwardPressed;
	public int backwardKey = Keys.S;
	protected boolean backwardPressed;
	public int leftKey = Keys.A;
	protected boolean leftPressed;
	public int rightKey = Keys.D;
	protected boolean rightPressed;
	public int rotateRightKey = Keys.E;
	protected boolean rotateRightPressed;
	public int rotateLeftKey = Keys.Q;
	protected boolean rotateLeftPressed;
	public int zoomInKey = Keys.Z;
	protected boolean zoomInPressed;
	public int zoomOutKey = Keys.X;
	protected boolean zoomOutPressed;

	/** The current (first) button being pressed. */
	protected int button = -1;

	private float startX, startY;
	private final Vector3 tmpV1 = new Vector3();
	private final Vector3 tmpV2 = new Vector3();
	protected final InputGestureListener gestureListener;

	public InputController(final InputGestureListener gestureListener, DualCameraScreen screen) {
		super(gestureListener);
		this.gestureListener = gestureListener;
		this.gestureListener.controller = this;
		this.screen = screen;
	}

	public void update () {
		if (rotateRightPressed || rotateLeftPressed || forwardPressed || backwardPressed || leftPressed || rightPressed || zoomInPressed || zoomOutPressed) {
			final float delta = Gdx.graphics.getDeltaTime();
			if (rotateRightPressed) screen.active.rotate(screen.active.up, -delta * rotateAngle);
			if (rotateLeftPressed) screen.active.rotate(screen.active.up, delta * rotateAngle);
			// forward and backward are not quite right. try it when looking straight down.
			if (forwardPressed) {
				screen.active.translate(tmpV1.set(screen.active.direction.x, 0, screen.active.direction.z).scl(delta * translateUnits));
				if (forwardTarget) target.add(tmpV1);
			}
			if (backwardPressed) {
				screen.active.translate(tmpV1.set(screen.active.direction.x, 0, screen.active.direction.z).scl(-delta * translateUnits));
				if (forwardTarget) target.add(tmpV1);
			}
			if (leftPressed) {
				Vector3 left = new Vector3().set(screen.active.direction).crs(screen.active.up).nor();
				screen.active.translate(tmpV1.set(left).scl(-delta * translateUnits));
				if (lateralTarget) target.add(tmpV1);
			}
			if (rightPressed) {
				Vector3 right = new Vector3().set(screen.active.direction).crs(screen.active.up).nor().scl(-1f);
				screen.active.translate(tmpV1.set(right).scl(-delta * translateUnits));
				if (lateralTarget) target.add(tmpV1);
			}
			if (zoomInPressed) {
				screen.active.translate(tmpV1.set(screen.active.direction).scl(delta * translateUnits));
				if (zoomTarget) target.add(tmpV1);
			}
			if (zoomOutPressed) {
				screen.active.translate(tmpV1.set(screen.active.direction).scl(-delta * translateUnits));
				if (zoomTarget) target.add(tmpV1);
			}
			if (autoUpdate) screen.active.update();
		}
	}

	private int touched;
	private boolean multiTouch;

	@Override
	public boolean touchDown (int screenX, int screenY, int pointer, int button) {
		touched |= (1 << pointer);
		multiTouch = !MathUtils.isPowerOfTwo(touched);
		if (multiTouch)
			this.button = -1;
		else if (this.button < 0 && (activateKey == 0 || activatePressed)) {
			startX = screenX;
			startY = screenY;
			this.button = button;
		}
		return super.touchDown(screenX, screenY, pointer, button) || (activateKey == 0 || activatePressed);
	}

	@Override
	public boolean touchUp (int screenX, int screenY, int pointer, int button) {
		touched &= -1 ^ (1 << pointer);
		multiTouch = !MathUtils.isPowerOfTwo(touched);
		if (button == this.button) this.button = -1;
		return super.touchUp(screenX, screenY, pointer, button) || activatePressed;
	}

	protected boolean process (float deltaX, float deltaY, int button) {
		if (button == rotateButton) {
			tmpV1.set(screen.active.direction).crs(screen.active.up).y = 0f;
			screen.active.rotateAround(target, tmpV1.nor(), deltaY * rotateAngle);
			screen.active.rotateAround(target, Vector3.Y, deltaX * -rotateAngle);
		} else if (button == translateButton) {
			screen.active.translate(tmpV1.set(screen.active.direction).crs(screen.active.up).nor().scl(-deltaX * translateUnits));
			screen.active.translate(tmpV2.set(screen.active.up).scl(-deltaY * translateUnits));
			if (translateTarget) target.add(tmpV1).add(tmpV2);
		} else if (button == forwardButton) {
			screen.active.translate(tmpV1.set(screen.active.direction).scl(deltaY * translateUnits));
			if (forwardTarget) target.add(tmpV1);
		}
		if (autoUpdate) screen.active.update();
		return true;
	}

	@Override
	public boolean touchDragged (int screenX, int screenY, int pointer) {
		boolean result = super.touchDragged(screenX, screenY, pointer);
		if (result || this.button < 0) return result;
		final float deltaX = (screenX - startX) / Gdx.graphics.getWidth();
		final float deltaY = (startY - screenY) / Gdx.graphics.getHeight();
		startX = screenX;
		startY = screenY;
		return process(deltaX, deltaY, button);
	}

	@Override
	public boolean scrolled (int amount) {
		return zoom(amount * scrollFactor * translateUnits);
	}

	public boolean zoom (float amount) {
		if (!alwaysScroll && activateKey != 0 && !activatePressed) return false;
		screen.active.translate(tmpV1.set(screen.active.direction).scl(amount));
		if (scrollTarget) target.add(tmpV1);
		if (autoUpdate) screen.active.update();
		return true;
	}

	protected boolean pinchZoom (float amount) {
		return zoom(pinchZoomFactor * amount);
	}

	@Override
	public boolean keyDown (int keycode) {
		if (keycode == activateKey) activatePressed = true;

		if (keycode == forwardKey)              forwardPressed = true;
		else if (keycode == backwardKey)       backwardPressed = true;
		else if (keycode == leftKey)               leftPressed = true;
		else if (keycode == rightKey)             rightPressed = true;
		else if (keycode == rotateRightKey) rotateRightPressed = true;
		else if (keycode == rotateLeftKey)   rotateLeftPressed = true;
		else if (keycode == zoomInKey)           zoomInPressed = true;
		else if (keycode == zoomOutKey)         zoomOutPressed = true;

		return false;
	}

	@Override
	public boolean keyUp (int keycode) {
		if (keycode == activateKey) {
			activatePressed = false;
			button = -1;
		}
		if (keycode == forwardKey)              forwardPressed = false;
		else if (keycode == backwardKey)       backwardPressed = false;
		else if (keycode == leftKey)               leftPressed = false;
		else if (keycode == rightKey)             rightPressed = false;
		else if (keycode == rotateRightKey) rotateRightPressed = false;
		else if (keycode == rotateLeftKey)   rotateLeftPressed = false;
		else if (keycode == zoomInKey)           zoomInPressed = false;
		else if (keycode == zoomOutKey)         zoomOutPressed = false;

		return false;
	}
}
