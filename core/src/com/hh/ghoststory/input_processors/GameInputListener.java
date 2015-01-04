package com.hh.ghoststory.input_processors;

import aurelienribon.tweenengine.*;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.math.*;
import com.badlogic.gdx.math.collision.Ray;
import com.hh.ghoststory.accessors.GameModelTweenAccessor;
import com.hh.ghoststory.screens.GameScreen;

import java.util.HashMap;

/**
 * Created by nils on 2/9/14.
 */
public class GameInputListener implements GestureDetector.GestureListener {
	final Vector3 curr = new Vector3();
	final Vector2 last = new Vector2(-1, -1);
	final Vector3 delta = new Vector3();
	private float initialScale = 1.0f;
	public GameScreen screen;
	final Plane xzPlane = new Plane(new Vector3(0, 1, 0), 0);
	final Vector3 intersection = new Vector3();
	private Vector3 position = new Vector3();
	private Quaternion currentRotation = new Quaternion();
	public GameInputListener(GameScreen screen) {
		this.screen = screen;
	}

	@Override
	public boolean touchDown(float x, float y, int pointer, int button) {
		initialScale = screen.camera.zoom;
		return false;
	}

	@Override
	public boolean tap(float x, float y, int count, int button) {
		Ray pickRay = screen.camera.getPickRay(Gdx.input.getX(), Gdx.input.getY());
		Intersector.intersectRayPlane(pickRay, xzPlane, intersection);

		position = screen.ghost.model.transform.getTranslation(position);

		currentRotation = screen.ghost.model.transform.getRotation(currentRotation);
		final float duration = intersection.dst(position) / screen.ghost.speed;
		float newRotation = MathUtils.atan2(intersection.x - position.x, intersection.z - position.z) * 180 / MathUtils.PI;
		// lines below also in getValues of the GhostModelTweenAccessor, maybe move them
		Vector3 axisVec = new Vector3();
		int angle = (int) (screen.ghost.model.transform.getRotation(new Quaternion()).getAxisAngle(axisVec) * axisVec.nor().y);

		float rotDuration = Math.abs(angle - newRotation) / 200;

		TweenCallback foo = new TweenCallback() {
//			public void TweenCallback(float duration) {
//				this.duration = duration;
//			}

			@Override
			public void onEvent(int i, BaseTween<?> baseTween) {
				Object duration = baseTween.getUserData();
//				float duration;
//				duration = (float) userData.get("duration");
				System.out.println(duration);
				Vector3 axisVec = new Vector3();
				int angle = (int) (screen.ghost.model.transform.getRotation(new Quaternion()).getAxisAngle(axisVec) * axisVec.nor().y);
				Tween.set(screen.ghost, GameModelTweenAccessor.ROTATION).target(angle);
				Tween.to(screen.ghost, GameModelTweenAccessor.POSITION_XYZ, (Float) baseTween.getUserData())
						.target(intersection.x, intersection.y, intersection.z)
						.ease(TweenEquations.easeNone)
						.start(screen.ghostManager);
			}
		};

		HashMap userData = new HashMap();
		userData.put("duration", duration);
//		screen.ghostManager.killTarget(screen.ghost);
		Tween.to(screen.ghost, GameModelTweenAccessor.ROTATION, Math.abs(angle - newRotation) / 200)
				.target(newRotation)
				.ease(TweenEquations.easeNone)
				.setUserData(duration)
//				.setCallback(foo)
				.start(screen.ghostManager);

		Tween.to(screen.ghost, GameModelTweenAccessor.POSITION_XYZ, duration)
				.target(intersection.x, intersection.y, intersection.z)
				.ease(TweenEquations.easeNone)
				.start(screen.ghostManager);
//		Timeline.createSequence()
//				.push(Tween.to(screen.ghost, GameModelTweenAccessor.ROTATION, duration).target(newRotation))
//				.push(Tween.set(screen.ghost, GameModelTweenAccessor.ROTATION).target(newRotation))
//				.push(Tween.to(screen.ghost, GameModelTweenAccessor.POSITION_XYZ, duration).target(intersection.x, intersection.y, intersection.z))
//				.push(Tween.set(screen.ghost, GameModelTweenAccessor.POSITION_XYZ).target(intersection.x, intersection.y, intersection.z))
//				.start(screen.ghostManager);

		return false;
	}

	public void move(float duration) {
		Tween.to(screen.ghost, GameModelTweenAccessor.POSITION_XYZ, duration)
				.target(intersection.x, intersection.y, intersection.z)
				.ease(TweenEquations.easeNone)
				.start(screen.ghostManager);
	}
	@Override
	public boolean longPress(float x, float y) {
		return true;
	}

	@Override
	public boolean fling(float velocityX, float velocityY, int button) {
		return false;
	}

	@Override
	public boolean pan(float x, float y, float deltaX, float deltaY) {
		Ray pickRay = screen.camera.getPickRay(x, y);
		Intersector.intersectRayPlane(pickRay, xzPlane, curr);

		if (!(last.x == -1 && last.y == -1)) {
			pickRay = screen.camera.getPickRay(last.x, last.y);
			Intersector.intersectRayPlane(pickRay, xzPlane, delta);
			delta.sub(curr);
			screen.camera.position.add(delta.x, delta.y, delta.z);
//			System.out.println("X: " + screen.camera.position.x + " Z: " + screen.camera.position.z);
		}

		last.set(x, y);

		return false;
	}

	@Override
	public boolean panStop(float x, float y, int pointer, int button) {
		last.set(-1, -1);
		return false;
	}

	@Override
	public boolean zoom(float initialDistance, float distance) {
		return false;
	}

	@Override
	public boolean pinch(Vector2 initialPointer1, Vector2 initialPointer2, Vector2 pointer1, Vector2 pointer2) {
		float ratio = initialPointer1.dst(initialPointer2) / pointer1.dst(pointer2);
		screen.camera.zoom = MathUtils.clamp(initialScale * ratio, 0.1f, 1.0f);
		return false;
	}
}