package com.hh.ghoststory.lib.tween;

import aurelienribon.tweenengine.*;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;

import java.util.Random;

/**
 * Class to hold TweenCallbacks.
 *
 * Using these functions to get new Timelines:
 *
 * Timeline bounce = Timelines.bounce(pointLight.position, Vector3Accessor.POSITION_XYZ, 1, new Vector3(0,0,0), new Vector3(10,0,10), TweenEquations.easeInSine);
 * bounce.start(tweenManager);
 *
 * Example of using these tweens as looping callbacks:
 *
 * TweenCallback callback = new TweenCallback() {
 *     @Override
 *     public void onEvent(int type, BaseTween<?> source) {
 *         Timeline timeline = Timelines.bounce(modelInstance.position, Vector3Accessor.POSITION_XYZ, 1, new Vector3(0,0,0), new Vector3(10,0,10), TweenEquations.easeInSine);
 *         timeline.setCallback(this);
 *         timeline.start(tweenManager);
 *     }
 * };
 *
 * TweenCallback callback = new TweenCallback() {
 *     @Override
 *     public void onEvent(int type, BaseTween<?> source) {
 *         Timeline timeline = Timelines.random(tweenable, ColorAccessor.COLORS, 1, TweenEquations.easeNone, count);
 *         timeline.setCallback(this);
 *         timeline.start(tweenManager);
 *     }
 * };
 *
 */
public class Timelines {
	/**
	 * Returns a Tween Timeline that will make an object move between
	 * two arrays (start and finish). Good for looping endlessly w/ callbacks.
	 *
	 * @param tweenable  Object that will be tweened.
	 * @param accessor   TweenAccessor that should be used for this tween.
	 * @param duration   Duration the tween should last.
	 * @param easing     Easing function to use use while approaching start or finish.
	 * @param start      Array of float values the tween will start and end at.
	 * @param finish     Array of float values the tween will reach before returning to start.
	 * @return           A configured TweenEngine Timeline object.
	 */
	public static Timeline bounce(final Object tweenable, final int accessor, final float duration,  final TweenEquation easing, final float[] start, final float[] finish) {
		Timeline timeline = Timeline.createSequence();
		timeline.push(Tween.to(tweenable, accessor, duration).target(finish).ease(easing));
		timeline.push(Tween.to(tweenable, accessor, duration).target(start).ease(easing));
		return timeline;
	}

	/**
	 * Returns a Tween Timeline that is set to target a randomly defined target. Good for
	 * colors and looping w/ callbacks.
	 *
	 * @param tweenable  Object that will be tweened.
	 * @param accessor   TweenAccessor that should be used for this tween.
	 * @param duration   Duration the tween should last.
	 * @param easing     Easing function to use use while approaching start or finish.
	 * @param count      The number of random numbers to generate.
	 * @return
	 */
	public static Timeline random(final Object tweenable, final int accessor, final float duration, final TweenEquation easing, final int count) {
		Random generator = new Random();
		float[] target = new float[count];
		Timeline timeline = Timeline.createSequence();

		for (int i = 0; i < count; i++) target[i] = generator.nextFloat();

		timeline.push(Tween.to(tweenable, accessor, duration).target(target).ease(easing));
		return timeline;
	}

	/**
	 * Returns a timeline with one rotate Tween and one translate Tween. It will cause
	 * its target to face a point and move towards it. Meant to be used on actors, which
	 * will have a position Quaternion and a rotation Vector3. They haven't been built yet.
	 * Use to move an actor that will first turn to face a target location, then move
	 * towards it.
	 *
	 * @param rotation        Current rotation of the Actor being tweened.
	 * @param translation      Current translation of the Actor being tweened.
	 * @param translationTarget  Target translation of the Actor being tweened.
	 * @param rate  The rate that controls the duration of the Tween.
	 *
	 * @return A configured Timeline.
	 * @TODO test some of the options and then clean up the comments.
	 */
	public static Timeline faceAndGo(Quaternion rotation, Vector3 translation, Vector3 translationTarget, final float rate) {
		float angle = rotation.getYaw();
//		float angle = rotation.getAxisAngle(new Vector3(0, 1, 0));
		float angleTarget = MathUtils.atan2(translationTarget.x - translation.x, translationTarget.z - translation.z);
		final float translationDuration = translationTarget.dst(translation) / rate;
		final Quaternion rotationTarget = new Quaternion(new Vector3(0,1,0), angleTarget).nor();

//		keep the angle between -180 and 180. Why doesn't the quat rotation take care of this?
		if (Math.abs(angleTarget - angle) >  180) angleTarget += angleTarget < angle ? 360 : -360;

//		invert he rotationTarget if the dot product between it and rotation is < 0
		if (rotation.dot(rotationTarget) < 0) rotationTarget.mul(-1);

//		Figure this out w/ quats, if possible.
		float rotationDuration = Math.abs(angle - angleTarget) / 200;
//		float rotationDuration = Math.abs(rotation.dot(rotationTarget));

		Tween rotate = Tweens.rotate(rotationDuration, TweenEquations.easeNone, rotation, rotationTarget);
		Tween translate = Tweens.translate(translationDuration, TweenEquations.easeNone, translation, translationTarget);

//		These can't be here anymore and should be moved to whatever is requesting the Timeline.
//		screen.killTween(screen.character.position, Vector3Accessor.POSITION_XYZ);
//		screen.killTween(screen.character.rotation, QuaternionAccessor.ROTATION);
		return Timeline.createSequence().push(rotate).push(translate);
	}
}