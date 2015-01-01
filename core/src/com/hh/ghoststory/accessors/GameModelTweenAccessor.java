package com.hh.ghoststory.accessors;

import aurelienribon.tweenengine.TweenAccessor;
import com.hh.ghoststory.game_models.core.DynamicModel;

/**
 * Created by nils on 12/31/14.
 */
public class GameModelTweenAccessor implements TweenAccessor<DynamicModel> {
    public static final int POSITION_X = 1;
    public static final int POSITION_Y = 2;
    public static final int POSITION_XY = 3;
    public static final int POSITION_XYZ = 4;

    public int getValues(DynamicModel target, int tweenType, float[] returnValues) {
        switch (tweenType) {
            case POSITION_X:
                returnValues[0] = target.position.x;
                return 1;
            case POSITION_Y:
                returnValues[0] = target.position.y;
                return 1;
            case POSITION_XY:
                returnValues[0] = target.position.x;
                returnValues[1] = target.position.y;
                return 2;
            case POSITION_XYZ:
                returnValues[0] = target.position.x;
                returnValues[1] = target.position.y;
                returnValues[2] = target.position.z;
                return 3;
            default:
                assert false;
                return -1;
        }

    }

    public void setValues(DynamicModel target, int tweenType, float[] newValues) {
        switch (tweenType) {
            case POSITION_X:
                target.model.transform.setToTranslation(newValues[0], target.position.y, target.position.z);
//                target.position.x = newValues[0];
                break;
            case POSITION_Y:
                target.model.transform.setToTranslation(target.position.x, newValues[0], target.position.z);
//                target.position.y = newValues[0];
                break;
            case POSITION_XY:
                target.model.transform.setToTranslation(newValues[0], newValues[1], target.position.z);
//                target.position.x = newValues[0];
//                target.position.y = newValues[1];
                break;
            case POSITION_XYZ:
                target.model.transform.setToTranslation(newValues[0], newValues[1], newValues[2]);
//                target.position.x = newValues[0];
//                target.position.y = newValues[1];
//                target.position.z = newValues[2];
                break;
            default:
                assert false;
                break;

        }
    }
}