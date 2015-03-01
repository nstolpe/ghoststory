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
//				System.out.println("returnValues: x-" + returnValues[0] + " y-" + returnValues[1] + " z-" + returnValues[2] + " w-" + returnValues[3]);
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
//				System.out.println( "newValues: x-" + newValues[0] + " y-" + newValues[1] + " z-" + newValues[2] + " w-" + newValues[3]);
//				quat.set(newValues[0], newValues[1], newValues[2], newValues[3]).nor();
				quat.slerp(new Quaternion(newValues[0], newValues[1], newValues[2], newValues[3]), 1).nor();
			default:
				assert false;
		}
	}
}