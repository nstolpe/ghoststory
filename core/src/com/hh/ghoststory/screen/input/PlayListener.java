package com.hh.ghoststory.screen.input;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.math.collision.Ray;
import com.hh.ghoststory.screen.core.DualCameraScreen;

/**
 * Created by nils on 8/29/15.
 */
public class PlayListener extends GestureDetector.GestureAdapter {
	private final DualCameraScreen screen;
	public PlayDetector controller;
	private float previousZoom;
	private Material cachedMat;
	private ModelInstance activeInstance;

	public PlayListener(DualCameraScreen screen) {
		this.screen = screen;
	}

	/**
	 * This needs some thought. A lot can happen on touchdown, depending on the target. Seems
	 * to be only getting the left click.
	 * Right now, it changes the color of the ghost it hits. It might also be better on touchUp
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
				BoundingBox box = instance.calculateBoundingBox(new BoundingBox());
				position.add(box.getCenter(new Vector3()));
				box.getCenter(center);
				box.getDimensions(dimensions);
				radius = dimensions.len() / 2f;
				Texture tex = new Texture(Gdx.files.internal("models/ghost_texture_blue.png"), true);
				tex.setFilter(Texture.TextureFilter.MipMapLinearLinear, Texture.TextureFilter.Nearest);
				float dist2 = ray.origin.dst2(position);

				if (distance >= 0f && dist2 > distance) continue;

				if (Intersector.intersectRaySphere(ray, position, radius, null)) {
					cachedMat = instance.materials.get(0).copy();
					activeInstance = instance;
	//				instance.materials.get(0).set(ColorAttribute.createDiffuse(Color.LIGHT_GRAY), ColorAttribute.createAmbient(Color.LIGHT_GRAY));
					instance.materials.get(0).set(new TextureAttribute(TextureAttribute.Diffuse, tex), new ColorAttribute(ColorAttribute.Specular, Color.GOLD));

					distance = dist2;
				}
			}
		}

		previousZoom = 0;
		return false;
	}

	@Override
	public boolean tap(float x, float y, int count, int button) {
		return false;
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
	 * @TODO Evaluate. This hasn't triggered due to no touch. Eventually uses the controller, so final can go to screen.
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
		return controller.pinchZoom(amount / ((w > h) ? h : w));
	}

	@Override
	public boolean pinch(Vector2 initialPointer1, Vector2 initialPointer2, Vector2 pointer1, Vector2 pointer2) {
		return false;
	}
};