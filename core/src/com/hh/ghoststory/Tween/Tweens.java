package com.hh.ghoststory.Tween;

import aurelienribon.tweenengine.*;
import java.util.Random;

/**
 * Class to hold TweenCallbacks.
 * Example of using these tweens as callbacks:
 *
 * TweenCallback callback = new TweenCallback() {
 *     @Override
 *     public void onEvent(int type, BaseTween<?> source) {
 *         Timeline timeline = bounce(tweenable, accessor, duration, start, finish, easing);
 *         timeline.setCallback(this);
 *         timeline.start(manager);
 *     }
 * };
 *
 * TweenCallback callback = new TweenCallback() {
 *     @Override
 *     public void onEvent(int type, BaseTween<?> source) {
 *         Timeline timeline = random(tweenable, accessor, duration, easing, count);
 *         timeline.setCallback(this);
 *         timeline.start(manager);
 *     }
 * };
 *
 */
public class Tweens {


	/**
	 * Returns a tween Timeline that will make an object move between
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
}
