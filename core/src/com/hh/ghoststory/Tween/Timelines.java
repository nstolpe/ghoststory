package com.hh.ghoststory.Tween;

import aurelienribon.tweenengine.*;
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
	 * @param rotationTarget    Target rotation of the Actor being tweened.
	 * @param rotationDuration    Duration the rotation tween will last.
	 *
	 * @param translation      Current translation of the Actor being tweened.
	 * @param translationTarget  Target translation of the Actor being tweened.
	 * @param translationDuration  Duration the translation tween will last.
	 * @return A configured Timeline.
	 */
	public static Timeline faceAndGoto(Quaternion rotation, Quaternion rotationTarget, float rotationDuration, Vector3 translation, Vector3 translationTarget, float translationDuration) {
		Tween rotate = Tweens.rotate(rotationDuration, TweenEquations.easeNone, rotation, rotationTarget);
		Tween translate = Tweens.translate(translationDuration, TweenEquations.easeNone, translation, translationTarget);

		return Timeline.createSequence().push(rotate).push(translate);
	}
}