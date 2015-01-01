package com.hh.ghoststory.accessors;

import aurelienribon.tweenengine.TweenAccessor;
import com.badlogic.gdx.math.Vector3;
import com.hh.ghoststory.game_models.core.DynamicModel;

/**
 * Created by nils on 12/31/14.
 */
public class GameModelTweenAccessor implements TweenAccessor<DynamicModel> {
    public static final int POSITION_X = 1;
    public static final int POSITION_Y = 2;
    public static final int POSITION_XY = 3;
    public static final int POSITION_YZ = 4;
    public static final int POSITION_ZX = 5;
    public static final int POSITION_XYZ = 6;
    private Vector3 trans = new Vector3();

    public int getValues(DynamicModel target, int tweenType, float[] returnValues) {
        trans = target.model.transform.getTranslation(trans);
        switch (tweenType) {
            case POSITION_X:
                returnValues[0] = trans.x;
                return 1;
            case POSITION_Y:
                returnValues[0] = trans.y;
                return 1;
            case POSITION_XY:
                returnValues[0] = trans.x;
                returnValues[1] = trans.y;
                return 2;
            case POSITION_YZ:
                returnValues[0] = trans.y;
                returnValues[1] = trans.z;
                return 2;
            case POSITION_ZX:
                returnValues[0] = trans.z;
                returnValues[1] = trans.x;
                return 2;
            case POSITION_XYZ:
                returnValues[0] = trans.x;
                returnValues[1] = trans.y;
                returnValues[2] = trans.z;
                return 3;
            default:
                assert false;
                return -1;
        }

    }

    public void setValues(DynamicModel target, int tweenType, float[] newValues) {
        trans = target.model.transform.getTranslation(trans);
        switch (tweenType) {
            case POSITION_X:
                target.model.transform.setToTranslation(newValues[0], trans.y, trans.z);
                break;
            case POSITION_Y:
                target.model.transform.setToTranslation(trans.x, newValues[0], trans.z);
                break;
            case POSITION_XY:
                target.model.transform.setToTranslation(newValues[0], newValues[1], trans.z);
                break;
            case POSITION_YZ:
                target.model.transform.setToTranslation(trans.x, newValues[1], newValues[2]);
                break;
            case POSITION_ZX:
                target.model.transform.setToTranslation(newValues[0], trans.y, newValues[2]);
                break;
            case POSITION_XYZ:
                target.model.transform.setToTranslation(newValues[0], newValues[1], newValues[2]);
                break;
            default:
                assert false;
                break;
        }
    }
}