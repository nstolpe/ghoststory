package com.hh.ghoststory.Tween;

import aurelienribon.tweenengine.TweenAccessor;
import com.badlogic.gdx.graphics.Color;

/**
 * Created by nils on 1/27/15.
 *
 * Tweens a color or alpha channel, all color channels, or all color channels and alpha.
 * The tween engine defaults to tweening 3 values at most, so to use this accessor you need to call
 * Tween.setCombinedAttributesLimit(4) when setting things up.
 */
public class ColorAccessor implements TweenAccessor<Color> {
	public static final int R      = 0;
	public static final int G      = 1;
	public static final int B      = 2;
	public static final int A      = 3;
	public static final int COLORS = 4;
	public static final int ALL    = 5;
	@Override
	public int getValues(Color color, int tweenType, float[] returnValues) {
		switch (tweenType) {
			case R:
				returnValues[0] = color.r;
				return 1;
			case G:
				returnValues[0] = color.g;
				return 1;
			case B:
				returnValues[0] = color.b;
				return 1;
			case A:
				returnValues[0] = color.a;
				return 1;
			case COLORS:
				returnValues[0] = color.r;
				returnValues[1] = color.g;
				returnValues[2] = color.b;
				return 3;
			case ALL:
				returnValues[0] = color.r;
				returnValues[1] = color.g;
				returnValues[2] = color.b;
				returnValues[3] = color.a;
				return 4;
			default:
				assert false;
				return -1;
		}
	}

	@Override
	public void setValues(Color color, int tweenType, float[] newValues) {
		switch (tweenType) {
			case R:
				color.r = newValues[0];
				break;
			case G:
				color.g = newValues[0];
				break;
			case B:
				color.b = newValues[0];
				break;
			case A:
				color.a = newValues[0];
				break;
			case COLORS:
				color.r = newValues[0];
				color.g = newValues[1];
				color.b = newValues[2];
				break;
			case ALL:
				color.r = newValues[0];
				color.g = newValues[1];
				color.b = newValues[2];
				color.a = newValues[3];
				break;
			default:
				assert false;
				break;
		}
	}
}
