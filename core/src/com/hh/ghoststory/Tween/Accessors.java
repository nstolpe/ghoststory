package com.hh.ghoststory.Tween;

import aurelienribon.tweenengine.*;
import com.badlogic.gdx.math.Vector3;

/**
 * Created by nils on 8/27/15.
 */
public class Accessors {
	/**
	 * Returns a tween ballback that will make an object
	 * move between two points (pos1 and pos2). Calls itself as a callback
	 * to keep it going.
	 * @TODO add easing option
	 */
	public static TweenCallback bounce3D(final TweenManager manager, final Object target, final float[] pos1, final float[] pos2, final float duration, final TweenEquation easing) {
		return new TweenCallback() {
			@Override
			public void onEvent(int i, BaseTween<?> basetween) {
				Timeline.createSequence()
						.push(Tween.to(target, Vector3Accessor.POSITION_XYZ, 1)
								.target(pos1)
								.ease(easing))
						.push(Tween.to(target, Vector3Accessor.POSITION_XYZ, 1)
								.target(pos2)
								.ease(easing))
						.setCallback(this)
						.start(manager);
			}
		};
	}

//	public static TweenCallback
}
