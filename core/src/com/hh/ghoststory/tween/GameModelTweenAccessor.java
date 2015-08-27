package com.hh.ghoststory.tween;

import aurelienribon.tweenengine.TweenAccessor;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;
import com.hh.ghoststory.GameModels.core.DynamicModel;

import java.util.Arrays;

/**
 * Created by nils on 12/31/14.
 */
public class GameModelTweenAccessor implements TweenAccessor<DynamicModel> {
    public static final int FLOAT = 0;
    public static final int POSITION_X = 1;
    public static final int POSITION_Y = 2;
    public static final int POSITION_Z = 3;
    public static final int POSITION_XY = 4;
    public static final int POSITION_YZ = 5;
    public static final int POSITION_ZX = 6;
    public static final int POSITION_XYZ = 7;
    public static final int ROTATION = 8;
    public static final int ALL = 9;
    private Vector3 trans = new Vector3();
    private Vector3 axisVec = new Vector3(0,1,0);
    private float angle;

    public int getValues(DynamicModel target, int tweenType, float[] returnValues) {
	    if (Arrays.asList(new int[]{ POSITION_X, POSITION_Y, POSITION_Z, POSITION_XY, POSITION_YZ, POSITION_ZX, POSITION_XYZ, ALL }).contains(tweenType))
		    this.trans = target.model.transform.getTranslation(this.trans);

        switch (tweenType) {
            case POSITION_X:
                returnValues[0] = this.trans.x;
                return 1;
            case POSITION_Y:
                returnValues[0] = this.trans.y;
                return 1;
            case POSITION_Z:
                returnValues[0] = this.trans.z;
                return 1;
            case POSITION_XY:
                returnValues[0] = this.trans.x;
                returnValues[1] = this.trans.y;
                return 2;
            case POSITION_YZ:
                returnValues[0] = this.trans.y;
                returnValues[1] = this.trans.z;
                return 2;
            case POSITION_ZX:
                returnValues[0] = this.trans.z;
                returnValues[1] = this.trans.x;
                return 2;
            case POSITION_XYZ:
                returnValues[0] = this.trans.x;
                returnValues[1] = this.trans.y;
                returnValues[2] = this.trans.z;
                return 3;
            // returns the current angle
            case ROTATION:
//	            this.angle = target.model.transform.getRotation(new Quaternion()).getYaw();
//                returnValues[0] = this.angle;
//                Quaternion quat = target.model.transform.getRotation(new Quaternion());
                returnValues[0] = target.model.transform.getRotation(new Quaternion()).getYaw();
                returnValues[1] = target.model.transform.getRotation(new Quaternion()).getPitch();
                returnValues[2] = target.model.transform.getRotation(new Quaternion()).getRoll();
                return 1;
	        case ALL:
		        this.angle = target.model.transform.getRotation(new Quaternion()).getYaw();
		        returnValues[0] = this.trans.x;
		        returnValues[1] = this.trans.y;
		        returnValues[2] = this.trans.z;
		        returnValues[3] = this.angle;
		        return 4;
            case FLOAT:
                returnValues[0] = this.trans.y;
                return 1;
            default:
                assert false;
                return -1;
        }

    }

    public void setValues(DynamicModel target, int tweenType, float[] newValues) {
        this.trans = target.model.transform.getTranslation(this.trans);
        switch (tweenType) {
            case POSITION_X:
                target.model.transform.setTranslation(newValues[0], this.trans.y, this.trans.z);
                break;
            case POSITION_Y:
                target.model.transform.setTranslation(this.trans.x, newValues[0], this.trans.z);
                break;
            case POSITION_Z:
                target.model.transform.setTranslation(this.trans.x, this.trans.y, newValues[0]);
                break;
            case POSITION_XY:
                target.model.transform.setTranslation(newValues[0], newValues[1], this.trans.z);
                break;
            case POSITION_YZ:
                target.model.transform.setTranslation(this.trans.x, newValues[0], newValues[1]);
                break;
            case POSITION_ZX:
                target.model.transform.setTranslation(newValues[1], this.trans.y, newValues[0]);
                break;
            case POSITION_XYZ:
                target.model.transform.setTranslation(newValues[0], this.trans.y, newValues[2]);
//                target.model.transform.setTranslation(newValues[0], newValues[1], newValues[2]);
                break;
            case ROTATION:
	            Quaternion currentRotation = target.model.transform.getRotation(new Quaternion());
//                currentRotation.add()
//	            target.model.transform.setFromEulerAngles(newValues[0], currentRotation.getPitch(), currentRotation.getRoll()).setTranslation(this.trans);
	            target.model.transform.setFromEulerAngles(newValues[0], newValues[1], newValues[2]).setTranslation(this.trans);
                break;
	        case ALL:
		        target.model.transform.setToRotation(Vector3.Y, newValues[3]).setTranslation(newValues[0], newValues[1], newValues[2]);
		        break;
            case FLOAT:
                target.model.transform.setTranslation(this.trans.x, newValues[0], this.trans.z);
            default:
                assert false;
                break;
        }
    }
}