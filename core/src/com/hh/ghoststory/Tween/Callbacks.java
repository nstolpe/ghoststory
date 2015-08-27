package com.hh.ghoststory.Tween;

import aurelienribon.tweenengine.*;
import java.util.Random;

/**
 * Class to hold TweenCallbacks.
 */
public class Callbacks {
	/**
	 * Returns a tween ballback that will make an object move between
	 * two arrays (pos1 and pos2). Calls itself as a callback to keep
	 * it going. Useful for bouncing between points or colors (alpha
	 * could be included).
	 *
	 * @param manager  TweenManager that will run this tween.
	 * @param accessor TweenAccessor that should be used for this tween.
	 * @param target   Object that will be tweened.
	 * @param duration Duration the tween should last.
	 * @param easing   Easing function to use use while approaching pos1 or pos2.
	 * @param loop     Whether or not this should loop.
	 * @param pos1     Array of float values the tween will start and end at.
	 * @param pos2     Array of float values the tween will reach before returning to pos1.
	 */
	public static TweenCallback bounce(final TweenManager manager, final int accessor, final Object target, final float duration, final TweenEquation easing, final boolean loop, final float[] pos1, final float[] pos2) {
		return new TweenCallback() {
			@Override
			public void onEvent(int type, BaseTween<?> source) {
				Timeline timeline = Timeline.createSequence();
				timeline.push(Tween.to(target, accessor, duration).target(pos2).ease(easing));
				timeline.push(Tween.to(target, accessor, duration).target(pos1).ease(easing));

				if (loop) timeline.setCallback(this);
				timeline.start(manager);
			}
		};
	}

	/**
	 * Cycles to randomly generated targets. Useful for colors or maybe a crazy robot.
	 *
	 * @param manager  TweenManager that will run this tween.
	 * @param accessor TweenAccessor that should be used for this tween.
	 * @param target   Object that will be tweened.
	 * @param duration Duration the tween should last.
	 * @param easing   Easing function to use use while approaching pos1 or pos2.
	 * @param loop     Whether or not this should loop.
	 * @param count    The number of random numbers to generate.
	 * @return
	 */
	public static TweenCallback random(final TweenManager manager, final int accessor, final Object target, final float duration, final TweenEquation easing, final boolean loop, final int count) {
		return new TweenCallback() {
			@Override
			public void onEvent(int type, BaseTween<?> source) {
				Random generator = new Random();
				float[] randoms = new float[count];

				for (int i = 0; i < count; i++)
					randoms[i] = generator.nextFloat();

				Timeline timeline = Timeline.createSequence();
				timeline.push(Tween.to(target, accessor, duration).target(randoms).ease(easing));

				if (loop) timeline.setCallback(this);
				timeline.start(manager);
			}
		};
	}
}
