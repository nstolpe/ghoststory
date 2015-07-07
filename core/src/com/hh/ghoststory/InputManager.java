package com.hh.ghoststory;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.math.*;
import com.badlogic.gdx.math.collision.Ray;
import com.hh.ghoststory.renderers.ModelBatchRenderer;
import com.hh.ghoststory.screens.GameScreen;
import com.hh.ghoststory.tween_accessors.QuaternionAccessor;
import com.hh.ghoststory.tween_accessors.Vector3Accessor;

import java.util.Random;

/**
 * Created by nils on 7/6/15.
 * This class has a lot of functionality moved here from GameScreen. It's also tightly bound to GameScreen and a few objects
 * attached to it. This should all be fixed.
 *
 * Maybe make this into a specific type of input manager...for a screen based on a camera maybe? OrthographicGameScreenHandler
 * Then move camera code from ModelBatchRenderer over here. Since camera and input are more closely related than rendering.
 * Look for `screen.renderer` for things that need fixing.
 */
public class InputManager {
	private InputMultiplexer multiplexer = new InputMultiplexer();
	private GameScreen screen;

	public InputManager(GameScreen screen) {
		this.screen = screen;
		this.multiplexer.addProcessor(getDefaultInputAdapter());
		this.multiplexer.addProcessor(getDefaultGestureDetector());
		Gdx.input.setInputProcessor(multiplexer);
	}
	/*
	 * Returns the InputAdapter for this screen. Only handles scroll now.
	 * Too tightly tied to the GameScreen right now. Lighting updates should happen first though,
	 * since this is working right now.
	 */
	private InputAdapter getDefaultInputAdapter() {
		return new InputAdapter() {
			@Override
			public boolean scrolled(int amount) {
				screen.renderer.zoomCamera(amount);
				return false;
			}
			@Override
			public boolean keyUp (int keycode) {
				switch(keycode) {
					// Switch the camera between orthographic and perspective when C is pressed.
					case Input.Keys.C:
						if (screen.renderer.getActiveCameraType() == ModelBatchRenderer.PERSPECTIVE) {
							screen.renderer.setActiveCameraType(ModelBatchRenderer.ORTHOGRAPHIC);
						} else if (screen.renderer.getActiveCameraType() == ModelBatchRenderer.ORTHOGRAPHIC) {
							screen.renderer.setActiveCameraType(ModelBatchRenderer.PERSPECTIVE);
						}
						screen.resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
						break;
					// Set the
					case Input.Keys.L:
						Random generator = new Random();
						float red = generator.nextFloat();
						float green = generator.nextFloat();
						float blue = generator.nextFloat();
						screen.barLight.set(new Color(red,green,blue,1f),12,1,10,1);
						break;
				}
				return false;
			}
		};
	}
	/*
	 * Returns the GestureDetector for this screen.
	 */
	private GestureDetector getDefaultGestureDetector() {
		return new GestureDetector(new GestureDetector.GestureListener() {
			private final Plane xzPlane = new Plane(new Vector3(0, 1, 0), 0);
			//			private Vector3 intersection = new Vector3();
			private Vector3 curr = new Vector3();
			private Vector2 last = new Vector2(-1, -1);
			private Vector3 delta = new Vector3();
			private Vector3 axisVec = new Vector3();
			private float initialScale = 1.0f;
			private Ray pickRay;

			@Override
			public boolean touchDown(float x, float y, int pointer, int button) {
//				this.initialScale = screen.camera.zoom;
				return false;
			}

			@Override
			public boolean tap(float x, float y, int count, int button) {
				pickRay = screen.renderer.getPickRay(x, y);
				Vector3 intersection = getIntersection(pickRay);

				Vector3 position = screen.ghost.position;
				Quaternion rotation = screen.ghost.rotation;

				float translationDuration = intersection.dst(position) / screen.ghost.speed;

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

				screen.tweenManager.killTarget(screen.ghost.position, Vector3Accessor.POSITION_XYZ);
				screen.tweenManager.killTarget(screen.ghost.rotation, QuaternionAccessor.ROTATION);

				screen.tweenFaceAndMoveTo(screen.ghost.rotation, newRotation, screen.ghost.position, new Vector3(intersection.x, intersection.y, intersection.z), rotationDuration, translationDuration);

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
				this.pickRay = screen.renderer.getActiveCamera().getPickRay(x, y);
				Intersector.intersectRayPlane(this.pickRay, xzPlane, curr);

				if (!(this.last.x == -1 && this.last.y == -1)) {
					this.pickRay = screen.renderer.getActiveCamera().getPickRay(this.last.x, this.last.y);
					Intersector.intersectRayPlane(this.pickRay, xzPlane, delta);
					this.delta.sub(this.curr);
					screen.renderer.getActiveCamera().position.add(this.delta.x, this.delta.y, this.delta.z);
				}

				this.last.set(x, y);

				return false;
			}

			@Override
			public boolean panStop(float x, float y, int pointer, int button) {
				this.last.set(-1, -1);
				return false;
			}

			@Override
			public boolean zoom(float initialDistance, float distance) {
				float zoom = distance - initialDistance;
//				float zoom = initialDistance / distance;
				float deltaTime = Gdx.graphics.getDeltaTime();
//				float speed = 0.1f * deltaTime;
				// amount fingers moved apart divided by time. should be speed of movement
				float speed = zoom / deltaTime;
				Vector3 camZoom = new Vector3();
				camZoom.set(screen.renderer.getActiveCamera().direction.cpy());
				camZoom.nor().scl(speed * deltaTime / 100);


				if( ((screen.renderer.getActiveCamera().position.y > 3f) && (zoom > 0)) || ((screen.renderer.getActiveCamera().position.y < 10f) && (zoom < 0)) ) {
					screen.renderer.getActiveCamera().translate(camZoom.x, camZoom.y, camZoom.z);
				}
//				float factor = distance / initialDistance;
//
//
//				if (initialDistance > distance && screen.camera.fieldOfView < 120f) {
//					screen.camera.fieldOfView += factor;
//				} else if (initialDistance < distance && screen.camera.fieldOfView > 10f) {
//					screen.camera.fieldOfView -= factor;
//				}
				System.out.println("here " + zoom);
				return false;
			}

			@Override
			public boolean pinch(Vector2 initialPointer1, Vector2 initialPointer2, Vector2 pointer1, Vector2 pointer2) {
//				System.out.println("pinch");
//				float ratio = initialPointer1.dst(initialPointer2) / pointer1.dst(pointer2);
////				screen.camera.zoom = MathUtils.clamp(this.initialScale * ratio, 0.1f, 1.0f);
//				screen.camera.fieldOfView += MathUtils.clamp(this.initialScale * ratio, 0.1f, 1.0f);
//				screen.camera.
				return false;
			}

			private Vector3 getIntersection(Ray pickRay) {
				Vector3 intersection = new Vector3();
				Intersector.intersectRayPlane(pickRay, this.xzPlane, intersection);
				return intersection;
			}
		});
	}
}
