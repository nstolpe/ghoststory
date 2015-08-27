package com.hh.ghoststory.Tween;

import aurelienribon.tweenengine.TweenAccessor;
import com.badlogic.gdx.math.Vector3;

/**
 * Created by nils on 1/27/15.
 */
public class Vector3Accessor implements TweenAccessor<Vector3> {
	public static final int POSITION_X   = 0;
	public static final int POSITION_Y   = 1;
	public static final int POSITION_Z   = 2;
	public static final int POSITION_XY  = 3;
	public static final int POSITION_YZ  = 4;
	public static final int POSITION_ZX  = 5;
	public static final int POSITION_XYZ = 6;

	@Override
	public int getValues(Vector3 vec, int tweenType, float[] returnValues) {
		switch (tweenType) {
			case POSITION_X:
				returnValues[0] = vec.x;
				return 1;
			case POSITION_Y:
				returnValues[0] = vec.y;
				return 1;
			case POSITION_Z:
				returnValues[0] = vec.z;
				return 1;
			case POSITION_XY:
				returnValues[0] = vec.x;
				returnValues[1] = vec.y;
				return 2;
			case POSITION_YZ:
				returnValues[0] = vec.y;
				returnValues[1] = vec.z;
				return 2;
			case POSITION_ZX:
				returnValues[0] = vec.z;
				returnValues[1] = vec.x;
				return 2;
			case POSITION_XYZ:
				returnValues[0] = vec.x;
				returnValues[1] = vec.y;
				returnValues[2] = vec.z;
				return 3;
			default:
				assert false;
				return -1;
		}
	}

	@Override
	public void setValues(Vector3 vec, int tweenType, float[] newValues) {
		switch (tweenType) {
			case POSITION_X:
				vec.x = newValues[0];
				break;
			case POSITION_Y:
				vec.y = newValues[0];
				break;
			case POSITION_Z:
				vec.z = newValues[0];
				break;
			case POSITION_XY:
				vec.x = newValues[0];
				vec.y = newValues[1];
				break;
			case POSITION_YZ:
				vec.y = newValues[0];
				vec.z = newValues[1];
				break;
			case POSITION_ZX:
				vec.z = newValues[0];
				vec.x = newValues[1];
				break;
			case POSITION_XYZ:
                vec.set(newValues[0], newValues[1], newValues[2]);
				break;
			default:
				assert false;
				break;
		}
	}
}
