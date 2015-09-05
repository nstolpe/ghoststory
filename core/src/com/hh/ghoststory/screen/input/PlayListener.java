package com.hh.ghoststory.screen.input;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
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
import com.hh.ghoststory.lib.tween.accessors.QuaternionAccessor;
import com.hh.ghoststory.lib.tween.accessors.Vector3Accessor;
import com.hh.ghoststory.screen.PlayScreen;

/**
 * Created by nils on 8/29/15.
 */
public class PlayListener extends GestureDetector.GestureAdapter {
	private final PlayScreen screen;
	public PlayDetector controller;
	private float previousZoom;
	// @TODO move cachedMat and activeDistance to the Screen.
	private Material cachedMat;
	private ModelInstance activeInstance;

	public PlayListener(PlayScreen screen) {
		this.screen = screen;
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
				BoundingBox box = instance.calculateBoundingBox(new BoundingBox());
				position.add(box.getCenter(new Vector3()));
				box.getCenter(center);
				box.getDimensions(dimensions);
				radius = dimensions.len() / 2f;
				Texture tex = new Texture(Gdx.files.internal("models/ghost_texture_blue.png"), true);
//                Pixmap pm = screen.assetManager.get("models/ghost_texture_blue.png", Pixmap.class);
//				Texture tex = new Texture(pm, true);
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

	/**
	 * @TODO make this only process the input. Then send the relevant data off as a message.
	 * @param x
	 * @param y
	 * @param count
	 * @param button
	 * @return
	 */
	@Override
	public boolean tap(float x, float y, int count, int button) {
        Ray pickRay = screen.getPickRay(x, y);
        Vector3 intersection = new Vector3();
        Plane xzPlane = new Plane(new Vector3(0, 1, 0), 0);
        Intersector.intersectRayPlane(pickRay, xzPlane, intersection);
        Vector3 position = Mappers.position.get(screen.pc).position;
        Quaternion rotation = Mappers.rotation.get(screen.pc).rotation;

        float translationDuration = intersection.dst(position) / 3;
//        float translationDuration = intersection.dst(position) / screen.character.speed;

        float newAngle = MathUtils.atan2(intersection.x - position.x, intersection.z - position.z) * 180 / MathUtils.PI;

        float currentAngle = rotation.getYaw();
//				float currentAngle = rotation.getAxisAngle(new Vector3(0,1,0));

        // keep the angle between -180 and 180. Why doesn't the quat rotation take care of this?
        if (Math.abs(newAngle - currentAngle) >  180)
            newAngle += newAngle < currentAngle ? 360 : -360;

        Quaternion newRotation = new Quaternion(new Vector3(0,1,0), newAngle).nor();

        // invert he newRotation if the dot product between it and rotation is < 0
        if (rotation.dot(newRotation) < 0) {
            newRotation.x = -newRotation.x;
            newRotation.y = -newRotation.y;
            newRotation.z = -newRotation.z;
            newRotation.w = -newRotation.w;
        }

        // Figure this out w/ quats, if possible.
        float rotationDuration = Math.abs(currentAngle - newAngle) / 200;
//				float rotationDuration = Math.abs(rotation.dot(newRotation));

//        screen.killTween(screen.character.position, Vector3Accessor.POSITION_XYZ);
//        screen.killTween(screen.character.rotation, QuaternionAccessor.ROTATION);
//
//        screen.tweenFaceAndMoveTo(screen.character.rotation, newRotation, screen.character.position, new Vector3(intersection.x, intersection.y, intersection.z), rotationDuration, translationDuration);

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