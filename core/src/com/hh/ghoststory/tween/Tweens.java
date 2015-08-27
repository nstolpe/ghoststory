package com.hh.ghoststory.tween;

import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenEquation;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;

/**
 * Class to store often used tweens.
 */
public class Tweens {
	/**
	 * Rotates a Quaternion.
	 * @param duration  Duration Tween should last.
	 * @param easing    Easting equation for the Tween.
	 * @param current   Quaternion holding rotation at start of Tween.
	 * @param target    Quaternion holding rotation at end of Tween.
	 * @return
	 */
	public static Tween rotate(float duration, final TweenEquation easing, Quaternion current, Quaternion target) {
		float[] targets = new float[4];

		targets[0] = target.x;
		targets[1] = target.y;
		targets[2] = target.z;
		targets[3] = target.w;

		return Tween.to(current, QuaternionAccessor.ROTATION, duration).target(targets).ease(easing);
	}

	/**
	 * Translates a Vector3
	 * @param duration  Duration Tween should last.
	 * @param easing    Easting equation for the Tween.
	 * @param current   Vector3 holding rotation at start of Tween.
	 * @param target    Vector3 holding rotation at end of Tween.
	 * @return
	 */
	public static Tween translate(float duration, final TweenEquation easing, Vector3 current, Vector3 target) {
		float[] targets = new float[3];

		targets[0] = target.x;
		targets[1] = target.y;
		targets[2] = target.z;

		return Tween.to(current, Vector3Accessor.POSITION_XYZ, duration).target(targets).ease(easing);
	}
}
