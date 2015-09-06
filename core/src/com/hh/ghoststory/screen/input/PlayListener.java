package com.hh.ghoststory.screen.input;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.ai.msg.MessageDispatcher;
import com.badlogic.gdx.ai.msg.Telegram;
import com.badlogic.gdx.ai.msg.Telegraph;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.math.*;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.math.collision.Ray;
import com.hh.ghoststory.entity.Mappers;
import com.hh.ghoststory.lib.MessageTypes;
import com.hh.ghoststory.screen.PlayScreen;

/**
 * Created by nils on 8/29/15.
 */
public class PlayListener extends GestureDetector.GestureAdapter implements Telegraph {
	private final PlayScreen screen;
	private final MessageDispatcher frameworkDispatcher;
	public PlayDetector detector;
	private float previousZoom;

	// @TODO move cachedMat and activeDistance to the Screen.
	private Material cachedMat;
	private ModelInstance activeInstance;

    // for use in methods instead of creating new ones all the time.
    private Ray ray;
    private Vector3 tmp1Vec3 = new Vector3();
    private Vector3 tmp2Vec3 = new Vector3();
    private Plane xzPlane = new Plane(new Vector3(0, 1, 0), 0);

    public PlayListener(PlayScreen screen, MessageDispatcher frameworkDispatcher) {
		this.screen = screen;
		this.frameworkDispatcher = frameworkDispatcher;
	}

	/**
	 * This needs some thought. A lot can happen on touchdown, depending on the target. Seems
	 * to be only getting the left click.
	 * Right now, it changes the color of the ghost it hits. It might also be better on touchUp
	 * @TODO make this only process the input. Then send the relevant data off as a message.
	 * @param x
	 * @param y
	 * @param pointer
	 * @param button
	 * @return
	 */
	@Override
	public boolean touchDown(float x, float y, int pointer, int button) {
		if (button == Input.Buttons.LEFT) {
			Ray ray = screen.active().getPickRay(x, y);
			Vector3 position = new Vector3();
			Vector3 center = new Vector3();
			Vector3 dimensions = new Vector3();
			float radius;
			float distance = -1;

			if (activeInstance != null && cachedMat != null) {
				activeInstance.materials.get(0).set(cachedMat);
				activeInstance = null;
			}

			for (int i = 0; i < screen.instances.size; ++i) {
				final ModelInstance instance = screen.instances.get(i);
				instance.transform.getTranslation(position);
				/**
				 * Bounding box should be stored on Entity
				 */
				BoundingBox box = instance.calculateBoundingBox(new BoundingBox());
				position.add(box.getCenter(new Vector3()));
				box.getCenter(center);
				box.getDimensions(dimensions);
				radius = dimensions.len() / 2f;
				/**
				 * not recalculated every touch.
				 */
				Texture tex = new Texture(Gdx.files.internal("models/ghost_texture_blue.png"), true);
//                Pixmap pm = screen.assetManager.get("models/ghost_texture_blue.png", Pixmap.class);
//				Texture tex = new Texture(pm, true);
				tex.setFilter(Texture.TextureFilter.MipMapLinearLinear, Texture.TextureFilter.Nearest);
				float dist2 = ray.origin.dst2(position);

				if (distance >= 0f && dist2 > distance) continue;

				if (Intersector.intersectRaySphere(ray, position, radius, null)) {
					cachedMat = instance.materials.get(0).copy();
					activeInstance = instance;
					instance.materials.get(0).set(new TextureAttribute(TextureAttribute.Diffuse, tex), new ColorAttribute(ColorAttribute.Specular, Color.GOLD));

					distance = dist2;
				}
			}
		}
		// @TODO something for touch would also be nice.
		if (button == Input.Buttons.LEFT) {
			frameworkDispatcher.dispatchMessage(this, MessageTypes.Framework.TOUCH_DOWN, new Vector2(x, y));
		}
		previousZoom = 0;
		return false;
	}

	/**
	 * @TODO make this only process the input. Then send the relevant data off as a message.
	 *       Code for running the tweens is in GameScreen and TweenHandler.
	 * @param x
	 * @param y
	 * @param count
	 * @param button
	 * @return
	 */
	@Override
	public boolean tap(float x, float y, int count, int button) {
        if (button == Input.Buttons.LEFT) {
            frameworkDispatcher.dispatchMessage(this, MessageTypes.Framework.TAP, screenToWorld(x, y));
        }
		return false;
	}

    /**
     * Gets 3d world coordinates from 2d screen inputs.
     * @param x
     * @param y
     * @return
     */
    private Vector3 screenToWorld(float x, float y) {
        ray = screen.active().getPickRay(x, y);
        Intersector.intersectRayPlane(ray, xzPlane, tmp1Vec3);
        return tmp1Vec3;
    }

	@Override
	public boolean longPress(float x, float y) {
		return false;
	}

	@Override
	public boolean fling(float velocityX, float velocityY, int button) {
		return false;
	}

	@Override
	public boolean pan(float x, float y, float deltaX, float deltaY) {
		return false;
	}

	/**
	 * Eventually passes zoom to the PlayDetector.zoom function.
	 * @TODO Evaluate. This hasn't triggered due to no touch. Eventually uses the detector, so final can go to screen.
	 * @param initialDistance
	 * @param distance
	 * @return
	 */
	@Override
	public boolean zoom(float initialDistance, float distance) {
		float newZoom = distance - initialDistance;
		float amount = newZoom - previousZoom;
		previousZoom = newZoom;
		float w = Gdx.graphics.getWidth(), h = Gdx.graphics.getHeight();
		return detector.pinchZoom(amount / ((w > h) ? h : w));
	}

	@Override
	public boolean pinch(Vector2 initialPointer1, Vector2 initialPointer2, Vector2 pointer1, Vector2 pointer2) {
		return false;
	}

	@Override
	public boolean handleMessage(Telegram msg) {
		return false;
	}
};