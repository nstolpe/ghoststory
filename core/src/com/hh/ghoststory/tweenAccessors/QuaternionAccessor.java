package com.hh.ghoststory.tweenAccessors;

import aurelienribon.tweenengine.TweenAccessor;
import com.badlogic.gdx.math.Quaternion;

/**
 * Created by nils on 1/27/15.
 */
public class QuaternionAccessor implements TweenAccessor<Quaternion> {
	public static final int ROTATION = 0;
	@Override
	public int getValues(Quaternion quat, int tweenType, float[] returnValues) {
		switch (tweenType) {
			case ROTATION:
				returnValues[0] = quat.x;
				returnValues[1] = quat.y;
				returnValues[2] = quat.z;
				returnValues[3] = quat.w;
				return 4;
			default:
				assert false;
				return -1;
		}
	}

	@Override
	public void setValues(Quaternion quat, int tweenType, float[] newValues) {
		switch (tweenType) {
			case ROTATION:
				quat.set(newValues[0], newValues[1], newValues[2], newValues[3]);
			default:
				assert false;
		}
	}
}