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
package com.hh.ghoststory.screen.input;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.math.collision.Ray;
import com.hh.ghoststory.screen.PlayScreen;

public class PlayDetector extends GestureDetector {
	private PlayScreen screen;
	/** The button for rotating the screen.active(). */
	public int rotateButton = Input.Buttons.RIGHT;
	/** The angle to rotate when moved the full width or height of the screen. */
	public float rotateAngle = 360f;
	/** The button for translating the screen.active() along the up/right plane */
	public int translateButton = Input.Buttons.MIDDLE;
	/** The units to translate the screen.active() when moved the full width or height of the screen. */
	public float translateUnits = 10f; // FIXME auto calculate this based on the target
	/** The button for translating the screen.active() along the direction axis */
	public int interactButton = Input.Buttons.LEFT;
	/** The weight for each scrolled amount. */
	public float scrollFactor = -0.1f;
	/** World units per screen size */
	public float pinchZoomFactor = 10f;
	/** Whether to update the screen.active() after it has been changed. */
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
	/** Whether to update the target on scroll */
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
	protected final PlayListener gestureListener;

    private Material cachedMat;
    private ModelInstance activeInstance;

    public PlayDetector(final PlayListener GestureListener, PlayScreen screen) {
		super(GestureListener);
		this.gestureListener = GestureListener;
		this.gestureListener.controller = this;
		this.screen = screen;
	}

	public PlayDetector(PlayScreen screen) {
		this(new PlayListener(screen), screen);
	}

	public void update (){
		if (rotateRightPressed || rotateLeftPressed || forwardPressed || backwardPressed || leftPressed || rightPressed || zoomInPressed || zoomOutPressed) {
			final float delta = Gdx.graphics.getDeltaTime();
//			if (rotateRightPressed) screen.active().rotate(screen.active().up, -delta * rotateAngle);
//			if (rotateLeftPressed) screen.active().rotate(screen.active().up, delta * rotateAngle);
			if (forwardPressed) {
				// check to correct for lock when looking straight down.
				if (screen.active().direction.equals(new Vector3(0,-1,0))) screen.active().rotate(new Vector3(-1,0,0), -1);

				screen.active().translate(tmpV1.set(screen.active().direction.x, 0, screen.active().direction.z).nor().scl(delta * translateUnits));

				if (forwardTarget) target.add(tmpV1);
			}
			if (backwardPressed) {
				// check to correct for lock when looking straight down.
				if (screen.active().direction.equals(new Vector3(0,-1,0))) screen.active().rotate(new Vector3(-1,0,0), 1);

				screen.active().translate(tmpV1.set(screen.active().direction.x, 0, screen.active().direction.z).nor().scl(-delta * translateUnits));
				if (forwardTarget) target.add(tmpV1);
			}
			if (leftPressed) {
				Vector3 left = new Vector3().set(screen.active().direction).crs(screen.active().up).nor();
				screen.active().translate(tmpV1.set(left).scl(-delta * translateUnits));
				if (lateralTarget) target.add(tmpV1);
			}
			if (rightPressed) {
				Vector3 right = new Vector3().set(screen.active().direction).crs(screen.active().up).nor().scl(-1f);
				screen.active().translate(tmpV1.set(right).scl(-delta * translateUnits));
				if (lateralTarget) target.add(tmpV1);
			}
			if (zoomInPressed) {
				zoom(delta * translateUnits);
				if (zoomTarget) target.add(tmpV1);
			}
			if (zoomOutPressed) {
				zoom(-delta * translateUnits);
				if (zoomTarget) target.add(tmpV1);
			}
			if (autoUpdate) screen.active().update();
		}
	}

	private int touched;
	private boolean multiTouch;

	/**
	 * touchDown is firstcalled here, from Lwjgl. Then it hit GestureDetector w/ super.
	 * GD hits another touchdown message, which eventually calls the listener touchdown.
     * Handling button here might be good for touch pads.
	 *
	 * @param screenX
	 * @param screenY
	 * @param pointer
	 * @param button
	 * @return
	 */
	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		touched |= (1 << pointer);
		multiTouch = !MathUtils.isPowerOfTwo(touched);
		if (multiTouch)
			this.button = -1;
		else if (this.button < 0) {
			startX = screenX;
			startY = screenY;
			this.button = button;
		}
		return super.touchDown(screenX, screenY, pointer, button);
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		touched &= -1 ^ (1 << pointer);
		multiTouch = !MathUtils.isPowerOfTwo(touched);
		if (button == this.button) this.button = -1;
		return super.touchUp(screenX, screenY, pointer, button);
	}

	/**
	 * Handles mouse button input.
	 * @param deltaX
	 * @param deltaY
	 * @param button
	 * @return
	 */
	protected boolean process(float deltaX, float deltaY, int button) {
		if (button == rotateButton) {
			tmpV1.set(screen.active().direction).crs(screen.active().up).y = 0f;
			screen.active().rotateAround(target, tmpV1.nor(), deltaY * rotateAngle);
			screen.active().rotateAround(target, Vector3.Y, deltaX * -rotateAngle);
		} else if (button == translateButton) {
			screen.active().translate(tmpV1.set(screen.active().direction).crs(screen.active().up).nor().scl(-deltaX * translateUnits));
			screen.active().translate(tmpV2.set(screen.active().up).scl(-deltaY * translateUnits));
			if (translateTarget) target.add(tmpV1).add(tmpV2);
		} else if (button == interactButton) {
		/**
		 * @TODO Make the interact button interact here. No zooming.
		 * zoom code.
		 * screen.active().translate(tmpV1.set(screen.active().direction).scl(deltaY * translateUnits));
		 */
		}
		if (autoUpdate) screen.active().update();
		return true;
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		boolean result = super.touchDragged(screenX, screenY, pointer);
		if (result || this.button < 0) return result;
		final float deltaX = (screenX - startX) / Gdx.graphics.getWidth();
		final float deltaY = (startY - screenY) / Gdx.graphics.getHeight();
		startX = screenX;
		startY = screenY;
		return process(deltaX, deltaY, button);
	}

	/**
	 * Goes directly to zoom.
	 * @param amount
	 * @return
	 */
	@Override
	public boolean scrolled(int amount) {
		return zoom(amount * scrollFactor * translateUnits);
	}

	/**
	 * 1st from keyboard zoom also, called in update()
	 * Maybe move this to the screen? It's the last in chain from scrolled at least?
	 *
	 * Or just set camera field to screen.active()
 	 * @param amount
	 * @return
	 */
	public boolean zoom(float amount) {
		screen.active().translate(tmpV1.set(screen.active().direction).scl(amount));
		if (scrollTarget) target.add(tmpV1);
		if (autoUpdate) screen.active().update();
		return true;
	}

	/**
	 * @TODO evaluate this, haven't seen on touchscreen yet
	 */
	protected boolean pinchZoom(float amount) {
		return zoom(pinchZoomFactor * amount);
	}

	/**
	 * Gets the input key and sets various values based on it.
	 * @param keycode
	 * @return
	 */
	@Override
	public boolean keyDown(int keycode) {
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

	/**
	 * Gets the end of an input key and unsets it's pressed variable.
	 * @param keycode
	 * @return
	 */
	@Override
	public boolean keyUp(int keycode) {
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

	/**
	 * This should be used to highlight scene/interface elements on hover.
	 * @TODO HOVER
	 * @param screenX
	 * @param screenY
	 * @return
	 */
	@Override
	public boolean mouseMoved (int screenX, int screenY) {
//            Ray ray = screen.active().getPickRay(screenX, screenY);
//            Vector3 position = new Vector3();
//            Vector3 center = new Vector3();
//            Vector3 dimensions = new Vector3();
//            float radius;
//            float distance = -1;
//
//            if (activeInstance != null && cachedMat != null) {
//                activeInstance.materials.get(0).set(cachedMat);
//                activeInstance = null;
//            }
//
//            for (int i = 0; i < screen.instances.size; ++i) {
//                final ModelInstance instance = screen.instances.get(i);
//                instance.transform.getTranslation(position);
//                BoundingBox box = instance.calculateBoundingBox(new BoundingBox());
//                position.add(box.getCenter(new Vector3()));
//                box.getCenter(center);
//                box.getDimensions(dimensions);
//                radius = dimensions.len() / 2f;
//                Texture tex = new Texture(Gdx.files.internal("models/ghost_texture_blue.png"), true);
////                Pixmap pm = screen.assetManager.get("models/ghost_texture_blue.png", Pixmap.class);
////				Texture tex = new Texture(pm, true);
//                tex.setFilter(Texture.TextureFilter.MipMapLinearLinear, Texture.TextureFilter.Nearest);
//                float dist2 = ray.origin.dst2(position);
//
//                if (distance >= 0f && dist2 > distance) continue;
//
//                if (Intersector.intersectRaySphere(ray, position, radius, null)) {
//                    cachedMat = instance.materials.get(0).copy();
//                    activeInstance = instance;
////				instance.materials.get(0).set(ColorAttribute.createDiffuse(Color.LIGHT_GRAY), ColorAttribute.createAmbient(Color.LIGHT_GRAY));
//                    instance.materials.get(0).set(new TextureAttribute(TextureAttribute.Diffuse, tex), new ColorAttribute(ColorAttribute.Specular, Color.GOLD));
//
//                    distance = dist2;
//                }
//            }
		return false;
	}
}