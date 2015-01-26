package com.hh.ghoststory.accessors;

import aurelienribon.tweenengine.TweenAccessor;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g3d.environment.PointLight;

/**
 * Created by nilsstolpe on 1/26/15.
 */
public class PointLightTweenAccessor implements TweenAccessor<PointLight> {
    public static final int POSITION_Z = 3;
    public static final int POSITION_XYZ = 7;
    @Override
    public int getValues(PointLight pointLight, int tweenType, float[] returnValues) {
        switch (tweenType) {
            case POSITION_XYZ:
                returnValues[0] = pointLight.position.x;
                returnValues[1] = pointLight.position.y;
                returnValues[2] = pointLight.position.z;
                return 3;
            case POSITION_Z:
                returnValues[0] = pointLight.position.z;
                return 1;
            default:
                assert false;
                return -1;
        }
    }

    @Override
    public void setValues(PointLight pointLight, int tweenType, float[] newValues) {
        switch (tweenType) {
            case POSITION_XYZ:
                pointLight.position.x = newValues[0];
                pointLight.position.y = newValues[1];
                pointLight.position.z = newValues[2];
                break;
            case POSITION_Z:
                pointLight.position.z = newValues[0];
                break;
            default:
                assert false;
        }


    }
}
