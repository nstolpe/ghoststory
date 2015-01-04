package com.hh.ghoststory.accessors;

import aurelienribon.tweenengine.TweenAccessor;
import com.badlogic.gdx.math.Quaternion;
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
    public static final int ROTATION = 7;
    public static final int ALL = 8;
    private Vector3 trans = new Vector3();
    private Vector3 axisVec = new Vector3();
    private Quaternion currentRotation = new Quaternion();
    private float angle;

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
//                System.out.println("x: " + returnValues[0] + "y: " + returnValues[1] + "z: " + returnValues[2]);
                return 3;
            // returns the current angle
            case ROTATION:
                axisVec = new Vector3();
//                float angle = (target.model.transform.getRotation(new Quaternion()).getAxisAngle(axisVec) * axisVec.nor().y);
                angle = target.model.transform.getRotation(new Quaternion()).getAxisAngle(axisVec) * axisVec.nor().y;
                returnValues[0] = angle;
//                System.out.println(returnValues[0]);
                return 1;
            case ALL:
                axisVec = new Vector3();
                angle = target.model.transform.getRotation(new Quaternion()).getAxisAngle(axisVec) * axisVec.nor().y;
                returnValues[0] = angle;
                returnValues[1] = trans.x;
                returnValues[2] = trans.y;
                returnValues[3] = trans.z;
                return 4;
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
//                System.out.println("x: " + newValues[0] + "y: " + newValues[1] + "z: " + newValues[2]);
                break;
            case ROTATION:
                target.model.transform.setToRotation(Vector3.Y, newValues[0]);
//                System.out.println(newValues[0]);
                break;
            case ALL:
                target.model.transform.setToTranslation(newValues[1], newValues[2], newValues[3]);
                target.model.transform.setToRotation(Vector3.Y, newValues[0]);
                break;
            default:
                assert false;
                break;
        }
    }
}