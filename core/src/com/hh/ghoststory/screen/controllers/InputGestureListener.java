package com.hh.ghoststory.screen.controllers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
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
public class InputGestureListener extends GestureDetector.GestureAdapter {
	private final DualCameraScreen screen;
	public InputController controller;
	private float previousZoom;
	private Vector3 position = new Vector3();

	public InputGestureListener (DualCameraScreen screen) {
		this.screen = screen;
	}
	@Override
	public boolean touchDown (float x, float y, int pointer, int button) {
//			Material mat = character.materials.get(0).copy();
//			character.materials.get(0).set(ColorAttribute.createDiffuse(Color.BLACK));
//			character.materials.get(0).set(mat);
		Ray ray = screen.active.getPickRay(x, y);
		int result = -1;
//		float distance = -1;
//		for (int i = 0; i < screen.instances.size; ++i) {
//			final ModelInstance instance = screen.instances.get(i);
//			instance.transform.getTranslation(position);
//			BoundingBox box = instance.calculateBoundingBox(new BoundingBox());
//			position.add(box.getCenter(new Vector3()));
//			float dist2 = ray.origin.dst2(position);
//			if (distance >= 0f && dist2 > distance) continue;
//			if (Intersector.intersectRaySphere(ray, position, instance.radius, null)) {
//				result = i;
//				distance = dist2;
//			}
//		}
//		return result;
		previousZoom = 0;
		return false;
	}

	@Override
	public boolean tap (float x, float y, int count, int button) {
		return false;
	}

	@Override
	public boolean longPress (float x, float y) {
		return false;
	}

	@Override
	public boolean fling (float velocityX, float velocityY, int button) {
		return false;
	}

	@Override
	public boolean pan (float x, float y, float deltaX, float deltaY) {
		return false;
	}

	@Override
	public boolean zoom (float initialDistance, float distance) {
		float newZoom = distance - initialDistance;
		float amount = newZoom - previousZoom;
		previousZoom = newZoom;
		float w = Gdx.graphics.getWidth(), h = Gdx.graphics.getHeight();
		return controller.pinchZoom(amount / ((w > h) ? h : w));
	}

	@Override
	public boolean pinch (Vector2 initialPointer1, Vector2 initialPointer2, Vector2 pointer1, Vector2 pointer2) {
		return false;
	}
};