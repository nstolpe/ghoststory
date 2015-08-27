package com.hh.ghoststory.Tween;

import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenEquation;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;

/**
 * Class to store often used tweens.
 */
public class Tweens {
	public static Tween rotate(float duration, final TweenEquation easing, Quaternion current, Quaternion target) {
		float[] targets = new float[4];

		targets[0] = target.x;
		targets[1] = target.y;
		targets[2] = target.z;
		targets[3] = target.w;

		return Tween.to(current, QuaternionAccessor.ROTATION, duration).target(targets).ease(easing);
	}

	public static Tween translate(float duration, final TweenEquation easing, Vector3 current, Vector3 target) {
		float[] targets = new float[3];

		targets[0] = target.x;
		targets[1] = target.y;
		targets[2] = target.z;

		return Tween.to(current, Vector3Accessor.POSITION_XYZ, duration).target(targets).ease(easing);
	}
}
